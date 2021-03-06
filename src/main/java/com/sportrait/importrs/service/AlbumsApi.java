package com.sportrait.importrs.service;


import ch.unartig.controller.Client;
import ch.unartig.exceptions.NotAuthorizedException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.sportrait.imgRecognition.ImageRecognitionPostProcessor;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.ImportStatus;
import ch.unartig.studioserver.businesslogic.SportsAlbumMapper;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.Album;
import com.sportrait.importrs.model.AlbumImportStatus;
import com.sportrait.importrs.model.ImportUpdates;
import com.sportrait.importrs.model.Importable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Path("/albums")
public class AlbumsApi {
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = LogManager.getLogger();

    static Album convertToAlbumDTO(ch.unartig.studioserver.model.Album album) {
        Album albumDTO = new Album();

        albumDTO.id(album.getGenericLevelId());
        albumDTO.description(album.getDescription());
        albumDTO.title(album.getLongTitle());
        albumDTO.status(album.getPublish() ? Album.StatusEnum.PUBLISHED : Album.StatusEnum.HIDDEN);
        albumDTO.freeHighresDownload(album.isHasFreeHighResDownload());
        albumDTO.photosCount(album.getNumberOfPhotos());
        albumDTO.creationDate(album.getFirstPhotoInAlbum().getUploadDate());
        // TODO later: ??
        // albumDTO.asvzLogoRelativeUrl(album.getProducts()): // needs a new DB field?
        // albumDTO.photosS3Uri(...); // needed?
        // albumDTO.photographer(album.getPhotographer());
        // albumDTO.products(album.getActiveProducts())

        return albumDTO;
    }


    /**
     * Returns all albums of authenticated photographer
     *
     * @return
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllAlbums() {
        _logger.info("GET /albums/");
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.debug("authenticated user : [" + client.getUsername() + "]");

        return Response
                .ok()
                .entity(client.getPhotographer().getAlbums()
                        .stream()
                        .map(AlbumsApi::convertToAlbumDTO)
                        .collect(Collectors.toList()))
                .build();

    }

    @Path("/{albumId}")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbum(@PathParam("albumId") long albumId) {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.debug("authenticated user : [" + client.getUsername() + "]");
        GenericLevelDAO genericLevelDAO = new GenericLevelDAO();
        SportsAlbum album = genericLevelDAO.get(albumId, SportsAlbum.class);
        if (album == null) {
            return Response.status(404, "albumId not found").build();
        }
        try {
            album.checkReadAccessFor(client);
        } catch (NotAuthorizedException e) {
            _logger.info(e);
            return Response.status(403, e.getLocalizedMessage()).build();
        }
        return Response
                .ok()
                .entity(convertToAlbumDTO(album))
                .build();
    }

    /**
     * list all "importables" - locations with uploaded photos that can be imported as albums
     * todo: it is assumed that all importables are folders under the s3 bucket key /upload. Make configurable in API
     * and under the current S3 bucket depending on the environment
     * @return
     */
    @Path("/importables")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listImportables() {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.debug("authenticated user : [" + client.getUsername() + "]");
        List<Importable> importables = new ArrayList<>();
        for (String uploadPathKey : Registry.getFileStorageProvider().getUploadPaths()) {
            String photosS3Uri="s3://"+Registry.getFileStorageProvider().getCurrenctS3Bucket()+"/"+uploadPathKey;
            int numberOfPhotos=Registry.getFileStorageProvider().getNumberOfFineImageFiles(uploadPathKey);
            Importable importable = new Importable();
            importable.setPhotosS3Uri(photosS3Uri);
            importable.setLabel(uploadPathKey);
            importable.setNumberOfPhotos(numberOfPhotos);
            importable.setUploadDate(new Date());
            importables.add(importable);
            _logger.debug("added new Importable ["+photosS3Uri+", "+ uploadPathKey +", "+numberOfPhotos+"]");
        }


        return Response
                .ok()
                .entity(importables)
                .build();
    }


    /**
     * Return a map with all currently imported albums and their import statuses - non-broadcast SSE resource
     * Todo : think about securing this endpoint (currently unsecured!)
     * @param eventSink context passed (Jersey injected) eventSink (i.e. output or target to send event data)
     * @param sse context passed (Jersey injected) factory to build the eventbuilder
     */
    @Path("importUpdatesSSE")
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void getServerSentEvents(@Context SseEventSink eventSink, @Context Sse sse) {
        new Thread(() -> {
            // todo think about broadcasting and start loop only once
            boolean lastImportUpdateIsEmpty=true;
            while(true) { // start infinite loop
                ImportUpdates importUpdates = getImportUpdates();
                if (!importUpdates.isEmpty()) { // only send event on available data
                    _logger.debug("sending sse import status - [" + importUpdates.size() + "] imports running ");
                    final OutboundSseEvent event = sse.newEventBuilder()
                            .name("import-status-update-event")
                            .mediaType(MediaType.APPLICATION_JSON_TYPE)
                            .data(ImportUpdates.class, importUpdates)
                            .build();
                    eventSink.send(event);
                    lastImportUpdateIsEmpty=false;
                } else if (!lastImportUpdateIsEmpty) {
                    // if the last poll before this one was not empty,
                    // send one one last empty one
                    final OutboundSseEvent event = sse.newEventBuilder()
                            .name("import-status-update-event")
                            .mediaType(MediaType.APPLICATION_JSON_TYPE)
                            .data(ImportUpdates.class, importUpdates)
                            .build();
                    eventSink.send(event);
                    lastImportUpdateIsEmpty=true;
                }
                try {
                    // ... and then wait 2 second
                    TimeUnit.SECONDS.sleep(Registry._ALBUM_IMPORT_STATUS_TIMEOUT_SEC);
                } catch (InterruptedException e) {
                    _logger.info("timeout interrupted - continuing", e);
                }
            }
        }).start();
    }


    /**
     * Return a map with all currently imported albums and their import statuses - regular REST API resource
     *
     * @return
     */
    @Path("/importUpdates")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumImportUpdates() {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.debug("authenticated user : [" + client.getUsername() + "]");
        // a bit complicated?
        ImportUpdates importUpdates = getImportUpdates();
        // todo : also consider open queued for number recognition to be returned

        return Response
                .ok()
                .entity(importUpdates)
                .build();
    }

    private ImportUpdates getImportUpdates() {
        ImportUpdates importUpdates = new ImportUpdates();
        // why loop through photosRemaining? ->
        // key of the map = album
        ImportStatus.getInstance().getCurrentlyImportedAlbums().forEach(album -> { // this adds all albums that have photoRemaining to be imported
            _logger.debug("setting status for album : " + album);
            AlbumImportStatus albumStatus = new AlbumImportStatus();
            albumStatus.setAlbumLabel(album.getDescription());
            albumStatus.setPhotosRemaining(ImportStatus.getInstance().getPhotosRemaining(album));
            albumStatus.setPhotosImported(ImportStatus.getInstance().getPhotosImported(album));
            albumStatus.setImportErrors(ImportStatus.getInstance().getImportErrors(album));
            albumStatus.setQueuedForNumberRecognition(ImportStatus.getInstance().getPhotosQueuedForNumberRecognition(album));
            importUpdates.put(album.getGenericLevelId().toString(),albumStatus);
        });
        return importUpdates;
    }

    @Path("/{albumId}")
    @PUT
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAlbum(@PathParam("albumId") long albumId, Album albumDto) {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.debug("authenticated user : [" + client.getUsername() + "]");


        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }

    @Path("/{albumId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAlbum(@PathParam("albumId") long albumId) {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.debug("authenticated user : [" + client.getUsername() + "]");

        GenericLevelDAO genericLevelDAO = new GenericLevelDAO();
        SportsAlbum album = genericLevelDAO.get(albumId, SportsAlbum.class);
        if (album == null) {
            return Response.status(404, "no album with given ID").build();
        }
        try {
            HibernateUtil.beginTransaction();
            album.checkReadAccessFor(client); // todo: check write or delete access!
            // Performance ? could be very slow if each photo deleted separately?
            album.deleteLevel();
            // genericLevelDAO.delete(album);
            HibernateUtil.commitTransaction();
        } catch (NotAuthorizedException e) {
            _logger.info(e);
            return Response.status(403, e.getLocalizedMessage()).build();
        }
        return Response
                .accepted()
                .build();
    }

    @Path("/{albumId}/publish")
    @PATCH
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response publishAlbum(@PathParam("albumId") long albumId) {
        return setAlbumPublishState(albumId, true);
    }


    @Path("/{albumId}/unpublish")
    @PATCH
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response unpublishAlbum(@PathParam("albumId") long albumId) {
        return setAlbumPublishState(albumId, false);
    }

    private Response setAlbumPublishState(long albumId, boolean newPublishState) {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.debug("authenticated user : [" + client.getUsername() + "]");
        GenericLevelDAO genericLevelDAO = new GenericLevelDAO();
        SportsAlbum album = genericLevelDAO.get(albumId, SportsAlbum.class);
        if (album == null) {
            return Response.status(404, "no album with given ID").build();
        }

        Album albumDTO;
        try {
            HibernateUtil.beginTransaction();
            album.setPublish(newPublishState, client);
            albumDTO = convertToAlbumDTO(album);
            HibernateUtil.commitTransaction();
        } catch (NotAuthorizedException e) {
            _logger.info(e);
            return Response.status(403, e.getLocalizedMessage()).build();
        }
        return Response
                .ok()
                .entity(albumDTO)
                .build();
    }


    @Path("/{albumId}/deleteMappings")
    @PATCH
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMappings(@PathParam("albumId") long albumId) {
        _logger.info("PATCH /albums/:albumId/deleteMapping");
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        String result;


        GenericLevelDAO glDao = new GenericLevelDAO();
        ch.unartig.studioserver.model.Album album = glDao.get(albumId, ch.unartig.studioserver.model.Album.class);
        if (album == null) {
            return Response.status(404, "album id not found").build();
        }
        try {
            album.checkReadAccessFor(client); // change to write check ?
            _logger.info("deleting mapping information for albumid " + album.getGenericLevelId());
            SportsAlbumMapper mapper = SportsAlbumMapper.createMapper(album);
            result = mapper.delete();
        } catch (NotAuthorizedException e) {
            _logger.info(e);
            return Response.status(403, e.getLocalizedMessage()).build();
        }

        return Response.accepted().entity(result).build();
    }


    /**
     * Starting the post processing to match known numbers associated with faces to additional face matches
     * @param albumId
     * @return
     */
    @Path("/{albumId}/startPostProcessing")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response startAlbumPostProcessing(@PathParam("albumId") long albumId) {
        _logger.info("POST /albums/:albumId/startPostProcessing");
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        GenericLevelDAO glDao = new GenericLevelDAO();
        ch.unartig.studioserver.model.Album album = glDao.get(albumId, ch.unartig.studioserver.model.Album.class);
        if (album == null) {
            return Response.status(404, "album id not found").build();
        }
        try {
            album.checkReadAccessFor(client); // change to write check ?
            ImageRecognitionPostProcessor postProcessor = new ImageRecognitionPostProcessor(album.getGenericLevelId());
            Thread postProcessorServer = new Thread(postProcessor);
            postProcessorServer.start();
            _logger.info("Post Processor for unknown faces started up - for etappe : " + album.getEventCategory());

        } catch (NotAuthorizedException e) {
            _logger.info(e);
            return Response.status(403, e.getLocalizedMessage()).build();        }

        return Response.accepted().entity("Post-Processing for Album with ID " + albumId + " started.").build();
    }


    @Path("/{albumId}/timingMapping")
    @POST
    @Consumes({"multipart/form-data"})
    @Produces({"application/json"})
    @Secured
    public Response addAlbumTimingMapping(
            @PathParam("albumId") Long albumId,
            @FormDataParam("timingFile") InputStream timingFileInputStream,
            @FormDataParam("timingFile") FormDataContentDisposition fileMetaData,
            @FormDataParam("differencePhotoTiming") Integer differencePhotoTiming,
            @FormDataParam("toleranceSlowFast") Integer toleranceSlowFast,
            @FormDataParam("photoPointAfterTiming") Boolean photoPointAfterTiming,
            @QueryParam("delimiter") String delimiter,
            @Context SecurityContext securityContext)
            throws NotFoundException {

        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.info("POST /albums/:albumId/timingMapping");

        String result;

        // InputStream timingFileInputStream=null;

        if (timingFileInputStream == null) {
            return Response.status(404, "timingFile missing - please provide a file containing the timing information").build();
        } else if (differencePhotoTiming == null) {
            return Response.status(404, "differencePhotoTiming missing").build();
        } else if (toleranceSlowFast == null) {
            return Response.status(404, "toleranceSlowFast missing").build();
        } else if (photoPointAfterTiming == null) {
            photoPointAfterTiming = false;
        }
        GenericLevelDAO glDao = new GenericLevelDAO();
        ch.unartig.studioserver.model.Album album = glDao.get(albumId, ch.unartig.studioserver.model.Album.class);
        if (album == null) {
            return Response.status(404, "album id not found").build();
        }
        try {
            album.checkReadAccessFor(client); // change to write check ?
            // _logger.debug("mappingFile [" + fileMetaData.getFileName() + "] called for albumId [" + albumId + "]");
            _logger.info("mapping for albumid " + album.getGenericLevelId());
            _logger.info("mapping file name : " + fileMetaData.getFileName());

            result = SportsAlbumMapper.createFinishTimeMapper(
                    timingFileInputStream,
                    album,
                    differencePhotoTiming,
                    toleranceSlowFast,
                    !photoPointAfterTiming // inverse logic used in old code!
            ).mapFinishOrStartTime();
        } catch (NotAuthorizedException e2) {
            _logger.info(e2);
            return Response.status(403, e2.getLocalizedMessage()).build();
        } catch (UnartigException e) {
            _logger.error("cannot map sports album : ", e);
            return Response.status(400, e.getLocalizedMessage()).build();
        }
        result = result + " - read" + fileMetaData.getSize() +" bytes";
        return Response.accepted().entity(result).build();
    }


}

package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.NotAuthorizedException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.businesslogic.SportsAlbumMapper;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.log4j.Logger;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Path("/albums")
public class AlbumsApi {
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());

    static Album convertToAlbumDTO(ch.unartig.studioserver.model.Album album) {
        Album albumDTO = new Album();

        albumDTO.id(album.getGenericLevelId());
        albumDTO.description(album.getDescription());
        albumDTO.title(album.getLongTitle());
        albumDTO.status(album.getPublish() ? Album.StatusEnum.PUBLISHED : Album.StatusEnum.HIDDEN);
        albumDTO.freeHighresDownload(album.isHasFreeHighResDownload());
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
        // alternative solution : (why?)
        // GenericLevelDAO genericLevelDAO = new GenericLevelDAO();
        // genericLevelDAO.listAlbumsForPhotographer(client.getPhotographer().getPhotographerId());
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
            genericLevelDAO.delete(album);
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

        GenericLevelDAO glDao = new GenericLevelDAO();
        ch.unartig.studioserver.model.Album album = glDao.get(albumId, ch.unartig.studioserver.model.Album.class);
        if (album == null) {
            return Response.status(404, "album id not found").build();
        }
        try {
            album.checkReadAccessFor(client); // change to write check ?
            _logger.info("deleting mapping information for albumid " + album.getGenericLevelId());
            SportsAlbumMapper mapper = SportsAlbumMapper.createMapper(album);
            mapper.delete();
        } catch (NotAuthorizedException e) {
            _logger.info(e);
            return Response.status(403, e.getLocalizedMessage()).build();
        }

        return Response.accepted().build();
    }


    @Path("/{albumId}/startPostProcessing")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response startAlbumPostProcessing(@PathParam("albumId") long albumId) {
        _logger.info("POST /albums/:albumId/startPostProcessing");


        // check how it's done on current admin page


        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }


    @POST
    @Path("/{albumId}/timingMapping")
    @Consumes({"multipart/form-data"})
    @Produces({"application/json"})
    @Secured
    @Operation(summary = "Post a new timing mapping for an album. Will start the mapping process", description = "", tags = {"album"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successful operation - mapping started", content = @Content(schema = @Schema(implementation = Album.class))),
            @ApiResponse(responseCode = "405", description = "Invalid input")})
    public Response addAlbumTimingMapping(
            @Parameter(in = ParameterIn.PATH, description = "ID of the album to add a timing mapping", required = true) @PathParam("albumId") Long albumId,
            @FormDataParam("timingFile") InputStream timingFileInputStream,
            @FormDataParam("differencePhotoTiming") Integer differencePhotoTiming,
            @FormDataParam("toleranceSlowFast") Integer toleranceSlowFast,
            @FormDataParam("photoPointAfterTiming") Boolean photoPointAfterTiming,
            @Parameter(in = ParameterIn.QUERY, description = "define the delimiter used in the timing file") @QueryParam("delimiter") String delimiter,
            @Context SecurityContext securityContext)
            throws NotFoundException {

        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.info("POST /albums/:albumId/timingMapping");

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
        try {
            album.checkReadAccessFor(client); // change to write check ?
            // _logger.debug("mappingFile [" + fileMetaData.getFileName() + "] called for albumId [" + albumId + "]");
            _logger.debug("mapping for albumid " + album.getGenericLevelId());

            SportsAlbumMapper.createFinishTimeMapper(
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

        return Response.accepted().build();
    }


}

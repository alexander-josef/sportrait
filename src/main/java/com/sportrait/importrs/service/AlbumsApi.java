package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.NotAuthorizedException;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.*;
import org.apache.log4j.Logger;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Path("")
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

        // should the mapping be a resource?


        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
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


    @Path("/{albumId}/timingMapping")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAlbumTimingMapping(@PathParam("albumId") long albumId, TimingMapping timingMappingDto) {
        _logger.info("POST /albums/:albumId/timingMapping");

        // must process a file here (timingFile)

        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }




}

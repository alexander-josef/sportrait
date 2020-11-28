package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.Album;
import com.sportrait.importrs.model.Product;
import com.sportrait.importrs.model.TimingMapping;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/albums")
public class AlbumsApi {
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * Todo think about visibility - only logged in user probably
     * @return
     */
    @Path("")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllAlbums(){

        _logger.info("GET /albums/");
        return  Response.ok().entity("not implemented").build();

    }

    @Path("/{albumId}")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbum(@PathParam("albumId") long albumId){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{albumId}")
    @PUT
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAlbum(@PathParam("albumId") long albumId, Album albumDto){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{albumId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAlbum(@PathParam("albumId") long albumId){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{albumId}/publish")
    @PATCH
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response publishAlbum(@PathParam("albumId") long albumId){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


    @Path("/{albumId}/unpublish")
    @PATCH
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response unpublishAlbum(@PathParam("albumId") long albumId){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


    @Path("/{albumId}/deleteMappings")
    @PATCH
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMappings(@PathParam("albumId") long albumId){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


    @Path("/{albumId}/startPostProcessing")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response startAlbumPostProcessing(@PathParam("albumId") long albumId){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


    @Path("/{albumId}/timingMapping")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAlbumTimingMapping(@PathParam("albumId") long albumId, TimingMapping timingMappingDto){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


    @Path("/{albumId}/products")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAlbumProducts(@PathParam("albumId") long albumId){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


    @Path("/{albumId}/products")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAlbumProduct(@PathParam("albumId") long albumId, Product productDto){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    @Path("/{albumId}/products/{productId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAlbumProduct(@PathParam("productId") long albumId){

        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter
        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


}

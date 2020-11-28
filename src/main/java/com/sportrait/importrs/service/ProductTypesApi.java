package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.ProductType;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/productTypes/")
public class ProductTypesApi {

    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = Logger.getLogger(getClass().getName());




    @Path("")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProductTypes(){

        _logger.info("GET /productTypes");
        return  Response.ok().entity("not implemented").build();

    }

    /**
     * Needed?
     * @param productType
     * @return
     */
    @Path("")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProductType(ProductType productType){
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }

    /**
     * Needed? A user will delete products from albums. An admin might want to maintain (and delete) productTypes
     * @param productTypeId
     * @return
     */
    @Path("/{productTypeId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProductType(@PathParam("productTypeId") long productTypeId){

        _logger.info("DELETE /productType/"+productTypeId);
        // load event category
        Client client = (Client)requestContext.getProperty("client"); // client from authentication filter

        return  Response.ok().entity("not implemented - authenticated user : ["+client.getUsername()+"]").build();
    }


}

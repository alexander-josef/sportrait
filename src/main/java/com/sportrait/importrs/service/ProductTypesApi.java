package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import ch.unartig.studioserver.persistence.DAOs.ProductTypeDAO;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.Price;
import com.sportrait.importrs.model.ProductType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/productTypes")
public class ProductTypesApi {

    // inject request context to read out client - client to be put to request context in auth filter
    @Context
    ContainerRequestContext requestContext;
    private final Logger _logger = LogManager.getLogger(getClass().getName());


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listProductTypes() {
        _logger.info("GET /productTypes");
        ProductTypeDAO pdDao = new ProductTypeDAO();
        return Response.ok()
                .entity(pdDao.listProductTypes()
                        .stream()
                        .map(this::convertToProductTypeDTO)
                        .collect(Collectors.toList()))
                .build();

    }

    private ProductType convertToProductTypeDTO(ch.unartig.studioserver.model.ProductType productType) {
        ProductType productTypeDTO = new ProductType();
        productTypeDTO.setProductTypeId(productType.getProductTypeId());
        productTypeDTO.setName(productType.getName());
        productTypeDTO.setDescription(productType.getDescription());
        productTypeDTO.setDigitalProduct(productType.getDigitalProduct());
        productTypeDTO.setPrices(
                productType.getPrices()
                        .stream()
                        .map(this::convertToPriceDTO)
                        .collect(Collectors.toList())
        );
        return productTypeDTO;
    }

    private Price convertToPriceDTO(ch.unartig.studioserver.model.Price price) {
        Price priceDTO = new Price();
        priceDTO.setPriceId(price.getPriceId());
        priceDTO.setStatus(Price.StatusEnum.ACTIVE);
        priceDTO.setPriceCHF(price.getPriceCHF());
        priceDTO.setPriceEUR(price.getPriceEUR());
        priceDTO.setComment(price.getComment());
        return priceDTO;
    }

    /**
     * Needed?
     *
     * @param productType
     * @return
     */
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProductType(ProductType productType) {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }

    /**
     * Needed? A user will delete products from albums. An admin might want to maintain (and delete) productTypes
     *
     * @param productTypeId
     * @return
     */
    @Path("/{productTypeId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProductType(@PathParam("productTypeId") long productTypeId) {

        _logger.info("DELETE /productType/" + productTypeId);
        // load event category
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter

        return Response.ok().entity("not implemented - authenticated user : [" + client.getUsername() + "]").build();
    }


}

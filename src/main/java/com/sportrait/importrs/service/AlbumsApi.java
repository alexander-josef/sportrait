package com.sportrait.importrs.service;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.NotAuthorizedException;
import ch.unartig.studioserver.model.SportsAlbum;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.ProductDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import com.sportrait.importrs.Secured;
import com.sportrait.importrs.model.*;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.Iterator;
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


    @Path("/{albumId}/products")
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAlbumProducts(@PathParam("albumId") long albumId) {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.info("GET /albums/:albumId/products");
        GenericLevelDAO genericLevelDAO = new GenericLevelDAO();
        SportsAlbum album = genericLevelDAO.get(albumId, SportsAlbum.class);
        if (album == null) {
            return Response.status(404, "no album with given ID").build();
        }
        try {
            album.checkReadAccessFor(client);
            return Response
                    .ok()
                    .entity(album.getProducts()
                            .stream()
                            .map(this::convertToProductDTO)
                            .collect(Collectors.toList()))
                    .build();
        } catch (NotAuthorizedException e) {
            return Response.status(404, e.getMessage()).build();
        }

    }

    private Product convertToProductDTO(ch.unartig.studioserver.model.Product product) {
        Product productDto = new Product();
        productDto.productId(product.getProductId());
        productDto.productName(product.getProductName());
        productDto.productType(convertToProductTypeDTO(product.getProductType()));
        productDto.price(convertToPriceDTO(product.getPrice()));
        productDto.status(!product.getInactive() ? Product.StatusEnum.ACTIVE : Product.StatusEnum.ARCHIVED);

        return productDto;
    }

    private Price convertToPriceDTO(ch.unartig.studioserver.model.Price price) {
        Price priceDto = new Price();
        priceDto.priceId(price.getPriceId());
        priceDto.priceCHF(price.getPriceCHF());
        priceDto.priceEUR(price.getPriceEUR());
        priceDto.comment(price.getComment());
        priceDto.status(Price.StatusEnum.ACTIVE); // todo ; not available yet, set later

        return priceDto;
    }

    private ProductType convertToProductTypeDTO(ch.unartig.studioserver.model.ProductType productType) {
        ProductType productTypeDto = new ProductType();
        productTypeDto.productTypeId(productType.getProductTypeId());
        productTypeDto.name(productType.getName());
        productTypeDto.description(productType.getDescription());
        productTypeDto.digitalProduct(productType.getDigitalProduct());
        productTypeDto.prices(productType.getPrices()
                .stream()
                .map(this::convertToPriceDTO)
                .collect(Collectors.toList()));
        return productTypeDto;
    }


    /**
     * Add a new product from the productDto for the given albumId
     * The following logic applies:
     * - there is exactly zero or one product for a given productType
     * - the price can be one of the possible prices defined as prices collection on the productType
     *
     * @param albumId
     * @param productDto
     * @return
     */
    @Path("/{albumId}/products")
    @POST
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAlbumProduct(@PathParam("albumId") long albumId, Product productDto) {
        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.info("POST /albums/:albumId/products");
        GenericLevelDAO genericLevelDAO = new GenericLevelDAO();
        SportsAlbum album = genericLevelDAO.get(albumId, SportsAlbum.class);
        if (album == null) {
            return Response.status(404, "no album with given ID").build();
        }
        try {
            album.checkReadAccessFor(client); // todo make it write access check
            HibernateUtil.beginTransaction();

            ch.unartig.studioserver.model.Product product = convertFromProductDTO(productDto, album);

            if (!album.getProducts().add(product)) {
                // product cannot be added due to set restrictions (identical element already existing)
                return Response
                        .status(405, "Cannot create a new product - product with identical productType already exists. " +
                                "Try updating existing product.")
                        .build();
            }
            GenericLevelDAO glDao = new GenericLevelDAO();
            // saving album is okay - new product is cascaded down (only saving or updating the product would be OK too - but how about deleting a product?)
            glDao.saveOrUpdate(album);
            Product newProductDto = convertToProductDTO(product); // create before commit closes session
            HibernateUtil.commitTransaction();
            newProductDto.setProductId(product.getProductId()); // id only available after db transaction
            return Response
                    .ok()
                    .entity(newProductDto)
                    .build();
        } catch (NotAuthorizedException e) {
            return Response.status(404, e.getMessage()).build();
        }

    }

    private ch.unartig.studioserver.model.Product convertFromProductDTO(Product productDto, ch.unartig.studioserver.model.Album album) {
        // todo : for existing product (productID passed)
        // for new product (product ID not given ) :
        ch.unartig.studioserver.model.Product product =
                new ch.unartig.studioserver.model.Product(
                        productDto.getProductType().getProductTypeId(),
                        productDto.getPrice().getPriceId(),
                        album
                );
        product.setProductName(productDto.getProductName());
        product.setInactive(productDto.getStatus() == Product.StatusEnum.ARCHIVED);
        return product;
    }

    /**
     * Used for saving new productTypes? When is it used?
     *
     * @param productTypeDto
     * @return
     */
    private ch.unartig.studioserver.model.ProductType convertFromProductTypeDTO(ProductType productTypeDto) {
        ch.unartig.studioserver.model.ProductType productType = new ch.unartig.studioserver.model.ProductType();
        productType.setDigitalProduct(productTypeDto.isDigitalProduct());
        productType.setDescription(productTypeDto.getDescription());
        productType.setName(productTypeDto.getName());
        productType.setPrices(productTypeDto.getPrices()
                .stream()
                .map(this::convertFromPriceDTO)
                .collect(Collectors.toSet()));
        return productType;
    }

    /**
     * Used to store new Price's ? When used?
     *
     * @param priceDto
     * @return
     */
    private ch.unartig.studioserver.model.Price convertFromPriceDTO(Price priceDto) {
        ch.unartig.studioserver.model.Price price = new ch.unartig.studioserver.model.Price();
        price.setPriceCHF(priceDto.getPriceCHF());
        price.setPriceEUR(priceDto.getPriceEUR());
        price.setComment(priceDto.getComment());
        return price;
    }

    /**
     * Todo : move to productsApi - own class, own tag in OAS definition
     * @param albumId
     * @return
     */
    @Path("/{albumId}/products/{productId}")
    @DELETE
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAlbumProduct(@PathParam("albumId") long albumId,@PathParam("productId") long productId) {
        _logger.info("DELETE /albums/:albumId/products/:productId");

        Client client = (Client) requestContext.getProperty("client"); // client from authentication filter
        _logger.debug("authenticated user : [" + client.getUsername() + "]");


        GenericLevelDAO genericLevelDAO = new GenericLevelDAO();
        ProductDAO productDAO = new ProductDAO();
        SportsAlbum album = genericLevelDAO.get(albumId, SportsAlbum.class);
        if (album == null) {
            return Response.status(404, "no album exists with given ID").build();
        }
        try {
            HibernateUtil.beginTransaction();
            album.checkReadAccessFor(client); // todo: check write or delete access!
            if (album.getProducts().remove(productDAO.get(productId))) { // checks both, if product exists at all and for album
                genericLevelDAO.saveOrUpdate(album);
                HibernateUtil.commitTransaction();
            } else {
                HibernateUtil.rollbackTransaction();
                return Response.status(404,"no product with given product Id exists").build();
            }

        } catch (NotAuthorizedException e) {
            _logger.info(e);
            return Response.status(403, e.getLocalizedMessage()).build();
        }
        return Response
                .accepted()
                .build();
    }


}

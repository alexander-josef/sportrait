/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 12, 2005$
 *
 * Copyright (c) 2005 unartig AG  --  All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.2  2007/03/13 16:55:03  alex
 * template for properties
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.9  2006/11/17 13:21:41  alex
 * email notifiaction fix
 *
 * Revision 1.8  2006/11/14 13:45:20  alex
 * fixing shopping cart when product is reset to default
 *
 * Revision 1.7  2006/11/14 11:07:46  alex
 * shipping handling
 *
 * Revision 1.6  2006/11/13 22:13:30  alex
 * shoppingcart
 *
 * Revision 1.5  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.4  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.3  2006/02/27 11:41:20  alex
 * shopping cart has now an initial format with quantity = 1
 *
 * Revision 1.2  2006/02/08 15:35:09  alex
 * confirmation message again
 *
 * Revision 1.1  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.5  2005/11/17 19:41:13  alex
 * new fix sc overview
 *
 * Revision 1.4  2005/11/17 13:36:06  alex
 * check out overview works
 *
 * Revision 1.3  2005/11/14 18:25:01  alex
 * pricing in shopping cart enabled, minor major units stuff works
 *
 * Revision 1.2  2005/11/14 18:15:07  alex
 * pricing in shopping cart enabled
 *
 * Revision 1.1  2005/11/12 23:14:11  alex
 * add ScOrderItem
 *
 ****************************************************************/
package ch.unartig.studioserver.beans;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigInvalidArgument;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.model.Price;
import ch.unartig.u_core.model.Product;
import ch.unartig.u_core.model.Photo;
import ch.unartig.u_core.persistence.DAOs.PhotoDAO;
import ch.unartig.u_core.persistence.DAOs.ProductDAO;
import org.apache.log4j.Logger;

/**
 * Bean class to represent a shopping cart order item
 * will be used in sc form to populate the shopping cart<br>
 * an item consits of :
 * - photoId (PK from db)
 * - produdctID (PK from db)
 * - quantity: how many of that product and photo
 * - item price: quantity x productprice
 */
public class ScOrderItem {
    private Long photoId;
    /* is -1 if no product has been selected*/
    private Long productId;
    private int quantity;
    /*back reference to shopping cart*/
    private ShoppingCart shoppingCart;
    /*item price, example: 0.60*/
    private double itemPriceCHF;
    private double itemPriceEUR;
    private double itemPriceGBP;
    private double itemPriceSEK;

    Logger _logger = Logger.getLogger(getClass().getName());

    public ScOrderItem() {
    }

    /**
     * constructor
     *
     * @param photoId
     * @param productId    id from view page. can be &lt;1 if no product has been selected
     * @param amount
     * @param shoppingCart
     */
    public ScOrderItem(Long photoId, Long productId, int amount, ShoppingCart shoppingCart) {
        this.photoId = photoId;
        this.productId = productId;
        this.quantity = amount;

        this.shoppingCart = shoppingCart;
    }

    /**
     * itemPrice: quantity x productprice
     * this needs to be called every time a photo has been updated in order to calculate the correct prices
     *
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *
     */
    public void updateItemPrice() throws UAPersistenceException {
        ProductDAO pDao = new ProductDAO();
        Product product = pDao.load(productId);

        _logger.debug("updating item price for product ["+product+"] with quantity ["+getQuantity()+"]");
        itemPriceCHF = product.getPrice().getPriceCHF().doubleValue() * getQuantity();
        itemPriceEUR = product.getPrice().getPriceEUR().doubleValue() * getQuantity();
    }

    /**
     * called to return formatted price for view
     * @return
     * @throws UnartigInvalidArgument
     */
    public String getFormattedPrice() throws UnartigInvalidArgument
    {
        return Price.monetaryAmountFormat.format(getPriceCustomerCurrency());
    }


    /**
     * this is called from the view after the the address has been entered. it shows the relevent amount in the correct currency
     *
     * @return the price as formatted string
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument in case customer currency not available
     */
    public String getPrice() throws UnartigInvalidArgument {
        return Price.monetaryAmountFormat.format(getPriceCustomerCurrency());
    }


    /**
     * Check the shopping cart to return the correct currency for the customer
     * (customer already filled out form)
     *
     * @return amount in correct currency as double
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     *          if custumer country not available
     */
    private double getPriceCustomerCurrency() throws UnartigInvalidArgument {
        // check that the country is available
        if (shoppingCart == null || shoppingCart.getCustomerCountry() == null) {
            throw new UnartigInvalidArgument("Customer Country not available");
        }

        if (Registry._SWITZERLAND_COUNTRY_CODE.equals(shoppingCart.getCustomerCountry())) {
            return itemPriceCHF;
        }
        // GERMANY, AUSTRIA
        else if (Registry._GERMANY_COUNTRY_CODE.equals(shoppingCart.getCustomerCountry()) || Registry._AUSTRIA_COUNTRY_CODE.equals(shoppingCart.getCustomerCountry()))
        {
            return itemPriceEUR;
        } else
        {
            throw new UnartigInvalidArgument("Unknown Country");

        }
    }



    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * @return the order item quantity or 1 if the product is digital
     */
    public int getQuantity() {
        // set to 1 if product is digital
        return isDigitalOrderItem() ? 1 : quantity;
//        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Convenience getter to get a photo object from the photoId
     *
     * @return either the Photo object or null if nothing was found
     */
    public Photo getPhoto() {
        PhotoDAO phDao = new PhotoDAO();
        return phDao.load(photoId);
    }

    /**
     * convenience getter to get a Product object from the productId
     * <p> order item can have productID of -1 ...
     *
     * @return either the Photo object or null if no product exists for this item
     */
    public Product getProduct() {
        Product retVal = null;
        ProductDAO pDao = new ProductDAO();
        if (productId.intValue() > 0) {
            try {
                return pDao.load(productId);
            } catch (UAPersistenceException e) {
                _logger.error("Cannot load Product from given productId : " + productId + ";returning null");
            }
        }
        return retVal;
    }

    /**
     * convenience getter; returns true if the shopping cart item is a digital product
     *
     * @return
     */
    public boolean isDigitalOrderItem() {
        return getProduct() != null && getProduct().isDigitalProduct();
    }


    public String toString() {
        return "ScOrderItem{" +
                "photoId=" + photoId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", shoppingCart=" + shoppingCart +
                ", itemPriceCHF=" + itemPriceCHF +
                ", itemPriceEUR=" + itemPriceEUR +
                ", itemPriceGBP=" + itemPriceGBP +
                ", itemPriceSEK=" + itemPriceSEK +
                '}';
    }

    /**
     * @return true if the product of this item is a digital product
     */
    public boolean isDigital() {
        return getProduct().isDigitalProduct();
    }

    /**
     * set prices for all currencies to 0
     */
    public void initPrice() {
        itemPriceCHF = 0;
        itemPriceEUR = 0;
        itemPriceGBP = 0;
        itemPriceSEK = 0;


    }

    public double getItemPriceCHF() {
        return itemPriceCHF;
    }

    public double getItemPriceEUR() {
        return itemPriceEUR;
    }

    public double getItemPriceGBP() {
        return itemPriceGBP;
    }

    public double getItemPriceSEK() {
        return itemPriceSEK;
    }

    public String getFormattedItemPriceCHF() {
        return Price.monetaryAmountFormat.format(itemPriceCHF);
    }

    public String getFormattedItemPriceEUR() {
        return Price.monetaryAmountFormat.format(itemPriceEUR);
    }

    public String getFormattedItemPriceGBP() {
        return Price.monetaryAmountFormat.format(itemPriceGBP);
    }

    public String getFormattedItemPriceSEK() {
        return Price.monetaryAmountFormat.format(itemPriceSEK);
    }
}

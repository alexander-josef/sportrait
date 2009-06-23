/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Nov 4, 2005$
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
 * Revision 1.3  2007/05/03 20:18:19  alex
 * shopping cart preview
 *
 * Revision 1.2  2007/05/01 11:03:09  alex
 * check out process
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.3  2007/03/14 02:41:01  alex
 * initial checkin
 *
 * Revision 1.2  2007/03/13 16:55:03  alex
 * template for properties
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.10  2006/11/14 17:14:26  alex
 * shopping cart fix
 *
 * Revision 1.9  2006/11/14 13:45:20  alex
 * fixing shopping cart when product is reset to default
 *
 * Revision 1.8  2006/11/14 11:07:46  alex
 * shipping handling
 *
 * Revision 1.7  2006/11/13 22:13:30  alex
 * shoppingcart
 *
 * Revision 1.6  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.5  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.4  2006/02/27 11:41:20  alex
 * shopping cart has now an initial format with quantity = 1
 *
 * Revision 1.3  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.2  2006/02/08 15:35:09  alex
 * confirmation message again
 *
 * Revision 1.1  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.24  2005/12/11 21:49:08  alex
 * unartiginfo weg aus text
 *
 * Revision 1.23  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.22  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.21  2005/11/25 10:56:58  alex
 *
 * Revision 1.20  2005/11/20 21:08:06  alex
 * delete of a photo in sc works correct
 *
 * Revision 1.19  2005/11/20 16:42:15  alex
 * sunntig obig
 *
 * Revision 1.18  2005/11/20 10:10:50  alex
 * bug 740 758
 *
 * Revision 1.17  2005/11/19 22:04:04  alex
 * shopping cart reflects different price segments
 *
 * Revision 1.16  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.15  2005/11/18 19:15:52  alex
 * stuff ...
 *
 * Revision 1.14  2005/11/18 17:28:36  alex
 * back link, incl. new interface for naviable objects
 *
 * Revision 1.13  2005/11/17 19:41:13  alex
 * new fix sc overview
 *
 * Revision 1.12  2005/11/17 13:36:06  alex
 * check out overview works
 *
 * Revision 1.11  2005/11/14 18:25:01  alex
 * pricing in shopping cart enabled, minor major units stuff works
 *
 * Revision 1.10  2005/11/14 18:15:07  alex
 * pricing in shopping cart enabled
 *
 * Revision 1.9  2005/11/14 11:52:22  alex
 * delete photo in cart works again
 *
 * Revision 1.8  2005/11/14 11:23:25  alex
 * using mapped property ordered photos in shopping cart overview ! works!
 *
 * Revision 1.7  2005/11/14 10:43:34  alex
 * shopping cart basic functions work. photo list needs a bit more work yet
 *
 * Revision 1.6  2005/11/12 23:15:27  alex
 * using indexed properties ... first step
 *
 * Revision 1.5  2005/11/09 21:59:36  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 * Revision 1.4  2005/11/05 16:00:41  alex
 * tiles error, more sc stuff
 *
 * Revision 1.3  2005/11/05 14:57:08  alex
 * images small correction
 *
 * Revision 1.2  2005/11/05 10:32:14  alex
 * shopping cart and minor problems, exception handling
 *
 * Revision 1.1  2005/11/04 23:02:54  alex
 * shopping cart session
 *
 ****************************************************************/
package ch.unartig.studioserver.beans;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigInvalidArgument;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.model.Product;
import ch.unartig.u_core.model.Price;
import ch.unartig.u_core.model.Photo;
import ch.unartig.u_core.presentation.NavigableObject;
import ch.unartig.u_core.persistence.DAOs.ProductDAO;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import java.io.Serializable;
import java.util.*;

/**
 * Session object to store shopping cart information
 */
public class ShoppingCart extends ActionForm implements Serializable, NavigableObject
{
    Logger _logger = Logger.getLogger(getClass().getName());
    public static final int _INDEX_PHOTO = 0;
    public static final int _INDEX_PRODUCT = 1;
    public static final int _INDEX_QUANTITY = 2;

    private Long orderedPhotoId; // property used when photo is placed in cart
    private Long orderedProductId; // property used when photo is placed in cart



    public void setOrderedProductId(Long orderedProductId) {
        this.orderedProductId = orderedProductId;
    }/*A map of photo-ids and Photos that have been put to the cart */
    private Map orderedPhotos = new HashMap();
    /*A map of products that have been put to the shopping cart*/
    private Map orderedProducts = new HashMap();

    private Map backToAlbumParams;
    /*all items in the cart as a list*/
    private List scItems = new ArrayList();
     /**/
    private double totalPhotosCHF;
    private double totalPhotosEUR;
    private double totalPhotosGBP;
    private double totalPhotosSEK;
    /*action after the form has been posted*/
    private String actionParam;
    private String currency="undefined";
    private String customerCountry;


    /**
     * bean spec need empty constructor
     */
    public ShoppingCart()
    {
        _logger.debug("ShoppingCart.ShoppingCart: Empty Constructor");
    }

    /**
     * convenience mehtod that is used in the preview frame
     * @return
     */
    public Collection getPhotosInShoppingcart()
    {
        return orderedPhotos.values();
    }

    /**
     *
     * @return true if the shopping cart contains only digital products.
     * <p/> --> this is used to select credit card as only valid payment in check out
     */
    public boolean isOnlyDigitalProducts()
    {
        try
        {
            for (Iterator iterator = getScItemsForConfirmation().iterator(); iterator.hasNext();)
            {
                ScOrderItem scItem = (ScOrderItem) iterator.next();
                if (!scItem.getProduct().isDigitalProduct())
                {
                    _logger.debug("found non-digital product, returning");
                    return false;
                }
            }
            _logger.debug("all products digital, returning true");
        } catch (UAPersistenceException e)
        {
            _logger.info("isOnlyDigitalProducts; Cannot query for shopping cart items, returning false",e);
            return false;
        }
        return true;
    }

    /**
     *
     * @return true if an invoice is available for the chosen customer country
     */
    public boolean isInvoiceAvailableCountry()
    {
        // list countries that don't offer invoices
        // AUSTRIA
        if (Registry._AUSTRIA_COUNTRY_CODE.equals(getCustomerCountry()))
        {
            return false;
        } else
        {
            return true;
        }
    }


    /**
     * this method is called from the removeFromCart action
     *
     * @param orderedPhotoId
     */
    public void remove(Long orderedPhotoId)
    {
        orderedPhotos.remove(orderedPhotoId);
        removePhotoFromScItems(orderedPhotoId);
    }

    /**
     * go through list of ordered items and remove all items that are for the orderedPhotoId param
     *
     * @param orderedPhotoId
     */
    private void removePhotoFromScItems(Long orderedPhotoId)
    {
        Collection toBeRemoved = new ArrayList();
        for (int i = 0; i < scItems.size(); i++)
        {
            ScOrderItem scOrderItem = (ScOrderItem) scItems.get(i);
            if (orderedPhotoId.equals(scOrderItem.getPhotoId()))
            {
                toBeRemoved.add(scOrderItem);
            }
        }
        scItems.removeAll(toBeRemoved);
    }

    public Collection getOrderedPhotos()
    {
        return orderedPhotos.values();
    }

    public Map getOrderedPhotosMap()
    {
        return orderedPhotos;
    }

    /**
     * accessor for map-backed property orderedPhoto
     *
     * @param key
     * @return a photo object
     */
    public Object getPhotoMapped(String key)
    {
        // todo check for expired session
        return orderedPhotos.get(new Long(key));
    }

    /**
     * accessor for map-backed property mappedProduct
     *
     * @param key
     * @return a product object
     */
    public Object getProductMapped(String key)
    {
        return orderedProducts.get(new Long(key));
    }

    /**
     * This is called for every item (photo) that is put to the shopping cart<br>
     * - add to orderedPhotos<br>
     * - add new batch (currently 4) of Shoppingcart order items to scitems list
     *
     * @param photo the object of the photo that has been ordered
     * @param product product to combine the ordered photo with (Null for default product)
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     */
    public void addPhoto(Photo photo, Product product) throws UnartigInvalidArgument
    {
        orderedPhotos.put(photo.getPhotoId(), photo);
        for (int i = 0; i < Registry._NUMBER_OF_SC_ITEMS_PER_PHOTO; i++)
        {
            // first item will have the passed product or an standard initial product assigned : (or -1 for the remaining scItems)
            ScOrderItem oi = new ScOrderItem(photo.getPhotoId(), i==0?(product!=null?product.getProductId():Product.getInitialProductIdFor(photo)): -1, i==0?1:0, this);
            addScOrderItem(oi);
        }

    }

    /**
     * this is called for every Product that is added to the shopping cart
     * @param product product object as defined in the product table
     */
    public void addProduct(Product product)
    {
        orderedProducts.put(product.getProductId(), product);
    }

    /**
     * <p>called every time the shopping cart is updated
     * <p>iterate over all scitems and calculate total
     */
    public void updatePrices()
    {
        double totalItemPricesCHF = 0;
        double totalItemPricesEUR = 0;
        double totalItemPricesGBP = 0;
        double totalItemPricesSEK = 0;
        for (Object scItem : scItems) {
            ScOrderItem scOrderItem = (ScOrderItem) scItem;
            totalItemPricesCHF += scOrderItem.getItemPriceCHF();
            totalItemPricesEUR += scOrderItem.getItemPriceEUR();
            totalItemPricesGBP += scOrderItem.getItemPriceGBP();
            totalItemPricesSEK += scOrderItem.getItemPriceSEK();
        }
        setTotalPhotosCHF(totalItemPricesCHF);
        setTotalPhotosEUR(totalItemPricesEUR);
        setTotalPhotosGBP(totalItemPricesGBP);
        setTotalPhotosSEK(totalItemPricesSEK);

    }


    /**
     * used by the view to show formatted subtotal
     * @return
     */
    public String getFormattedSubtotalPhotosCHF()
    {
        return Price.monetaryAmountFormat.format(totalPhotosCHF);
    }

    /**
     * used by the view to show formatted subtotal
     * @return
     */
    public String getFormattedSubtotalPhotosEUR()
    {
        return Price.monetaryAmountFormat.format(totalPhotosEUR);
    }

    /**
     * used by the view to show formatted subtotal
     * @return
     */
    public String getFormattedSubtotalPhotosGBP()
    {
        return Price.monetaryAmountFormat.format(totalPhotosGBP);
    }

    /**
     * used by the view to show formatted subtotal
     * @return
     */
    public String getFormattedSubtotalPhotosSEK()
    {
        return Price.monetaryAmountFormat.format(totalPhotosSEK);
    }

    /**
     * called by view for confirming order in the customer's currency
     * @return
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     */
    public String getFormattedSubtotalPhotos() throws UnartigInvalidArgument
    {
        // Switzerland:
        if (Registry._SWITZERLAND_COUNTRY_CODE.equals(customerCountry))
        {
            return getFormattedSubtotalPhotosCHF();
        }
        // Germany or Austria
        else if (Registry._GERMANY_COUNTRY_CODE.equals(customerCountry) || Registry._AUSTRIA_COUNTRY_CODE.equals(customerCountry))
        {
            return getFormattedSubtotalPhotosEUR();
        }
        throw new UnartigInvalidArgument("Country invalid");
    }




//
    /**
     * Shipping handling: CHE for shipping to Switzerland in CHF and DEU for shipping to Germany in EURO
     * called by view to show correct currency for customer
     * @return
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     */
    public String getFormattedShippingPrice() throws UnartigInvalidArgument
    {
        if (Registry._SWITZERLAND_COUNTRY_CODE.equals(customerCountry))
        {
            return getFormattedShippingCHE();
        }
        else if (Registry._GERMANY_COUNTRY_CODE.equals(customerCountry))
        {
            return getFormattedShippingInternationalEUR();
        }
        else if (Registry._AUSTRIA_COUNTRY_CODE.equals(customerCountry))
        {
            return getFormattedShippingInternationalEUR();
        }
        throw new UnartigInvalidArgument("Country invalid");
    }

    public String getFormattedShippingCHE()
    {
        return Price.monetaryAmountFormat.format(getShippingHandlingCHE());
    }

    public String getFormattedShippingInternationalEUR()
    {
        return Price.monetaryAmountFormat.format(getShippingHandlingInternationalEUR());
    }



    /**
     * Called by confirmation view to present amount in customer's currency , CHF or EUR currently
     * @return
     * @throws UnartigInvalidArgument
     */
    public String getFormattedTotal() throws UnartigInvalidArgument
    {
        if (Registry._SWITZERLAND_COUNTRY_CODE.equals(customerCountry))
        {
            return getFormattedTotalCHE();
        }
        else if (Registry._GERMANY_COUNTRY_CODE.equals(customerCountry) || Registry._AUSTRIA_COUNTRY_CODE.equals(customerCountry))
        {
            return getFormattedTotalEUR();
        }
        throw new UnartigInvalidArgument("Country invaled");
    }

    /**
     * total for orders from Switzerland (shipping handling part is ignored for digital-only orders)
     * @return
     */
    public String getFormattedTotalCHE()
    {
        double total = totalPhotosCHF + getShippingHandlingCHE();
        return Price.monetaryAmountFormat.format(total);
    }

    /**
     * Total for orders from germany (shipping handling part is ignored for digital-only orders)
     * @return
     */
    public String getFormattedTotalEUR()
    {
        double total = totalPhotosEUR + getShippingHandlingInternationalEUR();
        return Price.monetaryAmountFormat.format(total);
    }

    private void addScOrderItem(ScOrderItem scOrderItem)
    {
        if (scItems == null)
        {
            scItems = new ArrayList();
        }
        scItems.add(scOrderItem);
        _logger.debug("added new item to shopping cart order list : ");
        _logger.debug(scOrderItem);
    }

    public void setBackToAlbumParams(Map backToAlbumParams)
    {
        this.backToAlbumParams = backToAlbumParams;
    }

    public Map getBackToAlbumParams()
    {
        return backToAlbumParams;
    }

    /**
     * return all scItems, 4 for every photo
     * @return list of all scitmes
     */
    public List getScItems()
    {
        return scItems;
    }

    /**
     * return consolidated itmes for order confirmation
     * @return the sc items without empty scOrderItem entries, consolidated
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    public List getScItemsForConfirmation() throws UAPersistenceException
    {
        List consolidatedItems = new ArrayList();
        for (int i = 0; i < scItems.size(); i++)
        {
            ScOrderItem scOrderItem = (ScOrderItem) scItems.get(i);

            if (scOrderItem.getQuantity()!=0) // for every scOrder item that has amount > 0:
            {
                boolean consolidatedItemExists =false; // no existing consolidated item

                for (int j = 0; j < consolidatedItems.size(); j++) // loop through existing confirmItems:
                {
                    ScOrderItem eachConfItem = (ScOrderItem) consolidatedItems.get(j);
                    if (scOrderItem.getPhotoId().equals(eachConfItem.getPhotoId()) && scOrderItem.getProductId().equals(eachConfItem.getProductId()))
                    { // we have an existing item, add quantities, update price
                        eachConfItem.setQuantity(eachConfItem.getQuantity() + scOrderItem.getQuantity());
                        eachConfItem.updateItemPrice();
                        consolidatedItemExists =true;
                        break;
                    }
                }
                if (!consolidatedItemExists)
                {
                    ScOrderItem newConsolidatedItem = new ScOrderItem(scOrderItem.getPhotoId(),scOrderItem.getProductId(),scOrderItem.getQuantity(), this);
                    newConsolidatedItem.updateItemPrice();
                    consolidatedItems.add(newConsolidatedItem);
                }
            }
        }

        //debug
        if (_logger.isDebugEnabled())
        {
            for (int i = 0; i < consolidatedItems.size(); i++)
            {
                ScOrderItem scOrderItem = (ScOrderItem) consolidatedItems.get(i);
                _logger.debug("scOrderItem.getPhotoId() = " + scOrderItem.getPhotoId());
                _logger.debug("scOrderItem.getProductId() = " + scOrderItem.getProductId());
                _logger.debug("scOrderItem.getQuantity() = " + scOrderItem.getQuantity());
            }
        }
        return consolidatedItems;
    }

    public void setScItems(List scItems)
    {
        this.scItems = scItems;
    }

    /**
     * 'normal' scitem getter
     * CAUTION: session can expire, then this method will be accessed with invalid keys!!!
     * @param index
     * @return scItem
     * @throws Exception If array is empty because session has timed out
     */
    public ScOrderItem getScItem(int index) throws Exception {
        ScOrderItem retVal = null;
        if (scItems==null)
        { // probably null to expired sessiono!
            scItems = new ArrayList();
        }
        while (index >= scItems.size())
        {
            scItems.add(new ScOrderItem());
        }

        retVal = (ScOrderItem) scItems.get(index);
        return retVal;
    }


//    public ScOrderItem getScItemForConfirmation(int index)
//    {
//        System.out.println("ShoppingCart.getScItemForConfirmation ********************************");
//        return (ScOrderItem) scItems.get(index);
//    }


    public double getTotalPhotosCHF()
    {
        return totalPhotosCHF;
    }

    public void setTotalPhotosCHF(double totalPhotosCHF)
    {
        this.totalPhotosCHF = totalPhotosCHF;
    }

    public double getTotalPhotosEUR()
    {
        return totalPhotosEUR;
    }

    public void setTotalPhotosEUR(double totalPhotosEUR)
    {
        this.totalPhotosEUR = totalPhotosEUR;
    }

    public double getTotalPhotosGBP()
    {
        return totalPhotosGBP;
    }

    public void setTotalPhotosGBP(double totalPhotosGBP)
    {
        this.totalPhotosGBP = totalPhotosGBP;
    }

    public double getTotalPhotosSEK()
    {
        return totalPhotosSEK;
    }

    public void setTotalPhotosSEK(double totalPhotosSEK)
    {
        this.totalPhotosSEK = totalPhotosSEK;
    }


    /**
     * Does the necessary Business Logic to update the shopping cart:
     * - updates the price
     *
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     */
    public void updateCart() throws UAPersistenceException, UnartigInvalidArgument
    {
        ProductDAO pDao = new ProductDAO();
        boolean updated=false;
        for (int i = 0; i < getScItems().size(); i++)
        {
            ScOrderItem scOrderItem = (ScOrderItem) getScItems().get(i);
            // if a product has been chosen, update price:
            if (scOrderItem.getProductId().intValue() > 0)
            {
                scOrderItem.updateItemPrice();
                addProduct(pDao.load(scOrderItem.getProductId()));
                updated = true;
            } else
            { // no product chosen; make sure the item price is 0
                scOrderItem.setQuantity(0);
                scOrderItem.initPrice();
            }
        }
        updatePrices();
        if (!updated)
        {
            throw new UnartigInvalidArgument("no vaild product to update");
        }
    }

    public String getActionParam()
    {
        return actionParam;
    }

    public void setActionParam(String actionParam)
    {
        this.actionParam = actionParam;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    /**
     * Set shopping cart field for the customers country AND set the appropriate currency
     * whenever the country changes, this method should be called
     * @param customerCountry
     */
    public void setCustomerCountry(String customerCountry)
    {
        // SWITZERLAND
        if (Registry._SWITZERLAND_COUNTRY_CODE.equals(customerCountry))
        {
            // set to chf
            currency=Registry._CURRENCY_SWISS_FRANCS;
        }
        // GERMANY
        else if (Registry._GERMANY_COUNTRY_CODE.equals(customerCountry))
        {
            currency=Registry._CURRENCY_EURO;
        }
        // AUSTRIA
        else if (Registry._AUSTRIA_COUNTRY_CODE.equals(customerCountry))
        {
            currency=Registry._CURRENCY_EURO;
        }
        this.customerCountry = customerCountry;
    }


    public String getCustomerCountry()
    {

        return customerCountry;
    }

    /**
     * return the shipping handling costs minor units for CHE or 0 if only digital products are chosen
     * @return minor units
     */
    public double getShippingHandlingCHE()
    {
        return isOnlyDigitalProducts()?0:Product._SHIPPING_HANDLING_CHE_CHF;
    }

    /**
     * Return the shipping handling costs minor units for International Shippings or 0 if only digital products are chosen
     * @return minor units
     */
    public double getShippingHandlingInternationalEUR()
    {
        return isOnlyDigitalProducts()?0: Product._SHIPPING_HANDLING_INTERNATIONAL_EUR;
    }


    public Long getOrderedPhotoId() {
        return orderedPhotoId;
    }

    public void setOrderedPhotoId(Long orderedPhotoId) {
        this.orderedPhotoId = orderedPhotoId;
    }

    public Long getOrderedProductId() {
        return orderedProductId;
    }
}

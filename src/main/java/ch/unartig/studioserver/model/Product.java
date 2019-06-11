/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 9, 2005$
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
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.5  2007/03/15 21:45:27  alex
 * no more price segment
 *
 * Revision 1.4  2007/03/14 02:41:01  alex
 * initial checkin
 *
 * Revision 1.3  2007/03/13 16:55:03  alex
 * template for properties
 *
 * Revision 1.2  2007/03/09 23:44:24  alex
 * no message
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.13  2006/11/14 17:55:50  alex
 * clean up system out
 *
 * Revision 1.12  2006/11/12 15:54:07  alex
 * fix for static price segment block
 *
 * Revision 1.11  2006/11/12 11:58:47  alex
 * dynamic album ads
 *
 * Revision 1.10  2006/11/08 09:55:03  alex
 * dynamic priceinfo
 *
 * Revision 1.9  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.8  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.7  2006/02/27 15:35:29  alex
 * shopping cart has now an initial format with quantity = 1
 *
 * Revision 1.6  2006/02/27 11:41:20  alex
 * shopping cart has now an initial format with quantity = 1
 *
 * Revision 1.5  2005/11/23 20:52:10  alex
 * bug-fixes
 *
 * Revision 1.4  2005/11/19 22:04:04  alex
 * shopping cart reflects different price segments
 *
 * Revision 1.3  2005/11/14 18:15:07  alex
 * pricing in shopping cart enabled
 *
 * Revision 1.2  2005/11/12 23:15:27  alex
 * using indexed properties ... first step
 *
 * Revision 1.1  2005/11/09 21:59:36  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.persistence.DAOs.PriceDAO;
import ch.unartig.studioserver.persistence.DAOs.ProductTypeDAO;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Map;

/**
 * this business class represents a product that is sold to a customer<br>
 * a product consists of a format (or 'fun' article like mousemat) and a price<br>
 * there will be more than one product for the same format if this format is sold for different prices<br>
 */
@Entity
@Table(name = "products")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE) // changed to Read_Write after Hibernate 5 upgrade
public class Product implements java.io.Serializable {

    public static final double _SHIPPING_HANDLING_CHE_CHF = 4.90; // 4.90 SFr.
    public static final double _SHIPPING_HANDLING_INTERNATIONAL_EUR = 3.30; // 3.30 Euros

    // array of initial producttypes from highest to lowest priority, first one that exists gets chosen
    private static Long[] preselectedProductTypeIds = {(long) 5, (long)3, (long) 4};

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long productId;

    private String productName;
    private Boolean inactive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "priceid",nullable = false)
    private Price price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producttypeid",nullable = false)
    private ProductType productType;




    // added after migrating to hibernate annotations - not used before, but now necessary because existing db schema needs it (there would be an additional table necessary, otherwise))
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "albumid",nullable = false) // nullable = false should work - but not sure
    private Album album;

    public Product()
    {
    }

    /**
     * instantiate a new product
     * // todo : why was album not set here before hibernate 5 migration ? --> there was no mapping - but why?
     * @param productTypeId
     * @param priceId
     * @throws UAPersistenceException
     */
    public Product(Long productTypeId, Long priceId, Album album) throws UAPersistenceException
    {
        ProductTypeDAO ptDao = new ProductTypeDAO();
        setProductType(ptDao.load(productTypeId));
        PriceDAO priceDao = new PriceDAO();
        setPrice(priceDao.load(priceId));
        setAlbum(album);
    }



    /**
     * static method to return the product id for the product that should be shown as a default in the shopping cart for the first product
     * @return product ID
     * @param photo
     */
    public static Long getInitialProductIdFor(Photo photo)
    {
        for (Long preselectedProductTypeId : preselectedProductTypeIds) {
            Map availableProductTypes = photo.getAlbum().getAvailableProductTypes(true);
            if (availableProductTypes.containsKey(preselectedProductTypeId)) {
                return photo.getAlbum().getProductFor(preselectedProductTypeId).getProductId();
            }
        }
        return (long)-1;
    }

    /**
     * transitory function to not break the old code
     * @return true if product is digital product, false otherwise
     */
    public boolean isDigitalProduct()
    {
        return getProductType().getDigitalProduct();
    }

    public String getFormattedPriceCHF()
    {
        return Price.monetaryAmountFormat.format(getPrice().getPriceCHF());
    }
    public String getFormattedPriceEUR()
    {
        return Price.monetaryAmountFormat.format(getPrice().getPriceEUR());
    }
    public String getFormattedPriceSEK()
    {
        return Price.monetaryAmountFormat.format(getPrice().getPriceSEK());
    }
    public String getFormattedPriceGBP()
    {
        return Price.monetaryAmountFormat.format(getPrice().getPriceGBP());
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Boolean getInactive() {
        return this.inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Price getPrice() {
        return this.price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public ProductType getProductType() {
        return this.productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("productId").append("='").append(getProductId()).append("' ");
      buffer.append("productName").append("='").append(getProductName()).append("' ");
      buffer.append("inactive").append("='").append(getInactive()).append("' ");
      buffer.append("price").append("='").append(getPrice()).append("' ");
      buffer.append("productType").append("='").append(getProductType()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

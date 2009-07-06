/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 6, 2005$
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
 * Revision 1.5  2007/03/14 03:18:36  alex
 * no more price segment
 *
 * Revision 1.4  2007/03/13 16:55:03  alex
 * template for properties
 *
 * Revision 1.3  2007/03/12 18:57:02  alex
 * product types for albums
 *
 * Revision 1.2  2007/03/09 23:44:24  alex
 * no message
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.8  2006/11/10 15:55:30  alex
 * dynamic album ads
 *
 * Revision 1.7  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 * Revision 1.6  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.5  2006/02/08 18:04:49  alex
 * first steps for album type configuration
 *
 * Revision 1.4  2006/01/11 20:40:53  alex
 * level update form works
 *
 * Revision 1.3  2005/11/19 20:32:32  alex
 * price segments inserted
 *
 * Revision 1.2  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.1  2005/11/06 21:43:22  alex
 * overview, admin menu, index-photo upload
 *
 ****************************************************************/
package ch.unartig.u_core.beans;

import ch.unartig.u_core.model.Product;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class AdminForm extends ActionForm implements Serializable {
    private String navTitle;
    private String longTitle;
    private String description;
    private Long genericLevelId;
    private Long parentLevelId;
    private Boolean privateEvent;
    private Boolean noTime;
    private FormFile indexPhoto;
    private String levelType;
//    private Long priceSegmentId;
    private String eventDateDisplay;
    private String privateAccessCode;
    private String quickAccess;
    private String albumTypeString;
    private String skyBannerRightAd;
    /**
     * map that contains the procuct id as Key and the assigned price id as value
     */
    private Map productPrices = new HashMap();

    private Logger _logger = Logger.getLogger(getClass().getName());

    public String getNavTitle() {
        return navTitle;
    }

    public void setNavTitle(String navTitle) {
        this.navTitle = navTitle;
    }

    public String getLongTitle() {
        return longTitle;
    }

    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGenericLevelId() {
        return genericLevelId;
    }

    public void setGenericLevelId(Long genericLevelId) {
        this.genericLevelId = genericLevelId;
    }

    public FormFile getIndexPhoto() {
        return indexPhoto;
    }

    public void setIndexPhoto(FormFile indexPhoto) {
        this.indexPhoto = indexPhoto;
    }

    public Boolean getPrivateEvent() {
        return privateEvent;
    }

    public void setPrivateEvent(Boolean privateEvent) {
        this.privateEvent = privateEvent;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }

    public String getEventDateDisplay() {
        return eventDateDisplay;
    }

    public void setEventDateDisplay(String eventDateDisplay) {
        this.eventDateDisplay = eventDateDisplay;
    }

    public Boolean getNoTime() {
        return noTime;
    }

    public void setNoTime(Boolean noTime) {
        this.noTime = noTime;
    }

    public Long getParentLevelId() {
        return parentLevelId;
    }

    public void setParentLevelId(Long parentLevelId) {
        this.parentLevelId = parentLevelId;
    }

    public String getPrivateAccessCode() {
        return privateAccessCode;
    }

    public void setPrivateAccessCode(String privateAccessCode) {
        this.privateAccessCode = privateAccessCode;
    }

    public String getQuickAccess() {
        return quickAccess;
    }

    public void setQuickAccess(String quickAccess) {
        this.quickAccess = quickAccess;
    }

    public void setAlbumTypeString(String albumTypeString) {
        this.albumTypeString = albumTypeString;
    }

    public String getAlbumTypeString() {
        return albumTypeString;
    }


    public String getSkyBannerRightAd() {
        return skyBannerRightAd;
    }

    public void setSkyBannerRightAd(String skyBannerRightAd) {
        this.skyBannerRightAd = skyBannerRightAd;
    }


    public Map getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(Map productPrices) {
        this.productPrices = productPrices;
    }

    /**
     * bean-compliant setter
     * key = productTypeId; value = priceId
     * @param key
     * @param value
     */
    public void setProductPrice(String key,Object value) {
        productPrices.put(key,value);
    }

    /**
     * bean - compliant getter
     * @param key
     * @return
     */
    public Object getProductPrice(String key)
    {
        return productPrices.get(key);
    }

    /**
     * set up the map for the edited album
     * @param products
     */
    public void createProductPricesMap(Set products)
    {
        for (Object product1 : products) {
            Product product = (Product) product1;
            productPrices.put(product.getProductType().getProductTypeId().toString(), product.getPrice().getPriceId().toString());
        }
        _logger.debug("set product-prices map : " + productPrices);
    }
}

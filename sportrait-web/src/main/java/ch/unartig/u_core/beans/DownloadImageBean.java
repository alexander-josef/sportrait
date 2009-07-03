/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 20.06.2006$
 *
 * Copyright (c) 2006 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.2  2007/05/07 15:18:21  alex
 * id for digital negative, 400 x 600 Foto
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.8  2006/11/24 10:21:59  alex
 * download page fixes
 *
 * Revision 1.7  2006/11/22 21:01:52  alex
 * small fixes, types in text
 *
 * Revision 1.6  2006/11/22 14:39:05  alex
 * small fixes
 *
 * Revision 1.5  2006/11/21 13:58:45  alex
 * small fixes
 *
 * Revision 1.4  2006/11/14 16:19:24  alex
 * digital order
 *
 * Revision 1.3  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.2  2006/10/11 12:52:28  alex
 * typos, unartig AG replaces Westhous
 *
 * Revision 1.1  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 ****************************************************************/
package ch.unartig.u_core.beans;

import ch.unartig.u_core.exceptions.UnartigInvalidArgument;
import ch.unartig.u_core.imaging.ImagingHelper;
import ch.unartig.u_core.model.Photo;
import ch.unartig.u_core.model.ProductType;
import ch.unartig.u_core.persistence.DAOs.OrderDAO;
import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.*;
import ch.unartig.u_core.model.OrderItem;
import ch.unartig.u_core.model.Order;
import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 *  Module Refactoing:
 *  + Checked with unartig: downloadPhoto method is slightly different. Different signature.
 *
 * This bean serves the download-view with all necessary information for downloadable images of the relevant order
 * the relevant order is identified by the unique hash that is used in the constructor
 * this bean delivers:
 * <p/>
 */
public class DownloadImageBean
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /*the order that contains the downloadable images*/
    private Order order;
    private String orderHash;
    private String downloadUrl;
    private Photo downloadPhoto;
    private static final int _ID_DIGI_FOTO_400_600 = 2;
    private static final int _ID_DIGITAL_NEGATIVE = 3;

    /**
     * constructor; takes the order hash to retrieve the order
     *
     * @param orderHash the digest that is stored in the db to get an order
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *
     */
    public DownloadImageBean(String orderHash) throws UAPersistenceException
    {
        this.orderHash = orderHash;
        order = new OrderDAO().getOrderFromHash(orderHash);
    }

    /**
     * @param orderHash
     * @param downloadUrl
     * @throws UAPersistenceException
     */
    public DownloadImageBean(String orderHash, String downloadUrl) throws UAPersistenceException
    {
        this.orderHash = orderHash;
        this.downloadUrl = downloadUrl;
        order = new OrderDAO().getOrderFromHash(orderHash);
    }

    /**
     * prepare a photo for download<p/>
     *
     * @param orderItemId  the oderItemId-parameter value that has been passed in the request
     * @param response servlet response
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     *          if the passed oderItemId is not part of this order
     */
    public void downloadPhoto(String orderItemId, HttpServletResponse response) throws UnartigException
    {
        OrderItem downloadOrderItem = null;
        // iterate to find the orderitem
        for (Object o : order.getOrderItems()) {
            OrderItem orderItem = (OrderItem) o;
            if (orderItemId.equals(orderItem.getOrderItemId().toString())) {
                // we found the orderitem
                downloadOrderItem = orderItem;
                break;
            }
        }


        // exception if oderItemId not part of this order
        if (downloadOrderItem == null)
        {
            throw new UnartigInvalidArgument("passed oderItemId not part of order");
        }
        byte[] photoBytes = loadDigitalProduct(downloadOrderItem);

        // use application/octet-stream instead to download and not display the image??
        _logger.debug("streaming photo with name : " + downloadOrderItem.getPhoto().getFilename());
        response.setContentType("image/JPG");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadOrderItem.getPhoto().getFilename() + "\"");
        // Todo: What's the content length? set content-length header:
        // We need to know the size of the streamed object beforehand. How?
        response.setContentLength(photoBytes.length);
        // copy the photo to the outputstream:
        try {
            response.getOutputStream().write(photoBytes);
        } catch (IOException e) {
            throw new UnartigException("Problem streaming the photo to the output stream");
        }


    }

    /**
     * Check the ordered product, load the photo and return it.
     * <p/>
     * prepare the correct image and put it to the output stream from the action class
     *
     * @param orderItem the order item to genereate the digital image from
     * @throws ch.unartig.u_core.exceptions.UnartigImagingException
     *          if exception during the calculation of the ordered image is thrown
     * @return The photo as byte array
     */
    private byte[] loadDigitalProduct(OrderItem orderItem) throws UnartigImagingException
    {
        byte[] photoBytes;
        try
        {
            // digi foto 400 x 600 : (productIds 17 & 18)
            Photo photo = orderItem.getPhoto();
            ProductType productType = orderItem.getProduct().getProductType();
//            if (orderItem.getProduct().getProductId().intValue() == 17 || orderItem.getProduct().getProductId().intValue() == 18)
            if (productType.getProductTypeId() == _ID_DIGI_FOTO_400_600)
            {
                photoBytes = stream400x600Photo(photo);


            } else if (productType.getProductTypeId() == _ID_DIGITAL_NEGATIVE)
            {
                // digital negativ copy the file to the output stream
                _logger.info("streaming the digital negativ");
                photoBytes = FileUtils.readFileToByteArray(photo.getFile());
            } else // everything else
            {
                _logger.info("Not a digital product; streaming standard preview size");
                photoBytes = stream400x600Photo(photo);

            }
        } catch (UnartigImagingException e)
        {
            _logger.error("Cannot create ordered image file", e);
            throw new UnartigImagingException("Cannot create ordered image file", e);
        } catch (IOException e)
        {
            _logger.error("Cannot copy fine image to the output stream", e);
            throw new UnartigImagingException("Cannot copy fine image to the output stream", e);
        }

        return photoBytes;
    }

    private byte[] stream400x600Photo(Photo photo) throws UnartigImagingException
    {
        _logger.info("processing digi foto 600 * 400");
        double resampleFactor;
        double longerSidePixels = 600d;
        Integer originalWidthPixels = photo.isOrientationLandscape() ? photo.getWidthPixels() : photo.getHeightPixels();
        resampleFactor = longerSidePixels / (double) originalWidthPixels.intValue();
        _logger.debug("sample factor :" + resampleFactor);
        return ImagingHelper.reSample(photo.getFile(), resampleFactor, 0.75f);
    }

    /**
     * Return a set of downloadable Photos for the download screen
     *
     * @return Set of OrderItems
     */
    public Set getDownloadableOrderItems()
    {
        Set retVal = new TreeSet(DownloadableItemComp);
        Set returnedBonusPhotos = new HashSet();

        Set orderItems = order.getOrderItems();
        for (Iterator iterator = orderItems.iterator(); iterator.hasNext();)
        {
            OrderItem orderItem = (OrderItem) iterator.next();
            // add to retVal unless it's not a digital product and it's already in the set
            if (orderItem.getProduct().isDigitalProduct())
            { // digital products are always added
                _logger.debug("adding digital orderitem to set");
                retVal.add(orderItem);
            } else if (!returnedBonusPhotos.contains(orderItem.getPhoto()))
            { // only add if it's not already in the bonus-photos:
                retVal.add(orderItem);
                returnedBonusPhotos.add(orderItem.getPhoto());
            } else
            {
                _logger.debug("skipping orderitem");
            }
        }
        return retVal;
    }


    public Order getOrder()
    {
        return order;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }


    public String getOrderHash()
    {
        return orderHash;
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }


    public Photo getDownloadPhoto()
    {
        return downloadPhoto;
    }

    /**
     * Sort the list of downloadable items; digital items first;
     *
     */
    private static Comparator DownloadableItemComp = new Comparator()
    {
         public int compare(Object o1, Object o2)
        {
            OrderItem item1 = (OrderItem) o1;
            OrderItem item2 = (OrderItem) o2;
            if (item1.getProduct().isDigitalProduct() && !item2.getProduct().isDigitalProduct())
            {
                // lowest order (first) for digital
                return -1;
            } else if (!item1.getProduct().isDigitalProduct() && item2.getProduct().isDigitalProduct())
            { // same: compare orderitemid
                return 1;
            }else if (item1.getProduct().isDigitalProduct() && item2.getProduct().isDigitalProduct())
            { // same: compare orderitemid
                return item1.getOrderItemId().compareTo(item2.getOrderItemId());
            } else if (!item2.getProduct().isDigitalProduct())
            {
                return item1.getOrderItemId().compareTo(item2.getOrderItemId());
            } else
            {
                throw new RuntimeException("unexpected state");
            }
        }
    };
}

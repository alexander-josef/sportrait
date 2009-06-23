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
 * Revision 1.2  2007/05/03 20:18:19  alex
 * shopping cart preview
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.3  2007/03/14 03:18:36  alex
 * no more price segment
 *
 * Revision 1.2  2007/03/13 16:55:03  alex
 * template for properties
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.38  2006/12/18 16:22:58  alex
 * fixing shopping cart bug
 *
 * Revision 1.37  2006/12/15 16:26:03  alex
 * reporting works; averages and totals are still incorrect
 *
 * Revision 1.36  2006/12/06 18:42:08  alex
 * no js necessary for basic shopping cart functionality
 *
 * Revision 1.35  2006/11/22 14:39:05  alex
 * small fixes
 *
 * Revision 1.34  2006/11/21 13:58:45  alex
 * small fixes
 *
 * Revision 1.33  2006/11/13 22:13:30  alex
 * shoppingcart
 *
 * Revision 1.32  2006/11/12 15:54:07  alex
 * fix for static price segment block
 *
 * Revision 1.31  2006/11/08 09:55:03  alex
 * dynamic priceinfo
 *
 * Revision 1.30  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.29  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 * Revision 1.28  2006/02/27 11:41:20  alex
 * shopping cart has now an initial format with quantity = 1
 *
 * Revision 1.27  2006/02/22 20:37:25  alex
 * back to album in shopping cart works
 *
 * Revision 1.26  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.25  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.24  2005/12/27 15:22:22  alex
 * payment method shows now bill not credit card
 *
 * Revision 1.23  2005/12/11 21:49:08  alex
 * unartiginfo weg aus text
 *
 * Revision 1.22  2005/11/27 19:39:10  alex
 * fast upload
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
 * Revision 1.16  2005/11/18 17:28:36  alex
 * back link, incl. new interface for naviable objects
 *
 * Revision 1.15  2005/11/17 13:36:06  alex
 * check out overview works
 *
 * Revision 1.14  2005/11/14 21:49:28  alex
 * small changes
 *
 * Revision 1.13  2005/11/14 18:15:07  alex
 * pricing in shopping cart enabled
 *
 * Revision 1.12  2005/11/14 14:40:05  alex
 * back to album from shopping cart works
 *
 * Revision 1.11  2005/11/14 11:52:22  alex
 * delete photo in cart works again
 *
 * Revision 1.10  2005/11/14 10:43:34  alex
 * shopping cart basic functions work. photo list needs a bit more work yet
 *
 * Revision 1.9  2005/11/12 23:15:27  alex
 * using indexed properties ... first step
 *
 * Revision 1.8  2005/11/09 09:01:29  alex
 * check out form wizard
 *
 * Revision 1.7  2005/11/05 21:41:57  alex
 * overview und links in tree menu
 *
 * Revision 1.6  2005/11/05 16:37:25  alex
 * tiles error, more sc stuff
 *
 * Revision 1.5  2005/11/05 16:00:41  alex
 * tiles error, more sc stuff
 *
 * Revision 1.4  2005/11/05 15:22:30  alex
 * more shopping cart ...
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
package ch.unartig.studioserver.actions;

import ch.unartig.u_core.exceptions.UnartigInvalidArgument;
import ch.unartig.u_core.exceptions.UnartigSessionExpiredException;
import ch.unartig.u_core.Registry;
import ch.unartig.studioserver.beans.ScOrderItem;
import ch.unartig.studioserver.beans.ShoppingCart;
import ch.unartig.u_core.presentation.NavigationHelper;
import ch.unartig.u_core.model.Photo;
import ch.unartig.u_core.model.Product;
import ch.unartig.u_core.persistence.DAOs.PhotoDAO;
import ch.unartig.u_core.persistence.DAOs.ProductDAO;
import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartAction extends MappingDispatchAction
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * User clicked the 'place in shopping cart' link. Put new orderitem to shopping cart and place photo as 'ordered photo' in request.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward to shopping cart confirm
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     *
     */
    @SuppressWarnings({"JavaDoc"})
    public ActionForward placeInCart(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UAPersistenceException, UnartigInvalidArgument
    {
        PhotoDAO phDao = new PhotoDAO();
        ProductDAO productDao = new ProductDAO();
        // shopping cart in session already exists?
        ShoppingCart shoppingCart = getScFromSession(request);
        Long orderedPhotoId;
        Photo photo;
        Product product;
        orderedPhotoId = new Long(request.getParameter("orderedPhotoId"));
        Long orderedProductId = new Long(request.getParameter("orderedProductId"));
        photo = phDao.load(orderedPhotoId);
        product = orderedProductId > 0 ? productDao.load(orderedProductId) : null; // for id < 1 we assume no product has been chosen
        if (!shoppingCart.getOrderedPhotosMap().containsKey(orderedPhotoId)) {// photo not yet in orderedPhotosMap
            try {
                shoppingCart.addPhoto(photo, product);
                _logger.debug("Photo put to shopping cart; Number of Photos in cart : " + shoppingCart.getOrderedPhotos().size());
                // update the cart
                shoppingCart.updateCart();
            } catch (UnartigInvalidArgument unartigInvalidArgument) {
                unartigInvalidArgument.printStackTrace();
            }
        } else {// photo is in shopping cart
            _logger.info("Add to cart: Doing nothing. Photo is already in cart. ");
        }
        request.setAttribute(Registry._NAME_PHOTO_PARAM, photo);

        return mapping.findForward("inCartSuccess");
    }

    /**
     * general action to enter the shopping cart
     * prepare photos, prepare available products
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward to sc overview
     * @throws ch.unartig.u_core.exceptions.UnartigException
     *
     * @throws ch.unartig.u_core.exceptions.UnartigSessionExpiredException
     *
     */
    public ActionForward toShoppingCart(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException, UnartigSessionExpiredException
    {
        try
        {
            ShoppingCart shoppingCart = getScFromSession(request);
            _logger.debug("Shopping Cart content :");
            _logger.debug("Ordered  PHOTOS" + shoppingCart.getOrderedPhotos().size());
            _logger.debug("setting back navigation");
            NavigationHelper.setBackToAlbumLink(request, shoppingCart);
            // place available products to request; they will be shown on the shopping cart view
            _logger.debug("setting product list");
        } catch (UAPersistenceException e)
        {
            _logger.info("Could not list product. go to error page");
            return mapping.findForward("databaseError");
        }
        catch (Exception e2)
        {
            _logger.error("unexpected error : ", e2);
            throw new UnartigException(e2);
        }
        _logger.debug("going to shopping cart ...");
        return mapping.findForward("shoppingCartPage");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward to sc overview or session expired page
     */
    public ActionForward removeFromCart(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        Long orderedPhotoId;
        ShoppingCart sc;
        try
        {
            orderedPhotoId = (Long) PropertyUtils.getProperty(form, "orderedPhotoId");
        } catch (Exception e)
        {
            _logger.error("Error while orderedPhotoId; action removeFromCart", e);
            return mapping.findForward("error");
        }

        try
        {
            sc = getExistingScFromSession(request);
        } catch (UnartigSessionExpiredException e)
        {
            return mapping.findForward("sessionExpired");
        }
        _logger.debug("Shopping Cart content :");
        _logger.debug("----------- PHOTOS" + sc.getOrderedPhotos().size());

        if (orderedPhotoId != null)
        {
            sc.remove(orderedPhotoId);
            List toBeRemoved = new ArrayList(5);
            for (int i = 0; i < sc.getScItems().size(); i++)
            {
                ScOrderItem scOrderItem = (ScOrderItem) sc.getScItems().get(i);
                if (scOrderItem.getPhotoId().longValue() == orderedPhotoId.longValue())
                {
                    toBeRemoved.add(scOrderItem);
                }
            }
            sc.getScItems().removeAll(toBeRemoved);
            sc.updatePrices();
        }
        request.setAttribute(Registry._NAME_SHOPPING_CART_ATTR, sc);
        return mapping.findForward("toCartSuccess");
    }

    private ShoppingCart getExistingScFromSession(HttpServletRequest request) throws UnartigSessionExpiredException
    {
        HttpSession s = request.getSession();
        ShoppingCart shoppingCart = (ShoppingCart) s.getAttribute(Registry._NAME_SHOPPING_CART_SESSION);
        if (shoppingCart == null)
        {
            _logger.debug("no shopping cart found in session. throwing exception");
            throw new UnartigSessionExpiredException("Session expired, no Shopping cart available");
        }
        return shoppingCart;
    }

    /**
     * every time the shoppingcart gets updated, this action is called
     * <p> this action is also called upon checkout with the actionParam = checkout
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward to shopping cart, exception page or start the checkOut
     */
    public ActionForward updateShoppingCart(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigSessionExpiredException {
        ActionMessages msgs = null;
        _logger.debug("ShoppingCartAction.updateShoppingCart");
        if (request.getSession(false)==null) {
            throw new UnartigSessionExpiredException("Your sesssion has expired");
        }
        ShoppingCart shoppingCart = (ShoppingCart) form;
//        System.out.println("shoppingCart.getAction() = " + shoppingCart.getAction());

        try
        {
            shoppingCart.updateCart();
        } catch (UAPersistenceException e)
        {
            _logger.error("Error while updateing cart", e);
            return mapping.findForward("error");
        } catch (UnartigInvalidArgument unartigInvalidArgument)
        {
            _logger.debug("No valid product to update");
            msgs = new ActionMessages();
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shoppingcart.wrongproduct.error"));
        }
        saveMessages(request.getSession(), msgs);
        _logger.debug("Parameter actionParam = " + shoppingCart.getActionParam());
        if ("checkOut".equals(shoppingCart.getActionParam()))
        {
            return mapping.findForward("checkOut");
        }
        return mapping.findForward("toCartSuccess");
    }


    /**
     * check for seesion, create one if none exist and return a shopping cart or null
     *
     * @param request
     * @return shoppingCart or null if none exists
     */
    private ShoppingCart getScFromSession(HttpServletRequest request)
    {
        HttpSession s = request.getSession();

        ShoppingCart shoppingCart = (ShoppingCart) s.getAttribute(Registry._NAME_SHOPPING_CART_SESSION);
        if (shoppingCart == null)
        {
            _logger.debug("no shopping cart found in session. creating new shopping cart");
            shoppingCart = new ShoppingCart();
        }
        s.setAttribute(Registry._NAME_SHOPPING_CART_SESSION, shoppingCart);

        return shoppingCart;
    }
}

/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 8, 2005$
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
 * Revision 1.4  2007/05/05 15:00:16  alex
 * errorhandling no eventcategory
 *
 * Revision 1.3  2007/05/02 09:17:09  alex
 * check out process, shoppingcart, buttons to text where links where meant
 *
 * Revision 1.2  2007/04/27 17:56:25  alex
 * check out process
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.37  2006/12/20 22:40:28  alex
 * reporting for euros done
 *
 * Revision 1.36  2006/11/21 09:34:06  alex
 * no message
 *
 * Revision 1.35  2006/11/17 13:21:41  alex
 * email notifiaction fix
 *
 * Revision 1.34  2006/11/12 16:43:22  alex
 * small fixes shoppingcart
 *
 * Revision 1.33  2006/11/05 22:10:02  alex
 * credit card order works
 *
 * Revision 1.32  2006/11/05 16:41:43  alex
 * action messages work for order confirmation
 *
 * Revision 1.31  2006/11/03 14:36:43  alex
 * update to struts 1.2.9
 *
 * Revision 1.30  2006/11/03 13:15:19  alex
 * some changes
 *
 * Revision 1.29  2006/11/01 10:14:45  alex
 * cc interface check in, transactions work
 *
 * Revision 1.28  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.27  2006/10/17 08:07:07  alex
 * creating the order hashes
 *
 * Revision 1.26  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.25  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 * Revision 1.24  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.23  2006/02/08 15:35:09  alex
 * confirmation message again
 *
 * Revision 1.22  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.21  2006/01/12 23:53:42  alex
 * error zeugs
 *
 * Revision 1.20  2006/01/11 15:10:47  alex
 * agbs akzeptieren
 *
 * Revision 1.19  2006/01/11 13:35:37  alex
 * bug 856 backend
 *
 * Revision 1.18  2006/01/06 19:54:22  alex
 * implemented post-redirect-get for shopping-cart and checkout, including validator error messages
 *
 * Revision 1.17  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.16  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.15  2005/11/22 21:33:16  alex
 * ordering process
 *
 * Revision 1.14  2005/11/21 17:52:58  alex
 * no account action , photo order
 *
 * Revision 1.13  2005/11/20 21:08:06  alex
 * delete of a photo in sc works correct
 *
 * Revision 1.12  2005/11/20 16:42:15  alex
 * sunntig obig
 *
 * Revision 1.11  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.10  2005/11/18 19:15:52  alex
 * stuff ...
 *
 * Revision 1.9  2005/11/17 19:41:13  alex
 * new fix sc overview
 *
 * Revision 1.8  2005/11/17 13:36:06  alex
 * check out overview works
 *
 * Revision 1.7  2005/11/16 17:26:19  alex
 * validator enhanced
 *
 * Revision 1.6  2005/11/14 21:49:28  alex
 * small changes
 *
 * Revision 1.5  2005/11/12 23:15:27  alex
 * using indexed properties ... first step
 *
 * Revision 1.4  2005/11/09 21:59:36  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 * Revision 1.3  2005/11/09 15:48:16  alex
 * check out wizard
 *
 * Revision 1.2  2005/11/09 14:39:18  alex
 * check out form wizard
 *
 * Revision 1.1  2005/11/09 09:01:29  alex
 * check out form wizard
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.exceptions.UnartigSessionExpiredException;
import ch.unartig.u_core.util.DebugUtils;
import ch.unartig.u_core.Registry;
import ch.unartig.studioserver.beans.CheckOutForm;
import ch.unartig.studioserver.beans.ShoppingCart;
import ch.unartig.studioserver.businesslogic.PhotoOrderIF;
import ch.unartig.studioserver.businesslogic.SessionHelper;
import ch.unartig.studioserver.businesslogic.ShoppingCartLogic;
import ch.unartig.studioserver.model.Customer;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;


/**
 * Check out releated actions.
 */
public class CheckOutAction extends MappingDispatchAction
{
    Logger _logger = Logger.getLogger(getClass().getName());


    @SuppressWarnings({"UnusedDeclaration"})
    public ActionForward setMessages(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigSessionExpiredException
    {
        _logger.debug("CheckOutAction.setMessages start");
        DebugUtils.debugActionMessage(request);
        request.getSession().setAttribute(Globals.ERROR_KEY, request.getAttribute(Globals.ERROR_KEY));
        request.getSession().setAttribute(Globals.MESSAGE_KEY, request.getAttribute(Globals.MESSAGE_KEY));
        _logger.debug("CheckOutAction.setMessages end");
        DebugUtils.debugActionMessage(request);
        return mapping.findForward("success");
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward page
     * @throws ch.unartig.u_core.exceptions.UnartigSessionExpiredException
     *
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward checkSession(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigSessionExpiredException
    {
        try
        {
            getExistingScFromSession(request);
            saveToken(request);

        } catch (UnartigSessionExpiredException e)
        {
            _logger.debug("throwing no shopping cart exception");
            throw new UnartigSessionExpiredException(e);
        }
        return mapping.findForward("success");
    }

    /**
     * Place validator messages in session in order to survive redirect
     * forward to errorpage
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return a forward depending on logged in page
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward checkOutError(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        request.getSession().setAttribute(Globals.ERROR_KEY, request.getAttribute(Globals.ERROR_KEY));
        request.getAttribute(Globals.MESSAGES_KEY);
//        System.out.println("************* checkOutError  **********************");
//        System.out.println("request.getAttribute(Globals.ERROR_KEY) = " + request.getAttribute(Globals.ERROR_KEY));
//        System.out.println("request.getSession().getAttribute(Globals.ERROR_KEY) = " + request.getSession().getAttribute(Globals.ERROR_KEY));
//        System.out.println("************* checkOutError  **********************");
        return mapping.findForward("errorpage");
    }

    /**
     * Update shopping cart, checks if user is logged in and forwards accordingly<br/>
     * checks for non-empty shopping cart, adds error message and goes back to shopping cart
     *
     * @return a forward depending on logged in page
     */
    @SuppressWarnings({"UnusedDeclaration", "JavaDoc"})
    public ActionForward startCheckOut(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        ActionForward retVal = mapping.findForward("checkoutPage1");
        ActionMessages msgs;
        msgs = new ActionMessages();

        try
        {
            ShoppingCart shoppingCart = getExistingScFromSession(request);
//            shoppingCart.updateCart();
            if (shoppingCart.getScItemsForConfirmation().size() == 0)
            {
                // todo: check each photo. no photo with quantity < 1
                _logger.info("check-out attempt with no items in cart");
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("shoppingcart.empty.error"));
                retVal = mapping.findForward("backToCart");
            }
        } catch (UnartigSessionExpiredException e)
        {
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("session.expired"));
            _logger.info("session has expired, check out not possible");
            retVal = mapping.findForward("sessionExpired");
        } 
        saveMessages(request, msgs);
        return retVal;
    }

    /**
     * forward user to address form
     *
     * @return forward to address
     */
    @SuppressWarnings({"JavaDoc", "UnusedDeclaration"})
    public ActionForward checkOutAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        _logger.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        _logger.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        CheckOutForm coForm = (CheckOutForm) form;
        _logger.debug("coForm.isNewCustomer() = " + coForm.isNewCustomer());

        return mapping.findForward("toEnterAddress");
    }

    /**
     * Forward user to address form
     *
     *
     * @return forward to address
     * @throws ch.unartig.u_core.exceptions.UnartigSessionExpiredException
     */
    @SuppressWarnings({"UnusedDeclaration", "JavaDoc"})
    public ActionForward checkOutBillingMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigSessionExpiredException
    {
        CheckOutForm coForm = (CheckOutForm) form;
        // we need to set the country to the shopping-cart; the payment methods depend on it.
        // todo Session time out treatment! --> Nullpointerexception
        SessionHelper.getShoppingCartFromSession(request).setCustomerCountry(coForm.getCountry());
        checkSessionExpired(request);
        return mapping.findForward("success");
    }


    /**
     * Make sure session is alive and shopping cart available!<br>
     *
     * @return forward to overview or session expired page
     * @throws ch.unartig.u_core.exceptions.UnartigSessionExpiredException
     *
     */
    @SuppressWarnings({"UnusedDeclaration", "JavaDoc"})
    public ActionForward checkOutOverview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigSessionExpiredException
    {
        checkSessionExpired(request);
        CheckOutForm coForm = (CheckOutForm) form;
        SessionHelper.getShoppingCartFromSession(request).setCustomerCountry(coForm.getCountry());
        return mapping.findForward("checkOutOverviewSuccess");
    }

    /**
     * helper method to check for an experied session during the check out process
     *
     * @param request Http Request
     * @throws ch.unartig.u_core.exceptions.UnartigSessionExpiredException
     *          If Session has expired
     */
    private void checkSessionExpired(HttpServletRequest request) throws UnartigSessionExpiredException
    {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Registry._NAME_SHOPPING_CART_SESSION) == null)
        {
            _logger.warn("Session has expired during check-out process! throwing session-expired exception");
            throw new UnartigSessionExpiredException("Session expired during check-out");
        }
    }


    /**
     * User has hit the confirm button ; <br>
     * first check for double-submission using the token</br>
     * make sure AGBs have been accepted and write AGBs to the log file <br>
     * call the shopping cart logic class and store user and decide which page to forward to<br>
     * handle credit card payment details if necessary<br>
     * on successful completion of the checkout-session delete the shopping cart<br>
     * <p> IMPORTANT: delete credit card information from session
     *
     * @param mapping  struts action mapping
     * @param form     the action form
     * @param request  http request
     * @param response http response
     * @return forward to next page
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *          database problem
     * @throws ch.unartig.u_core.exceptions.UnartigSessionExpiredException
     *          if the current shopping sesssion has expired
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public ActionForward checkOutConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigSessionExpiredException, UAPersistenceException
    {
        ActionMessages msgs;
        ShoppingCartLogic shoppingCartLogic = new ShoppingCartLogic(request);
        String customerIpAddress;
        msgs = new ActionMessages();
        checkSessionExpired(request);
        HttpSession session = request.getSession(true);
        ShoppingCart sc = (ShoppingCart) session.getAttribute(Registry._NAME_SHOPPING_CART_SESSION);
        CheckOutForm coForm = (CheckOutForm) form;
        if (!isTokenValid(request))
        {
            // unexpected error; show order-not-ok message as confirmation message
            _logger.warn("wrong token! probably caused by a double submission");
            msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("warnings.invalid.token"));
            // stop here, no processing of the order
        } else
        {
            saveToken(request);

            _logger.debug("coForm.isAcceptTermsCondition() = " + coForm.isAcceptTermsCondition());

            if (!coForm.isAcceptTermsCondition())
            { // terms & conditions not accepted ... return, show error message
                _logger.debug("coForm.isAcceptTermsCondition() = " + coForm.isAcceptTermsCondition());
                _logger.debug("going back");
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.acceptTermsCondition.required"));
                saveMessages(request.getSession(), msgs);
                return mapping.findForward("coError");
            }
            shoppingCartLogic.setShoppingCart(sc);
            shoppingCartLogic.setCheckOutForm(coForm);
            shoppingCartLogic.setLocale((Locale) request.getSession().getAttribute(Globals.LOCALE_KEY));
            // retrieve the remote ip-address that will be stored for the records.
            customerIpAddress = request.getRemoteAddr();
            int photoOrderReturnCode;
            // use the shopping cart logic to store the order definitively
            try
            {
                photoOrderReturnCode = shoppingCartLogic.storeAndExecuteOrder(customerIpAddress);
                Customer customer = shoppingCartLogic.getCustomer();
                // keep the customer-userid in the session
//                session.setAttribute(Registry._NAME_CUSTOMER_ID_SESSION_ATTR, customer.getCustomerId());
                // keep the order details to be used with google analytics in the request
                session.setAttribute(Registry._NAME_SHOPPING_CART_ATTR, sc);
                // todo null pointer exception possible
                session.setAttribute("GAorderId", shoppingCartLogic.getOrder().getOrderId());
                session.setAttribute("GAorderItems", shoppingCartLogic.getPriceProducttypeConsolidatedItems());
                session.setAttribute("GAcustomerCity", coForm.getCity());
                // delete the shopping cart. Creditcard infos!!
                SessionHelper.remove(request, Registry._NAME_SHOPPING_CART_SESSION);
                // return code is not success
                if (photoOrderReturnCode!= PhotoOrderIF._SUCCESS)
                {
                    _logger.info("Return code from photo order: " + photoOrderReturnCode);
                    handlePhotoOrderError(request, msgs, photoOrderReturnCode);
                    return mapping.findForward("checkOutCcException");
                }
            } catch (UnartigException e)
            {
                // unexpected error; show order-not-ok message as confirmation message
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.creditcard.unknownerror"));
                _logger.error("unexpected error; preparing error massage for customer and sending confirmation page");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw new RuntimeException();
            }
        }
        saveMessages(request.getSession(), msgs);
        // is this enough for keeping the error-messages alive ???
        DebugUtils.debugActionMessage(request);
        // reset cc infos
        coForm.setCreditCardNumber("");
        coForm.setCreditCardExpiryMonth("");
        coForm.setCreditCardExpiryYear("");
        coForm.setCreditCardHolderName("");
        return mapping.findForward("checkOutConfirmOut");
    }

    /**
     * We got a return code, if it's not success, do the error treatment
     * @param request http request
     * @param msgs messages to forward to the view
     * @param photoOrderErrorCode Error code from photo order
     */
    private void handlePhotoOrderError(HttpServletRequest request, ActionMessages msgs, int photoOrderErrorCode)
    {
        _logger.info("Creditcard Validation error");
        switch (photoOrderErrorCode)
        {
            case PhotoOrderIF._CREDIT_CARD_REJECTED:
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.creditcard.rejected"));
                break;
            case PhotoOrderIF._CREDIT_CARD_TRANSACTION_FAILED:
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.creditcard.transactionfailed"));
                break;
            case PhotoOrderIF._CREDIT_CARD_EXPIRY_DATE_ERROR:
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.creditcard.expiryYear"));
                break;
            case PhotoOrderIF._UNKNOWN_ERROR:
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.creditcard.unknownerror"));
                break;
            default:
                // don't know what happened here.
                msgs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.unknown"));
        }
        saveMessages(request.getSession(), msgs);
    }

    /**
     * return a shopping cart or an exception if none exists in session
     *
     * @param request http request
     * @return the shopping cart
     * @throws UnartigSessionExpiredException
     */
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

}

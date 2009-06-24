/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 14, 2005$
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
 * Revision 1.4  2007/05/05 10:39:04  urban
 * resources email
 *
 * Revision 1.3  2007/05/04 16:56:00  alex
 * some fixes
 *
 * Revision 1.2  2007/05/03 16:05:14  alex
 * startnummernsuche works
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.17  2006/12/27 13:33:45  alex
 * better email
 *
 * Revision 1.16  2006/11/17 13:21:41  alex
 * email notifiaction fix
 *
 * Revision 1.15  2006/11/14 16:19:24  alex
 * digital order
 *
 * Revision 1.14  2006/11/05 22:10:02  alex
 * credit card order works
 *
 * Revision 1.13  2006/11/05 16:41:43  alex
 * action messages work for order confirmation
 *
 * Revision 1.12  2006/11/03 13:15:19  alex
 * some changes
 *
 * Revision 1.11  2006/11/01 10:14:45  alex
 * cc interface check in, transactions work
 *
 * Revision 1.10  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.9  2006/10/17 08:07:07  alex
 * creating the order hashes
 *
 * Revision 1.8  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.7  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 * Revision 1.6  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.5  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.4  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.3  2006/02/28 14:57:46  alex
 * added more resources (email for order confirmation), small fixes
 *
 * Revision 1.2  2006/02/08 15:35:09  alex
 * confirmation message again
 *
 * Revision 1.1  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 ****************************************************************/
package ch.unartig.studioserver.businesslogic;

import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.ordering.colorplaza.CoplaPhotoOrder;
import ch.unartig.studioserver.beans.CheckOutForm;
import ch.unartig.studioserver.beans.ScOrderItem;
import ch.unartig.studioserver.beans.ShoppingCart;
import ch.unartig.u_core.model.Customer;
import ch.unartig.u_core.model.Order;
import ch.unartig.u_core.model.OrderHash;
import ch.unartig.u_core.model.OrderItem;
import ch.unartig.u_core.model.Photo;
import ch.unartig.u_core.model.Product;
import ch.unartig.u_core.persistence.DAOs.OrderDAO;
import ch.unartig.u_core.persistence.DAOs.OrderHashDAO;
import ch.unartig.u_core.persistence.DAOs.PhotoDAO;
import ch.unartig.u_core.persistence.DAOs.ProductDAO;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import ch.unartig.u_core.util.CryptoUtil;
import ch.unartig.u_core.util.HttpUtil;
import ch.unartig.u_core.util.MailUtil;
import ch.unartig.u_core.exceptions.CreditCardException;
import ch.unartig.u_core.exceptions.UAPersistenceException;
import org.apache.log4j.Logger;
import org.apache.struts.util.MessageResources;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Business-Logic class to store all shopping cart related logic. check-out, storing orders and users etcetc.
 * Every Check-out process has its own instance of this class
 */
public class ShoppingCartLogic
{
    Logger _logger = Logger.getLogger(getClass().getName());

    private Customer customer;
    private String downloadLink;
    private ShoppingCart shoppingCart;
    private CheckOutForm checkOutForm;
    private HttpServletRequest request;
    private PhotoOrderIF photoOrder;
    private Locale locale;
    private Set orderItems;
    private Order order;


    public ShoppingCartLogic(HttpServletRequest request)
    {
        this.request = request;
    }

    /**
     * go through the shopping cart and create a list of orderItems
     * load photo, load product, get quantity
     * saves the orderitems
     * make sure no quantity = 0 scItems are included!
     *
     * @param consolidatedItems only order items that are relevant for order process, i.e. no zero amount order items etc.
     * @param order             instance of order object
     * @return a Set of OrderItems
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *          if order cannot be persisted
     */
    @SuppressWarnings({"unchecked"})
    private Set getOrderItems(List consolidatedItems, Order order) throws UAPersistenceException
    {
        PhotoDAO phDao = new PhotoDAO();
        ProductDAO productDao = new ProductDAO();
        Photo photo;
        Product product;
        int quantity;
        Set orderItems = new HashSet();
        for (Object consolidatedItem : consolidatedItems)
        {
            ScOrderItem scItem = (ScOrderItem) consolidatedItem;
            photo = phDao.load(scItem.getPhotoId());
            product = productDao.load(scItem.getProductId());
            quantity = scItem.getQuantity();
            OrderItem oi = new OrderItem(photo, product, quantity, order);
            orderItems.add(oi);
        }

        return orderItems;
    }



    /**
     * <p>Store the order, log the ip-address, send confirmation email to customer</p>
     * <p>Orders are collected in the unartig database. A service that runs every night collects the open orders and sends them to the lab.</p>
     * <p>NEW: for digital-only orders, no image files have to be sent with the nightly service, but the order will take place right away using the credit card interface</p>
     *
     * @param ipAddress retrieved from the request;
     * @return the return code from the photoOrder
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *          if a problem with the database occurs
     * @throws ch.unartig.u_core.exceptions.UnartigException On db rollback
     */
    public int storeAndExecuteOrder(String ipAddress) throws UnartigException {
        // defensive programming: initialize the error code to unknown error
        int returnCode;
        OrderDAO orderDao = new OrderDAO();
        // store the checked out order
        customer = new Customer(checkOutForm);
        // transaction for writing order to the database:
        try
        {
            // Think about exception handling: what happens if credit card payment does not work?
            // what if we can not create an order hash? etc.etc
            // transaction also includes credit card processing if payment method id creditcard
            HibernateUtil.beginTransaction();
            this.order = new Order(new Date());
            this.orderItems = getOrderItems(shoppingCart.getScItemsForConfirmation(), this.order);
            this.order.setOrderItems(orderItems);
            this.order.setCustomer(customer);
            orderDao.save(this.order);
            prepareDownloadLink(this.order);
            _logger.info("Order with orderid [" + this.order.getOrderId().toString() + "] commited from IP-Address [" + ipAddress + "]");
            _logger.info("successfully stored new order; oipsorderid = " + this.order.getOipsOrderId());
            if (checkOutForm.isPaymentMethodCreditCard())
            {
                // todo the "photoOrder" is only constructed in this method (and only if method is credit card!). Think about a better solution or initialize the photoOrder before.
                sendOrderImmediately(this.order);
            }
            HibernateUtil.commitTransaction();
            _logger.info("Order successfullly commited [OrderId:"+ this.order.getOrderId().toString()+"]. Starting new Transaction.");
            HibernateUtil.beginTransaction();
            // if everything goes well, send confirmation and set returncode to success.
            sendCustomerNotification();
            returnCode = PhotoOrderIF._SUCCESS;
        } catch (CreditCardException e)
        {
            // we ran into a creditcard problem. Rollback, set return code and continue!
            _logger.info("Credit Card validation exception : ",e);
            _logger.info("Rolling back, setting return code and continue");
            HibernateUtil.rollbackTransaction();
            returnCode = photoOrder.getErrorCode();
        }
        catch (Exception e2)
        {
            _logger.error("unexpected Exception .... Rolling back - check stacktrace", e2);
            HibernateUtil.rollbackTransaction();
            throw new UnartigException("Unexpected exception : " + e2.getMessage());
        }
        return returnCode;
    }

    /**
     * If payment method is credit card, the order is sent to the lab immediately
     * @param order the order to send to the lab
     * @return a return code from the order-processing
     * @throws UnartigException
     */
    private int sendOrderImmediately(Order order) throws UnartigException
    {
        int returnCode;// send order now! use credit card payment
        _logger.debug("payment method: credit card");
        photoOrder = new CoplaPhotoOrder(order, Registry.isDemoOrderMode(), Registry.isSimulateOrderOnly());
        String cardHolderName = getCardHolderName();
        CreditCardDetails ccDetail = new CreditCardDetails(checkOutForm.getCreditCardTypeCode(), checkOutForm.getCreditCardNumber(), null, cardHolderName, new Integer(checkOutForm.getCreditCardExpiryYear()), new Integer(checkOutForm.getCreditCardExpiryMonth()));
        photoOrder.setCreditCardDetails(ccDetail);
        photoOrder.processOrder();
        returnCode = photoOrder.getErrorCode();
        return returnCode;
    }

    private String getCardHolderName()
    {
        String cardHolderName = checkOutForm.getCreditCardHolderName();
        if (cardHolderName == null || "".equals(cardHolderName))
        {
            if (checkOutForm.getFirstName()!=null && !"".equals(checkOutForm.getFirstName()))
            {
                cardHolderName = checkOutForm.getFirstName()+' '+checkOutForm.getLastName();
            } else
            {
                cardHolderName="";
            }
        }
        return cardHolderName;
    }

    /**
     * <ul>
     * <li>prepare the digital photos for download
     * <li>create the link to the photos for download
     * </ul>
     *
     * @param order the persistent order object instance
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *          if orderHash can not be persisted
     */
    private void prepareDownloadLink(Order order) throws UnartigException
    {
        Date expiryDate;
        Calendar expiryDateCal = Calendar.getInstance();
        expiryDateCal.setTime(new Date());
        expiryDateCal.add(Calendar.DAY_OF_YEAR, Registry._ORDER_DOWNLOADS_VALID_DAYS);
        expiryDate = expiryDateCal.getTime();
        _logger.debug("setting expiry date to :" + expiryDate);
        String orderHash = createOrderHash(order, expiryDate);
        downloadLink = HttpUtil.getDownloadUrl(orderHash, request);

        _logger.info("created download link  : " + downloadLink);
    }

    /**
     * @param order      the persistend order object
     * @param expiryDate a date
     * @return the one time hash
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *          if orderHash can not be saved
     */
    private String createOrderHash(Order order, Date expiryDate)
    {
        String orderHashString;
        orderHashString = CryptoUtil.createHash();
        _logger.debug("Order Hash : " + orderHashString);
        OrderHash orderHash = new OrderHash(order, expiryDate, orderHashString);

        OrderHashDAO ohDao = new OrderHashDAO();
        ohDao.save(orderHash);

        return orderHashString;
    }

    /**
     * Send email message to customer after order has been confirmed
     * this service can time out ....
     *
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     *          database exception
     */
    private void sendCustomerNotification() throws UAPersistenceException
    {
        MessageResources contentResource = MessageResources.getMessageResources("contentText");

        StringBuffer localizedBody = MailUtil.generateBody(photoOrder,downloadLink, shoppingCart, contentResource, locale);
        String localizedSubject = contentResource.getMessage(locale,"email.orderAccepted.subject");
        try
        {
            _logger.debug("trying to send email message:\n");
            _logger.debug("localizedSubject \n\n" + localizedSubject);
            _logger.debug("localizedBody = \n\n" + localizedBody);
            MailUtil.sendMail(localizedSubject, localizedBody.toString(), customer.getEmail(), Registry.getOrderConfirmationFromAddress());
            _logger.debug("Confirmation email-message sent to customer");
        } catch (MessagingException e)
        {
            _logger.error("Error sending confirmation message", e);
            e.printStackTrace();
        }

    }

    public void setShoppingCart(ShoppingCart shoppingCart)
    {
        this.shoppingCart = shoppingCart;
    }

    public void setCheckOutForm(CheckOutForm checkOutForm)
    {
        this.checkOutForm = checkOutForm;
    }


    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }


    public PhotoOrderIF getPhotoOrder()
    {
        return photoOrder;
    }

    public void setPhotoOrder(PhotoOrderIF photoOrder)
    {
        this.photoOrder = photoOrder;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public Set getOrderItems() {
        return orderItems;
    }

    public Order getOrder() {
        return order;
    }


    /**
     * Return a set of unique productType-price combinations. Useful for Google Analytics E-Commerce.
     * @return set of sku-items
     */
    public Collection<OrderItem> getPriceProducttypeConsolidatedItems() {
        Map<String,OrderItem> retVal = new HashMap<String, OrderItem>();
        for (OrderItem orderItem : (Set<OrderItem>)orderItems) {

            // Think about creating a SKU property on the product:
            String skuKey = orderItem.getProduct().getProductType().getName() +"--"+ orderItem.getProduct().getPrice().getPriceCHF().toPlainString()+"CHF";
            if (retVal.containsKey(skuKey))
            { // this productType-price combination exists already  ==>  update quantity
                Integer newQuantity = retVal.get(skuKey).getQuantity() + orderItem.getQuantity();
                retVal.get(skuKey).setQuantity(newQuantity);
            }
            else
            {// create new entry
                OrderItem skuItem = new OrderItem(orderItem.getPhoto(),orderItem.getProduct(),orderItem.getQuantity(),orderItem.getOrder());
                skuItem.getProduct().setProductName(skuKey);
                retVal.put(skuKey,skuItem);
            }
        }
        return retVal.values();
    }
}

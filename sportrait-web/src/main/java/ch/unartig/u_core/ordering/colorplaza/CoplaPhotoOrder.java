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
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.2  2007/03/14 02:41:01  alex
 * initial checkin
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.25  2006/12/28 11:25:04  alex
 * checking in ddl
 *
 * Revision 1.24  2006/12/27 13:33:45  alex
 * better email
 *
 * Revision 1.23  2006/11/22 12:41:22  alex
 * order process crashed with invalid orderitem. now it's thrown and handeled gracefully
 *
 * Revision 1.22  2006/11/21 08:23:19  alex
 * news-fenster
 *
 * Revision 1.21  2006/11/14 16:19:24  alex
 * digital order
 *
 * Revision 1.20  2006/11/05 22:10:02  alex
 * credit card order works
 *
 * Revision 1.19  2006/11/05 16:41:43  alex
 * action messages work for order confirmation
 *
 * Revision 1.18  2006/11/01 16:26:58  alex
 * cc validator
 *
 * Revision 1.17  2006/11/01 10:51:20  alex
 * cc interface check in, transactions work
 *
 * Revision 1.16  2006/11/01 10:14:45  alex
 * cc interface check in, transactions work
 *
 * Revision 1.15  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.14  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.13  2006/01/10 15:44:56  alex
 * vm1 config files, new property "simulateOrderOnly"
 *
 * Revision 1.12  2005/12/27 15:22:22  alex
 * payment method shows now bill not credit card
 *
 * Revision 1.11  2005/12/11 21:49:08  alex
 * unartiginfo weg aus text
 *
 * Revision 1.10  2005/12/02 21:48:34  alex
 * order process bug fix, color correction on
 *
 * Revision 1.9  2005/11/30 17:08:34  alex
 * order process bug fix
 *
 * Revision 1.8  2005/11/28 17:52:16  alex
 * bug fixes
 *
 * Revision 1.7  2005/11/25 16:30:45  alex
 * order save fix
 *
 * Revision 1.6  2005/11/25 13:22:24  alex
 * resources
 *
 * Revision 1.5  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.4  2005/11/25 10:56:58  alex
 *
 * Revision 1.3  2005/11/23 21:58:43  alex
 * bug-fixes
 *
 * Revision 1.2  2005/11/22 21:33:16  alex
 * ordering process
 *
 * Revision 1.1  2005/11/21 17:52:58  alex
 * no account action , photo order
 *
 * Revision 1.1  2005/11/21 09:58:03  alex
 * init. version
 *
 * Revision 1.2  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.1  2005/11/09 21:59:36  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 ****************************************************************/
package ch.unartig.u_core.ordering.colorplaza;

import ch.unartig.u_core.exceptions.CreditCardException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.exceptions.UnartigInvalidArgument;
import ch.unartig.u_core.util.XmlHelper;
import ch.unartig.u_core.util.HttpUtil;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.ordering.colorplaza.OipsPidMapper;
import ch.unartig.u_core.model.Customer;
import ch.unartig.u_core.model.Order;
import ch.unartig.u_core.model.OrderItem;
import ch.unartig.u_core.model.Photo;
import ch.unartig.u_core.model.Product;
import ch.unartig.studioserver.businesslogic.PhotoOrderIF;
import ch.unartig.studioserver.businesslogic.CreditCardDetails;
import ch.unartig.u_core.persistence.DAOs.CustomerDAO;
import ch.unartig.u_core.persistence.DAOs.OrderDAO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CoplaPhotoOrder implements PhotoOrderIF
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /*
    ---- OIPS Stuff
    */
    public URL oipsServer;
    // todo palce in resource file
    public static final String coplaServerRoot = "http://oips.colorplaza.com";
    /*the service ID SEID determines the country for the order ??*/
    public static final Map _SEID_MAP = new HashMap();

    /**
     * oemid??
     */
    public static final String _OEMID = "27";
    public static final String _SWID = "901764";
    public static final String _OIPS_LOGIN = "unartig";
    public static final String _OIPS_PASSWORD = "hem31mot";
    public static final String _SWVERSION = "01.00.00";

    /*
    ---- TEST ACCOUNT SETTINGS
    */
    //    public static final String _SEID_DEMO = "90000";
    public static final String _OEMID_DEMO = "99";
    public static final String _SWID_DEMO = "99";
    public static final String _OIPS_LOGIN_DEMO = "demo";
    public static final String _OIPS_PASSWORD_DEMO = "demo";
    public static final String _SWVERSION_DEMO = "01.01.01";
    private static final String _OIPS_PID_DEMO = "5";// product id for the test-order; 13x18 cm photo; 0.65 CHF
    private boolean demoOrder;

    /**
     * Session Id is provided after using SessionLogin
     */
    public String sessionId;
    public String imageServer;
    /**
     * OrderId from oips is provided after the startOrder command
     */
    private String oipsOrderId;
    private String payId = "4"; // the payment id from payment methods (4 equals rechnung)


    private Customer unartigCustomer;
    private Order order;
    public static final String _LANG_ID = "DE";
    private String username;
    /**
     * Depending on the country that this is order is for, a different service id will be used
     */
    private final String serviceId;
    private CreditCardDetails ccDetail;
    private int errorCode;
    private static final int _DELIVERY_ID_NO_SHIPPING_HANDLING = 8;
    private boolean simulateOrderOnly;
    private static final int _CONN_TIMEOUT_MS = 10000;
    private static final int _CONNECTION_TIMEOUT_IN_MILLISECONDS = 10000;

    /**
     * private constructor only to be used with main method
     *
     * @param serviceString use "Demo", CHE, DEU or AUT
     */
    private CoplaPhotoOrder(String serviceString)
    {
        serviceId = (String) _SEID_MAP.get(serviceString);
    }


    // statically initialize the seid-map; enter new seid's here if necessary
    static
    {
        _SEID_MAP.put(Registry._SWITZERLAND_COUNTRY_CODE, "2700"); // serviceId for Switzerland CHE
        _SEID_MAP.put(Registry._GERMANY_COUNTRY_CODE, "2702"); // serviceid for Germany DEU
        _SEID_MAP.put(Registry._AUSTRIA_COUNTRY_CODE, "2712"); // serviceid for Austria AUT (Actually, 2712 is the SEID for all international services. We use this for all countries except Germany and Switzerland, where we have an invoice option for payment)
        _SEID_MAP.put("DEMO", "90000"); // serviceid for DEMO session
    }


    /**
     * use the main method for test with COPLAs OIPS
     *
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        CoplaPhotoOrder testOrder = new CoplaPhotoOrder("Demo");

        System.out.println("testOrder.getOipsServer() = " + testOrder.getOipsServer());
        // login in and get a session
        testOrder.sessionLogin();

        // get payment methods
    }

    /**
     * Constructor for the Colorplaza photo order class;
     *
     * @param order             the order to be sent to colorplaza
     * @param demoOrder         set demoOrder to true to use the demo service at colorplaza
     * @param simulateOrderOnly if set to true, order won't be sent out at all, only simulated
     */
    public CoplaPhotoOrder(Order order, boolean demoOrder, boolean simulateOrderOnly)
    {
        this.order = order;
        this.unartigCustomer = order.getCustomer();
        this.simulateOrderOnly = simulateOrderOnly;
        if (simulateOrderOnly)
        {
            _logger.info("*******************************************************************");
            _logger.info("******* ONLY Simulation of order ********");
            _logger.info("******* no order took place !!! ********");
            _logger.info("*******************************************************************");
            serviceId = (String) _SEID_MAP.get("DEMO");
        } else
        {
            _logger.info("Is DEMO Order ? " + demoOrder);
            setDemoOrder(demoOrder);
            // set the service id to either demo or the country depending serviceid (Switzerland, Germany or Austria)
            // Discussion with Markus, Copla: we use either the seid for switzerland or the international seid.
            serviceId = !demoOrder ? (String) _SEID_MAP.get(order.getCustomer().getCountry()) : (String) _SEID_MAP.get("DEMO");
        }
    }

    /**
     * @return true for a successful order todo return an error code here
     */
    public boolean processOrder() throws UnartigException {
        _logger.debug("processing order NOW...");
        _logger.debug("customer : " + unartigCustomer);
        _logger.debug("order :" + order);
        if (!simulateOrderOnly && (order != null) && (unartigCustomer != null))
        {
            // ccDetail == null means payment by invoice
            setPayId();
            _logger.info("set payid to [" + payId + "]");
            try
            {
                getOipsServer();

                if (isDemoOrder())
                {
                    demoSessionLogin();
                } else
                {
                    sessionLogin();
                }
                // check for existing customer or create a new customer
                if (!getCustomerFromOips())
                {
                    registerCustomerWithOips();
                }
                startOrder();
                addOrderItems();
                if (ccDetail != null)
                {
                    // credit card payment
                    _logger.info("!! going to verify Credit Card !!");
                    verifyCC();
                    if (errorCode != _SUCCESS)
                    {
                        _logger.info("error during credit card validation, code = " + errorCode);
                        throw new CreditCardException("Credit Card Exception");
                    }
                }
                // credit card payments??
                setPaymentMethod();

                if (order.isOnlyDigitalProducts())
                {
                    setDeliveryMethod(_DELIVERY_ID_NO_SHIPPING_HANDLING);
                    _logger.info("only digital products: setting free delivery option");
                }

                getImageServer();
                uploadImages();
                confirmOrder();
                _logger.info("order has been processed");
            } catch (Exception e)
            {
                _logger.error("unknown error", e);
                errorCode = _UNKNOWN_ERROR;
                throw new UnartigException("unknown error", e);
            }
        } else if ((order != null) && (unartigCustomer != null))
        {
            // order ok, customer ok, but simualation
            _logger.info("SIMULATED ORDER!!");
        }
        else
        {
            _logger.error("Order or Customer missing ...");
            errorCode = _ORDER_CUSTOMER_MISSING;
            return false;
        }
        return true;
    }

    /**
     * Optional: set a non-standard delivery method;
     * <p> this is used, when all order items are digital products and thus no shipping and handling applies
     * @param deliveryId
     * @throws IOException
     * @throws JDOMException
     */
    private void setDeliveryMethod(int deliveryId) throws IOException, JDOMException
    {
        _logger.debug("********************* SET DELIVERY METHOD ****************************");
        Map parameters = new HashMap();
        parameters.put("sid", sessionId);
        parameters.put("orderid", oipsOrderId);
        parameters.put("did", String.valueOf(deliveryId));

        String command = "SetDeliveryMethod.cpl";
        String url;
        url = HttpUtil.composeUrlFromParameters(parameters, oipsServer.toExternalForm(), command);


        _logger.debug("url = " + url);
        Element rootElement = XmlHelper.getJdomRootElement(url);
        String status = rootElement.getChild("Status").getTextTrim();

        _logger.info("status = " + status);
        switch (Integer.parseInt(status))
        {
            case 0:
                oipsOrderId = rootElement.getChild("OrderId").getTextTrim();
                _logger.info("oipsOrderId = " + oipsOrderId);
                break;
            case 1:
                ;
        }
    }

    private void setPayId()
    {
        if (ccDetail == null)
        {
            payId = "4"; // rechnung; TODO also for Germany ??
        } else if (CreditCardDetails._CC_TYPE_CODE_MASTERCARD == ccDetail.getCreditCardType())
        {
            payId = Integer.toString(ccDetail.getCreditCardType());
        }
    }



    public void setCreditCardDetails(CreditCardDetails ccDetail)
    {
        this.ccDetail = ccDetail;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    public int getErrorCode()
    {
        return errorCode;
    }


    /**
     * @throws Exception
     */
    private void confirmOrder() throws Exception
    {
        OrderDAO oDao = new OrderDAO();

        _logger.debug("********************* CONFIRM ORDER ****************************");

        Map parameters = new HashMap();
        parameters.put("sid", sessionId);
        parameters.put("orderid", oipsOrderId);

        String command = "ConfirmOrder.cpl";
        String url = null;
        try
        {
            url = HttpUtil.composeUrlFromParameters(parameters, oipsServer.toExternalForm(), command);
        } catch (UnsupportedEncodingException e)
        {
            _logger.info("During order confiurmation : ",e);
        }

        _logger.debug("url = " + url);
        Element rootElement = XmlHelper.getJdomRootElement(url);
        String status = rootElement.getChild("Status").getTextTrim();

        _logger.debug("status = " + status);
        switch (Integer.parseInt(status))
        {
            case 0:
                order.confirmUpload(oipsOrderId);
                oDao.save(order);
                break;
            case 1:
                throw new UnartigException("cannot confirm order! status code  :" + status);
        }
    }

    /**
     * Set the global payment method for the order. See the following query-outputs for "GetPaymentMethods".<br>
     * <b>todo should COPLA be queried for the correct payid every time by using the payName?<br>
     * For now use: 1 For Visa and 2 for Master Card</b>
     * <p>From "GetPaymentMethods" for Demo Order SEID:</p>
     * <pre>
     * payId = 4
     * payName = Rechnung
     * paymentType = 0
     * ----------------------------------
     * payId = 1
     * payName = VISA Kredit Karte
     * paymentType = 1
     * ----------------------------------
     * payId = 2
     * payName = Mastercard Kredit Karte
     * paymentType = 1
     * ----------------------------------
     * </pre>
     * <p>for 2712, INT</p>
     * <pre>
     * status = 0
     * payId = 1
     * payName = VISA Kredit Karte
     * paymentType = 1
     * ----------------------------------
     * payId = 2
     * payName = Mastercard Kredit Karte
     * paymentType = 1
     * ----------------------------------
     * </pre>
     * <p>for 2702, Germany</p>
     * <pre>
     * payId = 1
     * payName = VISA Kredit Karte
     * paymentType = 1
     * ----------------------------------
     * payId = 2
     * payName = Mastercard Kredit Karte
     * paymentType = 1
     * ----------------------------------
     * </pre>
     */
    private void setPaymentMethod()
    {
        // this seems to be optional if the payment mehtod is set in addOrderItems
    }

    /**
     * <p>Make a call to the VerifyCC service</p>
     * <p>this will check the credit card and return an authorization code</p>
     * <p></p>
     * <pre>
     * sid: session id
     * orderid: order id from StartOrder
     * ccnbr: Credit card number, only numeric data (0-9)
     * ccexp: credit card expiration date in format yymm
     * ccholder: Credit card holder name
     * </pre>
     *
     * @throws java.io.IOException
     * @throws org.jdom.JDOMException
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     *                                if this mehtod is called without valid credit card details
     */
    private void verifyCC() throws IOException, JDOMException, UnartigInvalidArgument
    {
        _logger.info("********************* Verify Credit Card ****************************");
        String oipsHttpsUrl;

        NumberFormat format = DecimalFormat.getInstance();

        format.setMinimumIntegerDigits(2);

        if (ccDetail != null)
        {

            Map parameters = new HashMap();
            parameters.put("sid", sessionId);
            parameters.put("orderid", oipsOrderId);
            parameters.put("ccnbr", ccDetail.getCreditCardNumber());
            // make sure ccexp is in the format yymm
            parameters.put("ccexp", ccDetail.getCreditCardExpiryYear().toString().substring(2) + format.format(ccDetail.getCreditCardExpiryMonth()));
            parameters.put("ccholder", ccDetail.getCreditCardHolderName());

            String command = "VerifyCC.cpl";
            String url;
            try
            {
                url = HttpUtil.composeUrlFromParameters(parameters, oipsServer.toExternalForm(), command);
            } catch (UnsupportedEncodingException e)
            {
                _logger.error("error while verifying credit card !", e);
                throw new UnartigInvalidArgument("error while verifying credit card !", e);
            }
            if (url!=null)
            {
                oipsHttpsUrl = url.replaceFirst("http", "https");

                _logger.debug("verifyCC url = " + oipsHttpsUrl);
                Element rootElement = XmlHelper.getJdomRootElement(oipsHttpsUrl);
                String status = rootElement.getChild("Status").getTextTrim();

                _logger.info("status = " + status);
                switch (Integer.parseInt(status))
                {
                    case 0:
                        _logger.info("SUCCESS!!!");
                        break;
                    case 1:

                        break;
                    case 4:
                        _logger.info("Invalid or missing expiry date");
                        setErrorCode(_CREDIT_CARD_EXPIRY_DATE_ERROR);
                        break;
                    case 40:
                        _logger.info("Card Rejected");
                        setErrorCode(_CREDIT_CARD_REJECTED);
                        break;
                    case 50:
                        _logger.info("transaction failed");
                        setErrorCode(_CREDIT_CARD_TRANSACTION_FAILED);
                        break;
                    default:
                        _logger.info("status = " + status);
                        _logger.info("unknown status code");
                        setErrorCode(_UNKNOWN_ERROR);
                        break;
                }// end switch
            }// end if
            else
            {
                _logger.error("Colorplaza Order URL = null!! ");
                throw new UnartigInvalidArgument("Colorplaza Order URL = null!! ");
            }

        } else
        {
            _logger.error("CoplaPhotoOrder.verifyCC: no credit card details ... error");
            throw new UnartigInvalidArgument("no credit card details given");
        }
    }

    /**
     * @throws Exception
     */
    private void getImageServer() throws Exception
    {
        _logger.debug("********************* Get Image Server ****************************");

        Map parameters = new HashMap();
        parameters.put("sid", sessionId);
        parameters.put("oemid", _OEMID);

        String command = "GetImageServer.cpl";
        String url;
        url = HttpUtil.composeUrlFromParameters(parameters, oipsServer.toExternalForm(), command);
        _logger.debug("url = " + url);
        Element rootElement = XmlHelper.getJdomRootElement(url);
        String status = rootElement.getChild("Status").getTextTrim();

        _logger.debug("status = " + status);
        switch (Integer.parseInt(status))
        {
            case 0:
                //do somethint
                ;
            case 1:
                ;
        }
        imageServer = rootElement.getChild("ImageServer").getTextTrim();

        _logger.debug("imageServer = " + imageServer);
    }

    /**
     * Upload all images for the current order:<br/>
     * create a set of ordered images; NOTE: ordered images is not equal to ordered items!
     *
     * @throws Exception If image server is not ready
     */
    private void uploadImages() throws Exception
    {
        List orderItems = new ArrayList(order.getOrderItems());
        Set orderedPhotos = new HashSet();
        for (Object orderItem1 : orderItems) {
            OrderItem orderItem = (OrderItem) orderItem1;
            orderedPhotos.add(orderItem.getPhoto());
        }
        _logger.debug("********************* UPLOAD IMAGE ****************************");
        if (imageServer == null || "".equals(imageServer))
        {
            getImageServer();
            if (imageServer == null || "".equals(imageServer))
            {
                throw new Exception("ImageServer not set");
            }
        }
        MultipartPostMethod filePost = null;
        try
        {

            for (Object orderedPhoto : orderedPhotos)
            {
                // todo: check for zero ordered photos
                Photo photo = (Photo) orderedPhoto;
                File imageFile = photo.getFile();

                _logger.debug("imageFile.getAbsolutePath() = " + imageFile.getAbsolutePath());
                _logger.debug("imageFile.isFile() = " + imageFile.isFile());
                _logger.debug("imageFile.length() = " + imageFile.length());

                /*the return code from the imageServer*/
                String urlParams = "cli+-+" + sessionId + "+1+-+-+-+-+yes+" + oipsEncoder(photo.getDisplayTitle()) + ".xup";
                URL targetURL;
                targetURL = new URL("http", imageServer, urlParams);
                _logger.debug("targetURL = " + targetURL.toExternalForm());

                filePost = new MultipartPostMethod(targetURL.toExternalForm());
                _logger.debug("Uploading " + imageFile.getName() + " to " + targetURL);

                filePost.addParameter(imageFile.getName(), imageFile);
                HttpClient client = new HttpClient();
                client.setConnectionTimeout(_CONNECTION_TIMEOUT_IN_MILLISECONDS);
                int status = client.executeMethod(filePost);
                if (status == HttpStatus.SC_OK) {
                    _logger.info("Upload complete, response=" + filePost.getResponseBodyAsString());
                } else {
                    _logger.error("Upload failed, response=" + HttpStatus.getStatusText(status));
                }
            }
        } catch (IOException ex)
        {
            _logger.error("Error: " + ex.getMessage(), ex);
            throw ex;
        } finally
        {
            try
            {
                filePost.releaseConnection();
            } catch (NullPointerException e)
            {
                _logger.info("JDomPhotoOrder.uploadImage null pointer exception", e);
            }
        }
    }

    /**
     * Add  order items for this order. can be called many times
     * request must be POST method<br/>
     * calc =1: return a product summary<br/>
     * payid = 4: Invoice (Switzerland only, probably !!!
     * opt = 0: no correction; opt = 1 : automatic correction (default)
     *
     * @throws java.io.IOException
     * @throws ch.unartig.u_core.exceptions.UnartigException
     *
     * @throws org.jdom.JDOMException
     */
    private void addOrderItems() throws IOException, JDOMException, UnartigException
    {
        HttpClient httpClient = new HttpClient();
        String command = "AddOrderItems.cpl";
        _logger.debug("********************* Add Order Items ****************************");

        List orderItems = new ArrayList(order.getOrderItems());
        String url = oipsServer.toExternalForm() + command;
        _logger.debug("url = " + url);
        PostMethod post = new PostMethod(url);
        // initial params:
        NameValuePair[] data = {new NameValuePair("sid", sessionId), new NameValuePair("orderid", oipsOrderId), new NameValuePair("count", String.valueOf(orderItems.size())), new NameValuePair("payid", payId), new NameValuePair("calc", "1")};
        post.setRequestBody(data);
        // additional parameter for every ordered image:
        for (int i = 0; i < orderItems.size(); i++)
        {
            // may produce null pointer exception:
            OrderItem orderItem = (OrderItem) orderItems.get(i);
            _logger.info("ordering image : " + orderItem.getPhoto().getFilename());
            /*used to number the arguments*/
            int imageCount = (i + 1);
            if (!orderItem.getProduct().isDigitalProduct())
            { // print product; stanard treatment
                _logger.info("non-digital product");
                _logger.info("using color-correction ('1' equals true)? : " + Registry.getOipsColorcorrection());
                post.addParameter("pid" + imageCount, isDemoOrder() ? _OIPS_PID_DEMO : OipsPidMapper.getInstance().getMappedProductId(orderItem.getProduct()));
                post.addParameter("qty" + imageCount, String.valueOf(orderItem.getQuantity()));
                post.addParameter("img" + imageCount, oipsEncoder(orderItem.getPhoto().getDisplayTitle()));
            } else if (orderItem.getProduct().isDigitalProduct())
            {
                _logger.info("digital product!");
                /*we can use the field oipsPID; it will always be the same pid though*/
                post.addParameter("pid" + imageCount, isDemoOrder() ? _OIPS_PID_DEMO : OipsPidMapper.getInstance().getMappedProductId(orderItem.getProduct()));
                int quantity = getDigitalProductPrice(orderItem.getProduct());
                _logger.info("Using quantity ["+quantity+"] to 'construct'  colorplaza price.");
                post.addParameter("qty" + imageCount, String.valueOf(quantity));
                post.addParameter("img" + imageCount, oipsEncoder(orderItem.getPhoto().getDisplayTitle()));
            } else
            {
                throw new UnartigInvalidArgument("unknown product category!!");
            }
            post.addParameter("opt" + imageCount, Registry.getOipsColorcorrection());

        }

        InputStream in = null;
        int responseCode;
        try
        {
            // execute method and handle any error responses.
            // increase standard timeout of 5000 ms
            httpClient.setConnectionTimeout(_CONN_TIMEOUT_MS);
            responseCode = httpClient.executeMethod(post);
            _logger.debug("responseCode = " + responseCode);
            in = post.getResponseBodyAsStream();
            if (in == null)
            {
                _logger.error("Response Stream == null ; returning");
                return;
            }
        } catch (IOException e)
        {
            _logger.error("while adding items : ", e);
        }
        // handle response.
//      -----------------------


        Element rootElement = XmlHelper.getJdomRootElement(in);
        String status = rootElement.getChild("Status").getTextTrim();

        _logger.info("status = " + status);
        switch (Integer.parseInt(status))
        {
            case 0:
                //do something
                ;
            case 1:
                ;
        }
    }

    /**
     * Return the price of a digital prodoct as it is stored in the product db
     * <p> the price will be in minor units. Example:
     * <p> Digital Negative costs 20 CHF.
     * <p> return value will be 2000
     * <p/> One 'digital' Colorplaza-unit equals 0.01 SFr.
     *
     * @param product the product form the orderItem
     * @return the price in minor units
     * @throws ch.unartig.u_core.exceptions.UnartigInvalidArgument
     *          if no the customers country is not valid for this transaction
     */
    private int getDigitalProductPrice(Product product) throws UnartigInvalidArgument
    {
        String country = order.getCustomer().getCountry();
        // CHF prices
        if (Registry._SWITZERLAND_COUNTRY_CODE.equals(country))
        {
            double priceChf = product.getPrice().getPriceCHF().doubleValue() * 100;
            return safeDoubleToIntConversion(priceChf);
        }
        // EURO prices:
        else if (Registry._GERMANY_COUNTRY_CODE.equals(country) || Registry._AUSTRIA_COUNTRY_CODE.equals(country))
        {
            double priceEur = product.getPrice().getPriceEUR().doubleValue() * 100;
            return safeDoubleToIntConversion(priceEur);
        } else
        {
            throw new UnartigInvalidArgument("Country not available for digital product");
        }
    }

    /**
     * Helper method that makes sure that the calculated quantity really reflects the price
     * @param doubleValue The double value to convert
     * @return an integer , but only if the conversion was safe.
     */
    private int safeDoubleToIntConversion(double doubleValue)
    {
        int intValue = (int) doubleValue;
        Double doubleFromInt = (double) intValue;
        if (!doubleFromInt.equals(doubleValue))
        {
            // there's a problem
            _logger.error("Conversion error!!!");
            throw new RuntimeException("Converstion Error!!!! double to int ! Check the prices");
        }
        return intValue;
    }


    /**
     *
     * @param inString
     * @return
     * @throws UnartigException
     */
    private static String oipsEncoder(String inString) throws UnartigException
    {
        try
        {
            return URLEncoder.encode(inString, "utf-8").replace('+', '_').replace('-', '_').replace(' ', '_');
        } catch (UnsupportedEncodingException e)
        {
            throw new UnartigException("can not encode oips image id : ", e);
        }
    }


    /**
     * Create a new customer on the oips system<br>
     * Receive username and password to be stored on the unartig customer db
     *
     * @throws Exception
     */
    private void registerCustomerWithOips() throws Exception
    {
        _logger.info("********************* NEW CUSTOMER ****************************");
        String userPassword;
        CustomerDAO cDao = new CustomerDAO();
        Map parameters = new HashMap();
        parameters.put("sid", sessionId);
        parameters.put("first", unartigCustomer.getFirstName());
        parameters.put("last", unartigCustomer.getLastName());
        parameters.put("addr1", unartigCustomer.getAddr1());
        if (unartigCustomer.getAddr2() != null)
        {
            parameters.put("addr2", unartigCustomer.getAddr2());
        }
        parameters.put("city", unartigCustomer.getCity());
        parameters.put("zip", unartigCustomer.getZipCode());
        parameters.put("cntry", unartigCustomer.getCountry());
        parameters.put("email", unartigCustomer.getEmail());
        parameters.put("remember", "Y");
        // No additional emails from colorplaza ?
        parameters.put("noemail", "Y");
        // gender: 1 male, 0 female
        parameters.put("gender", "f".equals(unartigCustomer.getGender()) ? "0" : "1");


        String command = "NewCustomer.cpl";
        String url = null;
        try
        {
            url = HttpUtil.composeUrlFromParameters(parameters, oipsServer.toExternalForm(), command);
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            _logger.error("unknown error",e);
        }

        String oipsHttpsUrl = url.replaceFirst("http", "https");


        _logger.debug("oipsHttpsUrl = " + oipsHttpsUrl);
        Element rootElement = XmlHelper.getJdomRootElement(oipsHttpsUrl);

        String status = rootElement.getChild("Status").getTextTrim();
        _logger.info("status = " + status);
        switch (Integer.parseInt(status))
        {
            case 0:
                username = rootElement.getChild("Username").getTextTrim();
                _logger.info("username = " + username);
                userPassword = rootElement.getChild("Userpw").getTextTrim();
                _logger.info("userpw = " + userPassword);
                unartigCustomer.setOipsUsername(username);
                unartigCustomer.setOipsPassword(userPassword);
                cDao.saveOrUpdate(unartigCustomer);
                break;
            case 24:
                _logger.error("COPLA ORDER ERROR CODE 24: customer does not belong to the chosen service");
                // todo handle case 24: customer does not belong to the chosen service
                break;
        }
    }

    /**
     * if the unartig unartigCustomer does have an oips login password load the unartigCustomer;
     *
     * @return true if unartigCustomer loads successfully or else false
     * @throws Exception
     */
    private boolean getCustomerFromOips() throws Exception
    {
        if (unartigCustomer.getOipsPassword() != null && unartigCustomer.getOipsUsername() != null)
        {
            return getCustomer();
        } else
        {
            return false;
        }
    }

    /**
     * get the customer from the oips interface
     *
     * @return true if operation was successful or false if it failed
     * @throws Exception
     */
    private boolean getCustomer() throws Exception
    {
        _logger.debug("********************* GET CUSTOMER ****************************");
        Map parameters = new HashMap();
        parameters.put("sid", sessionId);
        parameters.put("username", unartigCustomer.getOipsUsername());
        parameters.put("userpw", unartigCustomer.getOipsPassword());

        String command = "GetCustomer.cpl";
        String url;
        url = HttpUtil.composeUrlFromParameters(parameters, oipsServer.toExternalForm(), command);
        String oipsHttpsUrl = url.replaceFirst("http", "https");

        _logger.debug("url = " + oipsHttpsUrl);
        Element rootElement = XmlHelper.getJdomRootElement(oipsHttpsUrl);
        String status = rootElement.getChild("Status").getTextTrim();

        _logger.debug("status = " + status);
        switch (Integer.parseInt(status))
        {
            case 0: // ok
                // todo: sanity check for the returned customer ???
                String firstName = rootElement.getChild("First").getTextTrim();
                _logger.debug("first name  = " + firstName);
                username = unartigCustomer.getOipsUsername();
                return true;
            case 1: // problem
                return false;
        }
        return false;
    }

    /**
     * start the order and set the orderid needed for building the order
     *
     * @throws Exception
     */
    private void startOrder() throws Exception
    {
        _logger.debug("********************* START NEW ORDER ****************************");
        if (username == null)
        {
            throw new UnartigException("Can not start order without valid username. username = null");
        }
        Map parameters = new HashMap();
        parameters.put("sid", sessionId);
        parameters.put("username", username);

        String command = "StartOrder.cpl";
        String url;
        url = HttpUtil.composeUrlFromParameters(parameters, oipsServer.toExternalForm(), command);


        _logger.debug("url = " + url);
        Element rootElement = XmlHelper.getJdomRootElement(url);
        String status = rootElement.getChild("Status").getTextTrim();

        _logger.info("status = " + status);
        switch (Integer.parseInt(status))
        {
            case 0:
                oipsOrderId = rootElement.getChild("OrderId").getTextTrim();
                _logger.info("oipsOrderId = " + oipsOrderId);
                break;
            case 1:
                ;
        }
    }

    private void demoSessionLogin() throws Exception
    {
        _logger.info("********************* DEMO DEMO DEMO ****************************");
        _logger.debug("********************* DEMO DEMO DEMO ****************************");
        _logger.info("********************* Session Login ****************************");
        _logger.debug("********************* DEMO DEMO DEMO  ****************************");
        _logger.debug("********************* DEMO DEMO DEMO ****************************");
        String oipsUrl = oipsServer.toExternalForm();
        String oipsHttpsUrl = oipsUrl.replaceFirst("http", "https");
        String sessionLoginUrl = oipsHttpsUrl + "SessionLogin.cpl?login=" + _OIPS_LOGIN_DEMO + "&pw=" + _OIPS_PASSWORD_DEMO + "&swid=" + _SWID_DEMO + "&swversion=" + _SWVERSION_DEMO + "&langid=" + _LANG_ID + "&oemid=" + _OEMID_DEMO + "&seid=" + serviceId;
        _logger.debug("sessionLoginUrl = " + sessionLoginUrl);
        Element sessionLogin = XmlHelper.getJdomRootElement(sessionLoginUrl);

        sessionId = sessionLogin.getChild("Id").getTextTrim();
        imageServer = sessionLogin.getChild("ImageServer").getTextTrim();
        _logger.debug("sessionId = " + sessionId);
    }


    /**
     * log in to a session using https and the provided username and password
     * <p>the seid, the serviceId, is given according to the country that this order is for; the seid is set in the constructor</p>
     * <p></p>
     * <p></p>
     *
     * @throws Exception
     */
    private void sessionLogin() throws Exception
    {
        _logger.info("********************* Session Login ****************************");
        String oipsUrl = oipsServer.toExternalForm();
        String oipsHttpsUrl = oipsUrl.replaceFirst("http", "https");
        String sessionLoginUrl = oipsHttpsUrl + "SessionLogin.cpl?login=" + _OIPS_LOGIN + "&pw=" + _OIPS_PASSWORD + "&swid=" + _SWID + "&swversion=" + _SWVERSION + "&langid=" + _LANG_ID + "&oemid=" + _OEMID + "&seid=" + serviceId;
        _logger.debug("sessionLoginUrl = " + sessionLoginUrl);
        Element sessionLogin = XmlHelper.getJdomRootElement(sessionLoginUrl);

        sessionId = sessionLogin.getChild("Id").getTextTrim();
        imageServer = sessionLogin.getChild("ImageServer").getTextTrim();
        _logger.debug("sessionId = " + sessionId);
    }


    private URL getOipsServer()
    {
        _logger.debug("********************* GetOipsServer ****************************");

        URL retVal = null;
        InputStream is;
        Document doc;
        Element getServer = null;
        try
        {
            is = HttpUtil.getHttpResponseAsStream(coplaServerRoot + "/" + "GetServer.cpl");
            doc = new SAXBuilder().build(is);
            getServer = doc.getRootElement();
        } catch (Exception e)
        {
            _logger.error("ERROR in getting OIPS Server");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // assert getServer != null;
        String status = getServer.getChild("Status").getTextTrim();

        _logger.debug("status = " + status);
        String httpServer = getServer.getChild("HTTPServer").getTextTrim();
        String httpPath = getServer.getChild("HTTPPath").getTextTrim();

        try
        {
            retVal = new URL("http", httpServer, httpPath);
        } catch (MalformedURLException e)
        {
            _logger.error("JDomPhotoOrder.getOipsServer WRONG URL",e);
        }
        oipsServer = retVal;
        return retVal;
    }


    /**
     * setter for the unartig customer record (not the copla customer!)
     *
     * @return unartigCustomer
     */
    public Customer getUnartigCustomer()
    {
        return unartigCustomer;
    }

    /**
     * setter for the unartig customer record
     *
     * @param unartigCustomer set the unartig custonmer object, not the copla customer
     */
    public void setUnartigCustomer(Customer unartigCustomer)
    {
        this.unartigCustomer = unartigCustomer;
    }

    public Order getOrder()
    {
        return order;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }


    public boolean isDemoOrder()
    {
        return demoOrder;
    }

    public void setDemoOrder(boolean demoOrder)
    {
        this.demoOrder = demoOrder;
    }

}

package ch.unartig.studioserver.ordermodules;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.beans.CheckOutForm;
import ch.unartig.studioserver.beans.ShoppingCart;
import ch.unartig.studioserver.businesslogic.CreditCardDetails;
import ch.unartig.studioserver.businesslogic.PhotoOrderIF;
import ch.unartig.studioserver.model.Customer;
import ch.unartig.studioserver.model.Order;
import ch.unartig.studioserver.persistence.DAOs.OrderDAO;
import ch.unartig.util.HttpUtil;
import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.exceptions.PayPalException;
import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.NVPCallerServices;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by IntelliJ IDEA.
 * User: ajosef
 * Date: 12.02.11
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class PaypalPaymentOrder implements PhotoOrderIF {
    static Logger _logger = Logger.getLogger("PaypalPaymentOrder");
    // Return value for a successful transaction in paypal:
    private static final String PAYPAL_ACK_SUCCESS = "Success";

    private ShoppingCart shoppingCart;
    private boolean demoOrderMode;
    private boolean simulateOrderOnly;
    private String customerIpAddress;
    private Order order;

    /**
     *
     * @param profile
     * @throws PayPalException
     */
    public static void getPaypalLiveProfile(APIProfile profile) throws PayPalException {
        // todo: not so good to store that here in plain text ...
        profile.setAPIUsername("aj_api1.unartig.ch");
        profile.setAPIPassword("7U686HK6MN27GRX5");
        profile.setSignature("ATNLM76uyV2QXdOt2q18SNHXBqlGADXW4bOBVVLzgepvg0dQU2vV6igB");
        profile.setEnvironment("live");
    }

    /**
     *
     * @param profile
     * @throws PayPalException
     */
    public static void getPaypalSanboxProfile(APIProfile profile) throws PayPalException {
        profile.setAPIUsername("seller_1295092736_biz_api1.unartig.ch");
        profile.setAPIPassword("1295092747");
        profile.setSignature("AS09Xn3myqiJJijuHSqjKPSskBhVATzb4fUv-kxZGWqkAF5Dxcx3k3Jt");
        profile.setEnvironment("sandbox");
    }


    public PaypalPaymentOrder(ShoppingCart shoppingCart, boolean demoOrderMode, boolean simulateOrderOnly, String customerIpAddress, Order order) {

        this.shoppingCart = shoppingCart;
        this.demoOrderMode = demoOrderMode;
        this.simulateOrderOnly = simulateOrderOnly;
        this.customerIpAddress = customerIpAddress;
        this.order = order;
        if (simulateOrderOnly) {
            _logger.info("*******************************************************************");
            _logger.info("******* ONLY Simulation of order ********");
            _logger.info("******* no order took place !!! ********");
            _logger.info("*******************************************************************");
        } else {
            _logger.info("Is DEMO Order ? " + demoOrderMode);
        }

    }

    /**
     * First call to start paypal express checkout workflow.
     * @param request
     * @param coForm
     * @param shoppingCart
     * @return The token that is returned from the paypal service
     */
    public static String callSetupPaypalExpressCheckout(HttpServletRequest request, CheckOutForm coForm, ShoppingCart shoppingCart) {
        /* Paypal Code*/

        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();

        try {
            NVPCallerServices caller = new NVPCallerServices();

            APIProfile profile = ProfileFactory.createSignatureAPIProfile();
            /*
                WARNING: Do not embed plaintext credentials in your application code.
                Doing so is insecure and against best practices.
                Your API credentials must be handled securely. Please consider
                encrypting them for use in any production environment, and ensure
                that only authorized individuals may view or modify them.
                */

            // Set up your API credentials, PayPal end point, API operation and version.
            if (Registry.isDemoOrderMode()) {
                getPaypalSanboxProfile(profile);
            } else
            {
                getPaypalLiveProfile(profile);

            }
            profile.setSubject("");
            caller.setAPIProfile(profile);
            // todo use properties
            encoder.add("VERSION", "64.0");
            encoder.add("METHOD", "SetExpressCheckout");

            // Add request-specific fields to the request string.
            // todo check token on this page. only continue with identical token
            String returnURL = HttpUtil.getBaseUrl(request, false) + "/coWizard_page4.html";
//            String returnURL = "http://www.unartig.ch/coWizard_page4.html";
            encoder.add("RETURNURL", returnURL);
            String cancelURL="http://www.unartig.ch";
            encoder.add("CANCELURL", cancelURL);
            encoder.add("SOLUTIONTYPE", "Sole");
            encoder.add("NOSHIPPING", "1"); // don's show shipping address in paypal dialog
            encoder.add("LOCALECODE", "CH"); // todo set locale code according to session locale
            encoder.add("EMAIL", coForm.getEmail());
            encoder.add("LANDINGPAGE", "Billing"); //
            encoder.add("BRANDNAME", "unartig Studio"); //
            encoder.add("PAYMENTREQUEST_0_AMT", Double.toString(shoppingCart.getTotalPhotosCHF())); // how does the string look like?
            encoder.add("PAYMENTREQUEST_0_PAYMENTACTION", "Sale");
            encoder.add("PAYMENTREQUEST_0_CURRENCYCODE", "CHF"); // Todo check currency codes ...
            encoder.add("PAYMENTREQUEST_0_SHIPTOZIP", coForm.getZipCode().toString());
            encoder.add("PAYMENTREQUEST_0_SHIPTOCOUNTRYCODE", "CH"); // todo set correct country code from shopping cart
            encoder.add("PAYMENTREQUEST_0_SHIPTOCITY", coForm.getCity());
            encoder.add("PAYMENTREQUEST_0_SHIPTONAME", coForm.getFirstName() + " " + coForm.getLastName());
            encoder.add("PAYMENTREQUEST_0_SHIPTOSTREET", coForm.getAddr1());
            encoder.add("PAYMENTREQUEST_0_SHIPTOSTREET2", coForm.getAddr2());
            encoder.add("PAYMENTREQUEST_0_SHIPTOPHONENUM", "055 555 5555");

            // Execute the API operation and obtain the response.
            String nvpRequest = encoder.encode();
            String nvpResponse = caller.call(nvpRequest);

            decoder.decode(nvpResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // logging the response ...
        for (Object key : decoder.getMap().keySet() ) {
            _logger.info(key.toString() + " = " + decoder.getMap().get(key).toString());
        }


        String successMsg = decoder.get("ACK");

        // Store the token: It's needed again in the shopping cart logic storeandexecute order ...
        return decoder.get("TOKEN");
    }

    public void setUnartigCustomer(Customer customer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public boolean processOrder() throws UnartigException {

        callPaypalEcGetDetails();

        callPaypalEcDoPayment();

        return true;
    }

    private boolean callPaypalEcDoPayment() {

        /* Paypal Code*/

        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();

        try {
            NVPCallerServices caller = new NVPCallerServices();

            APIProfile profile = ProfileFactory.createSignatureAPIProfile();
            /*
                WARNING: Do not embed plaintext credentials in your application code.
                Doing so is insecure and against best practices.
                Your API credentials must be handled securely. Please consider
                encrypting them for use in any production environment, and ensure
                that only authorized individuals may view or modify them.
                */

            // Set up your API credentials, PayPal end point, API operation and version.
            if (Registry.isDemoOrderMode()) {
                _logger.debug("Paypal Demo Order ... DoExpressCheckoutPayment");
                getPaypalSanboxProfile(profile);
            } else
            {
                getPaypalLiveProfile(profile);

            }
            profile.setSubject("");
            caller.setAPIProfile(profile);
            // todo use properties
            encoder.add("VERSION", "64.0");
            encoder.add("METHOD", "DoExpressCheckoutPayment");

            // Add request-specific fields to the request string.
            // todo check token on this page. only continue with identical token
            // todo : configure paypal to use no shipping address
            encoder.add("TOKEN", shoppingCart.getPaypalToken());
            // IP-Address
//            encoder.add("??????????????", customerIpAddress);
            encoder.add("PAYERID", shoppingCart.getPaypalPayerId());
            encoder.add("PAYMENTREQUEST_0_AMT", Double.toString(shoppingCart.getTotalPhotosCHF())); // how does the string look like? is it correct?
            encoder.add("PAYMENTREQUEST_0_PAYMENTACTION", "Sale");
            encoder.add("PAYMENTREQUEST_0_CURRENCYCODE", "CHF"); // Todo check currency codes ...

            // Execute the API operation and obtain the response.
            String nvpRequest = encoder.encode();
            String nvpResponse = caller.call(nvpRequest);

            decoder.decode(nvpResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
            _logger.error(ex);
        }

        for (Object key : decoder.getMap().keySet()) {
            _logger.debug(key.toString() + " = " + decoder.getMap().get(key).toString());
        }


        String successMsg = decoder.get("ACK");
        String token = decoder.get("TOKEN");
        String transactionId = decoder.get("PAYMENTINFO_0_TRANSACTIONID");

        // todo read status (and write to order object?)

        if (PAYPAL_ACK_SUCCESS.equals(successMsg)) {
            OrderDAO oDao = new OrderDAO();
            order.confirmUpload(transactionId);
            try {
                _logger.info("Trying to save order for transaction: " + transactionId);
                oDao.save(order);
                _logger.info("Order persisted for transaction: " + transactionId);
            } catch (UAPersistenceException e) {
                _logger.error(e);            }
        }


        return false;
    }



    /**
     * get the paypal express checkout details (payerid needed for completing transaction)
     * @return
     */
    private boolean callPaypalEcGetDetails()
    {
        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();

        try {
            NVPCallerServices caller = new NVPCallerServices();

            APIProfile profile = ProfileFactory.createSignatureAPIProfile();
            /*
                WARNING: Do not embed plaintext credentials in your application code.
                Doing so is insecure and against best practices.
                Your API credentials must be handled securely. Please consider
                encrypting them for use in any production environment, and ensure
                that only authorized individuals may view or modify them.
                */

            // Set up your API credentials, PayPal end point, API operation and version.
            if (Registry.isDemoOrderMode()) {
                getPaypalSanboxProfile(profile);
                _logger.debug("Paypal demo Order");
            } else
            {
                // todo: insert values
                // todo: not so good to store that here in plain text ...
                getPaypalLiveProfile(profile);
            }
            profile.setSubject("");
            caller.setAPIProfile(profile);
            // todo use properties
            encoder.add("VERSION", "64.0");
            encoder.add("METHOD", "GetExpressCheckoutDetails");

            // Add request-specific fields to the request string.
            // todo check token on this page. only continue with identical token
            encoder.add("TOKEN", shoppingCart.getPaypalToken());

            // Execute the API operation and obtain the response.
            String nvpRequest = encoder.encode();
            String nvpResponse = caller.call(nvpRequest);

            decoder.decode(nvpResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // todo remove debug output...
        for (Object key : decoder.getMap().keySet()) {
            System.out.println(key.toString() + " = " + decoder.getMap().get(key).toString());
        }


        // todo check ACK status:
        String successMsg = decoder.get("ACK");
        String token = decoder.get("TOKEN");
        String payerId = decoder.get("PAYERID");
        shoppingCart.setPaypalPayerId(payerId);


        return false;
    }



    public void setCreditCardDetails(CreditCardDetails ccDetail) {
    }

    public void setErrorCode(int errorCode) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getErrorCode() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isDemoOrderMode() {
        return demoOrderMode;
    }

    public void setDemoOrderMode(boolean demoOrderMode) {
        this.demoOrderMode = demoOrderMode;
    }

    public boolean isSimulateOrderOnly() {
        return simulateOrderOnly;
    }

    public void setSimulateOrderOnly(boolean simulateOrderOnly) {
        this.simulateOrderOnly = simulateOrderOnly;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

}


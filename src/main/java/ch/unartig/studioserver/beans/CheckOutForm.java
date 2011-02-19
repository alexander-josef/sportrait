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
 * Revision 1.2  2007/05/05 15:00:16  alex
 * errorhandling no eventcategory
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.18  2006/11/14 13:45:20  alex
 * fixing shopping cart when product is reset to default
 *
 * Revision 1.17  2006/11/12 16:43:22  alex
 * small fixes shoppingcart
 *
 * Revision 1.16  2006/11/05 22:10:02  alex
 * credit card order works
 *
 * Revision 1.15  2006/11/03 13:15:19  alex
 * some changes
 *
 * Revision 1.14  2006/11/01 10:14:45  alex
 * cc interface check in, transactions work
 *
 * Revision 1.13  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.12  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.11  2006/01/11 15:10:47  alex
 * agbs akzeptieren
 *
 * Revision 1.10  2006/01/11 13:35:37  alex
 * bug 856 backend
 *
 * Revision 1.9  2005/12/27 15:22:22  alex
 * payment method shows now bill not credit card
 *
 * Revision 1.8  2005/11/21 17:52:58  alex
 * no account action , photo order
 *
 * Revision 1.7  2005/11/17 13:36:06  alex
 * check out overview works
 *
 * Revision 1.6  2005/11/16 17:26:19  alex
 * validator enhanced
 *
 * Revision 1.5  2005/11/16 14:26:49  alex
 * validator works for email, new library
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
package ch.unartig.studioserver.beans;

import ch.unartig.exceptions.UnartigInvalidArgument;
import ch.unartig.studioserver.businesslogic.CreditCardDetails;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Form for storing check-out relevant information
 *
 * @noinspection BooleanPropertyMissingResetInspection
 */
public class CheckOutForm extends ValidatorForm implements Serializable
{
    private String firstName;
    private String lastName;
    private String addr1;
    private String addr2;
    private Integer zipCode;
    private String city;
    private String country;
    private String gender;

    private String shippingBillingIdentical;
    private boolean paymentMethodInvoice;
    /**
     * either "creditCard" or "invoice"
     */
    private String paymentMethod;
    private String creditCardType;
    private boolean acceptTermsCondition;

    private String shippingFirstName;
    private String shippingLastName;
    private String shippingAddr1;
    private String shippingAddr2;
    private Integer shippingZipCode;
    private String shippingCity;
    private String shippingCountry;
    private String email;
    private String emailConfirmation;
    private String password;

    private String creditCardNumber;
    private String creditCardHolderName;
    private String creditCardStreet;
    private String creditCardZIP;
    private String creditCardCity;
    private String creditCardExpiryYear;
    private String creditCardExpiryMonth;

    private boolean newCustomer;
    private String downloadLink;


    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest)
    {
        ActionErrors errors  = super.validate(actionMapping,httpServletRequest);

Iterator errorsIter = errors.get();

        while (errorsIter.hasNext())
        {
            ActionMessage actionMessage = (ActionMessage) errorsIter.next();
            System.out.println("actionMessage = " + actionMessage);
            System.out.println("actionMessage.isResource() = " + actionMessage.isResource());
        }

        errors = super.validate(actionMapping,httpServletRequest);
        return errors;
    }

    
    public void clear()
    {
        // implement if needed
    }


    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getAddr1()
    {
        return addr1;
    }

    public void setAddr1(String addr1)
    {
        this.addr1 = addr1;
    }

    public String getAddr2()
    {
        return addr2;
    }

    public void setAddr2(String addr2)
    {
        this.addr2 = addr2;
    }

    public Integer getZipCode()
    {
        return zipCode;
    }

    public void setZipCode(Integer zipCode)
    {
        this.zipCode = zipCode;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getShippingBillingIdentical()
    {
        return shippingBillingIdentical;
    }

    public void setShippingBillingIdentical(String shippingBillingIdentical)
    {
        this.shippingBillingIdentical = shippingBillingIdentical;
    }

    public String getShippingFirstName()
    {
        return shippingFirstName;
    }

    public void setShippingFirstName(String shippingFirstName)
    {
        this.shippingFirstName = shippingFirstName;
    }

    public String getShippingLastName()
    {
        return shippingLastName;
    }

    public void setShippingLastName(String shippingLastName)
    {
        this.shippingLastName = shippingLastName;
    }

    public String getShippingAddr1()
    {
        return shippingAddr1;
    }

    public void setShippingAddr1(String shippingAddr1)
    {
        this.shippingAddr1 = shippingAddr1;
    }

    public String getShippingAddr2()
    {
        return shippingAddr2;
    }

    public void setShippingAddr2(String shippingAddr2)
    {
        this.shippingAddr2 = shippingAddr2;
    }

    public Integer getShippingZipCode()
    {
        return shippingZipCode;
    }

    public void setShippingZipCode(Integer shippingZipCode)
    {
        this.shippingZipCode = shippingZipCode;
    }

    public String getShippingCity()
    {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity)
    {
        this.shippingCity = shippingCity;
    }

    public String getShippingCountry()
    {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry)
    {
        this.shippingCountry = shippingCountry;
    }

    /**
     * 
     * @return
     */
    public boolean isPaymentMethodInvoice()
    {
        return "invoice".equals(paymentMethod);
    }

    public void setPaymentMethodInvoice(boolean paymentMethodInvoice)
    {
        this.paymentMethodInvoice = paymentMethodInvoice;
    }

    public boolean isAcceptTermsCondition()
    {
        return acceptTermsCondition;
    }

    public void setAcceptTermsCondition(boolean acceptTermsCondition)
    {
        this.acceptTermsCondition = acceptTermsCondition;
    }

    /**
     * convenience method ..
     *
     * @return true if customer choose credit card, false otherwise
     */
    public boolean isPaymentMethodCreditCard()
    {
        return "creditCard".equals(paymentMethod);
    }

    /**
     * Reset is called before the values are set. paymentmethodinvoice should not be reset to false for each page in the wizard ....
     *
     * @param actionMapping mapping
     * @param httpServletRequest request
     */
    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest)
    {
        /*do not reset payment method ... */
//        paymentMethodInvoice = false;
        /*reset to new customer*/
        acceptTermsCondition = false;
        newCustomer = true;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isNewCustomer()
    {
        return newCustomer;
    }

    public void setNewCustomer(boolean newCustomer)
    {
        this.newCustomer = newCustomer;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }


    public String getCreditCardNumber()
    {
        return creditCardNumber;
    }

    /**
     * for showing it on the confirmation page, use an obfusctaed cc number
     * @return
     */
    public String getObfuscatedCreditCardNumber()
    {
        return "***-"+creditCardNumber.substring(creditCardNumber.length()-4,creditCardNumber.length());
    }

    public void setCreditCardNumber(String creditCardNumber)
    {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * return the Credit Card holder name if one was filled out in the payment form, or the firstname+lastname if none was filled out
     * @return holdername of firstname+lastname
     */
    public String getCreditCardHolderName()
    {
        if (creditCardHolderName == null || "".equals(creditCardHolderName))
        {
            if (getFirstName()!=null && !"".equals(getFirstName()) && getLastName()!=null && !"".equals(getLastName()))
            {
                creditCardHolderName = getFirstName()+' '+getLastName();
            }
            else
            {
                creditCardHolderName = "";
            }
        }
        return creditCardHolderName;

    }

    public void setCreditCardHolderName(String creditCardHolderName)
    {
        this.creditCardHolderName = creditCardHolderName;
    }

    public String getCreditCardStreet()
    {
        return creditCardStreet;
    }

    public void setCreditCardStreet(String creditCardStreet)
    {
        this.creditCardStreet = creditCardStreet;
    }

    public String getCreditCardZIP()
    {
        return creditCardZIP;
    }

    public void setCreditCardZIP(String creditCardZIP)
    {
        this.creditCardZIP = creditCardZIP;
    }

    public String getCreditCardCity()
    {
        return creditCardCity;
    }

    public void setCreditCardCity(String creditCardCity)
    {
        this.creditCardCity = creditCardCity;
    }


    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }


    public String getCreditCardType()
    {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType)
    {
        this.creditCardType = creditCardType;
    }


    /**
     * TODO describe: what code? why is it needed?
     * todo: if this is needed use first digit of card to decide which cc schema
     * @return
     * @throws UnartigInvalidArgument
     */
    public int getCreditCardTypeCode() throws UnartigInvalidArgument {
        if ((getCreditCardNumber() == null) || getCreditCardNumber().isEmpty()) {
            return 0;
        }
        if ("masterCard".equals(getCreditCardType())) {
            return CreditCardDetails._CC_TYPE_CODE_MASTERCARD;
        } else if ("visa".equals(getCreditCardType())) {
            return CreditCardDetails._CC_TYPE_CODE_VISA;
        } else {
            throw new UnartigInvalidArgument("not a valid credit card type");
        }
    }


    public String getCreditCardExpiryYear()
    {
        return creditCardExpiryYear;
    }

    public void setCreditCardExpiryYear(String creditCardExpiryYear)
    {
        this.creditCardExpiryYear = creditCardExpiryYear;
    }

    public String getCreditCardExpiryMonth()
    {
        return creditCardExpiryMonth;
    }

    public void setCreditCardExpiryMonth(String creditCardExpiryMonth)
    {
        this.creditCardExpiryMonth = creditCardExpiryMonth;
    }


    public String toString()
    {
        return "CheckOutForm{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", addr1='" + addr1 + '\'' +
                ", addr2='" + addr2 + '\'' +
                ", zipCode=" + zipCode +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", gender='" + gender + '\'' +
                ", shippingBillingIdentical='" + shippingBillingIdentical + '\'' +
                ", paymentMethodInvoice=" + paymentMethodInvoice +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", creditCardType='" + creditCardType + '\'' +
                ", acceptTermsCondition=" + acceptTermsCondition +
                ", shippingFirstName='" + shippingFirstName + '\'' +
                ", shippingLastName='" + shippingLastName + '\'' +
                ", shippingAddr1='" + shippingAddr1 + '\'' +
                ", shippingAddr2='" + shippingAddr2 + '\'' +
                ", shippingZipCode=" + shippingZipCode +
                ", shippingCity='" + shippingCity + '\'' +
                ", shippingCountry='" + shippingCountry + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", creditCardNumber='" + getObfuscatedCreditCardNumber() + '\'' +
                ", creditCardHolderName='" + creditCardHolderName + '\'' +
                ", creditCardStreet='" + creditCardStreet + '\'' +
                ", creditCardZIP='" + creditCardZIP + '\'' +
                ", creditCardCity='" + creditCardCity + '\'' +
                ", creditCardExpiryYear='" + creditCardExpiryYear + '\'' +
                ", creditCardExpiryMonth='" + creditCardExpiryMonth + '\'' +
                ", newCustomer=" + newCustomer +
                '}';
    }

    public String getEmailConfirmation()
    {
        return emailConfirmation;
    }

    public void setEmailConfirmation(String emailConfirmation)
    {
        this.emailConfirmation = emailConfirmation;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDownloadLink() {
        return downloadLink;
    }
}

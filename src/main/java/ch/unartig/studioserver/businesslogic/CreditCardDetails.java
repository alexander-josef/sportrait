/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 25.10.2006$
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
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.3  2006/11/01 10:51:20  alex
 * cc interface check in, transactions work
 *
 * Revision 1.2  2006/11/01 10:14:45  alex
 * cc interface check in, transactions work
 *
 * Revision 1.1  2006/10/28 21:57:09  alex
 * reformat
 *
 ****************************************************************/
package ch.unartig.studioserver.businesslogic;

public class CreditCardDetails
{
    /**
     * type code for the credit card company; same type codes are used for payId with OIPS
     */
    public static final int _CC_TYPE_CODE_VISA = 1;
    public static final int _CC_TYPE_CODE_MASTERCARD = 2;


    private String creditCardHolderName;
    private int creditCardType;
    private String creditCardNumber;
    private Integer creditCardVerificationNumber;
    private Integer creditCardExpiryYear;
    private Integer creditCardExpiryMonth;

    /**
     * error code returned from unsuccesful transaction
     */
    private int creditCardPaymentErrorCode;

    /**
     * instanciate a new credit card detail
     * @param creditCardType Master or Visa card
     * @param creditCardNumber 12 digit cc number
     * @param creditCardVerificationNumber verification number of the back of the credit card
     * @param creditCardHolderName Name as known to the bank
     * @param creditCardExpiryYear Year in the format yyyy
     * @param creditCardExpiryMonth Month in the format mm (1-12)
     */
    public CreditCardDetails(int creditCardType, String creditCardNumber, Integer creditCardVerificationNumber, String creditCardHolderName, Integer creditCardExpiryYear,Integer creditCardExpiryMonth)
    {
        this.creditCardExpiryMonth = creditCardExpiryMonth;
        this.creditCardExpiryYear = creditCardExpiryYear;
        this.creditCardVerificationNumber = creditCardVerificationNumber;
        this.creditCardNumber = creditCardNumber;
        this.creditCardType = creditCardType;
        this.creditCardHolderName = creditCardHolderName;


    }



    public String getCreditCardHolderName()
    {
        return creditCardHolderName;
    }

    public void setCreditCardHolderName(String creditCardHolderName)
    {
        this.creditCardHolderName = creditCardHolderName;
    }

    public int getCreditCardType()
    {
        return creditCardType;
    }

    public void setCreditCardType(int creditCardType)
    {
        this.creditCardType = creditCardType;
    }

    public String getCreditCardNumber()
    {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber)
    {
        // after frontend validation, number should be correct according to checksum.
        this.creditCardNumber = creditCardNumber;
    }

    public Integer getCreditCardVerificationNumber()
    {
        return creditCardVerificationNumber;
    }

    public void setCreditCardVerificationNumber(Integer creditCardVerificationNumber)
    {
        this.creditCardVerificationNumber = creditCardVerificationNumber;
    }

    public Integer getCreditCardExpiryYear()
    {
        return creditCardExpiryYear;
    }

    public void setCreditCardExpiryYear(Integer creditCardExpiryYear)
    {
        this.creditCardExpiryYear = creditCardExpiryYear;
    }

    public Integer getCreditCardExpiryMonth()
    {
        return creditCardExpiryMonth;
    }

    public void setCreditCardExpiryMonth(Integer creditCardExpiryMonth)
    {
        this.creditCardExpiryMonth = creditCardExpiryMonth;
    }


    public int getCreditCardPaymentErrorCode()
    {
        return creditCardPaymentErrorCode;
    }

    public void setCreditCardPaymentErrorCode(int creditCardPaymentErrorCode)
    {
        this.creditCardPaymentErrorCode = creditCardPaymentErrorCode;
    }
}

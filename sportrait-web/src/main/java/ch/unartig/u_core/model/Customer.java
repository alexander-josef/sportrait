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
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.3  2006/08/25 23:27:58  alex
 * payment i18n
 *
 * Revision 1.2  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.1  2005/11/09 21:59:36  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 ****************************************************************/
package ch.unartig.u_core.model;

import ch.unartig.studioserver.beans.CheckOutForm;
import ch.unartig.u_core.model.GeneratedCustomer;

/**
 * Model class for the customer;
 */
public class Customer extends GeneratedCustomer
{

    /**
     * standard constructor
     */
    public Customer()
    {
    }

    /**
     * Constructor to create customer from check-out form
     * @param coForm
     */
    public Customer(CheckOutForm coForm)
    {
        this.setFirstName(coForm.getFirstName());
        this.setLastName(coForm.getLastName());
        this.setAddr1(coForm.getAddr1());
        this.setAddr2(coForm.getAddr2());
        this.setZipCode(coForm.getZipCode().toString());
        this.setCity(coForm.getCity());
        this.setCountry(coForm.getCountry());
        this.setState("");
        this.setEmail(coForm.getEmail());
        this.setGender("f".equals(coForm.getGender()) ? "f" : "m");
    }
}

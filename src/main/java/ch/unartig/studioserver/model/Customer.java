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
package ch.unartig.studioserver.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Model class for the customer;
 */
@Entity
@Table(name = "customers")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Customer implements java.io.Serializable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long customerId;

    private String firstName;
    private String lastName;
    private String title;
    private String addr1;
    private String addr2;
    private String zipCode;
    private String city;
    private String state;
    private String country;
    private String gender;
    private String oipsUsername;
    private String oipsPassword;
    private String email;
    private String phone;
    private String fax;
    private Boolean rememberMe;
    private Boolean noEmailFromCopla;
    private Boolean noUnartigAccount;

    /**
     * standard constructor
     */
    public Customer()
    {
    }


    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddr1() {
        return this.addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return this.addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getZipCode() {
        return this.zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOipsUsername() {
        return this.oipsUsername;
    }

    public void setOipsUsername(String oipsUsername) {
        this.oipsUsername = oipsUsername;
    }

    public String getOipsPassword() {
        return this.oipsPassword;
    }

    public void setOipsPassword(String oipsPassword) {
        this.oipsPassword = oipsPassword;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public Boolean getRememberMe() {
        return this.rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Boolean getNoEmailFromCopla() {
        return this.noEmailFromCopla;
    }

    public void setNoEmailFromCopla(Boolean noEmailFromCopla) {
        this.noEmailFromCopla = noEmailFromCopla;
    }

    public Boolean getNoUnartigAccount() {
        return this.noUnartigAccount;
    }

    public void setNoUnartigAccount(Boolean noUnartigAccount) {
        this.noUnartigAccount = noUnartigAccount;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("customerId").append("='").append(getCustomerId()).append("' ");
      buffer.append("firstName").append("='").append(getFirstName()).append("' ");
      buffer.append("lastName").append("='").append(getLastName()).append("' ");
      buffer.append("title").append("='").append(getTitle()).append("' ");
      buffer.append("addr1").append("='").append(getAddr1()).append("' ");
      buffer.append("addr2").append("='").append(getAddr2()).append("' ");
      buffer.append("zipCode").append("='").append(getZipCode()).append("' ");
      buffer.append("city").append("='").append(getCity()).append("' ");
      buffer.append("state").append("='").append(getState()).append("' ");
      buffer.append("country").append("='").append(getCountry()).append("' ");
      buffer.append("gender").append("='").append(getGender()).append("' ");
      buffer.append("oipsUsername").append("='").append(getOipsUsername()).append("' ");
      buffer.append("oipsPassword").append("='").append(getOipsPassword()).append("' ");
      buffer.append("email").append("='").append(getEmail()).append("' ");
      buffer.append("phone").append("='").append(getPhone()).append("' ");
      buffer.append("fax").append("='").append(getFax()).append("' ");
      buffer.append("rememberMe").append("='").append(getRememberMe()).append("' ");
      buffer.append("noEmailFromCopla").append("='").append(getNoEmailFromCopla()).append("' ");
      buffer.append("noUnartigAccount").append("='").append(getNoUnartigAccount()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

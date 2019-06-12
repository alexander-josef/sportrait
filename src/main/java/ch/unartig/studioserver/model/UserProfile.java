/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 14.03.2007$
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
 * Revision 1.2  2007/06/09 11:15:37  alex
 * photographer
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/15 21:45:27  alex
 * no more price segment
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Josef, unartig 2007
 * UserProfile represents an object for authenticated users; the persistent information stored in the table is queried by the DataSourceRealm authenticator
 */
@Entity
@Table(name = "userprofiles")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserProfile implements java.io.Serializable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long userProfileId;

    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String phone;
    private String phoneMobile;
    private String title;
    private String addr1;
    private String addr2;
    private String zipCode;
    private String city;
    private String state;
    private String country;
    private String gender;

    @OneToOne(mappedBy = "userProfile",fetch = FetchType.LAZY)
    // @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE) // don't cascade on the inverse mapping? leads to "a different object with the same identifier .." problem !!
    private Photographer photographer;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(name = "userprofiles2userroles",
            joinColumns = {@JoinColumn(name = "username") },
            inverseJoinColumns = { @JoinColumn(name = "rolename",referencedColumnName = "rolename") }
    )
    private Set<UserRole> roles = new HashSet<>(0);

    public UserProfile(UserRole role)
    {
        getRoles().add(role);
    }

    public UserProfile(Collection<UserRole> roles)
    {
        getRoles().addAll(roles);
    }


    /**
     *  default constructor; use Constructors with roles
     */
    public UserProfile()
    {
    }


    public void addPhotographer(Photographer eachPhotographer) {
        setPhotographer(eachPhotographer);
    }

    public Long getUserProfileId() {
        return this.userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneMobile() {
        return this.phoneMobile;
    }

    public void setPhoneMobile(String phoneMobile) {
        this.phoneMobile = phoneMobile;
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

    public Photographer getPhotographer() {
        return this.photographer;
    }

    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }

    public Set<UserRole> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("userProfileId").append("='").append(getUserProfileId()).append("' ");
      buffer.append("userName").append("='").append(getUserName()).append("' ");
      buffer.append("firstName").append("='").append(getFirstName()).append("' ");
      buffer.append("lastName").append("='").append(getLastName()).append("' ");
      buffer.append("emailAddress").append("='").append(getEmailAddress()).append("' ");
      buffer.append("phone").append("='").append(getPhone()).append("' ");
      buffer.append("phoneMobile").append("='").append(getPhoneMobile()).append("' ");
      buffer.append("title").append("='").append(getTitle()).append("' ");
      buffer.append("addr1").append("='").append(getAddr1()).append("' ");
      buffer.append("addr2").append("='").append(getAddr2()).append("' ");
      buffer.append("zipCode").append("='").append(getZipCode()).append("' ");
      buffer.append("city").append("='").append(getCity()).append("' ");
      buffer.append("state").append("='").append(getState()).append("' ");
      buffer.append("country").append("='").append(getCountry()).append("' ");
      buffer.append("gender").append("='").append(getGender()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }

    public boolean equals(Object other) {
          if ( (this == other ) ) return true;
          if ( (other == null ) ) return false;
          if ( !(other instanceof UserProfile) ) return false;
          UserProfile castOther = ( UserProfile) other;

          return ( (this.getUserName()==castOther.getUserName()) || ( this.getUserName()!=null && castOther.getUserName()!=null && this.getUserName().equals(castOther.getUserName()) ) );
    }

    public int hashCode() {
          int result = 17;


          result = 37 * result + ( getUserName() == null ? 0 : this.getUserName().hashCode() );
















          return result;
    }
}

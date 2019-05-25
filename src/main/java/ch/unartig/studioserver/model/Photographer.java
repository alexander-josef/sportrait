/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 02.04.2007$
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
 * Revision 1.1  2007/04/03 06:54:05  alex
 * added category and photographer
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.studioserver.persistence.DAOs.UserRoleDAO;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
@Entity
@Table(name = "photographers")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Photographer implements java.io.Serializable {

    @Id
    private Long photographerId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private UserProfile userProfile;

    @OneToMany(mappedBy = "photographer", fetch = FetchType.LAZY)
    private Set<Album> albums = new HashSet<>(0);

    private String cameraModel;
    private String website;
    private String contactInformation;

    /**
     * default constructor
     * @param userProfile UserProfile to construct photogapher with
     */
    public Photographer(UserProfile userProfile)
    {
        setUserProfile(userProfile);
    }

    /**
     * default constructor. Use constructor with a userprofile
     */
    public Photographer()
    {

    }

    /**
     * Convenience getter to return this userprofile's full name
     * @return The full name of the photographer
     */
    public String getFullName()
    {
        return getUserProfile().getFirstName() + " " + getUserProfile().getLastName();
    }

    public boolean isAdmin()
    {
        UserRoleDAO userRoleDao = new UserRoleDAO();
        return getUserProfile().getRoles().contains(userRoleDao.loadRoleByName(UserRole._ADMIN_ROLE_NAME));
    }

    public String toString()
    {
        return getPhotographerId()+";"+getFullName();
    }

    public Long getPhotographerId() {
        return this.photographerId;
    }

    public void setPhotographerId(Long photographerId) {
        this.photographerId = photographerId;
    }

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public Set getAlbums() {
        return this.albums;
    }

    public void setAlbums(Set albums) {
        this.albums = albums;
    }

    public String getCameraModel() {
        return this.cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContactInformation() {
        return this.contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public boolean equals(Object other) {
          if ( (this == other ) ) return true;
          if ( (other == null ) ) return false;
          if ( !(other instanceof Photographer) ) return false;
          Photographer castOther = (Photographer) other;

          return ( (this.getPhotographerId()==castOther.getPhotographerId()) || ( this.getPhotographerId()!=null && castOther.getPhotographerId()!=null && this.getPhotographerId().equals(castOther.getPhotographerId()) ) );
    }

    public int hashCode() {
          int result = 17;

          result = 37 * result + ( getPhotographerId() == null ? 0 : this.getPhotographerId().hashCode() );





          return result;
    }


}

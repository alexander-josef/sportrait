package ch.unartig.studioserver.model;


import ch.unartig.controller.Client;
import ch.unartig.exceptions.NotAuthorizedException;
import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.businesslogic.AlbumType;
import ch.unartig.studioserver.businesslogic.GenericLevelVisitor;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import org.apache.log4j.Logger;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "genericlevels")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DiscriminatorColumn(name = "HIERARCHY_LEVEL")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class GenericLevel implements java.io.Serializable, Comparable {


    @Transient
    Logger _logger = Logger.getLogger(getClass().getName());

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long genericLevelId;
    private String navTitle;
    private String longTitle;
    private String description;
    private String quickAccess;
    private Boolean isPrivate;
    private Boolean publish;
    private String privateAccessCode;

    public GenericLevel() {
    }




    public Long getGenericLevelId() {
        return this.genericLevelId;
    }

    public void setGenericLevelId(Long genericLevelId) {
        this.genericLevelId = genericLevelId;
    }

    public String getNavTitle() {
        return this.navTitle;
    }

    public void setNavTitle(String navTitle) {
        this.navTitle = navTitle;
    }

    public String getLongTitle() {
        return this.longTitle;
    }

    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuickAccess() {
        return this.quickAccess;
    }

    public void setQuickAccess(String quickAccess) {
        this.quickAccess = quickAccess;
    }

    public Boolean getIsPrivate() {
        return this.isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void accept(GenericLevelVisitor visitor) {

    }

    /**
     * @return
     */
    public abstract List listChildren();

    /**
     * get the parent level Class of this level
     *
     * @return the parent level of this level
     */
    public abstract Class getParentClazz();

    /**
     * index nav link helper method.<br>
     * configure url for action-link here
     *
     * @return string with index nav link
     */
    public String getIndexNavLink() {
        String url = "/overview/" + getGenericLevelId().toString() + "/" + getNavTitle() + "/show.html";
        if (getIsPrivate() != null && getIsPrivate()) {
            _logger.info("accessing private level : " + this.getGenericLevelId());
            url = "/private" + url;
        }
        return url;
    }

    /**
     * Count recursively all photos that are contained in this level
     *
     * @return number of photos
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    private int countPhotos() {
        // todo : check how many times this is called - how many times do we make a count on an album when starting up? seems many times for 1 album ...
        PhotoDAO photoDao = new PhotoDAO();
        int photoCount = 0;

        if (this instanceof Album) {
            photoCount = photoDao.countPhotos((Album) this);
        } else {
            List children = listChildren();
            for (int i = 0; i < children.size(); i++) {
                GenericLevel genericLevel = (GenericLevel) children.get(i);
                photoCount += countPhotos();
            }
        }

        return photoCount;
    }

    /**
     * Count all photos in the album (published or non-published). Return 0 if the album is not yet published.
     *
     * @return total number of photos of this level
     */
    public int getNumberOfPhotos() {
        _logger.debug("GenericLevel.getNumberOfPhotos going to count photos");
        return countPhotos();
    }

    /**
     * @return either "Category", "EventGroup", "Event" or "Album" as level type
     */
    public abstract String getLevelType();

    /**
     * adds a parent event to the generic level
     *
     * @param parentLevel
     */
    public abstract void setParentLevel(GenericLevel parentLevel) throws UnartigException;

    /**
     * @return the parent generic level
     */
    public abstract GenericLevel getParentLevel();

    public int compareTo(Object o) {
        return 0;
    }


    public abstract String getEventDateDisplay();

    public void setEventDate(Date eventDate)
    {
        // default implementation. do nothing
    }

    /**
     * "display" method ... convert string from view in a date
     *
     * @param eventDateDisplay
     */
    public void setEventDateDisplay(String eventDateDisplay) {
        // do nothing in default implementation
    }

    /**
     * @param albumType
     */
    public void setAlbumType(AlbumType albumType) {
        // default implementation. do nothing
    }

    /**
     * default getter returns null; overwritten in album
     *
     * @return the album type
     */
    public AlbumType getAlbumType() {
        return null;
    }

    /**
     * returns true if this level is a category
     *
     * @return true for level Category
     */
    public boolean isCategoryLevel() {
        return false;
    }

    /**
     * @return true if level is eventGroup
     */
    public boolean isEventGroupLevel() {
        return false;
    }

    /**
     * @return true if level is event
     */
    public boolean isEventLevel() {
        return false;
    }

    /**
     * @return true if level is of type SportsEventLevel
     */
    public boolean isSportsEventLevel() {
        return false;
    }

    /**
     * @return true if level is album
     */
    public boolean isAlbumLevel() {
        return false;
    }

    /**
     * @return true if level is sportAlbum
     */
    public boolean isSportsAlbumLevel() {
        return false;
    }

    /**
     * method deleteLevel must be implemented for all levels
     *
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public abstract void deleteLevel() throws UAPersistenceException;

    /**
     * if genericlevel is unpublished, check write access to publish using the client object an set to published
     *
     * @param publish
     * @param client
     */
    public void setPublish(Boolean publish, Client client) throws NotAuthorizedException {
        if (checkWriteAccessFor(client)) {
            this.publish=publish;
        }
    }



    public String getPrivateAccessCode() {
        return this.privateAccessCode;
    }

    public void setPrivateAccessCode(String privateAccessCode) {
        this.privateAccessCode = privateAccessCode;
    }

    /**
     * toString
     *
     * @return String
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
        buffer.append("genericLevelId").append("='").append(getGenericLevelId()).append("' ");
        buffer.append("navTitle").append("='").append(getNavTitle()).append("' ");
        buffer.append("longTitle").append("='").append(getLongTitle()).append("' ");
        buffer.append("description").append("='").append(getDescription()).append("' ");
        buffer.append("quickAccess").append("='").append(getQuickAccess()).append("' ");
        buffer.append("isPrivate").append("='").append(getIsPrivate()).append("' ");
        buffer.append("publish").append("='").append(getPublish()).append("' ");
        buffer.append("privateAccessCode").append("='").append(getPrivateAccessCode()).append("' ");
        buffer.append("]");

        return buffer.toString();
    }


    /**
     * Default implementation - check overridden implementations for Album etc.
     * protected, needs overwrite on implementation
     * @param client
     * @throws NotAuthorizedException
     * @return
     */
    protected boolean checkWriteAccessFor(Client client) throws NotAuthorizedException {
        if (!client.isAdmin()) {
            throw new NotAuthorizedException("No Administrator rights");
        }
        return true;
    }

    /**
     * Default implementation - check overridden implementations for Album etc.
     * protected, needs overwrite on implementation
     * @param client
     * @throws NotAuthorizedException
     * @return
     */
    protected boolean checkReadAccessFor(Client client) throws NotAuthorizedException {
        _logger.info("read access check on genericLevel");
        if (!client.isAdmin()) {
            throw new NotAuthorizedException("No Administrator rights");
        }
        return true;
    }



    public void toggleLevelPublishStatus(Client client) throws NotAuthorizedException {
        if (getPublish() != null && getPublish()) {
            setPublish(Boolean.FALSE, client);
        } else {
            setPublish(Boolean.TRUE, client);
        }
    }

    public Boolean getPublish() {
        if (publish != null)
        {
            return publish;
        } else
        {
            return false;
        }
    }
}



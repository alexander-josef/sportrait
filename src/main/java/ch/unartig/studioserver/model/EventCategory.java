/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 27.03.2007$
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
 * Revision 1.5  2007/05/07 11:44:54  alex
 * getphotos cannot be used!!!1
 *
 * Revision 1.4  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.3  2007/03/29 13:33:44  alex
 * working on photo upload
 *
 * Revision 1.2  2007/03/28 13:52:58  alex
 * edit events page
 *
 * Revision 1.1  2007/03/27 20:03:07  alex
 * added eventCategory
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.exceptions.UnartigException;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Example: Etappen in Sola
 */
@Entity
@Table(name = "eventcategories")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EventCategory implements java.io.Serializable {

    @Transient
    Logger _logger = Logger.getLogger(getClass().getName());

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long eventCategoryId;

    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "eventid",updatable = false, insertable = false,nullable = false)
    private SportsEvent event;

    @OneToMany(mappedBy = "eventCategory", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Album> albums = new HashSet<Album>(0);

    /**
     * default constructor needed for hibernate
     */
    public EventCategory()
    {
    }

    /**
     * 
     * @param eventCategory
     * @param event
     */
    public EventCategory(String eventCategory, SportsEvent event)
    {
        setTitle(eventCategory);
        setEvent(event);
    }

    /**
     * Return true if this eventcategory has uploaded, published photos.
     * @return true if category has photos
     * @throws ch.unartig.exceptions.UnartigException
     */
    public boolean hasPublishedPhotos() throws UnartigException {

        _logger.debug("EventCategory "+ getTitle() +" - EventCategory.getAlbums().size() = " + getAlbums().size());
        for (Object o : getAlbums()) {
            Album album = (Album) o;
            if (album.getPublish() && album.getNumberOfPhotos() > 0) {
                return true;
            }
        }
        return false;
    }

    public Long getEventCategoryId() {
        return this.eventCategoryId;
    }

    public void setEventCategoryId(Long eventCategoryId) {
        this.eventCategoryId = eventCategoryId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SportsEvent getEvent() {
        return this.event;
    }

    public void setEvent(SportsEvent event) {
        this.event = event;
    }

    public Set getAlbums() {
        return this.albums;
    }

    public void setAlbums(Set albums) {
        this.albums = albums;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("eventCategoryId").append("='").append(getEventCategoryId()).append("' ");
      buffer.append("title").append("='").append(getTitle()).append("' ");
      buffer.append("description").append("='").append(getDescription()).append("' ");
      buffer.append("event").append("='").append(getEvent()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

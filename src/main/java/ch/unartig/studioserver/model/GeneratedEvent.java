package ch.unartig.studioserver.model;
// Generated May 8, 2019 7:12:12 PM by Hibernate Tools 3.2.2.GA


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("EVENT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
public abstract class GeneratedEvent extends ch.unartig.studioserver.model.GenericLevel implements java.io.Serializable {


     private Date eventDate;
     private String weblink;


     private EventGroup eventGroup;

     @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = GeneratedAlbum.class)
     private Set<GeneratedAlbum> albums = new HashSet(0);

    public GeneratedEvent() {
    }

    public GeneratedEvent(String navTitle, String longTitle, String description, String quickAccess, Boolean isPrivate, Boolean publish, String privateAccessCode, Date eventDate, String weblink, EventGroup eventGroup, Set albums) {
        super(navTitle, longTitle, description, quickAccess, isPrivate, publish, privateAccessCode);        
       this.eventDate = eventDate;
       this.weblink = weblink;
       this.eventGroup = eventGroup;
       this.albums = albums;
    }
   
    public Date getEventDate() {
        return this.eventDate;
    }
    
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }


    public String getWeblink() {
        return this.weblink;
    }
    
    public void setWeblink(String weblink) {
        this.weblink = weblink;
    }


    public EventGroup getEventGroup() {
        return this.eventGroup;
    }
    
    public void setEventGroup(EventGroup eventGroup) {
        this.eventGroup = eventGroup;
    }


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = GeneratedAlbum.class)
    public Set<GeneratedAlbum> getAlbums() {
        return this.albums;
    }

    public void setAlbums(Set<GeneratedAlbum> albums) {
        this.albums = albums;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("weblink").append("='").append(getWeblink()).append("' ");			
      buffer.append("eventGroup").append("='").append(getEventGroup()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }



}



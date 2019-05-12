package ch.unartig.studioserver.model;
// Generated May 8, 2019 7:12:12 PM by Hibernate Tools 3.2.2.GA


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("ALBUM")
public abstract class GeneratedAlbum extends ch.unartig.studioserver.model.GenericLevel implements java.io.Serializable {


     private String albumTypeString;

     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "photographerId")
     private Photographer photographer;

     private Event event;

     @ManyToOne
     @JoinColumn(name = "eventCategoryId" )
     private EventCategory eventCategory;

     @OneToMany(mappedBy = "album",cascade = CascadeType.ALL, orphanRemoval = true)
     private Set<Photo> photos = new HashSet(0);

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
     private Set<Product> products = new HashSet(0);

    public GeneratedAlbum() {
    }

    public GeneratedAlbum(String navTitle, String longTitle, String description, String quickAccess, Boolean isPrivate, Boolean publish, String privateAccessCode, String albumTypeString, Photographer photographer, Event event, EventCategory eventCategory, Set photos, Set products) {
        super(navTitle, longTitle, description, quickAccess, isPrivate, publish, privateAccessCode);        
       this.albumTypeString = albumTypeString;
       this.photographer = photographer;
       this.event = event;
       this.eventCategory = eventCategory;
       this.photos = photos;
       this.products = products;
    }
   
    public String getAlbumTypeString() {
        return this.albumTypeString;
    }
    
    public void setAlbumTypeString(String albumTypeString) {
        this.albumTypeString = albumTypeString;
    }
    public Photographer getPhotographer() {
        return this.photographer;
    }
    
    public void setPhotographer(Photographer photographer) {
        this.photographer = photographer;
    }
    public Event getEvent() {
        return this.event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    public EventCategory getEventCategory() {
        return this.eventCategory;
    }
    
    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    @OneToMany(mappedBy = "album",cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Photo> getPhotos() {
        return this.photos;
    }
    
    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Product> getProducts() {
        return this.products;
    }
    
    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("photographer").append("='").append(getPhotographer()).append("' ");			
      buffer.append("event").append("='").append(getEvent()).append("' ");			
      buffer.append("eventCategory").append("='").append(getEventCategory()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }



}



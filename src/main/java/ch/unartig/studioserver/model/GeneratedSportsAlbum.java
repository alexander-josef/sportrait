package ch.unartig.studioserver.model;
// Generated May 8, 2019 7:12:12 PM by Hibernate Tools 3.2.2.GA


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Set;

@Entity
@DiscriminatorValue("SPORTSALBUM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
public abstract class GeneratedSportsAlbum extends ch.unartig.studioserver.model.Album implements java.io.Serializable {



    public GeneratedSportsAlbum() {
    }

    public GeneratedSportsAlbum(String navTitle, String longTitle, String description, String quickAccess, Boolean isPrivate, Boolean publish, String privateAccessCode, String albumTypeString, Photographer photographer, Event event, EventCategory eventCategory, Set photos, Set products) {
        super(navTitle, longTitle, description, quickAccess, isPrivate, publish, privateAccessCode, albumTypeString, photographer, event, eventCategory, photos, products);        
    }
   




}



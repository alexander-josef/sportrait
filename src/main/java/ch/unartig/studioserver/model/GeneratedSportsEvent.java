package ch.unartig.studioserver.model;
// Generated May 8, 2019 7:12:12 PM by Hibernate Tools 3.2.2.GA


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@DiscriminatorValue("SPORTSEVENT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
public abstract class GeneratedSportsEvent extends ch.unartig.studioserver.model.Event implements java.io.Serializable {


    @OneToMany()
    private List<EventCategory> eventCategories = new ArrayList(0);

    @OneToMany()
    private Set<EventRunner> eventRunners = new HashSet(0);

    public GeneratedSportsEvent() {
    }

    public GeneratedSportsEvent(String navTitle, String longTitle, String description, String quickAccess, Boolean isPrivate, Boolean publish, String privateAccessCode, Date eventDate, String weblink, EventGroup eventGroup, Set albums, List eventCategories, Set eventRunners) {
        super(navTitle, longTitle, description, quickAccess, isPrivate, publish, privateAccessCode, eventDate, weblink, eventGroup, albums);
        this.eventCategories = eventCategories;
        this.eventRunners = eventRunners;
    }

    @OneToMany()
    public List<EventCategory> getEventCategories() {
        return this.eventCategories;
    }

    public void setEventCategories(List<EventCategory> eventCategories) {
        this.eventCategories = eventCategories;
    }

    @OneToMany
    public Set<EventRunner> getEventRunners() {
        return this.eventRunners;
    }

    public void setEventRunners(Set<EventRunner> eventRunners) {
        this.eventRunners = eventRunners;
    }


}



package ch.unartig.studioserver.model;
// Generated May 8, 2019 7:12:12 PM by Hibernate Tools 3.2.2.GA


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("CATEGORY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
public abstract class GeneratedCategory extends ch.unartig.studioserver.model.GenericLevel implements java.io.Serializable {


    @OneToMany()
    private Set<GeneratedEventGroup> eventGroups = new HashSet(0);

    public GeneratedCategory() {
    }

    public GeneratedCategory(String navTitle, String longTitle, String description, String quickAccess, Boolean isPrivate, Boolean publish, String privateAccessCode, Set eventGroups) {
        super(navTitle, longTitle, description, quickAccess, isPrivate, publish, privateAccessCode);        
       this.eventGroups = eventGroups;
    }

    @OneToMany()
    public Set<GeneratedEventGroup> getEventGroups() {
        return this.eventGroups;
    }
    
    public void setEventGroups(Set<GeneratedEventGroup> eventGroups) {
        this.eventGroups = eventGroups;
    }




}



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
@DiscriminatorValue("EVENTGROUP")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
public abstract class GeneratedEventGroup extends ch.unartig.studioserver.model.GenericLevel implements java.io.Serializable {


     private Category category;
     private String zipcode;
     private String city;

     @OneToMany()
     private Set<GeneratedEvent> events = new HashSet(0);

    public GeneratedEventGroup() {
    }

    public GeneratedEventGroup(String navTitle, String longTitle, String description, String quickAccess, Boolean isPrivate, Boolean publish, String privateAccessCode, Category category, String zipcode, String city, Set events) {
        super(navTitle, longTitle, description, quickAccess, isPrivate, publish, privateAccessCode);        
       this.category = category;
       this.zipcode = zipcode;
       this.city = city;
       this.events = events;
    }
   
    public Category getCategory() {
        return this.category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    public String getZipcode() {
        return this.zipcode;
    }
    
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
    public String getCity() {
        return this.city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }

    @OneToMany()
    public Set<GeneratedEvent> getEvents() {
        return this.events;
    }
    
    public void setEvents(Set<GeneratedEvent> events) {
        this.events = events;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("category").append("='").append(getCategory()).append("' ");			
      buffer.append("zipcode").append("='").append(getZipcode()).append("' ");			
      buffer.append("city").append("='").append(getCity()).append("' ");			
      buffer.append("]");
      
      return buffer.toString();
     }



}



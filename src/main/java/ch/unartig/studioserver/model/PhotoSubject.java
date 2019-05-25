/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 16.03.2006$
 *
 * Copyright (c) 2005 unartig AG  --  All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2006/03/20 15:33:32  alex
 * first check in for new sports album logic and db changes
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * m:m table to map photos to event runners / can contain name and age
 */
@Entity
@Table(name = "customers")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PhotoSubject implements java.io.Serializable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long photoSubjectId;

    private String name;

    private Integer age;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "photosubjects2photos")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Photo> photos = new HashSet<>(0);

    @OneToMany(mappedBy = "photoSubject", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "photosubjectid")
    private Set<EventRunner> eventRunners = new HashSet<>(0);

    public Long getPhotoSubjectId() {
        return this.photoSubjectId;
    }

    public void setPhotoSubjectId(Long photoSubjectId) {
        this.photoSubjectId = photoSubjectId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    protected Set getPhotos() {
        return this.photos;
    }

    protected void setPhotos(Set photos) {
        this.photos = photos;
    }

    public Set getEventRunners() {
        return this.eventRunners;
    }

    public void setEventRunners(Set eventRunners) {
        this.eventRunners = eventRunners;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("photoSubjectId").append("='").append(getPhotoSubjectId()).append("' ");
      buffer.append("name").append("='").append(getName()).append("' ");
      buffer.append("age").append("='").append(getAge()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

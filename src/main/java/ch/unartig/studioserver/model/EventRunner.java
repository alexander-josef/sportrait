/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 29.03.2006$
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
 * Revision 1.1  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * class eventrunner (with a startnumber) maps events (i.e. Sola 2014 Etappe 9) with photosubjects (photosubjects then are mapped to a number of photos)
 */
@Entity
@Table(name = "eventrunners")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EventRunner implements java.io.Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "eventid")
    private Event event;

    @Id
    @ManyToOne
    @JoinColumn(name = "photosubjectid")
    private PhotoSubject photoSubject;

    private String startnumber;
    private Integer rank;
    private String runningTime;

    /**
     * mandatory default constructor
     */
    public EventRunner()
    {
    }

    public EventRunner(Event event, String startNumber, PhotoSubject photoSubject)
    {
        this.setEvent(event);
        this.setStartnumber(startNumber);
        this.setPhotoSubject(photoSubject);
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public PhotoSubject getPhotoSubject() {
        return this.photoSubject;
    }

    public void setPhotoSubject(PhotoSubject photoSubject) {
        this.photoSubject = photoSubject;
    }

    public String getStartnumber() {
        return this.startnumber;
    }

    public void setStartnumber(String startnumber) {
        this.startnumber = startnumber;
    }

    public Integer getRank() {
        return this.rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getRunningTime() {
        return this.runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    /**
     * toString
     * @return String
     */
    @Override
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("event").append("='").append(getEvent()).append("' ");
      buffer.append("photoSubject").append("='").append(getPhotoSubject()).append("' ");
      buffer.append("startnumber").append("='").append(getStartnumber()).append("' ");
      buffer.append("rank").append("='").append(getRank()).append("' ");
      buffer.append("runningTime").append("='").append(getRunningTime()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

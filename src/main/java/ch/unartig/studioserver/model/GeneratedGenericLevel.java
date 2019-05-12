package ch.unartig.studioserver.model;


import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Cache;

import javax.persistence.*;

@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "genericlevels")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DiscriminatorColumn(name = "HIERARCHY_LEVEL" )
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class GeneratedGenericLevel  implements java.io.Serializable {


     private Long genericLevelId;
     private String navTitle;
     private String longTitle;
     private String description;
     private String quickAccess;
     private Boolean isPrivate;
     private Boolean publish;
     private String privateAccessCode;

    public GeneratedGenericLevel() {
    }

    public GeneratedGenericLevel(String navTitle, String longTitle, String description, String quickAccess, Boolean isPrivate, Boolean publish, String privateAccessCode) {
       this.navTitle = navTitle;
       this.longTitle = longTitle;
       this.description = description;
       this.quickAccess = quickAccess;
       this.isPrivate = isPrivate;
       this.publish = publish;
       this.privateAccessCode = privateAccessCode;
    }

    @Id
    @GeneratedValue(generator="increment")
    @GenericGenerator(name="increment", strategy = "increment")
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
    public Boolean getPublish() {
        return this.publish;
    }
    
    public void setPublish(Boolean publish) {
        this.publish = publish;
    }
    public String getPrivateAccessCode() {
        return this.privateAccessCode;
    }
    
    public void setPrivateAccessCode(String privateAccessCode) {
        this.privateAccessCode = privateAccessCode;
    }

    /**
     * toString
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



}



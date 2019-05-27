/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 14.03.2007$
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
 * Revision 1.2  2007/06/09 11:15:37  alex
 * photographer
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/15 21:45:27  alex
 * no more price segment
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 *
 * @author Alexander Josef, unartig AG 2007
 * Update 2017-05-01 : DataSourceRealm authenticator does not exist anymore
 */
@Entity
@Table(name = "userroles")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserRole implements java.io.Serializable {

    @Transient
    static Logger _logger = Logger.getLogger(UserRole.class);

    public static final String _ADMIN_ROLE_NAME = "unartigadmin";
    public static final String _PHOTOGRAPHER_ROLE_NAME = "photographer";



    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long userRoleId;

    private String roleName;

    private String roleDescription;

    public Long getUserRoleId() {
        return this.userRoleId;
    }

    public void setUserRoleId(Long userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return this.roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("userRoleId").append("='").append(getUserRoleId()).append("' ");
      buffer.append("roleName").append("='").append(getRoleName()).append("' ");
      buffer.append("roleDescription").append("='").append(getRoleDescription()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }

    public boolean equals(Object other) {
          if ( (this == other ) ) return true;
          if ( (other == null ) ) return false;
          if ( !(other instanceof UserRole) ) return false;
          UserRole castOther = ( UserRole ) other;

          return ( (this.getRoleName()==castOther.getRoleName()) || ( this.getRoleName()!=null && castOther.getRoleName()!=null && this.getRoleName().equals(castOther.getRoleName()) ) );
    }

    public int hashCode() {
          int result = 17;


          result = 37 * result + ( getRoleName() == null ? 0 : this.getRoleName().hashCode() );

          return result;
    }
}

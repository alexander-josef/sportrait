/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 07.02.2007$
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
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
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
 * Describes the different types of products, i.e. 10x15cm, or digital photo
 * Currently the order in the database could be derived by the ProductTypeID key.
 * Future solution should contain a "ordinal" column that defines the order of the appearance of the product types.
 */
@Entity
@Table(name = "producttypes")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProductType implements java.io.Serializable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long productTypeId;

    private String name;
    private String description;
    private Boolean digitalProduct;


    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinTable(name = "prices2producttypes",
            joinColumns = { @JoinColumn(name = "producttypeid") },
            inverseJoinColumns = { @JoinColumn(name = "priceid") }
    )
    @OrderBy("priceCHF")
    private Set<Price> prices = new HashSet<>(0);

    public Long getProductTypeId() {
        return this.productTypeId;
    }

    public void setProductTypeId(Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDigitalProduct() {
        return this.digitalProduct;
    }

    public void setDigitalProduct(Boolean digitalProduct) {
        this.digitalProduct = digitalProduct;
    }

    public Set<Price> getPrices() {
        return this.prices;
    }

    public void setPrices(Set<Price> prices) {
        this.prices = prices;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("productTypeId").append("='").append(getProductTypeId()).append("' ");
      buffer.append("name").append("='").append(getName()).append("' ");
      buffer.append("description").append("='").append(getDescription()).append("' ");
      buffer.append("digitalProduct").append("='").append(getDigitalProduct()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

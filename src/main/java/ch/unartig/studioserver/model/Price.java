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
 * Revision 1.2  2007/03/13 16:55:03  alex
 * template for properties
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;


@Entity
@Table(name = "prices")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY) // at least until prices can't be configured in the UI
public class Price implements java.io.Serializable {


    private static Price notAvailablePrice = new Price();

    public static NumberFormat monetaryAmountFormat = DecimalFormat.getInstance();

    static
    {
        // initialize the NumberFormatter
        monetaryAmountFormat.setMinimumFractionDigits(2);
        monetaryAmountFormat.setMaximumFractionDigits(2);
        // configure the not available price
        
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long priceId;


    private BigDecimal priceCHF;
    private BigDecimal priceEUR;
    private BigDecimal priceGBP;
    private BigDecimal priceSEK;
    private String comment;

    public Price()
    {
    }

    public String getPriceLabel() {
        return getPriceCHF() + " CHF // " + getPriceEUR() + " EUR";
    }

    public Long getPriceId() {
        return this.priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public BigDecimal getPriceCHF() {
        return this.priceCHF;
    }

    public void setPriceCHF(BigDecimal priceCHF) {
        this.priceCHF = priceCHF;
    }

    public BigDecimal getPriceEUR() {
        return this.priceEUR;
    }

    public void setPriceEUR(BigDecimal priceEUR) {
        this.priceEUR = priceEUR;
    }

    public BigDecimal getPriceGBP() {
        return this.priceGBP;
    }

    public void setPriceGBP(BigDecimal priceGBP) {
        this.priceGBP = priceGBP;
    }

    public BigDecimal getPriceSEK() {
        return this.priceSEK;
    }

    public void setPriceSEK(BigDecimal priceSEK) {
        this.priceSEK = priceSEK;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("priceId").append("='").append(getPriceId()).append("' ");
      buffer.append("priceCHF").append("='").append(getPriceCHF()).append("' ");
      buffer.append("priceEUR").append("='").append(getPriceEUR()).append("' ");
      buffer.append("priceGBP").append("='").append(getPriceGBP()).append("' ");
      buffer.append("priceSEK").append("='").append(getPriceSEK()).append("' ");
      buffer.append("comment").append("='").append(getComment()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

/*-*
 *
 * FILENAME  :
 *
 *    @author alexanderjosef
 *    @since Nov 6, 2007
 *
 * Copyright (c) 2007 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision: $REVISION$
 *
 *    $Author: $AUTHOR$ $, $Locker$
 *
 *************************************************
 * $Log$
 *
 ****************************************************************/

package ch.unartig.studioserver.model;

import java.math.BigDecimal;

/**
 * Unmapped model class that serves as report Value Object.
 */
public class ReportMonthlySalesSummary
{

    private Long productTypeId;
    private Long priceId;
    private String productTypeName;
    private BigDecimal priceCHF; // 'average' of priceCHF ... should be exactly price CHF
    private Long photographerId;
    private String photographerLastName;
    private String photographerFirstName;
    private Integer year; //
    private Integer month; //
    private Long quantitySum; // aggregated sum of orderitem quantity
    private BigDecimal totalPriceCHF;// summed quantity * price CHF


    /**
     * Constructor to be used with dynamic instantiation in the hibernate query ; photographer total query
     */
    @SuppressWarnings({"JavaDoc"})
    public ReportMonthlySalesSummary(Long photographerId, String photographerLastName, String photographerFirstName, Long quantitySum, BigDecimal totalPriceCHF, Integer year, Integer month)
    {
        this.photographerId = photographerId;
        this.photographerLastName = photographerLastName;
        this.photographerFirstName = photographerFirstName;

        this.year = year;
        this.month = month;
        this.quantitySum = quantitySum;
        this.totalPriceCHF = totalPriceCHF;
    }

    /**
     * Constructor to be used with dynamic instantiation in the hibernate query
     */
    @SuppressWarnings({"JavaDoc"})
    public ReportMonthlySalesSummary(Long productTypeId, Long priceId, String productTypeName, BigDecimal priceCHF, int year, int month, Long quantitySum, BigDecimal totalPriceCHF)
    {
        this.productTypeId = productTypeId;
        this.priceId = priceId;
        this.productTypeName = productTypeName;
        this.priceCHF = priceCHF;
        this.year = year;
        this.month = month;
        this.quantitySum = quantitySum;
        this.totalPriceCHF = totalPriceCHF;
    }


    public Long getProductTypeId()
    {
        return productTypeId;
    }

    public void setProductTypeId(Long productTypeId)
    {
        this.productTypeId = productTypeId;
    }

    public Long getPriceId()
    {
        return priceId;
    }

    public void setPriceId(Long priceId)
    {
        this.priceId = priceId;
    }
    

    public String getProductTypeName()
    {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName)
    {
        this.productTypeName = productTypeName;
    }

    public BigDecimal getPriceCHF()
    {
        return priceCHF;
    }

    public void setPriceCHF(BigDecimal priceCHF)
    {
        this.priceCHF = priceCHF;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public Long getQuantitySum()
    {
        return quantitySum;
    }


    public BigDecimal getTotalPriceCHF()
    {
        return totalPriceCHF;
    }


    public Long getPhotographerId()
    {
        return photographerId;
    }

    public String getPhotographerLastName()
    {
        return photographerLastName;
    }

    public String getPhotographerFirstName()
    {
        return photographerFirstName;
    }
}

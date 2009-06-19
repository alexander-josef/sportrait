/*-*
 *
 * FILENAME  :
 *
 *    @author alexanderjosef
 *    @since Nov 19, 2007
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
package ch.unartig.sportrait.web.reports;

import ch.unartig.studioserver.model.ReportMonthlySalesSummary;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * This class is an abstract super class for all reports that are reporting data over months.
 * It shall contain data and logic common to all types of montly reports (i.e. Product reports, photographer reports etc).
 */
public abstract class MonthlyReport implements IReport
{
    final Logger _logger = Logger.getLogger(this.getClass());


    protected SortedSet<IReportColumn> columns ; // keep a list of column entries that this report has. Needs to be sorted
    public static final int _NUMBER_PRODUCTS_SOLD_REPORT = 1;
    public static final int _TURNOVER_PRODUCTS_SOLD_REPORT = 2;
    public static final int _EARNINGS_PRODUCTS_SOLD_REPORT = 3;
    protected int reportType = 1; // init report to numbers sold
    protected BigDecimal totalRowValue = new BigDecimal(0); // last column sums up all row values;
    public static Map<Integer,String> reportTypeUnitMap = new HashMap<Integer,String>();
    protected List<ReportMonthlySalesSummary> reportOrderitems;


    static
    {
        // what if we have units that depend upon the client? Move to object initialization and make map non-static
        reportTypeUnitMap.put(_NUMBER_PRODUCTS_SOLD_REPORT,"");
        reportTypeUnitMap.put(_TURNOVER_PRODUCTS_SOLD_REPORT,"CHF");
        reportTypeUnitMap.put(_EARNINGS_PRODUCTS_SOLD_REPORT,"CHF");
    }

    protected MonthlyReport(List<ReportMonthlySalesSummary> reportOrderitems)
    {
        this.reportOrderitems = reportOrderitems;
        if (reportOrderitems!=null) {
            initColumns();
        }

    }

    public void addColumn(int year, int month)
    {
        // set only accept a new Column
        final ReportSalesPerProductMonths.ReportColumn reportColumn = new ReportSalesPerProductMonths.ReportColumn(year, month);
        columns.add(reportColumn);
    }

    public BigDecimal getTotalRowValue()
    {
        return totalRowValue;
    }

    public void setTotalRowValue(BigDecimal totalRowValue)
    {
        this.totalRowValue = totalRowValue;
    }

    public SortedSet<IReportColumn> getColumns()
    {
        return columns;
    }

    public int getReportType()
    {
        return reportType;
    }

    public void setReportType(int reportType)
    {
        this.reportType = reportType;
    }

    /**
     * Use a static map to resolve the report unit string according to the report type.
     * @return Unit as String, example "CHF"
     */
    public String getReportUnit()
    {
        return reportTypeUnitMap.get(reportType);
    }

    /**
     */
    private void initColumns()
    {
        columns = new TreeSet<IReportColumn>();
        for (ReportMonthlySalesSummary reportOrderItem : reportOrderitems)
        {
            // for each orderitem, update the report:
            // 1) add a column entry (equal entries are ignored in the set)
            try
            {
                final IReportColumn reportColumn = new ReportColumn(reportOrderItem.getYear(), reportOrderItem.getMonth());
                columns.add(reportColumn);
            } catch (Exception e)
            {
                _logger.error("Error adding column",e);
                throw new RuntimeException("Error adding column",e);
            }
        }
    }

    public List<ReportMonthlySalesSummary> getReportOrderitems()
    {
        return reportOrderitems;
    }

    public void setReportOrderitems(List<ReportMonthlySalesSummary> reportOrderitems)
    {
        this.reportOrderitems = reportOrderitems;
    }



    /**
     * This class represents a column for the products and year/months report.
     */
    public class ReportColumn implements IReportColumn,Comparable // It's used in a TreeSet, MUST implement Comparable
    {
        private int year;
        private int month;

        public ReportColumn(int year, int month)
        {
            this.year = year;
            this.month = month;
        }

        /**
         * Equality is defined by the equality of the two fields
         * @param o the Column object to compare
         * @return true if the two object are considered equal.
         */
        public boolean equals(Object o)
        {
            // defensive:
            if (!(o instanceof ReportColumn))
            {
                throw new RuntimeException("Can not compare, wrong class!");
            }
            ReportColumn columnToCompare = (ReportColumn) o;
            return (year==columnToCompare.getYear() && month == columnToCompare.getMonth()) ;
        }


        /**
         * Overriden to define equality
         * @return the same hashCode if the objects are considered equal.
         */
        public int hashCode()
        {
            int result;
            result = year;
            result = 31 * result + month;
            return result;
        }


        /**
         * Comparison happens by year first then by month
         * @param o column to compare
         * @return compare result
         */
        public int compareTo(Object o)
        {
            // defensive:
            if (!(o instanceof ReportColumn))
            {
                throw new RuntimeException("Can not compare, wrong class!");
            }
            ReportColumn columnToCompare = (ReportColumn) o;
            if (year==columnToCompare.getYear())
            {
                return new Integer(month).compareTo(columnToCompare.getMonth());
            }
            return new Integer(year).compareTo(columnToCompare.getYear());
        }


        public int getYear()
        {
            return year;
        }

        public int getMonth()
        {
            return month;
        }
    }

}

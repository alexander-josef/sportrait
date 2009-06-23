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

import ch.unartig.u_core.model.ReportMonthlySalesSummary;
import ch.unartig.u_core.exceptions.UnartigException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.zkoss.zul.Columns;
import org.zkoss.zul.Column;
import org.zkoss.zul.Row;
import org.zkoss.zul.Label;

/**
 * Report class to be used when listing monthly sales of all photographers. Not by product.
 */
public  class ReportSalesPerPhotographerMonths extends MonthlyReport
{
    private static final double _PHOTOGRAPHER_EARNING_PERCENTAGE = 0.8;

    public ReportSalesPerPhotographerMonths(List<ReportMonthlySalesSummary> reportOrderitems)
    {
        super(reportOrderitems);
    }


    /**
     * Overridden for each Report. Here the specific strategy for calculationg a report value is implemented.
     * @param monthlySalesSummary
     * @param reportType
     * @return
     * @throws UnartigException
     */
    public BigDecimal calculateReportValue(ReportMonthlySalesSummary monthlySalesSummary, int reportType) throws UnartigException
    {
        BigDecimal retVal;
        final BigDecimal numbersSold = new BigDecimal(monthlySalesSummary.getQuantitySum());
        final BigDecimal turnoverChf = monthlySalesSummary.getTotalPriceCHF(); // we need the total, it's not about single products
        switch (reportType)
        {
            case ReportSalesPerAlbumProducts._NUMBER_PRODUCTS_SOLD_REPORT:
            {
                retVal = numbersSold;
                totalRowValue = totalRowValue.add(numbersSold);
                break;
            }
            case ReportSalesPerAlbumProducts._TURNOVER_PRODUCTS_SOLD_REPORT:
            {
                totalRowValue = totalRowValue.add(turnoverChf);
                retVal = turnoverChf;
                break;
            }
            case ReportSalesPerAlbumProducts._EARNINGS_PRODUCTS_SOLD_REPORT:
            {
                final BigDecimal earnings = turnoverChf.multiply(new BigDecimal(_PHOTOGRAPHER_EARNING_PERCENTAGE)); // use 80 % of the turnover as earnings
                totalRowValue = totalRowValue.add(earnings);
                retVal = earnings.setScale(2, RoundingMode.HALF_UP);
                // multiply with the number sold with the actual earning for a product
                // where to store that earning information? --> currently in the static product mapping
                // should that information be persisted when the actual purchase is made?

                break;
            }
            default:
            {
                // defensive:
                throw new RuntimeException("This type of report is unknown");
            }
        }

        return retVal;
    }

    /**
     * First label is photographer
     * @param summaryEntry
     * @return
     */
    public String getRowLabel1(ReportMonthlySalesSummary summaryEntry)
    {
        return summaryEntry.getPhotographerFirstName()+" " + summaryEntry.getPhotographerLastName();  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @param summaryEntry
     * @return
     */
    public String getRowLabel2(ReportMonthlySalesSummary summaryEntry)
    {
        return "n.a.";  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * @param summaryEntry
     * @return
     */
    public String getRowLabel3(ReportMonthlySalesSummary summaryEntry)
    {
        return "n.a.";  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * New changes when photographername changes
     * This mehtod is used by the view component to check for new row
     * @param summaryEntry
     * @param lastEntry
     * @return
     */
    public boolean isSameRow(ReportMonthlySalesSummary summaryEntry, ReportMonthlySalesSummary lastEntry)
    {
        return lastEntry!=null &&  !summaryEntry.getPhotographerId().equals(lastEntry.getPhotographerId());
    }

    public void insertDescriptiveColumns(Columns columns)
    {
        final Column oipsPid = new Column("Fotograf");
        oipsPid.setWidth("150px");
        oipsPid.setAlign("right");
        columns.appendChild(oipsPid);
    }

    public int getNumberOfDescriptiveRows()
    {
        return 1;
    }

    /**
     * Insert row title: Photographer name
     *
     * @param row          zk row
     * @param summaryEntry data value object with the summary result
     */
    public void insertRowDescriptors(Row row, ReportMonthlySalesSummary summaryEntry)
    {
        final Label label1 = new Label(summaryEntry.getPhotographerFirstName() + " " + summaryEntry.getPhotographerLastName());
        label1.setWidth("150px");
        label1.setStyle("font-weight:bold;");
        row.appendChild(label1);
    }
}

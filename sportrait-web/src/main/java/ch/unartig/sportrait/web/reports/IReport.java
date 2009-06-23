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
import java.util.List;

import org.zkoss.zul.Columns;
import org.zkoss.zul.Row;

/**
 * This interface describes the methods used by sportrait reports
 */
public interface IReport
{

    /**
     * Different Report types are distinguished by type-codes. We currently have three different report types:
     * - numbers sold
     * - turnover
     * - earnings
     * @param typeInt
     */
    void setReportType(int typeInt);

    /**
     * Return the data column headers
     * @see ch.unartig.sportrait.web.reports.IReportColumn
     * @return
     */
    Iterable<IReportColumn> getColumns();

    /**
     * Reports might be in different currencies depending on the report-user. Return the correct report currency.
     * @return Currently CHF
     */
    String getReportUnit();

    /**
     * Setter for the report type code.
     * @see #setReportType(int)
     * @return
     */
    int getReportType();

    /**
     * Depending on the report type the value is calculated differently from the MonthlySalesSummary.
     * Overridden for each Report. Here the specific strategy for calculationg a report value is implemented.
     * @param summaryEntry
     * @param reportType A type code for the report type
     * @return
     * @throws UnartigException
     */
    BigDecimal calculateReportValue(ReportMonthlySalesSummary summaryEntry, int reportType) throws UnartigException;

    /**
     * Calculates the total. Usually used as the last position in a report row.
     * @return
     */
    BigDecimal getTotalRowValue();

    public List<ReportMonthlySalesSummary> getReportOrderitems();

    public void setReportOrderitems(List<ReportMonthlySalesSummary> reportOrderitems);


    /**
     * @param bigDecimal
     */
    void setTotalRowValue(BigDecimal bigDecimal);

    /**
     * todo check if still needed
     * @param summaryEntry summaryEntry Model class containing the information for one row
     * @return
     */
    String getRowLabel1(ReportMonthlySalesSummary summaryEntry);

    /**
     * todo check if still needed
     * @param summaryEntry summaryEntry Model class containing the information for one row
     * @return
     */
    String getRowLabel2(ReportMonthlySalesSummary summaryEntry);

    /**
     * todo check if still needed
     * @param summaryEntry Model class containing the information for one row
     * @return
     */
    String getRowLabel3(ReportMonthlySalesSummary summaryEntry);

    /**
     * Decide whether the next entry still belongs to the same row. Todo: is this report dependant?
     * @param summaryEntry
     * @param lastEntry
     * @return
     */
    boolean isSameRow(ReportMonthlySalesSummary summaryEntry, ReportMonthlySalesSummary lastEntry);

    /**
     * todo report interface contains zk dependency. remove this dependency if possible.
     * @param columns
     */
    void insertDescriptiveColumns(Columns columns);

    int getNumberOfDescriptiveRows();

    /**
     * Insert row titles, OIPS-PID, Description, Price CHF
     *
     * @param row          zk row
     * @param summaryEntry data value object with the summary result
     */
    void insertRowDescriptors(Row row, ReportMonthlySalesSummary summaryEntry);
}
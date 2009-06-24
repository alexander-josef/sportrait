/*-*
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 10.10.2007$
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
 ****************************************************************/
package ch.unartig.sportrait.web.zk.components;

import ch.unartig.u_core.controller.Client;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.model.ReportMonthlySalesSummary;
import ch.unartig.sportrait.web.reports.IReport;
import ch.unartig.sportrait.web.reports.IReportColumn;
import ch.unartig.sportrait.web.reports.ReportSalesPerPhotographerMonths;
import ch.unartig.sportrait.web.reports.ReportSalesPerProductMonths;
import ch.unartig.u_core.persistence.DAOs.ReportingDAO;
import org.apache.log4j.Logger;
import org.zkoss.zul.*;
import org.zkoss.zk.ui.Executions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Component .... Controller for a report that shows sales according to product and month
 * Currently in use for product report and photographer report
 *
 * @author <a href=mailto:alexander.josef@unartig.ch>Alexander Josef</a>, (c) 2007 unartig AG
 */
public class ZReportSalesProductMonth extends Div
{
    private static final String _GRID_VALUE_COLUMN_WIDTH = "40px";

    final Logger _logger = Logger.getLogger(this.getClass());
    private Client client;

    private IReport report;
    private Grid grid;
    private Map<IReportColumn, BigDecimal> columnTotalMap;


    /**
     * Is called upon creation of the main view.
     * Initialization of the report model is deferred until the report is requested.
     */
    public void onCreate()
    {
        _logger.debug("ZReportSalesProductMonth.onCreate");
        this.client = (Client) Executions.getCurrent().getDesktop().getSession().getAttribute(Registry._SESSION_CLIENT_NAME);

    }

    /**
     * Called from the zul-page
     * Report shall be lazy loaded when requested.
     * This calls the constructor for the report model class and loads the data.
     */
    public void initReport()
    {
        // cache if we were loaded before
        if (grid == null && report == null)
        {
            ReportingDAO reportingDao = new ReportingDAO();
            // rows for the report
            List<ReportMonthlySalesSummary> reportOrderitems = reportingDao.getProductMonthsOrderitems(client);
            report = new ReportSalesPerProductMonths(reportOrderitems);
            createGridColumns();
        }
        initReportRows();
    }

    /**
     */
    public void initPhotographerReport()
    {
        // cache if we were loaded before
        if (grid == null && report == null)
        {
            ReportingDAO reportingDao = new ReportingDAO();
            List<ReportMonthlySalesSummary> photographerSales = reportingDao.listTotalPhotographerSalesByMonth();
            report = new ReportSalesPerPhotographerMonths(photographerSales);
            createGridColumns();
        }
        initReportRows();

    }

    /**
     * Create the content grid row by row; Is called every time the view is updated (type of report changed)
     */
    private void initReportRows()
    {
        columnTotalMap = new HashMap<IReportColumn, BigDecimal>();
        // detach rows if they exist;
        if (grid.getRows() != null)
        {
            grid.getRows().detach();
        }
        Rows rows = new Rows();
        grid.appendChild(rows);
        // calculate the values and construct the rows:
        appendValueRows(rows);
        appendTotalRow(rows);

    }


    /**
     * At the end of the report, insert a total row
     *
     * @param rows zk rows
     */
    private void appendTotalRow(Rows rows)
    {
        Row totalRow = new Row();
        // check for totoal descriptive rows in report:
        int numberOfDescriptiveRows = report.getNumberOfDescriptiveRows();
        totalRow.setSpans(""+numberOfDescriptiveRows);
        totalRow.setAlign("left");
        rows.appendChild(totalRow);
        final Label totalLabel = new Label("TOTAL");
        totalLabel.setStyle("font-weight:bold;");
        // width = 250 px
        totalRow.appendChild(totalLabel);
        Label totalValueLabel;
        BigDecimal grandTotal = new BigDecimal(0); // scale?
        if (report.getColumns()!=null) {
            for (IReportColumn reportColumn : report.getColumns())
            {
                BigDecimal value = columnTotalMap.get(reportColumn);
                if (!report.getReportUnit().equals(""))
                {
                    value = value.setScale(2, RoundingMode.HALF_UP);
                }
                totalValueLabel = new Label(value.toString() + " " + report.getReportUnit());
                totalValueLabel.setStyle("font-weight:bold;");
                totalRow.appendChild(totalValueLabel);
                grandTotal = grandTotal.add(value);
            }
        }
        // insert grand total at the end of the last row
        final Label grandTotalLabel = new Label(grandTotal + " " + report.getReportUnit());
        grandTotalLabel.setStyle("font-weight:bold;");
        totalRow.appendChild(grandTotalLabel);
    }

    /**
     * The tabbox is set to default 900 px; for the reports, we need more horizonal space
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private void adjustTabbox()
    {
        Tabbox tbox = (Tabbox) getFellow("tbox");
        System.out.println("tbox.getId() = " + tbox.getId());
        System.out.println("tbox.getWidth() = " + tbox.getWidth());
        tbox.setWidth("1500px");
    }


    /**
     * For each row that is in the report, go through all the columns and insert the value that is of interest.
     *
     * @param rows ZK rows of the grid
     */
    private void appendValueRows(Rows rows)
    {
        // different strategy could be (could perform better than sorting): get the events first and then loop through the categories in the event and get the albums to the category
        List<ReportMonthlySalesSummary> monthlySalesSummaryList = report.getReportOrderitems();

        Row row = null;
        ReportMonthlySalesSummary lastEntry = null; // this var is used to check for row-change in the list
        Iterator<IReportColumn> columnIter = null; // column iterator used when walking through the summary result.
        // for every consolidated orderitem (ReportMonthlySalesSummery) we do:
        if (monthlySalesSummaryList!=null)
        {
            for (ReportMonthlySalesSummary summaryEntry : monthlySalesSummaryList)
            {
                // do we have a new row ? (if the price AND the producttype changes we advance one row. The resultset is ordered by prodcuttypeid and price)
                if (report.isSameRow(summaryEntry,lastEntry))
                {
                    // go through columns and enter a value if we have the right column
                    // row descriptors have already been entered
                    insertRowValue(row, summaryEntry, columnIter);
                } else
                {// new row; only here we advance one row down!
                    if (lastEntry != null)
                    {// ... but first finish the previous row
                        completePreviousRow(row, columnIter, "-");
                    }
                    row = new Row();
                    report.setTotalRowValue(new BigDecimal(0)); // init total for row since we start a new row.
                    rows.appendChild(row);
                    columnIter = report.getColumns().iterator(); // init iter
                    report.insertRowDescriptors(row, summaryEntry);
                    insertRowValue(row, summaryEntry, columnIter);
                }
                lastEntry = summaryEntry;
            }
            completePreviousRow(row, columnIter, "-");
        }
        // end of report data
        // make sure last row is filled
    }


    /**
     * Checks if we are at the end of regular columns and inserts empty value if necessary.
     * Inserts the computed totals at the end of the row
     *
     * @param row        zk row
     * @param columnIter column iterator
     * @param fillValue  the character that shall be used to fill empty values
     */
    private void completePreviousRow(Row row, Iterator<IReportColumn> columnIter, final String fillValue)
    {
        if (columnIter != null)
        {
            while (columnIter.hasNext())
            {
                columnIter.next();
                row.appendChild(new Label(fillValue));
            }
            // end of regualr columns, insert summary, this is the end of the row:
            if (report.getReportUnit()!=null && !report.getReportUnit().equals(""))
            {
                row.appendChild(new Label(report.getTotalRowValue().setScale(2,RoundingMode.HALF_UP) + " " + report.getReportUnit()));
            } else
            {
                row.appendChild(new Label(report.getTotalRowValue().toString()));
            }
        } else
        {
            // defensive:
            throw new RuntimeException("Unexpected Result while drawing report");
        }
    }

    /**
     * Advance with the columns and insert a value if the column is found where this value needs to be inserted.
     * Depends on sorted Result set!! Ordered by producttypeid and pricechf.
     *
     * @param row          zk row
     * @param summaryEntry summary data value object
     * @param columnIter   iterator for columns
     */
    private void insertRowValue(Row row, ReportMonthlySalesSummary summaryEntry, Iterator<IReportColumn> columnIter)
    {
        while (columnIter.hasNext())
        {
            IReportColumn reportColumn = columnIter.next();
            if (reportColumn.getMonth() == summaryEntry.getMonth() && reportColumn.getYear() == summaryEntry.getYear())
            {
                // we have the right column
                // insert value in row cell
                // value could be sum of qunatity, total turnover, total earnings
                final BigDecimal value;
                Label rowCellEntry;
                try
                {
                    value = report.calculateReportValue(summaryEntry, report.getReportType());
                    rowCellEntry = new Label(value + " " + report.getReportUnit());
                    updateColumnTotal(reportColumn, value);
                } catch (UnartigException e)
                {
                    _logger.info("Exception calculating Cell Value");
                    rowCellEntry = new Label("--ERROR--");
                }
                rowCellEntry.setWidth(_GRID_VALUE_COLUMN_WIDTH);
                row.appendChild(rowCellEntry);
                // If we don't break here we go too far for the next summary entry. Next summary entry cannot ! come before here because of ordering.
                break;
            } else
            {
                // insert empty row cell
                final Label emptyCell = new Label("-");
                emptyCell.setWidth(_GRID_VALUE_COLUMN_WIDTH);
                row.appendChild(emptyCell);
            }
        }
    }

    /**
     * Update the total for this column with the current value
     *
     * @param reportColumn the reportColumn object
     * @param value        the current report value
     */
    private void updateColumnTotal(IReportColumn reportColumn, BigDecimal value)
    {
        BigDecimal currentColumnTotalValue = columnTotalMap.get(reportColumn);
        BigDecimal newColumnTotalValue;
        if (currentColumnTotalValue != null)
        {
            newColumnTotalValue = currentColumnTotalValue.add(value);
        } else
        {
            newColumnTotalValue = value;
        }
        columnTotalMap.put(reportColumn, newColumnTotalValue);
    }

    /**
     * Ask the report class to return a Set of columns that can be used as headers to the rows.
     *
     * @return an initialized grid with column headers
     */
    private Grid createGridColumns()
    {
        grid = new Grid();
        this.appendChild(grid);
        Columns columns = new Columns();
        columns.setSizable(true);
        grid.appendChild(columns);

        // descriptive columns:
        report.insertDescriptiveColumns(columns);

        // ... and now all the value columns; user "month/year"
        if (report.getColumns()!=null) {
            for (IReportColumn reportColumn : report.getColumns())
            {
                _logger.debug("adding column for ["+reportColumn.getMonth()+"/"+reportColumn.getYear()+"] ");
                final Column column = new Column(reportColumn.getMonth() + "/" + reportColumn.getYear());
                column.setWidth(_GRID_VALUE_COLUMN_WIDTH);
                columns.appendChild(column);
            }
            // and the summary of row header:
            final Column rowTotal = new Column("Gesamt");
            rowTotal.setWidth("50px");
            columns.appendChild(rowTotal);
        }
        return grid;
    }


    /**
     * ... good, use the type for all reports ...
     * The type of report has been changed. Read the type parameter. Reload the table with the new report value.
     *
     * @param type type code for report type
     */
    public void setReportType(String type)
    {
        _logger.debug("Type of report = " + type);
        _logger.debug("ID of report = " + getId());
        // cut the "type" at the beginning and make it an integer.
        // ... find better solution
        final int typeInt = Integer.parseInt(type.replace(getId()+"type", ""));
        report.setReportType(typeInt);

        initReportRows();

    }

    // Getter Setter

    public Client getClient()
    {
        return client;
    }

    public void setClient(Client client)
    {
        this.client = client;
    }


}
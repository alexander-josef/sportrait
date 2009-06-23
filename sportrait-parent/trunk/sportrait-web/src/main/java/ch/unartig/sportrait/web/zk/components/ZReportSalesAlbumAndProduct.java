/*-*
 *
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
import ch.unartig.sportrait.web.reports.EntryProductSale;
import ch.unartig.sportrait.web.reports.ReportSalesPerAlbumProducts;
import ch.unartig.sportrait.web.reports.RowSalesPerAlbumProducts;
import ch.unartig.u_core.model.Album;
import ch.unartig.u_core.model.Event;
import ch.unartig.u_core.persistence.DAOs.ReportingDAO;
import ch.unartig.u_core.Registry;
import org.apache.log4j.Logger;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabbox;
import org.zkoss.zk.ui.Executions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.SortedMap;

/**
 * Component .... Controller for a report
 *
 * @author <a href=mailto:alexander.josef@unartig.ch>Alexander Josef</a>, (c) 2007 unartig AG
 */
public class ZReportSalesAlbumAndProduct extends Div
{

    final Logger _logger = Logger.getLogger(this.getClass());
    private Client client;

    private ReportSalesPerAlbumProducts report;
    private Grid grid;
    private HashMap<ReportSalesPerAlbumProducts.ReportColumn, BigDecimal> columnTotalMap;
    private static final int _ROW_DESCRIPTORS_SPAN = 2; // How many columns are describing the values??



    public void onCreate()
    {
        // does the reading of the client cause problems with a hibernate query?
        this.client = (Client) Executions.getCurrent().getDesktop().getSession().getAttribute(Registry._SESSION_CLIENT_NAME);
        _logger.debug("ZReportSalesAlbumAndProduct.onCreate");
    }

    /**
     * Report shall be lazy loaded upon request. Called via script in report-zul page
     */
    public void initReport()
    {
        // use cached objects if we were called before:
        if (grid==null && report==null)
        {
            adjustTabbox();
            report = getReport();
            grid = createGridColumns();
        }
        initReportRows();
    }

    /**
     * Create the grid content row by row; Is called every time the view is updated (type of report changed)
     */
    private void initReportRows()
    {

        // todo the ReportColumn must be an interface!!!!
        columnTotalMap = new HashMap<ReportSalesPerAlbumProducts.ReportColumn, BigDecimal>();
        // detach rows if they exist;
        if (grid.getRows()!=null)
        {
            grid.getRows().detach();
        }
        Rows rows = new Rows();
        grid.appendChild(rows);
        appendValueRows(rows);
        appendTotalRow(rows);

    }

    private void appendValueRows(Rows rows)
    {
        // for each row that is in the report, go through all the columns and insert the value that is of interest.
        // different strategy could be (could perform better than sorting): get the events first and then loop through the categories in the event and get the albums to the category
        SortedMap<Album,RowSalesPerAlbumProducts> albumRowMap = report.getAlbumAlbumreportMap();

        Event lastEvent = null; // this var is used to check for event-change in the list
        for (Album album : albumRowMap.keySet())
        {
            RowSalesPerAlbumProducts reportRow = albumRowMap.get(album);
            Row row = new Row();
            rows.appendChild(row);


            if (lastEvent==null || !album.getEvent().equals(lastEvent))
            {// lastEvent different from this event:
                createNewEventRowEntries(row,reportRow);
            }
            else
            {
                // still same event create
                createSameEventRowEntry(row,reportRow);
            }
            lastEvent = album.getEvent();
        }
    }


    /**
     * At the end of the report, insert a total row
     * @param rows zk rows
     */
    private void appendTotalRow(Rows rows)
    {
        Row totalRow = new Row();
        totalRow.setSpans(_ROW_DESCRIPTORS_SPAN +"");
        totalRow.setAlign("left");
        rows.appendChild(totalRow);
        final Label totalLabel = new Label("TOTAL");
        totalLabel.setStyle("font-weight:bold;");
        // width = 250 px
        totalRow.appendChild(totalLabel);
        Label totalValueLabel;
        BigDecimal grandTotal = new BigDecimal(0);
        for (ReportSalesPerAlbumProducts.ReportColumn reportColumn : report.getColumns())
        {
            final BigDecimal value = columnTotalMap.get(reportColumn);
            totalValueLabel = new Label(value.toString() + " " + report.getReportUnit());
            totalValueLabel.setStyle("font-weight:bold;");
            totalRow.appendChild(totalValueLabel);
            grandTotal = grandTotal.add(value);
        }
        // insert grand total at the end of the last row
        final Label grandTotalLabel = new Label(grandTotal + " " + report.getReportUnit());
        grandTotalLabel.setStyle("font-weight:bold;");
        totalRow.appendChild(grandTotalLabel);
    }

    /**
     * The tabbox is set to default 900 px; for the reports, we need more horizonal space
     */
    private void adjustTabbox()
    {
        Tabbox tbox = (Tabbox)getFellow("tbox");
        System.out.println("tbox.getId() = " + tbox.getId());
        System.out.println("tbox.getWidth() = " + tbox.getWidth());
        tbox.setWidth("1500px");
    }



    /**
     * for each row, go through the productType/price entries and insert the value.
     * First two entries are album(category) / event description
     * @param row the ZUL row
     * @param reportRow ReportRow class
     */
    private void createNewEventRowEntries(Row row, RowSalesPerAlbumProducts reportRow)
    {
        final Label eventLabel = new Label(reportRow.getReportAlbum().getEvent().getLongTitle());
        eventLabel.setStyle("font-weight:bold;");
        row.appendChild(eventLabel);
        final Label eventCategoryLabel = new Label(reportRow.getReportAlbum().getEventCategory().getTitle());
        row.appendChild(eventCategoryLabel);
        row.setAlign("right");

        insertRowValues(row, reportRow);


    }


    /**
     * Same event, no event title necessary; span first two cells
     * @param row zul row
     * @param reportRow report row
     */
    private void createSameEventRowEntry(Row row, RowSalesPerAlbumProducts reportRow)
    {
        final Label eventCategoryLabel = new Label(reportRow.getReportAlbum().getEventCategory().getTitle());
        row.appendChild(eventCategoryLabel);
        row.setSpans("2");
        row.setAlign("right");

        insertRowValues(row, reportRow);

    }


    /**
     * Loops through all columns of one row and inserts the values (if existing)
     * @param row zul row
     * @param reportRow row model; contains data for one album in one row
     */
    private void insertRowValues(Row row, RowSalesPerAlbumProducts reportRow)
    {
        // CAUTION: this is completely different than the other report. todo adjust somehow

        // init total:
        report.setTotalRowValue(new BigDecimal(0));
        // and now loop to the columns and insert a value (if possible)
        for (ReportSalesPerAlbumProducts.ReportColumn column : report.getColumns())
        {
            // for each column:
            final EntryProductSale productSale = reportRow.getProductSaleFor(column.getProductType(), column.getPrice());
            if (productSale!=null)
            {
                final BigDecimal value; // can be numbers sold, turnover or earnings
                Label rowCellEntry;
                try
                {
                    value = report.calculateReportValue(productSale,report.getReportType());
                    rowCellEntry = new Label(value + " "  + report.getReportUnit());
                    // when we have a value, we update map for the column total with the value
                    updateColumnTotal(column,value);
                } catch (UnartigException e)
                {
                    _logger.error("Error inserting row value",e);
                    rowCellEntry = new Label("--ERROR--");
                }
                row.appendChild(rowCellEntry);
            }
            else
            {
                // no value
                row.appendChild(new Label("-"));
            }
        } // end of regular columns

        // add total for row:
        row.appendChild(new Label(report.getTotalRowValue()+" " + report.getReportUnit()));

    }


    /**
     * Update the total for this column with the current value
     * @param reportColumn the reportColumn object
     * @param value the current report value
     */
    private void updateColumnTotal(ReportSalesPerAlbumProducts.ReportColumn reportColumn, BigDecimal value)
    {
        BigDecimal currentColumnTotalValue = columnTotalMap.get(reportColumn);
        BigDecimal newColumnTotalValue;
        if (currentColumnTotalValue !=null)
        {
            newColumnTotalValue = currentColumnTotalValue.add(value);
        }
        else
        {
            newColumnTotalValue = value;
        }
        columnTotalMap.put(reportColumn,newColumnTotalValue);
    }


    private Grid createGridColumns()
    {
        Grid grid = new Grid();
        this.appendChild(grid);
        grid.setWidth("100%");
        Columns columns = new Columns();
        columns.setSizable(true);
        grid.appendChild(columns);
        final Column eventColumn = new Column("Anlass");
        eventColumn.setWidth("150px");
        eventColumn.setAlign("right");
        columns.appendChild(eventColumn);

        final Column categoryColumn = new Column("Kategorie");
        categoryColumn.setWidth("150px");
        categoryColumn.setAlign("right");
        columns.appendChild(categoryColumn);
        for (ReportSalesPerAlbumProducts.ReportColumn reportColumn : report.getColumns())
        {
            final Column column = new Column(reportColumn.getProductType().getName() + " (" + reportColumn.getPrice().getPriceCHF() + " CHF)");
            column.setWidth("100px");
            columns.appendChild(column);
        }
        columns.appendChild(new Column("Gesamt"));

        return grid;
    }


    /**
     * Given all relevant orderitems, construct a report from that.
     * @return Report Class
     */
    private ReportSalesPerAlbumProducts getReport()
    {
        ReportingDAO reportingDao = new ReportingDAO();
        _logger.debug("Generating Report for userprofile :" + client.getUserProfile().getUserProfileId());

        if (client.isAdmin()) {
            // return all orderitems
            return new ReportSalesPerAlbumProducts(reportingDao.getReportOrderItems());
        } else {
            // return orderitems per logged in client
            return new ReportSalesPerAlbumProducts(reportingDao.getReportOrderItems(client));
        }
    }



    /**
     * The type of report has been changed. Read the type parameter. Reload the table with the new report value.
     * @param type type code for report
     */
    public void setReportType(String type)
    {
        _logger.debug("Type of report = " + type);
        // cut the "type" at the beginning and make it an integer. UGLY
        final int typeInt = Integer.parseInt(type.replace("type", ""));
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
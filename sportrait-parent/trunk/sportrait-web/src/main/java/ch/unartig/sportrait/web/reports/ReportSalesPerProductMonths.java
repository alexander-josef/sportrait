package ch.unartig.sportrait.web.reports;

import ch.unartig.u_core.model.ReportMonthlySalesSummary;
import ch.unartig.u_core.ordering.colorplaza.OipsPidMapper;
import ch.unartig.u_core.exceptions.UnartigException;

import java.util.List;
import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Column;
import org.zkoss.zul.Row;
import org.zkoss.zul.Label;

/**
 * Container for Report Rows of type RowSalesPerAlbumProducts
 * User: alexanderjosef
 * Date: Nov 1, 2007
 * Time: 9:51:21 AM
 */
public class ReportSalesPerProductMonths extends MonthlyReport
{
    final Logger _logger = Logger.getLogger(this.getClass());

    /**
     * Constructor for Report. Takes the relevant orderitems and creates a report.
     * Developer Note: Performance of this construction should be watched. If necessary, use better query and less construction in Java.
     * @param reportOrderitems injected by the view; this list contains the raw meterial for the report
     */
    public ReportSalesPerProductMonths(List<ReportMonthlySalesSummary> reportOrderitems)
    {
        super(reportOrderitems);
    }


    /**
     * Depending on the kind of report, return the correct value.
     * @param monthlySalesSummary the object that contains the product sale information
     * @param reportType type code for this report value
     * @return string value that wille be shown in the report.
     * @throws ch.unartig.u_core.exceptions.UnartigException if something goes wrong calculating the report values or totals
     */
    public BigDecimal calculateReportValue(ReportMonthlySalesSummary monthlySalesSummary, int reportType) throws UnartigException
    {
        BigDecimal retVal;
        final BigDecimal numbersSold = new BigDecimal(monthlySalesSummary.getQuantitySum());
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
                final BigDecimal turnoverChf = numbersSold.multiply(monthlySalesSummary.getPriceCHF());
                totalRowValue = totalRowValue.add(turnoverChf);
                retVal = turnoverChf;
                break;
            }
            case ReportSalesPerAlbumProducts._EARNINGS_PRODUCTS_SOLD_REPORT:
            {
                final BigDecimal productEarning;
                try
                {
                    productEarning = OipsPidMapper.getInstance().getUnartigEarnings(monthlySalesSummary.getProductTypeId(), monthlySalesSummary.getPriceId());
                    final BigDecimal earnings = numbersSold.multiply(productEarning);
                    totalRowValue = totalRowValue.add(earnings);
                    retVal = earnings;
                } catch (UnartigException e)
                {
                    _logger.error("Error getting Product information",e);
                    throw new UnartigException("Number calculation error",e);
                }

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

    public String getRowLabel3(ReportMonthlySalesSummary summaryEntry)
    {
        return summaryEntry.getPriceCHF().toString();
    }

    public boolean isSameRow(ReportMonthlySalesSummary summaryEntry, ReportMonthlySalesSummary lastEntry)
    {
        // if last entry has not yet been initialized we are in the first row.
        return lastEntry != null && isSamePriceProduct(summaryEntry, lastEntry);
    }

    public void insertDescriptiveColumns(Columns columns)
    {
        final Column oipsPid = new Column("OIPS-PID");
        oipsPid.setWidth("50px");
        oipsPid.setAlign("right");
        columns.appendChild(oipsPid);

        final Column description = new Column("Bezeichnung");
        description.setWidth("150px");
        description.setAlign("right");
        columns.appendChild(description);

        final Column priceChf = new Column("Preis CHF");
        priceChf.setWidth("50px");
        priceChf.setAlign("right");
        columns.appendChild(priceChf);
    }

    public int getNumberOfDescriptiveRows()
    {
        return 3;
    }

    /**
     * Insert row titles, OIPS-PID, Description, Price CHF
     *
     * @param row          zk row
     * @param summaryEntry data value object with the summary result
     */
    public void insertRowDescriptors(Row row, ReportMonthlySalesSummary summaryEntry)
    {
        final String externalProductId = OipsPidMapper.getInstance().getMappedProductId(summaryEntry.getProductTypeId(), summaryEntry.getPriceId());
        final Label label1 = new Label(externalProductId);
        label1.setWidth("50px");
        label1.setStyle("font-weight:bold;");
        row.appendChild(label1);

        final Label label2 = new Label(summaryEntry.getProductTypeName());
        label2.setWidth("150px");
        label2.setStyle("font-weight:bold;");
        row.appendChild(label2);

        final Label label3 = new Label(summaryEntry.getPriceCHF().toString());
        row.appendChild(label3);
        label3.setWidth("50px");
        label3.setStyle("font-weight:bold;");
        row.setAlign("right");
    }

    private boolean isSamePriceProduct(ReportMonthlySalesSummary summaryEntry, ReportMonthlySalesSummary lastEntry)
    {
        return summaryEntry.getPriceCHF().equals(lastEntry.getPriceCHF()) && summaryEntry.getProductTypeId().equals(lastEntry.getProductTypeId());
    }

    /**
     * row 1 label for this implementation is the external product id
     * @return
     * @param summaryEntry
     */
    public String getRowLabel1(ReportMonthlySalesSummary summaryEntry)
    {
        final String externalProductId = OipsPidMapper.getInstance().getMappedProductId(summaryEntry.getProductTypeId(), summaryEntry.getPriceId());
        return externalProductId;
    }

    public String getRowLabel2(ReportMonthlySalesSummary summaryEntry)
    {
        return summaryEntry.getProductTypeName();
    }

    // GETTER SETTER


}
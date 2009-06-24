package ch.unartig.sportrait.web.reports;

import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.OrderItem;
import ch.unartig.studioserver.model.Price;
import ch.unartig.studioserver.model.ProductType;
import ch.unartig.u_core.ordering.colorplaza.OipsPidMapper;
import ch.unartig.u_core.exceptions.UnartigException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * Container for Report Rows of type RowSalesPerAlbumProducts
 * User: alexanderjosef
 * Date: Nov 1, 2007
 * Time: 9:51:21 AM
 */
public class ReportSalesPerAlbumProducts
{
    final Logger _logger = Logger.getLogger(this.getClass());

    private SortedSet<ReportColumn> columns ; // keep a list of column entries that this report has.

    private SortedMap<Album,RowSalesPerAlbumProducts> albumAlbumreportMap;
    public static final int _NUMBER_PRODUCTS_SOLD_REPORT = 1;
    public static final int _TURNOVER_PRODUCTS_SOLD_REPORT = 2;
    public static final int _EARNINGS_PRODUCTS_SOLD_REPORT = 3;

    public static Map<Integer,String> reportTypeUnitMap = new HashMap<Integer,String>(); 

    private int reportType = 1; // init report to numbers sold

    private BigDecimal totalRowValue = new BigDecimal(0);


    static
    {
        // what if we have units that depend upon the client? Move to object initialization and make map non-static
        reportTypeUnitMap.put(_NUMBER_PRODUCTS_SOLD_REPORT,"");
        reportTypeUnitMap.put(_TURNOVER_PRODUCTS_SOLD_REPORT,"CHF");
        reportTypeUnitMap.put(_EARNINGS_PRODUCTS_SOLD_REPORT,"CHF");
    }

    /**
     * Constructor for Report. Takes the relevant orderitems and creates a report.
     *  for each orderitem, update the report:
     * 1) get the album that is concerned by this orderitem
     * 2) retrieve the row - if any -  in the report for this album
     * 3) create a row if none exists
     * 4) or append the existing row with the values in the orderitem.
     * Developer Note: Performance of this construction should be watched. If necessary, use better query and less construction in Java.
     * @param orderitems from data warehouse for reports.
     */
    public ReportSalesPerAlbumProducts(List<OrderItem> orderitems)
    {
        // init colums
        columns = new TreeSet<ReportColumn>();
        albumAlbumreportMap = new TreeMap<Album, RowSalesPerAlbumProducts>(new Album.EventCategoryComparator());

        for (OrderItem orderItem : orderitems)
        {
            Album itemAlbum = orderItem.getPhoto().getAlbum();
            RowSalesPerAlbumProducts row = albumAlbumreportMap.get(itemAlbum);
            if (row ==null)
            { // no row for album exists so far
                // construct row
                row = new RowSalesPerAlbumProducts(orderItem,this);
                // and add it to the map
                albumAlbumreportMap.put(orderItem.getPhoto().getAlbum(), row);
            }
            else
            { // recalculate / append existing row
                row.append(orderItem);
            }
        }
    }


    public void addColumn(ProductType productType, Price price)
    {
        // set only accept a new Column
        final ReportColumn reportColumn = new ReportColumn(productType, price);
        columns.add(reportColumn);
    }



    // GETTER SETTER

    public Set<ReportColumn> getColumns()
    {
        return columns;
    }

    public SortedMap<Album, RowSalesPerAlbumProducts> getAlbumAlbumreportMap()
    {
        return albumAlbumreportMap;
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
     * Depending on the kind of report, return the correct value.
     * @param productSale the object that contains the product sale information
     * @param reportType type code for this report value
     * @return string value that wille be shown in the report.
     * @throws ch.unartig.u_core.exceptions.UnartigException in case the values cannot be calculated
     */
    public BigDecimal calculateReportValue(EntryProductSale productSale, int reportType) throws UnartigException
    {
        BigDecimal retVal;
        final BigDecimal numbersSold = productSale.getNumbersSold();
        switch (reportType)
        {
            case _NUMBER_PRODUCTS_SOLD_REPORT:
            {
                retVal = numbersSold;
                totalRowValue = totalRowValue.add(numbersSold);
                break;
            }
            case _TURNOVER_PRODUCTS_SOLD_REPORT:
            {
                final BigDecimal turnoverChf = numbersSold.multiply(productSale.getPrice().getPriceCHF());
                totalRowValue = totalRowValue.add(turnoverChf);
                retVal = turnoverChf;
                break;
            }
            case _EARNINGS_PRODUCTS_SOLD_REPORT:
            {
                final BigDecimal productEarning;
                try
                {
                    productEarning = OipsPidMapper.getInstance().getUnartigEarnings(productSale.getProductType().getProductTypeId(), productSale.getPrice().getPriceId());
                    final BigDecimal earnings = numbersSold.multiply(productEarning);
                    totalRowValue = totalRowValue.add(earnings);
                    retVal = earnings;
                } catch (UnartigException e)
                {
                    _logger.error("Error getting Product information",e);
                    throw new UnartigException("Number calculation error",e);
                }
                // multiply with the number sold with the actual earning for a product
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
     * Use a static map to resolve the report unit string according to the report type.
     * @return Unit as String, example "CHF"
     */
    public String getReportUnit()
    {
        return reportTypeUnitMap.get(reportType);
    }
    
    public BigDecimal getTotalRowValue()
    {
        return totalRowValue;
    }

    public void setTotalRowValue(BigDecimal totalRowValue)
    {
        this.totalRowValue = totalRowValue;
    }

    // INNER

    /**
     * This class represents a column for the album and products report.
     */
    public class ReportColumn implements Comparable
    {
        private Price price;
        private ProductType productType;

        public ReportColumn(ProductType productType, Price price)
        {
            this.productType = productType;
            this.price = price;
        }

        /**
         * Equality is defined by the equality of the two fields, price and productType
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
            return isPriceProducttypeEqual(columnToCompare);
        }

        private boolean isPriceProducttypeEqual(ReportColumn columnToCompare)
        {
            return ((this.price.equals(columnToCompare.getPrice())) &&  (this.productType.equals(columnToCompare.getProductType())) );
        }

        /**
         * Overriden to define equality
         * @return the same hashCode if the objects are considered equal.
         */
        public int hashCode()
        {
            int result;
            result = price.hashCode();
            result = 31 * result + productType.hashCode();
            return result;
        }

        /**
         *
         * @param o comparable object
         * @return result
         */
        public int compareTo(Object o)
        {
            ReportColumn columnToCompare = (ReportColumn) o;

            // first order by product type
            if (compareProductTypeId(columnToCompare)!=0)
            {
                return compareProductTypeId(columnToCompare);
            }
            else 
            {
                // second order by price CHF
                return comparePrice(columnToCompare);
            }

        }

        /**
         * return the other productTypeId compared to this productTypeId. If 
         * @param columnToCompare other column
         * @return id comparison
         */
        private int compareProductTypeId(ReportColumn columnToCompare)
        {
            return this.getProductType().getProductTypeId().compareTo(columnToCompare.getProductType().getProductTypeId());
        }


        private int comparePrice(ReportSalesPerAlbumProducts.ReportColumn columnToCompare)
        {
            return this.price.getPriceCHF().compareTo(columnToCompare.getPrice().getPriceCHF());
        }



        public Price getPrice()
        {
            return price;
        }

        public ProductType getProductType()
        {
            return productType;
        }

    }

}

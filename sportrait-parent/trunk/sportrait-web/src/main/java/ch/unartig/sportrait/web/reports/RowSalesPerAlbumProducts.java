package ch.unartig.sportrait.web.reports;

import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.OrderItem;
import ch.unartig.studioserver.model.Price;
import ch.unartig.studioserver.model.ProductType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Report Row class. This class describes the sales for one album listed by products.
 * @author alexanderjosef
 * Date: Oct 31, 2007
 * Time: 10:49:26 AM
 */
public class RowSalesPerAlbumProducts
{
    private Album reportAlbum;
    private List<EntryProductSale> productSales; // list of EntryProductSale; one entry for a productType / Price combination

    private BigDecimal albumTotal; // sum up on album row (numbers sold? turnover ? earnings ?)
    private ReportSalesPerAlbumProducts report; // the report this row is part of


    /**
     * Constructor for new Album - ReportRow.
     * @param orderItem OrderItem that contains the initial information to create this row.
     * @param reportSalesPerAlbumProducts Keep a reference to the report that this row is part of
     */
    public RowSalesPerAlbumProducts(OrderItem orderItem, ReportSalesPerAlbumProducts reportSalesPerAlbumProducts)
    {
        // init
        report = reportSalesPerAlbumProducts;
        productSales = new ArrayList<EntryProductSale>();
        albumTotal = new BigDecimal(0);
        // set the album
        reportAlbum = orderItem.getPhoto().getAlbum();

        // set the category?

        // EntryProductSale exists for this productType / Price combination ? Update Or create a EntryProductSale
        updateOrCreateProductSale(orderItem);
    }

    private void updateOrCreateProductSale(OrderItem orderItem)
    {
        final ProductType productType = orderItem.getProduct().getProductType();
        final Price price = orderItem.getProduct().getPrice();

        EntryProductSale productSale = getProductSaleFor(productType, price);
        if (productSale!=null)
        {
            // update the available productSale
            productSale.update(orderItem.getQuantity());
        }
        else
        {
            // create new productSale entry
            productSale = new EntryProductSale(productType,price,orderItem.getQuantity());
            productSales.add(productSale);

            report.addColumn(productType,price);

        }
        // todo update the total for this row (which currency? all currencies?)
    }


    /**
     * Retrieve an entry for a column defined by productType and Price
     * @param productType product type object
     * @param price price object
     * @return a report entry class EntryProductSale
     */
    public EntryProductSale getProductSaleFor(ProductType productType, Price price)
    {
        // possible performance improvement: maintain a map with array {prodTypeID,priceID} as key and reportProductSale as value
        EntryProductSale retVal = null;
        for (EntryProductSale reportProductSale : productSales)
        {
            if (reportProductSale.getProductType().equals(productType) && reportProductSale.getPrice().equals(price))
            {
                return reportProductSale;
            }
        }
        return retVal;
    }

    /**
     * Append / recalculate this report row for the album with the passed orderitem
     * @param orderItem OrderItem that contains the information to update this row.
     */
    public void append(OrderItem orderItem)
    {
        updateOrCreateProductSale(orderItem);
    }

    public Album getReportAlbum()
    {
        return reportAlbum;
    }

    public List<EntryProductSale> getProductSales()
    {
        return productSales;
    }

    public BigDecimal getAlbumTotal()
    {
        return albumTotal;
    }
}

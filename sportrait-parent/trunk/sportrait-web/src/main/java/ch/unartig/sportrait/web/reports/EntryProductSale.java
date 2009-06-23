package ch.unartig.sportrait.web.reports;

import ch.unartig.studioserver.model.Price;
import ch.unartig.studioserver.model.ProductType;

import java.math.BigDecimal;

/**
 * Sums up all (numbersSold / turnaround / earnings) for a (productType / Price) combination that has been sold for the album
 */
public class EntryProductSale
{

    private ProductType productType;
    private Price price;

    private BigDecimal numbersSold;

    /**
     * The constructor sets the first parameters according to the orderitem
     *
     * @param productType type of product, i.e. 10x15 print, Digital negative etc
     * @param price       Price
     * @param quantity    quantity that this price/producttype entry has been sold
     */
    public EntryProductSale(ProductType productType, Price price, Integer quantity)
    {
        this.productType = productType;
        this.price = price;
        numbersSold = new BigDecimal(quantity);
    }

    /**
     * Update the numbers sold for this productSale
     *
     * @param quantity quantity from the orderitem entry
     */
    public void update(Integer quantity)
    {   // update numbers sold
        numbersSold = numbersSold.add(new BigDecimal(quantity));
    }

    public ProductType getProductType()
    {
        return productType;
    }

    public Price getPrice()
    {
        return price;
    }


    public BigDecimal getNumbersSold()
    {
        return numbersSold;
    }

}

/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 13.03.2007$
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
 * Revision 1.3  2007/06/01 06:21:47  alex
 * wrong mapping for mousepad
 *
 * Revision 1.2  2007/05/03 19:09:50  alex
 * complete mappings for colorplaza, 3 price tables
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.3  2007/03/15 21:45:27  alex
 * no more price segment
 *
 * Revision 1.2  2007/03/14 02:41:02  alex
 * initial checkin
 *
 * Revision 1.1  2007/03/13 23:24:06  alex
 * initial checkin
 *
 ****************************************************************/
package ch.unartig.studioserver.colorplaza;

import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.studioserver.businesslogic.ProductMapperIF;
import ch.unartig.studioserver.model.Product;
import ch.unartig.studioserver.model.ProductType;
import ch.unartig.studioserver.model.Price;

import java.math.BigDecimal;

/**
 * Mapper between unartig album products and the colorplaza oips products
 */
public class OipsPidMapper implements ProductMapperIF {

    private static OipsPidMapper instance;
    /**
     * 2-dimensional arrays for producttype-price-oipspid mapping<br/>
     * [ProductTypeId][PriceId]
     */
    Object[][] productPricesOipsid = new Object[30][30];
    private static final int _OIPS_PID_INDEX = 0; // location of the oips pid information in the array
    private static final int _UNARTIG_EARNINGS_INDEX = 1 ; // index for the position of the unartig earnings value in the array


    private OipsPidMapper() {
    }

    public static OipsPidMapper getInstance() {
        if (instance==null)
        {
            instance = new OipsPidMapper();
            instance.init();
        }
        return instance;
    }

    /**
     * called only once upon creation. set all static data
     * [ProductTypeId] [PriceId]
     * The stored value is a String Array in the form: {"OIPS_PiD","Earnings-Unartig after production/invoicing"}
     */
    private void init() {
        // digifoto 400 x 600 (all digital oipspid)
        productPricesOipsid[2][19]= new String[] {"998","0.45"};
        productPricesOipsid[2][22]= new String[] {"998","3.51"};
        productPricesOipsid[2][28]= new String[] {"998","14.40"};
        // digital negative
        productPricesOipsid[3][2]= new String[] {"998","4.95"};
        productPricesOipsid[3][26]= new String[] {"998","26.55"};
        productPricesOipsid[3][29]= new String[] {"998","8.91"};
        // 10 x 15 print
        productPricesOipsid[4][1]= new String[] {"6301","4.05"};
        productPricesOipsid[4][10]= new String[] {"6501","8.55"};
        productPricesOipsid[4][19]= new String[] {"6101","0"};
        // 11 x 17 print
        productPricesOipsid[5][2]= new String[] {"6304","4.455"};
        productPricesOipsid[5][11]= new String[] {"6504","10.305"};
        productPricesOipsid[5][20]= new String[] {"6104","0"};
        // 13 x 19 print
        productPricesOipsid[6][3]= new String[] {"6302","5.31"};
        productPricesOipsid[6][12]= new String[] {"6502","11.61"};
        productPricesOipsid[6][21]= new String[] {"6102","0"};
        // 20 x 30 print
        productPricesOipsid[7][4]= new String[] {"6303","9.09"};
        productPricesOipsid[7][13]= new String[] {"6503","21.69"};
        productPricesOipsid[7][22]= new String[] {"6103","0"};
        // 30 x 45 print
        productPricesOipsid[8][5]= new String[] {"6324","10.8"};
        productPricesOipsid[8][14]= new String[] {"6524","28.8"};
        productPricesOipsid[8][23]= new String[] {"6124","0"};
        // 40 x 60 print
        productPricesOipsid[9][6]= new String[] {"6317","13.5"};
        productPricesOipsid[9][15]= new String[] {"6517","46.8"};
        productPricesOipsid[9][24]= new String[] {"6117","0"};
        // 50 x 70 print
        productPricesOipsid[10][7]= new String[] {"6310","15.3"};
        productPricesOipsid[10][16]= new String[] {"6510","55.8"};
        productPricesOipsid[10][25]= new String[] {"6110","0"};
        // Mousepad

         // leave this until products 26,134 and 89 are set back to price 9 !!!!! Then delete.
        productPricesOipsid[12][6]= new String[] {"6316","11.7"};
        productPricesOipsid[12][15]= new String[] {"6516","20.7"};
        productPricesOipsid[12][24]= new String[] {"6116","0"};
        // then delete

//        correct settings:
        productPricesOipsid[12][9]= new String[] {"6316","11.7"};
        productPricesOipsid[12][18]= new String[] {"6516","20.7"};
        productPricesOipsid[12][27]= new String[] {"6116","0"};

    }

    /**
     * @see ch.unartig.studioserver.businesslogic.ProductMapperIF
     */
    public String getMappedProductId(Product product)
    {
        // todo catch possible out of array exception and re-throw typed exception
        return getMappedProductId(product.getProductType().getProductTypeId(),product.getPrice().getPriceId());
    }

    public String getMappedProductId(Long productTypeId,Long priceId)
    {
        final String[] mappedArray = getMappedArray(productTypeId,priceId);
        return mappedArray[_OIPS_PID_INDEX];

    }
    public BigDecimal getUnartigEarnings(Product product) throws UnartigException
    {
        return getUnartigEarnings(product.getProductType(),product.getPrice());
    }

    /**
     * Delegating method to ch.unartig.studioserver.businesslogic.ProductMapperIF#getUnartigEarnings(java.lang.Long,java.lang.Long)
     * @see ch.unartig.studioserver.businesslogic.ProductMapperIF#getUnartigEarnings(java.lang.Long,java.lang.Long)
     * @param productType product type object
     * @param price price object
     * @return unartig earnings from colorplaza
     * @throws UnartigException
     */
    public BigDecimal getUnartigEarnings(ProductType productType, Price price) throws UnartigException
    {
        return getUnartigEarnings(productType.getProductTypeId(),price.getPriceId());
    }


    public BigDecimal getUnartigEarnings(Long productTypeId, Long priceId) throws UnartigException
    {
        final String string;
        BigDecimal retVal;
        try
        {
            final String[] mappedArray= getMappedArray(productTypeId,priceId);
            string = mappedArray[_UNARTIG_EARNINGS_INDEX];

            retVal = new BigDecimal(string);
        } catch (Exception e)
        {
           throw new UnartigException("Error getting the mapped values",e);
        }
        return retVal;
    }

    private String[] getMappedArray(Product product)
    {
        final Long productTypeId = product.getProductType().getProductTypeId();
        final Long priceId = product.getPrice().getPriceId();
        return (String[])productPricesOipsid[productTypeId.intValue()][priceId.intValue()];
    }



    private String[] getMappedArray(final Long productTypeId, final Long priceId)
    {
        return (String[])productPricesOipsid[productTypeId.intValue()][priceId.intValue()];
    }



}

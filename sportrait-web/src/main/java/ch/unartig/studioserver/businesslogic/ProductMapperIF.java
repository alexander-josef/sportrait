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
* Revision 1.1  2007/03/27 15:54:27  alex
* initial commit sportrait code base
*
* Revision 1.2  2007/03/14 02:41:01  alex
* initial checkin
*
* Revision 1.1  2007/03/13 23:22:44  alex
* initial checkin
*
****************************************************************/
package ch.unartig.studioserver.businesslogic;

import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.studioserver.model.Price;
import ch.unartig.studioserver.model.Product;
import ch.unartig.studioserver.model.ProductType;

import java.math.BigDecimal;


/**
 *
 */
public interface ProductMapperIF {

    /**
     * Given a Product, map the product to the customID
     * @param product the unartig product that needs an external ID mapped
     * @return the string that identifies the mapped product
     */
    public String getMappedProductId(Product product);

    
    public String getMappedProductId(Long productTypeId,Long priceId);


    /**
     *
     * @param product product object
     * @return The Amount that goes to unartig from this product after costs from a partner for  production, shipping, invoicing etc. have been deducted.
     * @throws UnartigException In case a mapping error occurs.
     */
    public BigDecimal getUnartigEarnings(Product product) throws UnartigException;
    public BigDecimal getUnartigEarnings(ProductType productType, Price price) throws UnartigException;
    public BigDecimal getUnartigEarnings(Long productTypeId, Long priceId) throws UnartigException;

}

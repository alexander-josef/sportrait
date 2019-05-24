/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 9, 2005$
 *
 * Copyright (c) 2005 unartig AG  --  All rights reserved
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
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.2  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.1  2005/11/11 10:20:37  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 ****************************************************************/
package ch.unartig.studioserver.model;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "orderitems")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class OrderItem  implements java.io.Serializable {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long orderItemId;

    private Integer quantity;
    private String photoFileName;

    @ManyToOne
    @JoinColumn(name = "photoid",nullable = false)
    private Photo photo;

    @ManyToOne
    @JoinColumn(name = "productid",nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "orderid",nullable = false)
    private Order order;

    public OrderItem()
    {
    }

    public OrderItem(Photo photo, Product product, int quantity, Order order)
    {
        setQuantity(quantity);
        setPhoto(photo);
        setProduct(product);
        setOrder(order);
        
    }


    public Long getOrderItemId() {
        return this.orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPhotoFileName() {
        return this.photoFileName;
    }

    public void setPhotoFileName(String photoFileName) {
        this.photoFileName = photoFileName;
    }

    public Photo getPhoto() {
        return this.photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("orderItemId").append("='").append(getOrderItemId()).append("' ");
      buffer.append("quantity").append("='").append(getQuantity()).append("' ");
      buffer.append("photoFileName").append("='").append(getPhotoFileName()).append("' ");
      buffer.append("photo").append("='").append(getPhoto()).append("' ");
      buffer.append("product").append("='").append(getProduct()).append("' ");
      buffer.append("order").append("='").append(getOrder()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

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
 * Revision 1.8  2006/11/21 08:23:19  alex
 * news-fenster
 *
 * Revision 1.7  2006/11/14 16:19:24  alex
 * digital order
 *
 * Revision 1.6  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.5  2006/10/17 08:07:06  alex
 * creating the order hashes
 *
 * Revision 1.4  2006/06/29 15:03:58  alex
 * reporting, download photos check in
 *
 * Revision 1.3  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.2  2005/11/18 19:15:52  alex
 * stuff ...
 *
 * Revision 1.1  2005/11/09 21:59:36  alex
 * Order process classes and logic,
 * database creation script now inserts start-data, sql scripts
 * build script
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Processed Order after checking out the shopping cart
 */
@Entity
@Table(name = "orders")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Order implements java.io.Serializable {

    @Transient
    Logger _logger = LogManager.getLogger(getClass().getName());

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long orderId;

    private Date orderDate;
    private Date uploadCompletedDate;
    private String oipsOrderId;

    @ManyToOne(fetch = FetchType.EAGER,optional = false)
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "customerid")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrderItem> orderItems = new HashSet<OrderItem>(0);

    /**
     * standard noarg constructor
     */
    public Order()
    {
    }

    public Order(Date date)
    {
        setOrderDate(date);
    }

    /**
     * after an order has completly uploaded, write the orderid and the upload completed date to the db
     * TODO : rename in DB
     * TODO : add field "payment provider" to order object (i.e. "Paypal", or whoever processes a payment)
     * @param transactionId id given by payment processer (used to be copla, now Paypal)
     */
    public void confirmUpload(String transactionId)
    {
        setOipsOrderId(transactionId);
        setUploadCompletedDate(new Date());
    }

    /**
     * check the orderitems for digital photos
     *
     * @return true if the order contains digital photo orderitems, false if not
     */
    public boolean hasDigitalOrderItems()
    {
        // todo implement if needed
        return true;
    }

    /**
     * This method will be used to return all
     * items to create either the digital negativ or a standard format
     * <p/>
     * return a list with all orderitems that contain an order for a digital photo<br>
     * currently all photo-product will be processsed, i.e. the customer gets a small digi photo from every orderitem
     *
     * @return list of @see 'ch.unartig.studioserver.model.orderItem'
     */
    public Set getDigitalItems()
    {
        // todo if different from orderItems
        return getOrderItems();
    }

    /**
     * return true if order contains only digital products
     * <p> this can be used for instance to set a free delivery method, if no print products are involved in the order
     * @return true or false
     */
    public boolean isOnlyDigitalProducts()
    {
        _logger.debug("checking for digital products in order");
        for (Object o : getOrderItems()) {
            OrderItem scItem = (OrderItem) o;
            if (!scItem.getProduct().isDigitalProduct()) {
                _logger.debug("found non-digital product, returning false");
                return false;
            }
        }
        _logger.debug("all products digital, returning true");
        return true;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return this.orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getUploadCompletedDate() {
        return this.uploadCompletedDate;
    }

    public void setUploadCompletedDate(Date uploadCompletedDate) {
        this.uploadCompletedDate = uploadCompletedDate;
    }

    public String getOipsOrderId() {
        return this.oipsOrderId;
    }

    public void setOipsOrderId(String oipsOrderId) {
        this.oipsOrderId = oipsOrderId;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set orderItems) {
        this.orderItems = orderItems;
    }

    /**
     * toString
     * @return String
     */
     public String toString() {
	  StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("orderId").append("='").append(getOrderId()).append("' ");
      buffer.append("orderDate").append("='").append(getOrderDate()).append("' ");
      buffer.append("uploadCompletedDate").append("='").append(getUploadCompletedDate()).append("' ");
      buffer.append("oipsOrderId").append("='").append(getOipsOrderId()).append("' ");
      buffer.append("customer").append("='").append(getCustomer()).append("' ");
      buffer.append("]");

      return buffer.toString();
     }
}

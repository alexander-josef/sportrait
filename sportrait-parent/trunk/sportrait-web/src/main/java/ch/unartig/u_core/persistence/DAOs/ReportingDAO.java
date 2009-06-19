/*-*
 *
 * FILENAME  :
 *
 *    @author alex$
 *    @since Nov 5, 2007$
 *
 * Copyright (c) 2007 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision: $REVISION$
 *
 *    $Author: $AUTHOR$ $, $Locker$
 *
 *************************************************
 * $Log$
 *
 ****************************************************************/
package ch.unartig.u_core.persistence.DAOs;

import ch.unartig.studioserver.model.ReportMonthlySalesSummary;
import ch.unartig.studioserver.model.OrderItem;
import ch.unartig.studioserver.model.Photographer;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import ch.unartig.u_core.controller.Client;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.CacheMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * This class contains reporting related data access code.
 */
public class ReportingDAO
{

    final Logger _logger = Logger.getLogger(this.getClass());


    /**
     * Group orderitems of the same product/Price by month, filter by photographer
     * SQL:
     * <pre>
            select  pt.producttypeid, pt.name, pri.pricechf as pricechf, sum(quantity)*pricechf as orderitemtotal, date_part('year',uploadcompleteddate) as year, date_part('month',uploadcompleteddate) as month
            from orderitems oi
            join photos p on p.photoid = oi.photoid
            JOIN genericlevels album ON p.albumid = album.genericlevelid
            JOIN genericlevels event ON album.eventid = event.genericlevelid
            join products prod on prod.productid = oi.productid
            join orders o on o.orderid = oi.orderid
            join producttypes pt on pt.producttypeid = prod.producttypeid
            join prices pri on pri.priceid = prod.priceid

            group by pt.producttypeid, pt.name, pricechf, year,month
     * </pre>
     * @return
     * @param client
     */
    public List<ReportMonthlySalesSummary> getProductMonthsOrderitems(Client client)
    {
        List<ReportMonthlySalesSummary> orderItems = null;
//        List reportObjectList;

        // todo this fails if no uploadcompleteddate is available of an order record

//        String hqlQuery = "select " +
        String hqlQuery = "select new ch.unartig.studioserver.model.ReportMonthlySalesSummary(" +
                "   oi.product.productType.productTypeId, " +
                "   oi.product.price.priceId, " +
                "   oi.product.productType.name, " +
                "   oi.product.price.priceCHF , " +
                "   year(oi.order.uploadCompletedDate), " +
                "   month(oi.order.uploadCompletedDate), " +
                "   sum(oi.quantity) , " +
                "   oi.product.price.priceCHF * sum(oi.quantity)" +
                ")  \n" +
                " from OrderItem oi " +
                " where oi.photo.album.photographer = :photographer " +
                " group by oi.product.productType.productTypeId, oi.product.productType.name, oi.product.price.priceId, year(oi.order.uploadCompletedDate), month(oi.order.uploadCompletedDate), oi.product.price.priceCHF" +
                " order by oi.product.productType.productTypeId, oi.product.price.priceCHF";

        try
        {
            Query query = HibernateUtil.currentSession().createQuery(hqlQuery);
            query.setEntity("photographer",client.getPhotographer());
            orderItems = query.list();
//            reportObjectList = HibernateUtil.currentSession().createQuery(hqlQuery).list();
        } catch (HibernateException e)
        {
            _logger.error("Query error : ",e);
            throw new RuntimeException(e);
        }



        // todo debug remove me:
/*
        for (int i = 0; i < reportObjectList.size(); i++)
        {
            Object[] objects = (Object[]) reportObjectList.get(i);
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            for (int j = 0; j < objects.length; j++)
            {
                Object object = objects[j];
                System.out.println("object.getClass().getName() = " + object.getClass().getName());
            }
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        }
*/

        return orderItems;
//        return null;

    }


    /**
     * Query for orderitems accoring to logged in photographer
     * @param client The client object needed to query by photographer
     * @return
     */
    public List<OrderItem> getReportOrderItems(Client client)
    {
        PhotographerDAO photographerDao = new PhotographerDAO();
        Photographer photographer = photographerDao.load(client.getPhotographer().getPhotographerId());
        List retVal;
        try {
            Criteria criteria = HibernateUtil.currentSession().createCriteria(OrderItem.class)
                    .createAlias("photo", "p")
                    .createAlias("p.album", "a")
                    .add(Restrictions.eq("a.photographer",photographer))
                    ;
            retVal = criteria.list();
            _logger.debug("NON ADMIN QUERY : Size of list : " + retVal.size());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new RuntimeException(e);
        }
        return retVal;



//        _logger.debug("getting orderitems for ID : " + client.getPhotographer().getPhotographerId());
//        Query query = HibernateUtil.currentSession().createQuery("from OrderItem oi where oi.photo.album.photographer.photographerId = " + client.getPhotographer().getPhotographerId());
//        List<OrderItem> orderItems = query.list();
//        _logger.debug("orderItems.size() = " + orderItems.size());
//        return orderItems;
    }

    public List<OrderItem> getReportOrderItems()
    {
        HibernateUtil.currentSession().setCacheMode(CacheMode.IGNORE);
        final String hqlQuery = "from OrderItem";
        List<OrderItem> orderItems = HibernateUtil.currentSession().createQuery(hqlQuery).list();
        _logger.debug("orderItems.size() = " + orderItems.size());
        return orderItems;
    }

    /**
     * Query according to the following SQL:
     * <pre>
            select  photographer.photographerid, userprofile.lastname,sum(oi.quantity) as productsSold,sum(oi.quantity * pri.pricechf) as turnover,date_part('year',uploadcompleteddate) as year, date_part('month',uploadcompleteddate) as month
            from orderitems oi
            join photos p on p.photoid = oi.photoid
            JOIN genericlevels album ON p.albumid = album.genericlevelid
            JOIN photographers photographer ON album.photographerid = photographer.photographerid
            JOIN userprofiles userprofile ON userprofile.userprofileid = photographer.userprofileid
            JOIN genericlevels event ON album.eventid = event.genericlevelid
            join products prod on prod.productid = oi.productid
            join orders o on o.orderid = oi.orderid
            join producttypes pt on pt.producttypeid = prod.producttypeid
            join prices pri on pri.priceid = prod.priceid



	        group by year,month,photographer.photographerid, userprofile.lastname
	        order by year, month
     * </pre>
     * @return
     */
    public List<ReportMonthlySalesSummary> listTotalPhotographerSalesByMonth()
    {
        List<ReportMonthlySalesSummary> orderItems = null;
//        List reportObjectList;

        String hqlQuery = "select new ch.unartig.studioserver.model.ReportMonthlySalesSummary(" +
//        String hqlQuery = "select " +
                "   oi.photo.album.photographer.photographerId," +
                "   oi.photo.album.photographer.userProfile.lastName," +
                "   oi.photo.album.photographer.userProfile.firstName," +
                "   sum(oi.quantity) as productsSold, " +
                "   sum(oi.quantity * oi.product.price.priceCHF), " +
                "   YEAR(oi.order.uploadCompletedDate), " +
                "   MONTH(oi.order.uploadCompletedDate)" +
                ") " +
                " \n" +
                " from OrderItem oi " +
                " group by YEAR(oi.order.uploadCompletedDate),MONTH(oi.order.uploadCompletedDate), oi.photo.album.photographer.photographerId,oi.photo.album.photographer.userProfile.lastName,oi.photo.album.photographer.userProfile.firstName" +
                " order by YEAR(oi.order.uploadCompletedDate),MONTH(oi.order.uploadCompletedDate)";

        try
        {
            orderItems = HibernateUtil.currentSession().createQuery(hqlQuery).list();
//            reportObjectList = HibernateUtil.currentSession().createQuery(hqlQuery).list();
        } catch (HibernateException e)
        {
            _logger.error("Query error : ",e);
            throw new RuntimeException(e);
        }
        // todo debug remove me:
//        for (int i = 0; i < reportObjectList.size(); i++)
//        {
//            Object[] objects = (Object[]) reportObjectList.get(i);
//            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//            for (int j = 0; j < objects.length; j++)
//            {
//                Object object = objects[j];
//                System.out.println("object.getClass().getName() = " + object.getClass().getName());
//            }
//            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        }


        return orderItems;
//        return null;
    }

    /**
     * <pre>
            select  photographer.photographerid, userprofile.lastname,pt.name,pri.pricechf,sum(oi.quantity),sum(oi.quantity) * pri.pricechf,date_part('year',uploadcompleteddate) as year, date_part('month',uploadcompleteddate) as month
            from orderitems oi
            join photos p on p.photoid = oi.photoid
            JOIN genericlevels album ON p.albumid = album.genericlevelid
            JOIN photographers photographer ON album.photographerid = photographer.photographerid
            JOIN userprofiles userprofile ON userprofile.userprofileid = photographer.userprofileid
            JOIN genericlevels event ON album.eventid = event.genericlevelid
            join products prod on prod.productid = oi.productid
            join orders o on o.orderid = oi.orderid
            join producttypes pt on pt.producttypeid = prod.producttypeid
            join prices pri on pri.priceid = prod.priceid



	        group by year,month,photographer.photographerid, userprofile.lastname,pt.name,pri.pricechf
	        order by year, month
     * </pre>
     * @return
     */
    public List listPhotographerProductSalesByMonth()
    {
        // todo implement if needed
        return null;
    }

}

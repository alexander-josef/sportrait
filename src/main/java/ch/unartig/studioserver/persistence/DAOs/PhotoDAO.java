/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 24, 2005$
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
 * Revision 1.8  2007/06/03 21:35:21  alex
 * Bug #1234 : Ordnung eventcategory: wird nun nach als liste gefuehrt, ordnung wird eingehalten
 *
 * Revision 1.7  2007/06/01 06:38:03  alex
 * Bug #2106, page number for photos without a unique time
 *
 * Revision 1.6  2007/05/07 11:44:54  alex
 * getphotos cannot be used!!!1
 *
 * Revision 1.5  2007/05/04 22:21:46  alex
 * changing login error page
 *
 * Revision 1.4  2007/04/20 14:29:11  alex
 * shopping cart, photographer album edit page
 *
 * Revision 1.3  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.24  2006/12/05 22:51:57  alex
 * album kann jetzt freigeschaltet werden oder geschlossen sein
 *
 * Revision 1.23  2006/11/12 11:58:47  alex
 * dynamic album ads
 *
 * Revision 1.22  2006/05/01 12:43:49  alex
 * fix for album reload for sports and event album
 *
 * Revision 1.21  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.20  2006/04/10 21:07:02  alex
 * finish time mapping works
 *
 * Revision 1.19  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.18  2006/03/20 17:20:37  alex
 * ui improvements, sportsalbum
 *
 * Revision 1.17  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.16  2006/03/08 17:42:26  alex
 * small fixes
 *
 * Revision 1.15  2006/02/22 16:10:25  alex
 * added back link
 *
 * Revision 1.14  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.13  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.12  2006/01/27 09:30:36  alex
 * new pager implemenatation
 *
 * Revision 1.11  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.10  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.9  2005/11/05 10:32:14  alex
 * shopping cart and minor problems, exception handling
 *
 * Revision 1.8  2005/11/04 23:05:00  alex
 * error ...
 *
 * Revision 1.7  2005/11/04 23:02:54  alex
 * shopping cart session
 *
 * Revision 1.6  2005/11/02 09:10:09  alex
 * album view works
 *
 * Revision 1.5  2005/11/01 11:28:39  alex
 * pagination works; put logic in overview bean
 *
 * Revision 1.4  2005/10/26 20:40:12  alex
 * first view impl
 *
 * Revision 1.3  2005/10/26 15:36:44  alex
 * some fixes
 *
 * Revision 1.2  2005/10/26 14:34:32  alex
 * first version of album overview
 * new mappings in struts for the /album/** url
 *
 * Revision 1.1  2005/10/24 13:50:07  alex
 * upload of album
 * import in db
 * processing of images
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.EventAlbum;
import ch.unartig.studioserver.model.*;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import ch.unartig.util.DebugUtils;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.criterion.Order;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.*;

public class PhotoDAO {

    Logger _logger = Logger.getLogger(getClass().getName());

    public void saveOrUpdate(Photo photo) throws UAPersistenceException {
        try {
            HibernateUtil.currentSession().saveOrUpdate(photo);
        } catch (HibernateException e) {
            _logger.error("Cannot save or update a Photo, see stack trace");
            throw new UAPersistenceException("Cannot save or update a Photo, see stack trace", e);
        }

    }

    /**
     * get first photo after the time the user has entered, or,  if no photo exists for the chosen hour, get first photo of album
     * <br>(helper query to find the correct page)
     * use the time/date functions like here (from ReportingDAO)
     * todo: replace deprecated call from classic session
     * <pre>
     *
     * String hqlQuery = "select new ch.unartig.studioserver.model.ReportMonthlySalesSummary(" +
     * "   oi.product.productType.productTypeId, " +
     * "   oi.product.price.priceId, " +
     * "   oi.product.productType.name, " +
     * "   oi.product.price.priceCHF , " +
     * "   year(oi.order.uploadCompletedDate), " +
     * "   month(oi.order.uploadCompletedDate), " +
     * "   sum(oi.quantity) , " +
     * "   oi.product.price.priceCHF * sum(oi.quantity)" +
     * ")  \n" +
     * " from OrderItem oi " +
     * " where oi.photo.album.photographer = :photographer " +
     * " group by oi.product.productType.productTypeId, oi.product.productType.name, oi.product.price.priceId, year(oi.order.uploadCompletedDate), month(oi.order.uploadCompletedDate), oi.product.price.priceCHF" +
     * " order by oi.product.productType.productTypeId, oi.product.price.priceCHF";
     *
     * </pre>
     *
     * @param hour
     * @param minutes
     * @param album
     * @return Photo
     * @throws UAPersistenceException
     */
    private Photo getFirstPhotoAfterTime(int hour, int minutes, Album album) {
        _logger.debug("getting first photo for time and album");
        Photo firstPhoto;
        String sql = "SELECT * from photos as foto ";
        sql += " WHERE date_part('hour',foto.pictureTakenDate)=" + hour + " AND date_part('minute',foto.pictureTakenDate) >=     " + minutes;
        sql += " AND foto.albumId = '" + album.getGenericLevelId() + "' ";
        sql += " ORDER BY foto.pictureTakenDate ";
        sql += " LIMIT 1";

        try {
            // needed to refactor after migration to Hibernate 5 - does it work? -> only used for unartig app - ignore
            // firstPhoto = (Photo) HibernateUtil.currentSession().createSQLQuery(sql, "foto", Photo.class).uniqueResult();
            NativeQuery query = HibernateUtil.currentSession().createSQLQuery(sql);
            query.addEntity(Photo.class);
            firstPhoto = (Photo)query.uniqueResult();

        } catch (HibernateException e) {
            _logger.error("Exception when getting first photo for time and albuem", e);
            throw new UAPersistenceException("trying to find first photo for time and albuem ... exception!!", e);
        }
        _logger.debug("firstPhoto = " + firstPhoto);
        if (firstPhoto == null) {
            _logger.debug("no photo found for given time ... return first photo for album");
            firstPhoto = getFirstPhotoFor(album);
        }
        return firstPhoto;
    }


    /**
     * make a query to the db and return a list of photos for the passed album during the passed interval
     * Todo replace deprecated call
     * use the time/date functions like here (from ReportingDAO)
     * <pre>
     *
     * String hqlQuery = "select new ch.unartig.studioserver.model.ReportMonthlySalesSummary(" +
     * "   oi.product.productType.productTypeId, " +
     * "   oi.product.price.priceId, " +
     * "   oi.product.productType.name, " +
     * "   oi.product.price.priceCHF , " +
     * "   year(oi.order.uploadCompletedDate), " +
     * "   month(oi.order.uploadCompletedDate), " +
     * "   sum(oi.quantity) , " +
     * "   oi.product.price.priceCHF * sum(oi.quantity)" +
     * ")  \n" +
     * " from OrderItem oi " +
     * " where oi.photo.album.photographer = :photographer " +
     * " group by oi.product.productType.productTypeId, oi.product.productType.name, oi.product.price.priceId, year(oi.order.uploadCompletedDate), month(oi.order.uploadCompletedDate), oi.product.price.priceCHF" +
     * " order by oi.product.productType.productTypeId, oi.product.price.priceCHF";
     *
     * </pre>
     *
     * @param hour
     * @param minutes
     * @param interval
     * @param album
     * @return a lost of photos
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public List getPhotosForInterval(int hour, int minutes, int interval, Album album) throws UAPersistenceException {
        List photos;
        String sql = "SELECT * from photos as foto ";
        sql += "WHERE date_part('hour',foto.pictureTakenDate)=" + hour + " AND date_part('minute',foto.pictureTakenDate) BETWEEN " + minutes + " and " + (minutes + interval - 1) + " ";
        sql += "AND foto.albumId = '" + album.getGenericLevelId() + "' ";
        sql += "ORDER BY foto.pictureTakenDate";

        try {
            NativeQuery query = HibernateUtil.currentSession().createSQLQuery(sql);
            query.addEntity(Photo.class);
            photos = query.list();

        } catch (HibernateException e) {
            throw new UAPersistenceException("cannot query photos for interval, see stack trace", e);
        }

        return photos;

/*
Check the following part of the manual for replacing the deprecated use of the classic session

17.2. Alias and property references

The {cat.*} notation used above is a shorthand for "all properties". Alternatively, you may list the columns explicity, but even then you must let Hibernate inject the SQL column aliases for each property. The placeholder for a column alias is just the property name qualified by the table alias. In the following example, we retrieve Cats from a different table (cat_log) to the one declared in the mapping metadata. Notice that we may even use the property aliases in the where clause if we like. The {}-syntax is not required for named queries. See more in Section 17.3, �Named SQL queries�

String sql = "select cat.originalId as {cat.id}, " +
"cat.mateid as {cat.mate}, cat.sex as {cat.sex}, " +
"cat.weight*10 as {cat.weight}, cat.name as {cat.name} " +
"from cat_log cat where {cat.mate} = :catId"

List loggedCats = sess.createSQLQuery(sql)
.addEntity("cat", Cat.class)
.setLong("catId", catId)
.list();

Note: if you list each property explicitly, you must include all properties of the class and its subclasses!

*/
    }

    /**
     * list photos for a page ...
     * <br> if page is < 1 set page to 1
     *
     * @param page
     * @param album       the album is used as filter criterium
     * @param itemsOnPage
     * @return
     * @throws UAPersistenceException
     */
    public List getPhotosForPage(int page, Album album, int itemsOnPage) throws UAPersistenceException {
        List photos;
        if (page < 1) {
            page = 1;
        }
        Criteria criteria = HibernateUtil.currentSession().createCriteria(Photo.class)
                .add(Expression.eq("album", album))
                .addOrder(Order.asc("pictureTakenDate"))
                .setMaxResults(itemsOnPage)
                .setFirstResult((page - 1) * itemsOnPage);

        try {
            photos = criteria.list();
        } catch (HibernateException e) {
            throw new UAPersistenceException("Problem while retrieving Photos for Event : " + album.getLongTitle() + " ; see stack trace");
        }
        DebugUtils.debugPhotos(photos);
        return photos;
    }

    /**
     * list photos for a page ... list all photos of page and add one at the end and one at the beginning for the previews<br>
     * if page=1 do not extend selection at the beginning<br>
     * if page=1 extend maxresults only by 1 not by 2
     * todo: wrong result if album has exactly 16 photos
     *
     * @param page
     * @param album       the album is used as filter criterium
     * @param itemsOnPage
     * @return
     * @throws UAPersistenceException
     */
    public List listPhotosOnPagePlusPreview(int page, Album album, int itemsOnPage) throws UAPersistenceException {
        _logger.debug("extended get photos for page");
        List photos;
        if (page < 1) {
            page = 1;
        }
        //if page=1 do not extend selection at the beginning:
        int firstResult = page == 1 ? ((page - 1) * itemsOnPage) : ((page - 1) * itemsOnPage) - 1;
        int maxResults = page == 1 ? itemsOnPage + 1 : itemsOnPage + 2;
        Criteria criteria = HibernateUtil.currentSession().createCriteria(Photo.class)
                .createAlias("album", "album")
                .add(Expression.eq("album.publish", Boolean.TRUE))
                .add(Expression.eq("album", album))
                .addOrder(Order.asc("pictureTakenDate"))
                .setMaxResults(maxResults)
                .setFirstResult(firstResult);

        try {
            photos = criteria.list();
        } catch (HibernateException e) {
            throw new UAPersistenceException("Problem while retrieving Photos for Event : " + album.getLongTitle() + " ; see stack trace", e);
        }
        DebugUtils.debugPhotos(photos);
        return photos;
    }

    /**
     * Performant count on photos
     *
     * @param album
     * @return
     * @throws UAPersistenceException
     */
    public int countPhotos(Album album) {
        // todo check performance of both calculations!
        // todo : How many times is this called?
  // hbm3:

        // new with Criteria hibernate 5.4
        CriteriaBuilder criteriaBuilder = HibernateUtil.currentSession().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);

        Root<Photo> photoRoot = criteria.from(Photo.class);

        criteria.select(criteriaBuilder.count(photoRoot));
        criteria.where(criteriaBuilder.equal(photoRoot.get("album"),album));

        Long retValCriteria = HibernateUtil.currentSession()
                .createQuery(criteria)
                .setCacheable(true)
                .getSingleResult();



        // hbm3: old - deprecated -- use for comparison
        Number resultValue2 = (Number) HibernateUtil.currentSession()
                .createCriteria(Photo.class)
                .createAlias("album", "album")
                // .add(Expression.eq("album.publish", Boolean.TRUE))
                .add(Expression.eq("album", album))
                .setProjection(Projections.rowCount())
                .setCacheable(true)
                .uniqueResult();


//        return resultValue1;
        return retValCriteria.intValue();

    }


    /**
     * get the first photo of an album<br>
     * used to be private method
     *
     * @param album
     * @return
     */
    public Photo getFirstPhotoFor(Album album) {
        Criteria c = HibernateUtil.currentSession().createCriteria(Photo.class);
        c.add(Expression.eq("album", album))
                .addOrder(Order.asc("pictureTakenDate"))
                .setMaxResults(1)
                .setCacheable(true);
        Photo firstPhoto = (Photo) c.uniqueResult();
        if (firstPhoto == null) {
            _logger.info("No photo found for getFirstPhoto in album [" + album.getLongTitle() + "].");
        }
        return firstPhoto;
    }

    public Photo load(Long photoId) throws UAPersistenceException {
        try {
            return (Photo) HibernateUtil.currentSession().load(Photo.class, photoId);
        } catch (HibernateException e) {
            throw new UAPersistenceException("Could not load Generic Level, see stack trace", e);
        }
    }

    /**
     * user has chosen a time for a certain album. find out page nr where this time starts in the album
     *
     * @param album
     * @param hour
     * @param minutes
     * @return
     */
    public int getPageNrFor(Album album, int hour, int minutes) {
        Photo firstPhoto = getFirstPhotoAfterTime(hour, minutes, album);
        return (getAlbumPageNrFor(firstPhoto));
    }

    /**
     * user has chosen a time for a certain album. find out page nr where this time starts in the album
     *
     * @param album   the album to chose the photos from
     * @param hour    0 - 23
     * @param minutes 0 - 59
     * @return page number (starting at 1)
     */
/*
    public int getAlbumPageNrFor(Album album, int hour, int minutes) throws UAPersistenceException
    {
        int page;
        //count = count how many are BEFORE the chosen time ; position of first photo after chosen time = count + 1
        int count = 0;
        // counting without initializing the collection
//        ( (Integer) session.iterate("select count(*) from ....").next() ).intValue();
        try
        {
            Photo firstPhoto = getFirstPhotoForTime(hour, minutes, album);
            String query = "select count(*) " + "from ch.unartig.studioserver.model.Photo as photo " + "where photo.album = :album " + "and photo.pictureTakenDate < :firstPhotoDate";
            Map map = new HashMap();
            map.put("album", album);
            map.put("firstPhotoDate", firstPhoto.getPictureTakenDate());
            count = ((Integer) HibernateUtil.getUnique(query, map)).intValue();
        } catch (UAPersistenceException e)
        {
            _logger.error("error getting page nr.", e);
            throw new UAPersistenceException("getting page nr error", e);
        } catch (Exception e2)
        {
            _logger.error("unknown exception", e2);
        }
        // count = pos -1
        // page = (count / itemsOnPage) +1;
        page = (count / Registry.getItemsOnPage()) + 1;
        return page;
    }
*/

    /**
     * query for the position of the photo within the album ????<br>
     * use this result to calculate page<br/>
     * todo: refactor all three getAlbumPageNrFor methods!!!! they're more or less the same!!
     * todo : for sportrait? this only works if photos in album and eventcategory are identical. what about many albums per eventcategory?
     *
     * @param photo
     * @return '1' in case photo is null or a page number for the passed photo.
     * @throws UAPersistenceException
     */
    private int getAlbumPageNrFor(Photo photo) {
        if (photo == null) {
            return 1;
        }
        int page;
        int position; // position of that photo within the album

        //hbm3: change
        Criteria c = HibernateUtil.currentSession().createCriteria(Photo.class);

        Object queryResult = c
                .add(Expression.le("pictureTakenDate", photo.getPictureTakenDate()))
                .add(Expression.eq("album", photo.getAlbum()))
                .setCacheable(true)
                .setProjection(Projections.rowCount())
                .uniqueResult();

        position = ((Long) queryResult).intValue(); // used to be Integer - changed after hibernate 5 migration
        _logger.debug("position : " + position);
        page = ((position - 1) / Registry.getItemsOnPage()) + 1;
        _logger.debug("page : " + page);
        return page;
    }


    /**
     * convenience method to call <link>getAlbumPageNrFor(Photo photo)</link>
     *
     * @param displayPhotoId
     * @return
     * @throws UAPersistenceException
     */
    public int getAlbumPageNrFor(Long displayPhotoId) throws UAPersistenceException {
        return getAlbumPageNrFor(load(displayPhotoId));
    }

    /**
     * given the photoId , startnumber (if any), calculate the pagenumber for the passed EVENT album (i.e. among all photo in the event of the album)
     *
     * @param displayPhotoId
     * @param eventAlbum
     * @param startNumber
     * @return
     * @throws UAPersistenceException
     */
    public int getAlbumPageNrFor(Long displayPhotoId, EventAlbum eventAlbum, String startNumber) throws UAPersistenceException {
        _logger.debug("getting page number for eventAlbum ....");
        Photo photo = load(displayPhotoId);
        int page = 1;
        int position;
        Criteria c = createEventAlbumStartNumberCriteria(eventAlbum, startNumber);

        Object queryResult = c
                .add(Expression.le("pictureTakenDate", photo.getPictureTakenDate()))
                .setProjection(Projections.rowCount())
                .uniqueResult();

        position = ((Integer) queryResult).intValue();
        _logger.debug("position : " + position);
        page = ((position - 1) / Registry.getItemsOnPage()) + 1;
        _logger.debug("page : " + page);
        return page;
    }

    /**
     * given the photoId , startnumber (if any), calculate the pagenumber for the passed album
     *
     * @param displayPhotoId
     * @param sportsAlbum
     * @param startNumber
     * @return
     * @throws UAPersistenceException
     */
    public int getAlbumPageNrFor(Long displayPhotoId, SportsAlbum sportsAlbum, String startNumber) throws UAPersistenceException {
        _logger.debug("getting page number for sportsalbum ....");
        Photo photo = load(displayPhotoId);
        int page = 1;
        int position;
        Criteria c = createStartnumberCriteria(sportsAlbum, startNumber);

        Object queryResult = c
                .add(Expression.le("pictureTakenDate", photo.getPictureTakenDate()))
                .setProjection(Projections.rowCount())
                .uniqueResult();

        position = ((Integer) queryResult).intValue();
        _logger.debug("position : " + position);
        page = ((position - 1) / Registry.getItemsOnPage()) + 1;
        _logger.debug("page : " + page);
        return page;
    }

    /**
     * Given the photoId , startnumber (if any, use null or empty value if no startnumber shall be used in the query) and an EventCategory, calculate the pagenumber
     *
     * @param displayPhotoId
     * @param eventCategory
     * @param startNumber can be null or empty if it shouldn't be used in the criteria
     * @return
     * @throws UAPersistenceException
     */
    public int getAlbumPageNrFor(Long displayPhotoId, EventCategory eventCategory, String startNumber) throws UAPersistenceException {
        _logger.debug("getting page number for sportsalbum ....");
        Photo photo = load(displayPhotoId);
        int page;
        int position;

        // new : hibernate 5.4 with criteria API
        CriteriaBuilder criteriaBuilder = HibernateUtil.currentSession().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Predicate startNumberPredicate = null;
        Predicate photoIdPredicate;

        Root<Photo> photoRoot = criteriaQuery.from(Photo.class);
        Join<Photo,Album> photoPhotoSubjectsJoin = photoRoot.join("photoSubjects");
        Join<Photo,Album> photoSubjectsEventRunnerJoin = photoPhotoSubjectsJoin.join("eventRunners");


        Predicate publishedForEventCategoryPredicate = getPublishedForEventCategoryPredicate(eventCategory, criteriaBuilder, photoRoot);

        Predicate pictureTakenDatePredicate = criteriaBuilder
                .lessThanOrEqualTo(photoRoot.get("pictureTakenDate"), photo.getPictureTakenDate());



        Predicate finalPredicate = criteriaBuilder
                .and(publishedForEventCategoryPredicate,pictureTakenDatePredicate);

        if (startNumber != null && !"".equals(startNumber)) {
            // create combined predicate
            startNumberPredicate = criteriaBuilder
                    .equal(photoSubjectsEventRunnerJoin.get("startnumber"),startNumber);
            finalPredicate = criteriaBuilder.and(finalPredicate,startNumberPredicate);
        }

        criteriaQuery.select(criteriaBuilder.count(photoRoot))
                .where(finalPredicate);


        try {
            Long criteriaResult = HibernateUtil.currentSession()
                    .createQuery(criteriaQuery)
                    .setCacheable(true)
                    .getSingleResult();


        } catch (NonUniqueObjectException e) {
            // exceptional case: not a single result: we now calcualte the page according to the photoid:
            _logger.info("not a unique result for this timestamp : " + photo.getPictureTakenDate());
            _logger.info("calculating page for this photo according to photoid");

            photoIdPredicate = criteriaBuilder
                    .lessThanOrEqualTo(photoRoot.get("photoId"), photo.getPhotoId());

            Predicate exceptionalPredicate = criteriaBuilder
                    .and(publishedForEventCategoryPredicate,photoIdPredicate);

            if (startNumber != null && !"".equals(startNumber)) {
                // create combined predicate
                exceptionalPredicate = criteriaBuilder.and(exceptionalPredicate, startNumberPredicate);
            }

            criteriaQuery.select(criteriaBuilder.count(photoRoot))
                    .where(exceptionalPredicate);

            Long criteriaResult = HibernateUtil.currentSession()
                    .createQuery(criteriaQuery)
                    .setCacheable(true)
                    .getSingleResult();

        }


        // check if there is not a unique result for the time of the given photo:
        Object queryResult;
        try {
            // also include filename for check?

            // this would fail for pictures without a date (i.e. with all the same entries for date)
            queryResult = createSportsPhotoCriteria(eventCategory, startNumber)
                    .add(Expression.le("pictureTakenDate", photo.getPictureTakenDate()))
                    .setProjection(Projections.rowCount())
                    .setCacheable(true)
                    .uniqueResult();

        } catch (HibernateException e) {
            // exceptional case: not a single result: we now calcualte the page according to the photoid:
            _logger.info("not a unique result for this timestamp : " + photo.getPictureTakenDate());
            _logger.info("calculating page for this photo according to photoid");

            queryResult = createSportsPhotoCriteria(eventCategory, startNumber)
                    .add(Expression.le("photoId", photo.getPhotoId()))
                    .setProjection(Projections.rowCount())
                    .setCacheable(true)
                    .uniqueResult();
        }


        position = ((Long) queryResult).intValue();
        _logger.debug("position : " + position);
        page = ((position - 1) / Registry.getItemsOnPage()) + 1;
        _logger.debug("page : " + page);
        return page;
    }

    /**
     * Helper method to reuse the predicate for published eventCategories of an album - see pendant with old hibernate criteria
     * @param eventCategory
     * @param criteriaBuilder
     * @param photoRoot
     * @return
     */
    private Predicate getPublishedForEventCategoryPredicate(EventCategory eventCategory, CriteriaBuilder criteriaBuilder, Root<Photo> photoRoot) {
        Join<Photo, Album> photoAlbumJoin = photoRoot.join("album");
        return criteriaBuilder
                .and(criteriaBuilder
                        .equal(photoAlbumJoin.get("publish"),Boolean.TRUE),criteriaBuilder
                        .equal(photoAlbumJoin.get("eventCategory"),eventCategory));
    }


    /**
     * dao method to list photos for a given startnumber
     *
     * @param page
     * @param album
     * @param itemsOnPage
     * @param startnumber
     * @return a list of photos
     * @throws ch.unartig.exceptions.UAPersistenceException
     * @deprecated probably shouldn't be used - album not a valid criterion
     */
    public List listSportsPhotosOnPagePlusPreview(int page, Album album, int itemsOnPage, String startnumber) throws UAPersistenceException {
        _logger.debug("extended get sport photos for page");
        List photos;
        if (page < 1) {
            page = 1;
        }
        //if page=1 do not extend selection at the beginning:
        int firstResult = page == 1 ? ((page - 1) * itemsOnPage) : ((page - 1) * itemsOnPage) - 1;
        int maxResults = page == 1 ? itemsOnPage + 1 : itemsOnPage + 2;
        Criteria criteria = createStartnumberCriteria(album, startnumber);
        criteria.setMaxResults(maxResults)
                .setFirstResult(firstResult)
                .addOrder(Order.asc("pictureTakenDate"))
                .addOrder(Order.asc("photoId"));
        try {
            photos = criteria.list();
        } catch (HibernateException e) {
            throw new UAPersistenceException("Problem while retrieving Photos for Event : " + album.getLongTitle() + " ; see stack trace", e);
        }
        if (_logger.isDebugEnabled()) {
            DebugUtils.debugPhotos(photos);
        }
        return photos;
    }

    /**
     * Retrieve all photos of an EventCategory that match the given parameter
     * first order is picturetakendate
     * set order if no time is available to photoid
     *
     * @param page
     * @param eventCategory
     * @param itemsOnPage   the number of photos on page // 0 for all photos for given eventCategory and startNumber
     * @param startNumber   if null, ignore
     * @return a list of Photo s
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public List listSportsPhotosOnPagePlusPreview(int page, EventCategory eventCategory, int itemsOnPage, String startNumber) throws UAPersistenceException {
        _logger.debug("extended get sport photos for page");
        List photos;
        if (page < 1) {
            page = 1;
        }

        // new hibernate 5.4
        Query<Photo> photoQuery = getPublishedPhotosForEventCategoryQuery(eventCategory);
        // --


        Criteria criteria = createSportsPhotoCriteria(eventCategory, startNumber);


        if (itemsOnPage == 0) { // don't limit result for pagination
            // new hibernate 5.4: nothing here - see above photoQuery
            // nothing ...
            //--- old
            criteria.addOrder(Order.asc("pictureTakenDate"))
                    .addOrder(Order.asc("photoId"))
                    .setCacheable(true) // also enable query caching
            ;
            // ---

        } else {//if page=1 do not extend selection at the beginning:
            int firstResult = page == 1 ? ((page - 1) * itemsOnPage) : ((page - 1) * itemsOnPage) - 1;
            int maxResults = page == 1 ? itemsOnPage + 1 : itemsOnPage + 2;
            // new Hibernate 5.4
            photoQuery
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResults);


            // hbm3: -- old
            criteria.setMaxResults(maxResults)
                    .setFirstResult(firstResult)
                    .addOrder(Order.asc("pictureTakenDate"))
                    .addOrder(Order.asc("photoId"))
                    .setCacheable(true) // also enable query caching
            ;
            // ---
        }
        List<Photo> criteriaQueryResult;
        try {
            criteriaQueryResult = photoQuery.getResultList();

            photos = criteria.list(); // hbm3: old result
        } catch (HibernateException e) {
            throw new UAPersistenceException("Problem while retrieving Photos for Event : " + eventCategory.getTitle() + " ; see stack trace", e);
        }
        if (_logger.isDebugEnabled()) {
            DebugUtils.debugPhotos(photos); // hbm3:
            DebugUtils.debugPhotos(criteriaQueryResult);
        }
        return criteriaQueryResult; // used to be "photos" from old hibernate 3.x criteria
    }


    /**
     * for EventAlbum
     * todo is this still needed?
     *
     * @param page
     * @param album
     * @param itemsOnPage
     * @param startnumber
     * @return
     * @throws UAPersistenceException
     * @deprecated still used ? see other signatures of same method that uses eventcategory - eventalbum probably not used this way
     */
    public List listSportsPhotosOnPagePlusPreview(int page, EventAlbum album, int itemsOnPage, String startnumber) throws UAPersistenceException {
        _logger.debug("sport photos for EVENT ALBUM!!");

        List photos;
        if (page < 1) {
            page = 1;
        }
        //if page=1 do not extend selection at the beginning:
        int firstResult = page == 1 ? ((page - 1) * itemsOnPage) : ((page - 1) * itemsOnPage) - 1;
        int maxResults = page == 1 ? itemsOnPage + 1 : itemsOnPage + 2;


        Criteria criteria = createEventAlbumStartNumberCriteria(album, startnumber);


        criteria.setMaxResults(maxResults)
                .setFirstResult(firstResult)
                .addOrder(Order.asc("pictureTakenDate"))
                .addOrder(Order.asc("photoId"));
        try {
            photos = criteria.list();
        } catch (HibernateException e) {
            throw new UAPersistenceException("Problem while retrieving Photos for Event : " + album.getLongTitle() + " ; see stack trace", e);
        }
        if (_logger.isDebugEnabled()) {
            DebugUtils.debugPhotos(photos);
        }
        return photos;
    }


    /**
     * construct a criteria for a passed album and startNumber
     *
     * @param album
     * @param startnumber
     * @return
     * @throws UAPersistenceException
     * @deprecated should not be used anymore
     */
    private Criteria createStartnumberCriteria(Album album, String startnumber) throws UAPersistenceException {
        Criteria criteria = HibernateUtil.currentSession().createCriteria(Photo.class)
                .add(Restrictions.eq("album", album));

        addStartNumberCriteria(startnumber, criteria);
        return criteria;
    }

    /**
     * construct a criteria for a passed eventCategory and startNumber
     *
     * @param eventCategory An event has photos one or more categories, i.e. "junioren", "elite", "etappe1", "impressionen" etc.
     * @param startnumber can be null or empty if it should not be used in criteria
     * @return
     * @throws UAPersistenceException
     * @deprecated
     */
    private Criteria createSportsPhotoCriteria(EventCategory eventCategory, String startnumber) throws UAPersistenceException {
        Criteria criteria = HibernateUtil.currentSession().createCriteria(Photo.class)
                .createAlias("album", "a")
                .add(Restrictions.eq("a.eventCategory", eventCategory))
                .add(Restrictions.eq("a.publish", Boolean.TRUE))
                .setCacheable(true);

        addStartNumberCriteria(startnumber, criteria);
        return criteria;
    }

    /**
     * helper method to create startnumber criteria (if given)
     * @param startnumber can be null or empty
     * @param criteria will only be added a startnumber if one is given
     */
    private void addStartNumberCriteria(String startnumber, Criteria criteria) {
        if (startnumber != null && !"".equals(startnumber)) {
            criteria.createAlias("photoSubjects", "sub")
                    .createAlias("sub.eventRunners", "eventRunner")
                    .add(Restrictions.eq("eventRunner.startnumber", startnumber));
        }
    }

    /**
     * Count all photos for a passed startNumber and Event Category
     *
     * @param startNumber
     * @param eventCategory
     * @return
     * @throws UAPersistenceException
     */
    public int countPhotosFor(String startNumber, EventCategory eventCategory) throws UAPersistenceException {
        Long count; // Long vs int
        count = (Long)createSportsPhotoCriteria(eventCategory, startNumber)
                .setProjection(Projections.rowCount())
                .setCacheable(true)
                .uniqueResult();
        _logger.debug("Photo count = " + count); // used to be int before migration to hibernate 5.4 (change seem to be introduced with hibernate 3.5)
        return count.intValue();
    }


    /**
     * USED for mapping, tries to find a photo by its albumid and a given (case-insensitive) filename<br>
     * return null if no photos is found
     *
     * @param album
     * @param filename
     * @return Photo or null
     */
    public Photo findPhoto(Album album, String filename) throws UAPersistenceException {
        Criteria criteria = HibernateUtil.currentSession().createCriteria(Photo.class)
                .add(Restrictions.eq("album", album))
                .add(Restrictions.ilike("filename", filename));

        return (Photo) criteria.uniqueResult();
    }

    /**
     * find the last photo in an album filtered by start number
     *
     * @param eventCategory
     * @param startNumber
     * @return the last photo in an eventCategory filtered by startnumber
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public Photo getLastPhotoInCategoryAndSelection(EventCategory eventCategory, String startNumber) throws UAPersistenceException {

        Photo lastPhoto = (Photo) createSportsPhotoCriteria(eventCategory, startNumber)
                .addOrder(Order.asc("pictureTakenDate"))
                .addOrder(Order.asc("photoId"))
                .setMaxResults(1)
                .setFirstResult(lastIndexFor(startNumber, eventCategory))
                .uniqueResult();
        _logger.debug("returning last lastPhoto : " + lastPhoto);
        return lastPhoto;
    }

    /**
     * find the last photo in an event-album filtered by start number
     *
     * @param album
     * @param startNumber
     * @return the last photo in an album filtered by startnumber
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public Photo getLastPhotoInAlbumAndSelection(EventAlbum album, String startNumber) throws UAPersistenceException {


        Integer count = (Integer) createEventAlbumStartNumberCriteria(album, startNumber)
                .setProjection(Projections.rowCount())
                .uniqueResult();
        _logger.debug("Photo count = " + count);

        Photo lastPhoto = (Photo) createEventAlbumStartNumberCriteria(album, startNumber)
                .addOrder(Order.asc("pictureTakenDate"))
                .addOrder(Order.asc("photoId"))
                .setMaxResults(1)
                .setFirstResult(count - 1)
                .uniqueResult();
        _logger.debug("returning last lastPhoto : " + lastPhoto);
        return lastPhoto;
    }

    /**
     * construct a criteria for a passed EventAlbum and startNumber
     *
     * @param album
     * @param startNumber
     * @return criteria
     * @throws UAPersistenceException
     */
    private Criteria createEventAlbumStartNumberCriteria(EventAlbum album, String startNumber) throws UAPersistenceException {
        Criteria criteria = HibernateUtil.currentSession().createCriteria(Photo.class)
                .createAlias("album", "etappe")
                .add(Restrictions.eq("etappe.event", album.getEvent()));

        addStartNumberCriteria(startNumber, criteria);
        return criteria;
    }


    /**
     * return the first photo for a given album and selection
     * <br> used to calculate the settings for the preview photo
     *
     * @param eventCategory
     * @param startNumber
     * @return first Photo
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public Photo getFirstPhotoInAlbumAndSelection(EventCategory eventCategory, String startNumber) throws UAPersistenceException {
        Photo firstPhoto = (Photo) createSportsPhotoCriteria(eventCategory, startNumber)
                .addOrder(Order.asc("pictureTakenDate"))
                .addOrder(Order.asc("photoId"))
                .setMaxResults(1)
                .uniqueResult();
        _logger.debug("returning  firstPhoto : " + firstPhoto);
        return firstPhoto;
    }

    /**
     * return the first photo for a given event-album and selection
     * <br> used to calculate the settings for the preview photo
     *
     * @param album
     * @param startNumber
     * @return first Photo
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public Photo getFirstPhotoInAlbumAndSelection(EventAlbum album, String startNumber) throws UAPersistenceException {
        Criteria criteria = createEventAlbumStartNumberCriteria(album, startNumber);

        Photo firstPhoto = (Photo) criteria
                .addOrder(Order.asc("pictureTakenDate"))
                .addOrder(Order.asc("photoId"))
                .setMaxResults(1)
                .uniqueResult();
        _logger.debug("returning  firstPhoto : " + firstPhoto);
        return firstPhoto;
    }


    /**
     * calculates the index of the last photo for the given album and startnumber
     *
     * @param startNumber
     * @param eventCategory
     * @return index of the last photo for given album and startnumber
     * @throws UAPersistenceException
     */
    private int lastIndexFor(String startNumber, EventCategory eventCategory) throws UAPersistenceException {
        return countPhotosFor(startNumber, eventCategory) - 1;
    }

    /**
     * used for sportsalbum: this method returns all photo within the specifed time in the passed arguments
     * USED for MAPPING
     * ASSUMPTION: the photos of the etappe have the same day, month and year as the passed date !!
     * Assumption: all etappen or albums of the event have the same date!!!
     * todo: adapt if day or month changes during an event
     * <br>
     *
     * @param album
     * @param minMatchTime
     * @param maxMatchTime
     * @return List of Photo s
     */
    public List findFinishTimePhotos(Album album, Date minMatchTime, Date maxMatchTime) throws UAPersistenceException {
        List retVal;

        // todo: Only time, not dates!, are compared.
        // todo : quick and dirty solution ignoring the dates: get first photo of etappe and replace the time information, assuming the date information is invariable
        // todo: replace for any sports event that crosses a date border. I.E. 24h races or so ....

        Criteria crit = HibernateUtil.currentSession().createCriteria(Photo.class)
                .add(Expression.eq("album", album))
                .add(Restrictions.le("pictureTakenDate", maxMatchTime))
                .add(Restrictions.ge("pictureTakenDate", minMatchTime))
                .addOrder(Order.asc("pictureTakenDate"));

        retVal = crit.list();
        return retVal;
    }

    /**
     * DAO method to delete single photo
     *
     * @param photoId
     */
    public void delete(Long photoId) throws UAPersistenceException {

        try {
            HibernateUtil.currentSession().delete(HibernateUtil.currentSession().load(Photo.class, photoId));

        } catch (HibernateException h) {
            throw new UAPersistenceException("Could not load and delete Photo by ID, see stack trace", h);
        }
    }

    /**
     * added after having problem with laziliy initializing photosubject with the new image recognition import
     *
     * @param photo
     */
    public void initializePhotoSubjects(Photo photo) {
        Set photoSubjects = photo.getPhotoSubjects();
        Hibernate.initialize(photoSubjects);
    }

    /**
     * Used for REST Service in order to return a list of photos that contains neighboring photos to the given photoId
     * On the frontend, this list is used in JSON format to swipe through the photos. once it reaches the end of the list,
     * the REST API will be called for a new set of images
     *
     * @param photoId
     * @param eventCategory
     * @param startNumber
     * @param backward
     * @param forward
     * @return
     */
    public List listNearbySportsPhotosFor(Long photoId, EventCategory eventCategory, String startNumber, int backward, int forward) {
        List photos; // result value

        _logger.debug("loading photoId : " + photoId);

        Photo photo = load(photoId);
        _logger.debug("listing nearby photos for photo : " + photo.getFilename());

        // todo : this does not work for photos with identical picturetakendates - it should be a row_number() query, ordered by picturetakendate and filename (or photoId)

        // hbm3: migrate!!
        // subquery for photo position
        DetachedCriteria subquery = DetachedCriteria.forClass(Photo.class, "p");
        subquery.createAlias("album", "a")
                .add(Restrictions.eq("a.eventCategory", eventCategory))
                .add(Restrictions.eq("a.publish", Boolean.TRUE))
                .add(Restrictions.le("p.pictureTakenDate", photo.getPictureTakenDate()))
                .setProjection(Projections.rowCount());

        // also take care of startnumber - if exists:
        if (startNumber != null && !"".equals(startNumber)) {
            _logger.debug("adding startnumber ["+startNumber+ "] for subquery" );
            subquery.createAlias("photoSubjects", "sub")
                    .createAlias("sub.eventRunners", "eventRunner")
                    .add(Restrictions.eq("eventRunner.startnumber", startNumber));
        }


        // todo: remove, only debug ? can this be integrated as a subquery?
        int position = 0;
        try {
            position = ((Long)subquery.getExecutableCriteria(HibernateUtil.currentSession()).uniqueResult()).intValue(); // used to be an Integer - changed after Hibernate 5 migration
        } catch (HibernateException e) {
            _logger.error("Error retrieving photos",e);
        }
        _logger.debug("Photo ["+photoId+"] at position : " + position);





//        Criteria criteria = session.createCriteria(User.class, "u")
//                .add( Subqueries.lt(10, subquery) );
//
//        Criteria photoCriteria  = createSportsPhotoCriteria(eventCategory,startNumber);
//        photoCriteria.add(Expression)
//
//
        int firstResult = (position - backward)>0?(position-backward):1; // position of first photo to show, or 1 in case 1st result would be smaller 1
        int maxResults = backward + forward;

        // new hibernate 5.4
        Query<Photo> photoQuery = getPublishedPhotosForEventCategoryQuery(eventCategory);

        photoQuery
                .setFirstResult(firstResult-1) // starts with 0
                .setMaxResults(maxResults+1); // starts with 0

        List<Photo> criteriaQueryResult = photoQuery.getResultList();


        // hbm3: old
        Criteria criteria = createSportsPhotoCriteria(eventCategory, startNumber); // needed

        criteria.setMaxResults(maxResults+1) // starts with 0
                .setFirstResult(firstResult-1) // starts with 0
                .addOrder(Order.asc("pictureTakenDate"))
                .addOrder(Order.asc("photoId"))
                .setCacheable(true) // also enable query caching
        ;

        try
        {
            photos = criteria.list();
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("Problem while retrieving Photos for Event : " + eventCategory.getTitle() + " ; see stack trace", e);
        }
        if (_logger.isDebugEnabled())
        {
            DebugUtils.debugPhotos(photos);
            DebugUtils.debugPhotos(criteriaQueryResult);
        }

        return criteriaQueryResult;

    }

    /**
     * private helper for constructing a cached JPA query that return photos for an eventcategory, ordered 1st by picturetakendate, then photoId
     * @param eventCategory eventCategory with the photos
     * @return a Query that can be further processed
     */
    private Query<Photo> getPublishedPhotosForEventCategoryQuery(EventCategory eventCategory) {
        CriteriaBuilder cb = HibernateUtil.currentSession().getCriteriaBuilder();

        javax.persistence.criteria.CriteriaQuery<Photo> criteriaQuery = cb.createQuery(Photo.class);
        Root<Photo> photoRoot = criteriaQuery.from(Photo.class);

        Predicate publishedForEventCategoryPredicate = getPublishedForEventCategoryPredicate(eventCategory, cb, photoRoot);
        criteriaQuery
                .select(photoRoot)
                .where(publishedForEventCategoryPredicate)
                .orderBy(cb.asc(photoRoot.get("pictureTakenDate")))
                .orderBy(cb.asc(photoRoot.get("photoId")));

        return HibernateUtil.currentSession()
                .createQuery(criteriaQuery)
                .setCacheable(true);
    }


}


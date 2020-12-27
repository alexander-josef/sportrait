/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 3, 2005$
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
 * Revision 1.5  2007/06/09 11:15:37  alex
 * photographer
 *
 * Revision 1.4  2007/06/04 07:28:30  alex
 * Bug #2100 , Reihenfolge events auf startliste
 *
 * Revision 1.3  2007/03/28 14:42:52  alex
 * home page most recent events are working
 *
 * Revision 1.2  2007/03/28 13:52:58  alex
 * edit events page
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.17  2006/11/12 13:32:49  alex
 * dynamic album ads
 *
 * Revision 1.16  2006/11/12 11:58:47  alex
 * dynamic album ads
 *
 * Revision 1.15  2006/11/10 15:55:30  alex
 * dynamic album ads
 *
 * Revision 1.14  2006/03/20 15:33:33  alex
 * first check in for new sports album logic and db changes
 *
 * Revision 1.13  2006/03/08 17:42:26  alex
 * small fixes
 *
 * Revision 1.12  2006/02/16 17:13:46  alex
 * admin interface: deletion of levels works now
 *
 * Revision 1.11  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 * Revision 1.10  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.9  2005/11/07 21:57:43  alex
 * admin interface refactored
 *
 * Revision 1.8  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.7  2005/11/06 21:43:22  alex
 * overview, admin menu, index-photo upload
 *
 * Revision 1.6  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.5  2005/10/26 14:34:32  alex
 * first version of album overview
 * new mappings in struts for the /album/** url
 *
 * Revision 1.4  2005/10/08 10:52:36  alex
 * jstl 1.1 integrated, new web.xml
 *
 * Revision 1.3  2005/10/06 08:54:04  alex
 * cleaning up the model
 *
 * Revision 1.2  2005/10/04 11:36:48  alex
 * level imports
 *
 * Revision 1.1  2005/10/03 14:50:25  alex
 * first daos
 *
 ****************************************************************/
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.*;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenericLevelDAO {
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * Save a generic Level
     *
     * @param genericLevel An instance of a concrete generic Level
     * @throws UAPersistenceException
     */
    public void saveOrUpdate(GenericLevel genericLevel) throws UAPersistenceException, NonUniqueObjectException {
        try {
            HibernateUtil.currentSession().saveOrUpdate(genericLevel);
            _logger.info("successful saveOrUpdate of a generic level");
        } catch (NonUniqueObjectException e1) {
            _logger.info("NonUniqueObject exception - throwing exception to caller");
            throw new NonUniqueObjectException("nonUniqueObject exception for genericLevel", genericLevel.getGenericLevelId(), genericLevel.getClass().getName());
        } catch (HibernateException e2) {
            _logger.error("Cannot save or update a Category, see stack trace", e2);
            throw new UAPersistenceException("Cannot save or update a generic Level, see stack trace", e2);
        }
    }

    /**
     * Merging a generic Level - usually as a strategy to react on the NonUniqueObject exception
     *
     * @param genericLevel An instance of a concrete generic Level
     */
    public void merge(GenericLevel genericLevel) {
        HibernateUtil.currentSession().merge(genericLevel);
        _logger.info("successful merge of a generic level");

    }


    /**
     * @deprecated use generic
     */
    @SuppressWarnings({"JavaDoc"})
    public List listCategories() throws UAPersistenceException {
        try {
            return HibernateUtil.currentSession().createCriteria(Category.class).list();
        } catch (HibernateException e) {
            throw new UAPersistenceException("cannot list categories, see stack trace ", e);
        }
    }

    /**
     * @deprecated use generic list
     */
    @SuppressWarnings({"JavaDoc"})
    public List listEventGroups() throws UAPersistenceException {
        try {
            return HibernateUtil.currentSession().createCriteria(EventGroup.class).list();
        } catch (HibernateException e) {
            throw new UAPersistenceException("cannot list eventGroups, see stack trace ", e);
        }
    }

    /**
     * why deprecated? replacement?
     *
     * @deprecated
     */
    @SuppressWarnings({"JavaDoc"})
    public List listEvents() throws UAPersistenceException {
        throw new UAPersistenceException("not implemented - deprecated");
/*
        try
        {
            return HibernateUtil.currentSession().createCriteria(Event.class).list();
        } catch (HibernateException e)
        {
            throw new UAPersistenceException("cannot list events, see stack trace ", e);
        }
*/
    }

    /**
     * List all objects of the passed class descending ordered by id
     *
     * @param levelClass The type of generic level to list
     * @return List of GenericLevel s
     */
    public List<SportsEvent> listGenericLevel(Class levelClass) {

        Session session = HibernateUtil.currentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object> criteriaQuery = cb.createQuery(levelClass);
        Root<Object> root = criteriaQuery.from(levelClass);

        criteriaQuery.select(root).orderBy(cb.desc(root.get("genericLevelId")));

        List resultList = session.createQuery(criteriaQuery).getResultList();

        return resultList;
    }

    /**
     * Generically load a hierachy level. must be casted by calling method to appropriate concrete class.
     *
     * @param genericLevelId Level ID
     * @param levelClass     the concrete class
     * @return a generic Level ; needs to be casted to appropriate class
     * @throws UAPersistenceException
     */
    public GenericLevel load(Long genericLevelId, Class levelClass) {
        try {
            Session session = HibernateUtil.currentSession();
            return (GenericLevel) session.load(levelClass, genericLevelId);
        } catch (HibernateException e) {
            _logger.error("Could not load Generic Level, see stack trace", e);
            throw new UAPersistenceException("Could not load Generic Level, see stack trace", e);
        }
    }

    /**
     * Get a hierarchy level.
     *
     * @param genericLevelId Level ID
     * @param levelClass     the concrete class
     * @return a generic Level or null, if no record exists for given levelId
     * @throws UAPersistenceException
     */
    public <T extends GenericLevel> T get(Long genericLevelId, Class<T> levelClass) {
            return HibernateUtil.currentSession().get(levelClass, genericLevelId);
    }

    /**
     * Load a concrete instance of a GenericLevel (it's assumed that the ressource existis!)
     *
     * @param genericLevelId Level Id
     * @return a GenericLevel
     * @throws UAPersistenceException
     */
    public GenericLevel load(Long genericLevelId) throws UAPersistenceException {
        try {
            return HibernateUtil.currentSession().load(GenericLevel.class, genericLevelId);
        } catch (HibernateException e) {
            throw new UAPersistenceException("Could not load Generic Level, see stack trace", e);
        }
    }

    /**
     * Get a concrete instance of a GenericLevel
     *
     * @param genericLevelId Level Id
     * @return a GenericLevel
     * @throws UAPersistenceException
     */
    public GenericLevel get(Long genericLevelId) {
        return HibernateUtil.currentSession().get(GenericLevel.class, genericLevelId);
    }

    public void delete(Long genericLevelId) throws UAPersistenceException {
        delete(load(genericLevelId));
    }

    public void delete(GenericLevel level) throws UAPersistenceException {
        HibernateUtil.currentSession().delete(level);
    }

    /**
     * @param event an Event
     * @return a list of all sports albums for the passed sports event
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public List getSportsAlbumsFor(Event event) throws UAPersistenceException {
        return HibernateUtil.currentSession().createCriteria(SportsAlbum.class)
                .add(Expression.eq("event", event))
                .list();
    }


    /**
     * return a unique result for an eventgroup that has the passed zipcode
     *
     * @param zipCode Zip Code
     * @return an EventGroup
     * @throws UAPersistenceException
     */
    public EventGroup loadEventGroupByZipCode(String zipCode) throws UAPersistenceException {
        return HibernateUtil.currentSession().createQuery(
                "select eg " +
                        "from EventGroup eg " +
                        "where eg.zipcode = :zipCode", EventGroup.class)
                .setParameter("zipCode", zipCode)
                .uniqueResult();

    }

    /**
     * List all sports events up until the passed date; Used on the homepage for sportrait
     *
     * @param date A Date
     * @return List of SportsEvents
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public List<SportsEvent> getSportsEventsBefore(Date date) throws UAPersistenceException {
        // do we need to restrict it? maybe later


        // Create CriteriaBuilder
        CriteriaBuilder builder = HibernateUtil.currentSession().getCriteriaBuilder();
        // Create CriteriaQuery
        CriteriaQuery<SportsEvent> criteria = builder.createQuery(SportsEvent.class);
        // get Root element
        Root<SportsEvent> root = criteria.from(SportsEvent.class);
        criteria.select(root)
                .where(builder.lessThanOrEqualTo(root.get("eventDate"), date))
                .orderBy(builder.desc(root.get("eventDate")), builder.desc(root.get("navTitle")))
        ;

        List<SportsEvent> retValCriteria = HibernateUtil.currentSession()
                .createQuery(criteria)
                .setCacheable(true)
                .getResultList();


        return retValCriteria;


    }


    /**
     * Query for retrieving events when a photographer is logged in;
     * Return events that have at least one album where the photographer is the logged in photographer
     *
     * @param eventGroup   List events for this event group (location)
     * @param photographer The Photographer object to get events for.
     * @return Events that have albums for the passed photographer id
     * @throws UAPersistenceException
     */
    public List listEventsWithAlbums(EventGroup eventGroup, Photographer photographer) throws UAPersistenceException {

        List newEventList = HibernateUtil.currentSession().createQuery(
                "select distinct e " +
                        "from Event e " +
                        "inner join e.albums a " +
                        "where e.eventGroup = :eventGroup " +
                        "and a.photographer = :photographer")
                .setParameter("eventGroup", eventGroup)
                .setParameter("photographer", photographer)
                .getResultList();

        _logger.debug("found [" + newEventList.size() + "] events for photographer with id [" + photographer + "]");

        return newEventList;
    }

    /**
     * Query for retrieving all events (not per eventGroup) when a photographer is logged in;
     * Returns all events (not per eventGroup) that have at least one album where the photographer of the album is the logged in photographer
     *
     * @param photographer The Photographer object to get events for.
     * @return Events that have albums for the passed photographer id
     */
    public List<SportsEvent> listEventsWithAlbums(Photographer photographer) throws UAPersistenceException {

        List<SportsEvent> newEventList = HibernateUtil.currentSession().createQuery(
                "select distinct e " +
                        "from Event e " +
                        "inner join e.albums a " +
                        "where e.eventGroup = :eventGroup " +
                        "and a.photographer = :photographer", SportsEvent.class)
                .setParameter("photographer", photographer)
                .getResultList();

        _logger.debug("found [" + newEventList.size() + "] events for photographer with id [" + photographer + "]");
        return newEventList;
    }

    /**
     * 'Admin'-routine: return all events with an album - used for web album admin page only?
     *
     * @param eventGroup The EventGroup
     * @return List of Events
     * @throws UAPersistenceException
     */
    public List listEventsWithAlbums(EventGroup eventGroup) throws UAPersistenceException {
        // Alias on album causes  events to appear more than once .... hence the result tranformer with DISTINCT_ROOT_ENTITY
        _logger.debug("eventGroup = " + eventGroup);
        List eventList = null;


        List newEventList = HibernateUtil.currentSession().createQuery(
                "select distinct e " +
                        "from Event e " +
                        "where e.eventGroup = :eventGroup " +
                        "order by e.eventDate desc, e.navTitle desc ")
                .setParameter("eventGroup", eventGroup)
                .list();
        return newEventList;


    }

    /**
     * Admin View
     * Query for retrieving Albums when a photographer is logged in
     * join fetch products - needed in administration window in order to avoid lazy initialization exception
     * see https://vladmihalcea.com/the-best-way-to-handle-the-lazyinitializationexception/
     *
     * @param event        Event
     * @param photographer Photographer
     * @return A list of Albums
     */
    public List<Album> listAlbumsForPhotographer(Event event, Photographer photographer) throws UAPersistenceException {
        List<Album> albumList = HibernateUtil.currentSession().createQuery(
                "select distinct a from Album a " +
                        "left join fetch a.products " +
                        "left join fetch a.event " +
                        "where a.event = :event " +
                        "and a.photographer = :photographer " +
                        "order by a.eventCategory.eventCategoryId asc", Album.class) // ordering by the eventcategory
                .setParameter("event", event)
                .setParameter("photographer", photographer)
                .list();

        return albumList;
    }

    /**
     * Admin View
     * Return all albums for an event
     * join fetch products - needed in administration window in order to avoid lazy initialization exception
     * see https://vladmihalcea.com/the-best-way-to-handle-the-lazyinitializationexception/
     *
     * @param event The Event
     * @return List of Album s
     */
    public List<Album> listAlbumsForPhotographer(Event event) throws UAPersistenceException {

        @SuppressWarnings("UnnecessaryLocalVariable")
        List<Album> albumList = HibernateUtil.currentSession().createQuery(
                "select distinct a from Album a " +
                        "left join fetch a.products " +
                        "left join fetch a.event " +
                        "where a.event = :event " +
                        "order by a.eventCategory.eventCategoryId asc", Album.class)
                .setParameter("event", event)
                .getResultList();


        return albumList;
    }

    /**
     * Query for retrieving Albums when a photographer is logged in
     *
     * @param photographerId Id of the photographer
     * @return list of albums
     * @throws UAPersistenceException
     */
    public List<Album> listAlbumsForPhotographer(Long photographerId) throws UAPersistenceException {
        List<Album> albumList = HibernateUtil.currentSession().createQuery(
                "select a from Album a " +
                        "where a.photographer.photographerId = :photographerId " +
                        "order by navTitle desc ", Album.class)
                .setParameter("photographerId", photographerId)
                .getResultList();

        return albumList;

    }

    /**
     * If available return the sportsalbum that belongs to the passed photographer and category
     *
     * @param photographer  the photographer who owns the alblum
     * @param eventCategory the category of wanted album
     * @return null or a single event category
     * @throws UAPersistenceException In case we have more than one matching album
     */
    public SportsAlbum getSportsAlbumFor(Photographer photographer, EventCategory eventCategory) throws UAPersistenceException {
        return (SportsAlbum) HibernateUtil.currentSession().createCriteria(SportsAlbum.class)
                .add(Expression.eq("eventCategory", eventCategory))
                .add(Expression.eq("photographer", photographer))
                .uniqueResult();

    }

}

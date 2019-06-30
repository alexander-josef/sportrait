package ch.unartig.sportrait.zk.windows;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.Event;
import ch.unartig.studioserver.model.EventCategory;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.EventCategoryDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.NonUniqueObjectException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;


public class EventWindow extends Window {
    private Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * The Event object to edit or create
     */
    SportsEvent eachEvent;

    /**
     * Constructor checks for available eachEvent reference. If null, a new event needs to be created.
     */
    public EventWindow() {
        _logger.debug("Constructing EventWindow");
        eachEvent = (SportsEvent) Executions.getCurrent().getDesktop().getAttribute("event");
        // check for edit or creation of new photographer
        if (eachEvent==null) {
            eachEvent = new SportsEvent();
        }
    }

    public void saveEvent(Event event) {
        _logger.debug("Saving event ....");
        _logger.debug("Event = " + event);
        try {
            GenericLevelDAO genericLevelDao = new GenericLevelDAO();
            try {
                genericLevelDao.saveOrUpdate(event);
            } catch (NonUniqueObjectException e) {
                // can we avoid all possibilities where multiple objects of persistent entities with the same identifier are associated with the session? Probably not
                // use this strategy to react to a this specific exception with a merge
                _logger.info("saving an event cause NonUniqueObjectException - trying to merge instead");
                genericLevelDao.merge(event);
            }
            HibernateUtil.commitTransaction();
        } catch (Exception e) {
            _logger.error("Can not save event", e);
            e.printStackTrace();
            throw new RuntimeException("Can not save event, see exception", e);
        }

        // reload the main page:
        Executions.sendRedirect("main-zul.html?tab=tab4");

    }

    /**
     *
     * @param category
     * @param title
     */
    public void updateCategory(EventCategory category,String title)
    {
        _logger.debug("category : " + category);
        _logger.debug("title : " + title);

        category.setTitle(title);
        EventCategoryDAO eventCategoryDao = new EventCategoryDAO();
        eventCategoryDao.saveOrUpdate(category);
    }

    public void deleteCategory(EventCategory category)
    {
        _logger.debug("deleting : " + category.getTitle());
        // deleting ONLY possible if albums and photos that are attached to that category are deleted as well ......
        EventCategoryDAO eventCategoryDao = new EventCategoryDAO();
        // re-loading the category to the session:
        category = eventCategoryDao.load(category.getEventCategoryId());
        int numberOfAlbums = category.getAlbums().size();
        if (numberOfAlbums !=0)
        {
            Messagebox.show("Kategorie hat noch "+numberOfAlbums+" Album(s) angehaengt. Loeschen nicht moeglich.");

        } else
        {
            SportsEvent event = category.getEvent();
            event.getEventCategories().remove(category);
            saveEvent(event);
        }

    }

    public void addCategory(String title)
    {
        eachEvent.getEventCategories().add(new EventCategory(title.trim(),eachEvent));
        saveEvent(eachEvent);
    }

    public SportsEvent getEachEvent() {
        return eachEvent;
    }

    public void setEachEvent(SportsEvent eachEvent) {
        this.eachEvent = eachEvent;
    }

}
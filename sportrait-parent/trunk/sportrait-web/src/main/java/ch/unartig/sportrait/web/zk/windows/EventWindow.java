package ch.unartig.sportrait.web.zk.windows;

import ch.unartig.studioserver.model.Event;
import ch.unartig.studioserver.model.EventCategory;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import ch.unartig.u_core.persistence.DAOs.EventCategoryDAO;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;


public class EventWindow extends Window {
    Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * The Photographer object to edit or create
     */
    SportsEvent eachEvent;

    /**
     * Constructor checks for available eachEvent reference. If null, a new event needs to be created.
     */
    public EventWindow() {
        _logger.debug("Constructing PhotographerWindow");
        eachEvent = (SportsEvent) Executions.getCurrent().getDesktop().getAttribute("event");
        // check for edit or creation of new photographer
        if (eachEvent==null) {
            eachEvent = new SportsEvent();
        }
    }

    public void saveEvent(Event event) {
        _logger.debug("Saving event ....");
        System.out.println("Event = " + event);
        try {
            GenericLevelDAO genericLevelDao = new GenericLevelDAO();
            genericLevelDao.saveOrUpdate(event);
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
            try {
                Messagebox.show("Kategorie hat noch "+numberOfAlbums+" Album(s) angehaengt. Loeschen nicht moeglich.");
            } catch (InterruptedException e) {
                throw new RuntimeException("Messagebox exception");
            }
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
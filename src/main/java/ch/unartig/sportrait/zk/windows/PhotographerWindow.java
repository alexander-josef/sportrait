package ch.unartig.sportrait.zk.windows;

import org.apache.log4j.Logger;
import org.zkoss.zul.Window;
import org.zkoss.zk.ui.Executions;
import ch.unartig.studioserver.persistence.DAOs.PhotographerDAO;
import ch.unartig.studioserver.persistence.DAOs.UserRoleDAO;
import ch.unartig.studioserver.persistence.DAOs.UserProfileDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import ch.unartig.studioserver.model.Photographer;
import ch.unartig.studioserver.model.UserProfile;
import ch.unartig.studioserver.model.UserRole;
import ch.unartig.studioserver.Registry;
import ch.unartig.controller.Client;


public class PhotographerWindow extends Window {
    private Logger _logger = Logger.getLogger(getClass().getName());


    /**
     * The Photographer object to edit or create
     */
    private Photographer eachPhotographer;
    private Client client;

    /**
     * Constructor checks for available eachPhotographer reference. If null, a new photographer needs to be created.
     */
    public PhotographerWindow() {
        _logger.debug("Constructing PhotographerWindow");
        eachPhotographer = (Photographer) Executions.getCurrent().getDesktop().getAttribute("photographer");
        this.client = (Client) Executions.getCurrent().getDesktop().getSession().getAttribute(Registry._SESSION_CLIENT_NAME);
        UserRoleDAO userRoleDao = new UserRoleDAO();
        // check for edit or creation of new photographer
        if (eachPhotographer == null) {
            UserProfile userProfile = new UserProfile(userRoleDao.loadRoleByName(UserRole._PHOTOGRAPHER_ROLE_NAME));
            // remember, we need to set both sides of the association:
            eachPhotographer = new Photographer(userProfile);
            userProfile.addPhotographer(eachPhotographer);
            userProfile.setGender("m");
        }
    }

    /**
     * Since the photographer is part of the userprofile, the userprofile is saved and the photographer is saved cascaded  // <- this changed after hibernate 5 migration
     * @param photographer
     */
    public void savePhotographer(Photographer photographer) {
        _logger.debug("Saving photographer ....");
//        checkPasswordsMatch();
        try {
            // UserProfileDAO userProfileDao = new UserProfileDAO();
            // photographer is part of the userprofile
            // userProfileDao.saveOrUpdate(photographer.getUserProfile());

            // todo: load photographer againn by ID?

           PhotographerDAO photographerDao = new PhotographerDAO();
           photographerDao.saveOrUpdate(photographer);

            HibernateUtil.commitTransaction();
        } catch (Exception e) {
            _logger.error("Can not save photographer", e);
            e.printStackTrace();
            throw new RuntimeException("Can not save photographer, see exception", e);
        }

//        Executions.sendRedirect("main-zul.html?tab=tab7");
        Executions.sendRedirect("main-zul.html");

    }

    /**
     *
     */
    public void resetNewPhotographer() {
        // ???
        Executions.sendRedirect("main-zul.html");
    }


    public Photographer getEachPhotographer() {
        return eachPhotographer;
    }

    public void setEachPhotographer(Photographer eachPhotographer) {
        this.eachPhotographer = eachPhotographer;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

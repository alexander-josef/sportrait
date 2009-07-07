/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 07.06.2007$
 *
 * Copyright (c) 2007 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.1  2007/06/09 11:15:37  alex
 * photographer
 *
 ****************************************************************/
package ch.unartig.u_core.actions;

import ch.unartig.u_core.controller.Client;
import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.beans.AdminForm;
import ch.unartig.u_core.presentation.NoTimeAlbum;
import ch.unartig.u_core.model.*;
import ch.unartig.sportrait.web.beans.PhotographerAdminBean;
import ch.unartig.u_core.presentation.AlbumType;
import ch.unartig.u_core.persistence.DAOs.*;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PhotographerAdminAction extends MappingDispatchAction
{

    Logger _logger = Logger.getLogger(getClass().getName());

    @SuppressWarnings({"UnusedDeclaration"})
    public ActionForward listPhotographers(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {
        PhotographerDAO photographerDao = new PhotographerDAO();

        List photographers = photographerDao.list();
        request.setAttribute("photographers", photographers);
        return mapping.findForward("success");

    }

    public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {
        // hook for an action after login
        return mapping.findForward("success");

    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UnartigException
     */
    public ActionForward editCreatePhotographer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {
        PhotographerDAO photographerDao = new PhotographerDAO();
        DynaActionForm photographerForm = (DynaActionForm) form;
        final String photographerId = request.getParameter("photographerId");
        if (photographerId != null && !"".equals(photographerId))
        {
            request.setAttribute("photographerId",photographerId);
            Photographer photographer = photographerDao.load(new Long(photographerId));

            try
            {
                BeanUtils.copyProperties(photographerForm, photographer.getUserProfile());
                BeanUtils.copyProperties(photographerForm, photographer);

            } catch (IllegalAccessException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                throw new UnartigException(e);
            } catch (InvocationTargetException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                throw new UnartigException(e);
            }


        }
        return mapping.findForward("success");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UnartigException
     */
    public ActionForward saveUpdatePhotographer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {

        ActionMessages msgs = new ActionMessages();

        PhotographerDAO photographerDao = new PhotographerDAO();
        DynaActionForm photographerForm = (DynaActionForm) form;
        final String photographerId = request.getParameter("photographerId");
        Photographer photographer;
        HibernateUtil.beginTransaction();
        if (photographerId != null && !"".equals(photographerId))
        {
            photographer = photographerDao.load(new Long(photographerId));
        } else
        {
            // make sure the id is null
            photographerForm.set("photographerId",null);
            UserRoleDAO userRoleDao = new UserRoleDAO();
            UserProfile userProfile = new UserProfile(userRoleDao.loadRoleByName(UserRole._PHOTOGRAPHER_ROLE_NAME));
            photographer = new Photographer(userProfile);

        }

// copy properties from form to bean
        try
        {
            BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
            // the following lines ensures that empty ids are stored as 'null' -- not as null, which leads to a database error
            ConvertUtils.register(new LongConverter(null), Long.class);
            BeanUtils.copyProperties(photographer.getUserProfile(), photographerForm);
            BeanUtils.copyProperties(photographer, photographerForm);
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new UnartigException(e);
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new UnartigException(e);
        }

//save or update
        try
        {
            photographerDao.saveOrUpdate(photographer);
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e)
        {
            HibernateUtil.rollbackTransaction();
            throw new UnartigException("cannot save photographer", e);
        } finally
        {
//            HibernateUtil.finishTransaction();
        }
        // todo add success message for user
        msgs.add(ActionMessages.GLOBAL_MESSAGE,new ActionMessage("message.photographer.saved"));
        saveMessages(request.getSession(),msgs);
        // redirect with photographer id
        return new ActionForward(mapping.findForward("success").getPath() + "?photographerId=" + photographer.getPhotographerId());
    }


    /**
     * prepare all levels of the photographer to be displayed in the level overview
     * </br> if a level (album) ID has been selected, prepare level data to be edited
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws UnartigException
     */
    public ActionForward getOverview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws UnartigException
    {
        // todo filter for photographer
        // show only events that have albums of the photographer

        _logger.debug("preparing overview");
        AdminForm adminForm = (AdminForm) form;
        List categories;
        List eventGroups;
        Client client = (Client)request.getSession().getAttribute(Registry._SESSION_CLIENT_NAME);
        GenericLevelDAO levelDao = new GenericLevelDAO();
        eventGroups = levelDao.listGenericLevel(EventGroup.class);
        PhotographerAdminBean photographerAdminBean = new PhotographerAdminBean(client);

        photographerAdminBean.setEventGroups(eventGroups);
        photographerAdminBean.setLevel(null);

        request.setAttribute("photographerAdminBean",photographerAdminBean);
//        request.setAttribute("eventGroups", eventGroups);
        request.setAttribute("level", null);
        if (adminForm.getGenericLevelId()!=null && !"".equals(adminForm.getGenericLevelId().toString().trim()))
        {
            editLevel(mapping,form, photographerAdminBean);
        }
        return mapping.findForward("levelOverview");
    }


    /**
     * load level and prepare form to edit level
     *
     * @param mapping
     * @param form
     * @param photographerAdminBean
     * @return
     * @throws UnartigException
     */
    public ActionForward editLevel(ActionMapping mapping, ActionForm form, PhotographerAdminBean photographerAdminBean) throws UnartigException
    {
        AdminForm adminForm = (AdminForm) form;
        ProductTypeDAO ptDao = new ProductTypeDAO();

        GenericLevelDAO levelDao = new GenericLevelDAO();
        Long levelId = adminForm.getGenericLevelId();
        GenericLevel level = levelDao.load(levelId);

        // get the 'master data' (stammdaten):
//        prepareAdminAttributes(level.getLevelType(), request);
        List productTypeList = ptDao.listProductTypes();


        adminForm.setEventDateDisplay(level.getEventDateDisplay());
        adminForm.setLongTitle(level.getLongTitle());
        adminForm.setNavTitle(level.getNavTitle());
        adminForm.setParentLevelId(level.getParentLevel().getGenericLevelId());
        // or reload as album ...
        if (Registry._NAME_ALBUM_LEVEL_TYPE.equals(level.getLevelType()))
        {
            // reload level as Album
            Album album = (Album)levelDao.load(levelId, Album.class);
            // set the product-prices map
            adminForm.createProductPricesMap(album.getProducts());
            if (level.getAlbumType() != null && level.getAlbumType() instanceof NoTimeAlbum)
            {
                adminForm.setNoTime(Boolean.TRUE);
            }

        }
        adminForm.setPrivateEvent(level.getIsPrivate());
        adminForm.setDescription(level.getDescription());
        adminForm.setQuickAccess(level.getQuickAccess());
        adminForm.setPrivateAccessCode(level.getPrivateAccessCode());
        adminForm.setLevelType(level.getLevelType());
        if (level.getAlbumType() == AlbumType._noTimeAlbum)
        {
            adminForm.setNoTime(Boolean.TRUE);
        }
        photographerAdminBean.setLevel(level);
        photographerAdminBean.setProductTypeList(productTypeList);

        return mapping.findForward("success");
    }
    

}

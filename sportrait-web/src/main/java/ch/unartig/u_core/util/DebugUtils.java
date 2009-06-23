/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 2, 2005$
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
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.3  2006/11/05 16:41:43  alex
 * action messages work for order confirmation
 *
 * Revision 1.2  2006/01/27 09:30:36  alex
 * new pager implemenatation
 *
 * Revision 1.1  2005/11/03 15:50:55  alex
 * languages and upload stuff
 *
 ****************************************************************/
package ch.unartig.u_core.util;

import ch.unartig.u_core.Registry;
import ch.unartig.studioserver.beans.AlbumBean;
import ch.unartig.u_core.model.Photo;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessages;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

public class DebugUtils
{
     private static Logger _logger = Logger.getLogger("ch.unartig.u_core.util.DebugUtils");

    /**
     * helper for showing the content of the request
     *
     * @param request
     */
    public static void debugRequest(HttpServletRequest request)
    {

        Enumeration requestParamNames = request.getParameterNames();
        _logger.debug("******** Method = " + request.getMethod());
        _logger.debug("******** ContentType = " + request.getContentType());
        _logger.debug("******** ContentType = " + request.getContentType());
        while (requestParamNames.hasMoreElements())
        {
            String s = (String) requestParamNames.nextElement();
            _logger.debug("******** Parameter [" + s + "] =  " + request.getParameter(s));
        }

        try
        {
            ServletInputStream sis = request.getInputStream();
            _logger.debug("-----------------------------------------------");
            int c;
            StringBuffer requestString = new StringBuffer();
            requestString.append((char) (Character.LINE_SEPARATOR));
            while ((c = sis.read()) != -1)
            {
                requestString.append((char) c);
            }
            _logger.debug(requestString.toString());
            _logger.debug("-----------------------------------------------");
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static AlbumBean debugAlbumBeanSession(int hour, int minutes, Long albumId, HttpSession session)
    {
        System.out.println("AlbumAction.trySessionForAlbum");
        System.out.println("hour = " + hour);
        System.out.println("minutes = " + minutes);
        System.out.println("albumId = " + albumId);
        AlbumBean ob = (AlbumBean) session.getAttribute(Registry._NAME_ALBUM_BEAN_ATTR);
        if (ob != null)
        {
            System.out.println("ob.getHour() = " + ob.getHour());
            System.out.println("ob.getMinutes() = " + ob.getMinutes());
            System.out.println("ob.getAlbum() = " + ob.getAlbum().getGenericLevelId().toString());
        } else
        {
            System.out.println("Overview not present !!!!!!!!!!!!!!!!!!");
        }
        return ob;
    }

    public static void debugPhotos(List photos)
    {
        _logger.debug("Debugging returned photos ");

        for (int i = 0; i < photos.size(); i++)
        {
            Photo photo = (Photo) photos.get(i);
            _logger.debug("ID : " + photo.getPhotoId());
            _logger.debug("Album : " + photo.getAlbum().getNavTitle());
            _logger.debug("Picture Taken Date : " + photo.getPictureTakenDate().toString());

        }
    }


    
    public static void debugActionMessage(HttpServletRequest request)
    {
        ActionErrors errors = (ActionErrors)request.getAttribute(Globals.ERROR_KEY);
        if (errors!=null)
        {

        System.out.println("request.getAttribute(Globals.ERROR_KEY) = " + request.getAttribute(Globals.ERROR_KEY));
        System.out.println("request.getSession().getAttribute(Globals.ERROR_KEY) = " + request.getSession().getAttribute(Globals.ERROR_KEY));

/*
            System.out.println("DebugUtils.debugActionMessage : action errors" + errors.get().toString());

            Iterator iterator = errors.get();
            while (iterator.hasNext())
            {
                ActionMessage actionError = (ActionMessage) errors.get().next();
                System.out.println("actionError.getKey() = " + actionError.getKey());
                System.out.println("actionError.getValues() = " + actionError.getValues());
                System.out.println("--------------------------------------------------------");
            }
*/
        } else
        {
            System.out.println("DebugUtils.debugActionMessage errors = null");
        }

        ActionMessages messages = (ActionMessages)request.getAttribute(Globals.MESSAGE_KEY);
        if (messages != null)
        {

            System.out.println("request.getAttribute(Globals.MESSAGE_KEY) = " + request.getAttribute(Globals.MESSAGE_KEY));
            System.out.println("request.getSession().getAttribute(Globals.MESSAGE_KEY) = " + request.getSession().getAttribute(Globals.MESSAGE_KEY));

/*
            System.out.println("DebugUtils.debugActionMessage : action messages" + messages.get().toString());
            Iterator iterator = messages.get();
            while (iterator.hasNext())
            {
                ActionMessage actionMessage = (ActionMessage) messages.get().next();
                System.out.println("actionMessage.getKey() = " + actionMessage.getKey());
                System.out.println("actionMessage.getValues() = " + actionMessage.getValues());
                System.out.println("--------------------------------------------------------");
            }
*/
        } else
        {
            System.out.println("DebugUtils.debugActionMessage messages = null");
        }
    }
}

/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Nov 18, 2005$
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
 * Revision 1.7  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.6  2006/02/22 14:00:51  alex
 * new album nav concept works also in display
 *
 * Revision 1.5  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.4  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 * Revision 1.3  2005/11/19 16:31:43  alex
 * bookmarks of displays should work now
 *
 * Revision 1.2  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.1  2005/11/18 17:28:36  alex
 * back link, incl. new interface for naviable objects
 *
 ****************************************************************/
package ch.unartig.u_core.util;

import ch.unartig.u_core.Registry;
import ch.unartig.u_core.beans.AbstractAlbumBean;
import ch.unartig.studioserver.beans.ShoppingCart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionHelper
{
    /**
     * helper method to get an instance of an album bean from the session
     * @param request
     * @return overviewBean
     */
    public static AbstractAlbumBean getAlbumBeanFromSession(HttpServletRequest request)
    {
        // use abstract album bean ... it could be any album bean
        return (AbstractAlbumBean) request.getSession().getAttribute(Registry._NAME_ALBUM_BEAN_ATTR);
    }

    /**
     * removes the attribute with the passed name from the session if it exists.
     * @param request
     * @param attributeName
     * @return true if attributeName has been removed, false if session or attribute didn't exist
     */
    public static boolean remove(HttpServletRequest request, String attributeName)
    {
        HttpSession session = request.getSession(false);
        if (session!=null && session.getAttribute(attributeName)!=null)
        {
            session.removeAttribute(attributeName);
            return true;
        }
        return false;
    }

    public static ShoppingCart getShoppingCartFromSession(HttpServletRequest request)
    {
        HttpSession s = request.getSession();
        return (ShoppingCart) s.getAttribute(Registry._NAME_SHOPPING_CART_SESSION);

    }
}

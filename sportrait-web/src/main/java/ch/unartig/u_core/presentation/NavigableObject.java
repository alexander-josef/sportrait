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
 * Revision 1.2  2006/02/20 16:54:49  alex
 * new album nav concept works
 *
 * Revision 1.1  2005/11/18 17:28:36  alex
 * back link, incl. new interface for naviable objects
 *
 ****************************************************************/
package ch.unartig.u_core.presentation;

import java.util.Map;

/**
 * a navigable object has the possibility to navigate to certain positions in the album, shopping cart or so</br>
 * most importantly, it defines a back link to the album
 */
public interface NavigableObject
{
    /**
     * method to set a back-link on a navigable object
     * @param backToAlbumParams
     */
    public void setBackToAlbumParams(Map backToAlbumParams);
}

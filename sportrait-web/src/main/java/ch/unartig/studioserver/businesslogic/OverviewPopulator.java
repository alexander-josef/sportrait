/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 26, 2005$
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
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.5  2006/01/27 09:30:36  alex
 * new pager implemenatation
 *
 * Revision 1.4  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.3  2005/11/05 10:32:14  alex
 * shopping cart and minor problems, exception handling
 *
 * Revision 1.2  2005/11/01 11:28:39  alex
 * pagination works; put logic in overview bean
 *
 * Revision 1.1  2005/10/26 14:34:32  alex
 * first version of album overview
 * new mappings in struts for the /album/** url
 *
 ****************************************************************/
package ch.unartig.studioserver.businesslogic;

import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.model.Album;
import ch.unartig.studioserver.beans.AlbumBean;

/**
 * Strategy Interface for populating an overview
 */
public interface OverviewPopulator
{
    public static String DEFAULT_STRATEGY = "FSPath";

    public void populate(AlbumBean ob, Album album) throws UnartigException;
}
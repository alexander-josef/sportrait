/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 29.04.2006$
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
 * Revision 1.2  2006/05/01 12:43:48  alex
 * fix for album reload for sports and event album
 *
 * Revision 1.1  2006/04/29 23:34:56  alex
 * many sola features, bugs, hibernate config
 *
 ****************************************************************/
package ch.unartig.u_core.presentation;

import ch.unartig.u_core.model.Event;
import ch.unartig.u_core.model.Album;

/**
 * this album can not be persisted!! is only exists to be used to be created dynamically from different albums. hence the name 'EventAlbum'
 * @author Alexander Josef, 2006 unartig AG
 */
public class EventAlbum extends Album {


    private String startNumber;
    public EventAlbum(Event event, String startNumber)
    {
        setGenericLevelId(new Long(-1));
        setEvent(event);
        setNavTitle("");
        setActionStringPart("/sportsAlbum/");
        this.startNumber=startNumber;
    }


    /**
     * overridden for special action string vor event album
     *
     *
     * @return string
     */
    public String getActionString()
    {
        String actionString = getActionStringPart() + getGenericLevelId().toString() + "/" + "query" + ".html" +
                "?etappe=-1" +
                "&startNumber=" + startNumber +
                "&levelId=" + getEvent().getGenericLevelId().toString() +
                "&albumLevel=false" +
                "&eventAlbum=true";
        return actionString;
    }

}

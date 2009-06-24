/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 08.02.2006$
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
 * Revision 1.3  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 * Revision 1.2  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.1  2006/02/08 18:04:50  alex
 * first steps for album type configuration
 *
 ****************************************************************/
package ch.unartig.u_core.presentation;

public abstract class AlbumType
{
    protected String designator;

    public static NoTimeAlbum _noTimeAlbum = new NoTimeAlbum();
    public static TimedAlbum _timedAlbum = new TimedAlbum();
    public static StartNummerAlbum _startNummerAlbum = new StartNummerAlbum();

    public String getDesignator()
    {
        return designator;
    }

    /**
     *
     * @param albumTypeString
     * @return a new instance of an album type
     */
    public static AlbumType getAlbumType(String albumTypeString)
    {
        if ("notime".equals(albumTypeString))
        {
            return _noTimeAlbum;
        }
        else
        {
            return _timedAlbum;
        }
    }

    /**
     * used in admin interface for level editing
     * @return startnummer album type
     */
    public StartNummerAlbum getStartNummerAlbum()
    {
        return _startNummerAlbum;
    }

}

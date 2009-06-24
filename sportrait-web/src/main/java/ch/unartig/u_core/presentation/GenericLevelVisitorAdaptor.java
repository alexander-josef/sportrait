/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 15.02.2006$
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
 * Revision 1.1  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 ****************************************************************/
package ch.unartig.u_core.presentation;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.model.Category;
import ch.unartig.u_core.model.Event;
import ch.unartig.u_core.model.EventGroup;
import ch.unartig.u_core.model.Album;

public class GenericLevelVisitorAdaptor implements GenericLevelVisitor
{

    public void visit(Category category) throws UnartigException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visit(EventGroup eventGroup) throws UAPersistenceException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visit(Event event) throws UAPersistenceException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void visit(Album album) 
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

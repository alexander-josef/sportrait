/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 29.03.2006$
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
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.1  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 ****************************************************************/
package ch.unartig.u_core.persistence.DAOs;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.model.PhotoSubject;
import ch.unartig.u_core.model.Album;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class PhotoSubjectDAO
{
    /**
     * Returns a photosubject with matching startnumber
     * One or  more photosubjects can be returned? if there could be more, a different import routine that has more information shall be used
     * @param startNumber
     * @param album
     * @return
     * @throws UAPersistenceException
     */
    public PhotoSubject findByStartNumberAndAlbum(String startNumber, Album album) throws UAPersistenceException
    {
        Criteria criteria = HibernateUtil.currentSession().createCriteria(PhotoSubject.class)
                .createAlias("eventRunners", "runner")
                .add(Restrictions.eq("runner.startnumber", startNumber))
                .add(Restrictions.eq("runner.event", album.getEvent()));


        return (PhotoSubject) criteria.uniqueResult();
    }

    /**
     * @param photoSubject
     * @return
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    public Long saveOrUpdate(PhotoSubject photoSubject) throws UAPersistenceException
    {
        return (Long)HibernateUtil.currentSession().save(photoSubject);
    }
}

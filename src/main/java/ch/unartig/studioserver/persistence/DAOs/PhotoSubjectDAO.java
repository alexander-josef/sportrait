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
package ch.unartig.studioserver.persistence.DAOs;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.EventRunner;
import ch.unartig.studioserver.model.PhotoSubject;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

public class PhotoSubjectDAO
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * Returns a photosubject with matching startnumber
     * One or  more photosubjects can be returned? if there could be more, a different import routine that has more information shall be used
     * // todo : enhance with faceID information ! this is exactly what a photosubject was planned for and not implemented before
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
     * @throws UAPersistenceException
     */
    public Long saveOrUpdate(PhotoSubject photoSubject) throws UAPersistenceException
    {
        return (Long)HibernateUtil.currentSession().save(photoSubject);
    }


    private PhotoSubject createPhotoSubject() throws UAPersistenceException
    {
        PhotoSubjectDAO subjDao = new PhotoSubjectDAO();
        PhotoSubject retVal = new PhotoSubject();
        HibernateUtil.beginTransaction();
        Long photoSubjectId = null;
        try
        {
            _logger.debug("going to save new PhotoSubject");
            photoSubjectId = subjDao.saveOrUpdate(retVal);
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e)
        {
            _logger.error("cannot save photoSubject, rolling back", e);
            HibernateUtil.rollbackTransaction();
            throw new UAPersistenceException("cannot save photoSubject", e);
        } finally
        {
//            HibernateUtil.finishTransaction();
        }
        _logger.debug("created new photo subject with id : " + retVal.getPhotoSubjectId());
        _logger.debug("id : " + photoSubjectId);
        return retVal;
    }


    /**
     * instantiates and saves a new PhotoSubject with the given startNumber:
     * <ol>
     * <li>create a photosubject, save it
     * <li>create an eventrunner entry with given startnumber,event (from album) and new photo subject
     * <li>save eventrunner
     * </ol>
     *
     * @param startNumber
     * @param album
     * @return PhotoSubject
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public PhotoSubject createNewSubject(String startNumber, Album album) throws UAPersistenceException
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        EventRunnerDAO eventRunnerDao = new EventRunnerDAO();
        PhotoSubject photoSubject = createPhotoSubject();

        // since the session was closed before we need to reload album:
        // if we uncomment the following line, does the 'no session' problem disappear?? YES!
//        System.out.println("buuuh!");
//        System.out.println("album = " + album);
        try
        {
            HibernateUtil.beginTransaction();
            album = (Album) glDao.load(album.getGenericLevelId(), Album.class);
            _logger.debug("creating and saving new eventRunner");
            EventRunner eventRunner = new EventRunner(album.getEvent(), startNumber, photoSubject);
            eventRunnerDao.save(eventRunner);
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e)
        {
            _logger.error("cannot save eventRunner, rolling back", e);
            HibernateUtil.rollbackTransaction();
            throw new UAPersistenceException("cannot save eventRunner", e);
        }
        return photoSubject;
    }

    /**
     * <ol>
     * Look for an existing photosubject (via the eventRunners table)
     * <li>search in eventRunners for given album and startNumber
     * <li>asdf
     *
     * @param startNumber
     * @param album
     * @return
     * @throws UAPersistenceException
     */
    public PhotoSubject findOrCreateSubjectByStartNumber(String startNumber, Album album) throws UAPersistenceException
    {
        PhotoSubjectDAO photoSubjDao = new PhotoSubjectDAO();

        // todo : enhance search with FaceID information

        PhotoSubject subj = photoSubjDao.findByStartNumberAndAlbum(startNumber, album);
        if (subj == null)
        {
            _logger.debug("no photo subject found, going to creating a new one");
            subj = photoSubjDao.createNewSubject(startNumber, album);
        }
        _logger.debug("returning photo-subject : " + subj);
        return subj;
    }

}

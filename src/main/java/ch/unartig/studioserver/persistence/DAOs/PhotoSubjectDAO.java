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
     * todo : CAUTION : used existing "name" field for faceID - change later
     * @param startNumber
     * @param album
     * @param faceId new with image recognition - amazon face collection ID - WILL BE SAVED TO NAME FIELD OF SUBJECT !!
     * @return
     * @throws UAPersistenceException
     */
    public PhotoSubject findByStartNumberAndAlbum(String startNumber, Album album, String faceId) throws UAPersistenceException
    {
        Criteria criteria = HibernateUtil.currentSession().createCriteria(PhotoSubject.class)
                .createAlias("eventRunners", "runner")
                .add(Restrictions.eq("runner.startnumber", startNumber))
                .add(Restrictions.eq("runner.event", album.getEvent()));

        if (faceId!=null && !faceId.isEmpty()) {
            //todo : CAUTION : used existing "name" field for faceID - change later
            criteria
                    .add(Restrictions.eq("name", faceId));
        }


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
     * @param faceId new face id with image recognition - FROM NAME FIELD OF SUBJECT - CAUTION - todo: CHANGE LATER
     * @return PhotoSubject
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    public PhotoSubject createNewSubject(String startNumber, Album album, String faceId) throws UAPersistenceException
    {
        GenericLevelDAO glDao = new GenericLevelDAO();
        EventRunnerDAO eventRunnerDao = new EventRunnerDAO();
        PhotoSubject photoSubject = createPhotoSubject();
        // CAUTTION : faceID saved in name field
        photoSubject.setName(faceId); // needs to be saved separately in creation method ??

        // since the session was closed before we need to reload album:
        // if we uncomment the following line, does the 'no session' problem disappear?? YES!
//        System.out.println("buuuh!");
//        System.out.println("album = " + album);
        try
        {
            // HibernateUtil.beginTransaction();
            album = (Album) glDao.load(album.getGenericLevelId(), Album.class);
            _logger.debug("creating and saving new eventRunner");
            EventRunner eventRunner = new EventRunner(album.getEvent(), startNumber, photoSubject);
            eventRunnerDao.save(eventRunner);
            _logger.debug("eventrunner saved to db");
            // HibernateUtil.commitTransaction();
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
     * @param faceId
     * @return
     * @throws UAPersistenceException
     */
    public PhotoSubject findOrCreateSubjectByStartNumberAndFace(String startNumber, Album album, String faceId) throws UAPersistenceException
    {
        PhotoSubjectDAO photoSubjDao = new PhotoSubjectDAO();

        PhotoSubject subj = photoSubjDao.findByStartNumberAndAlbum(startNumber, album, faceId);
        if (subj == null)
        {
            _logger.debug("no photo subject found, going to creating a new one - with faceId : " + faceId);
            subj = photoSubjDao.createNewSubject(startNumber, album, faceId);
        }
        _logger.debug("returning photo-subject : " + subj);
        return subj;
    }

}

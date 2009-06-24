/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since 28.03.2006$
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
 * Revision 1.3  2007/05/02 19:09:49  alex
 * mapping, first actions
 *
 * Revision 1.2  2007/03/27 16:39:17  alex
 * refactored studioalbum into album
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.7  2006/05/02 17:55:54  alex
 * fix for mapping, no session closed exception
 *
 * Revision 1.6  2006/05/01 18:29:31  alex
 * delete function fixed in mapping
 *
 * Revision 1.5  2006/05/01 12:43:48  alex
 * fix for album reload for sports and event album
 *
 * Revision 1.4  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.3  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.2  2006/04/10 21:07:02  alex
 * finish time mapping works
 *
 * Revision 1.1  2006/04/06 18:31:22  alex
 * display fixed for sports album
 *
 ****************************************************************/
package ch.unartig.u_core.presentation;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.model.PhotoSubject;
import ch.unartig.u_core.model.Album;
import ch.unartig.u_core.model.Photo;
import ch.unartig.u_core.model.EventRunner;
import ch.unartig.u_core.persistence.DAOs.EventRunnerDAO;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import ch.unartig.u_core.persistence.DAOs.PhotoDAO;
import ch.unartig.u_core.persistence.DAOs.PhotoSubjectDAO;
import ch.unartig.u_core.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SportsAlbumMapper
{

    private Album album;
    private InputStream mappingInputStream;
    /*running-difference from the time measurement to the photopoint in seconds*/
    private int photoPointDifference;
    private int photoPointTolerance;
    private boolean photopointBeforeFinishTime;
    Logger _logger = Logger.getLogger(getClass().getName());

    public SportsAlbumMapper(InputStream mappingInputStream, Album album)
    {

        this.album = album;
        this.mappingInputStream = mappingInputStream;
    }

    private SportsAlbumMapper()
    {
        //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * Creation method for a finish time mapper
     *
     * @param mappingInputStream
     * @param album
     * @param photoPointDifference
     * @param photoPointTolerance
     * @param photopointBeforeFinishtime
     * @return mapper
     */
    public static SportsAlbumMapper createFinishTimeMapper(InputStream mappingInputStream, Album album, int photoPointDifference, String photoPointTolerance, boolean photopointBeforeFinishtime)
    {
        SportsAlbumMapper mapper = new SportsAlbumMapper(mappingInputStream, album);
        mapper.photoPointDifference = photoPointDifference;
        if (photoPointTolerance != null && !"".equals(photoPointTolerance))
        {
            mapper.photoPointTolerance = Integer.parseInt(photoPointTolerance);
        } else
        {
            mapper.photoPointTolerance = Registry._DEFAULT_PHOTOPOINT_TOLERANCE_SECONDS;
        }
        mapper.photopointBeforeFinishTime = photopointBeforeFinishtime;
        return mapper;
    }

    /**
     * simple creation method to create a mapper for a given album
     *
     * @param album
     * @return a mapper
     */
    public static SportsAlbumMapper createMapper(Album album)
    {
        SportsAlbumMapper mapper = new SportsAlbumMapper();
        mapper.album = album;
        return mapper;
    }

    /**
     * called by action to map a startnumber mapping file
     * read the arguments and trim trailing or leading whitespace
     *
     * @throws UnartigException
     */
    public void map() throws UnartigException {
        BufferedReader bufMappingStream = new BufferedReader(new InputStreamReader(mappingInputStream));
        String mappingLine;
        String [] parts;
        try
        {
            while (bufMappingStream.ready())
            {
                mappingLine = bufMappingStream.readLine();
                _logger.debug("mappingLine = " + mappingLine);
                parts = mappingLine.split("\t");
                if (parts.length > 1)
                {
                    _logger.debug("parts[0] = " + parts[0]);
                    _logger.debug("parts[1] = " + parts[1]);
                    mapLine(parts[0].trim(), parts[1].trim());
                } else
                {
                    _logger.debug("ignoring line : " + mappingLine);
                }
            }
        } catch (IOException e)
        {
            _logger.error("cannot read from input stream : ", e);
            throw new UnartigException(e);
        }

    }

    /**
     * this method finds the photo, the photoSubject (or creates it) and maps the photo to the subject
     * <br>if no photo can be found for the given photoFilename, log a warning and return
     *
     * @param photoFilename
     * @param startNumber
     * @throws UAPersistenceException
     */
    private void mapLine(String photoFilename, String startNumber) throws UAPersistenceException
    {
        PhotoDAO photoDao = new PhotoDAO();

        PhotoSubject subj = findOrCreateSubjectByStartNumber(startNumber);
        try
        {
            HibernateUtil.beginTransaction();
            Photo photo = photoDao.findPhoto(album, photoFilename);
            if (photo == null)
            {
                _logger.warn("no photo found for given photoFilename!!");
                return;
            }
            photo.addPhotoSubject(subj);
//            photoDao.saveOrUpdate(photo);
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e)
        {
            _logger.error("problem saving mapped photo : ", e);
            HibernateUtil.rollbackTransaction();
        }

    }

    /**
     * map line for finish or start time mapping
     *
     * @param etappe
     * @param startNumber
     * @param timeString
     * @param name
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    private void mapLine(String etappe, String startNumber, String timeString, String name) throws UAPersistenceException
    {
        PhotoDAO photoDao = new PhotoDAO();
        Date finishTime = null;
        PhotoSubject subj = findOrCreateSubjectByStartNumber(startNumber);

        // we will ignore the year month and day information of the date and only focus on the time part
        SimpleDateFormat simpleFormater = new SimpleDateFormat("HH:mm:ss");
        try
        {
            finishTime = simpleFormater.parse(timeString);
        } catch (ParseException e)
        {
            _logger.error("could not parse time format; continuing with import", e);
        }
        _logger.debug("finishTime date format (hours minutes and seconds)= " + finishTime);

        checkEtappe();
        Calendar finishTimeDateCalendar = setFinishTimeDateCalendar(finishTime);
        _logger.debug("adjusted finish time date: " + finishTimeDateCalendar.getTime());

        // *** from dao
        Date minMatchTime;
        Date maxMatchTime;

        minMatchTime = calculateMinMatchTime(finishTimeDateCalendar.getTime());
        maxMatchTime = calculateMaxMatchTime(finishTimeDateCalendar.getTime());

        _logger.debug("max match time :" + maxMatchTime);
        _logger.debug("min match time : " + minMatchTime);

        // *** end from dao
        try
        {
            HibernateUtil.beginTransaction();
            List photos;
            photos = photoDao.findFinishTimePhotos(album, minMatchTime, maxMatchTime);


            if (photos != null)
            {
                for (Object photo1 : photos) {
                    Photo photo = (Photo) photo1;
                    if (photo == null) {
                        _logger.warn("no photo found for given photoFilename!!");
                        return;
                    }
                    photo.addPhotoSubject(subj);
                }
            }
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e)
        {
            _logger.error("problem saving mapped photo : ", e);
            HibernateUtil.rollbackTransaction();
        } finally
        {
//            HibernateUtil.finishTransaction();
        }

    }

    private Date calculateMaxMatchTime(Date finishTimeDate)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(finishTimeDate);

        Date maxMatchTime;
        int photopointStartFinishFactor;
        int toleranceStartFinishFactor;
        if (photopointBeforeFinishTime)
        {
            photopointStartFinishFactor = -1;
            toleranceStartFinishFactor = +1;
        } else
        {
            photopointStartFinishFactor = +1;
            toleranceStartFinishFactor = +1;
        }

        c.add(Calendar.SECOND, photopointStartFinishFactor * photoPointDifference);
        c.add(Calendar.SECOND, toleranceStartFinishFactor * photoPointTolerance);
        maxMatchTime = c.getTime();
        return maxMatchTime;
    }

    /**
     * This methods takes the finish or start timing and calculates the lower time-limit
     *
     * @param finishTimeDate
     * @return minimum time
     */
    private Date calculateMinMatchTime(Date finishTimeDate)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(finishTimeDate);

        Date minMatchTime;
        int photopointStartFinishFactor;
        int toleranceStartFinishFactor;
        if (photopointBeforeFinishTime)
        {
            photopointStartFinishFactor = -1;
            toleranceStartFinishFactor = -1;
        } else
        {
            photopointStartFinishFactor = +1;
            toleranceStartFinishFactor = -1;
        }
        c.add(Calendar.SECOND, photopointStartFinishFactor * photoPointDifference);
        c.add(Calendar.SECOND, toleranceStartFinishFactor * photoPointTolerance);
        minMatchTime = c.getTime();
        return minMatchTime;
    }

    /**
     * helper to do the Calendar calculation based on the date of the parent event
     *
     * @param finishTime
     * @return a Calendar object of the finish time
     */
    private Calendar setFinishTimeDateCalendar(Date finishTime)
    {
        Calendar finishTimeCalendar = Calendar.getInstance();
        finishTimeCalendar.setTime(finishTime);
        Calendar eventDateCalendar = Calendar.getInstance();
        // we need to reload album; maybe it's solved by setting lazy to false in album .... YES!
        eventDateCalendar.setTime(album.getEvent().getEventDate());
        finishTimeCalendar.set(Calendar.DAY_OF_MONTH, eventDateCalendar.get(Calendar.DAY_OF_MONTH));
        finishTimeCalendar.set(Calendar.MONTH, eventDateCalendar.get(Calendar.MONTH));
        finishTimeCalendar.set(Calendar.YEAR, eventDateCalendar.get(Calendar.YEAR));
        return finishTimeCalendar;
    }


    /**
     * <ol>
     * Look for an existing photosubject (via the eventRunners table)
     * <li>search in eventRunners for given album and startNumber
     * <li>asdf
     *
     * @param startNumber
     * @return
     * @throws UAPersistenceException
     */
    private PhotoSubject findOrCreateSubjectByStartNumber(String startNumber) throws UAPersistenceException
    {
        PhotoSubjectDAO photoSubjDao = new PhotoSubjectDAO();

        PhotoSubject subj = photoSubjDao.findByStartNumberAndAlbum(startNumber, album);
        if (subj == null)
        {
            _logger.debug("no photo subject found, going to creating a new one");
            subj = createNewSubject(startNumber, album);
        }
        _logger.debug("returning photo-subject : " + subj);
        return subj;
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
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    private PhotoSubject createNewSubject(String startNumber, Album album) throws UAPersistenceException
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
     * Reads line by line the bip-time file and maps it
     *
     * @throws UnartigException
     */
    public void mapFinishOrStartTime() throws UnartigException
    {
        _logger.debug("@@@@@@@@@@@@@@@@@ photopointBeforeFinishtime = " + photopointBeforeFinishTime);
        _logger.debug("photopoint tolereance : " + photoPointTolerance);
        String mappingLine;
        String [] parts;
        try
        {
            BufferedReader bufMappingStream = new BufferedReader(new InputStreamReader(mappingInputStream, "ISO-8859-1"));
            while (bufMappingStream.ready())
            {
                mappingLine = bufMappingStream.readLine();
                _logger.debug("mappingLine = " + mappingLine);
                parts = mappingLine.split("\t");
                _logger.debug("parts[0] = " + parts[0]);
                _logger.debug("parts[1] = " + parts[1]);
                _logger.debug("parts[2] = " + parts[2]);
                _logger.debug("parts[3] = " + parts[3]);
                mapLine(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim());
            }
        } catch (IOException e)
        {
            _logger.error("cannot read from input stream : ", e);
            throw new UnartigException(e);
        }

/*        get photo
        subject or
        create it
        find photos
        for
        given time
        and tolerence
        make the
        entries in
        the photosubject
        2
        photos link
*/
    }


    private boolean checkEtappe()
    {
        // todo implement if needed
        return true;
    }


    /**
     * Iterate over all photos of an album and set their photosubject-mapping to an empty set
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    public void delete() throws UAPersistenceException
    {
        PhotoDAO photoDao = new PhotoDAO();
        HibernateUtil.beginTransaction();
        try
        {
            Set photos = album.getPhotos();
            for (Object photo1 : photos) {

                Photo photo = (Photo) photo1;
                _logger.debug("deleting photosubject mappings for photo with id [" + photo.getPhotoId() + "]");
                photo.getPhotoSubjects().clear();
                photoDao.saveOrUpdate(photo);
                _logger.debug("photo saved");

            }
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e)
        {
            _logger.error("problem while deleteing photosubject mapping");
            HibernateUtil.rollbackTransaction();
            throw new UAPersistenceException("problem while deleting photosubject mappiong", e);
        }

    }
}

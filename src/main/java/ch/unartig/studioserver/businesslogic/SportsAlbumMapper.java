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
package ch.unartig.studioserver.businesslogic;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.Photo;
import ch.unartig.studioserver.model.PhotoSubject;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import ch.unartig.studioserver.persistence.DAOs.PhotoSubjectDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SportsAlbumMapper {

    private Album album;
    private InputStream mappingInputStream;
    /*running-difference from the time measurement to the photopoint in seconds*/
    private int photoPointDifference;
    private int photoPointTolerance;
    private boolean photopointBeforeFinishTime;
    private Logger _logger = LogManager.getLogger(getClass().getName());

    public SportsAlbumMapper(InputStream mappingInputStream, Album album) {

        this.album = album;
        this.mappingInputStream = mappingInputStream;
    }

    private SportsAlbumMapper() {

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
    public static SportsAlbumMapper createFinishTimeMapper(InputStream mappingInputStream, Album album, int photoPointDifference, int photoPointTolerance, boolean photopointBeforeFinishtime) {
        SportsAlbumMapper mapper = new SportsAlbumMapper(mappingInputStream, album);
        mapper.photoPointDifference = photoPointDifference;
        mapper.photoPointTolerance = photoPointTolerance;
        mapper.photopointBeforeFinishTime = photopointBeforeFinishtime;
        return mapper;
    }

    /**
     * simple creation method to create a mapper for a given album
     *
     * @param album
     * @return a mapper
     */
    public static SportsAlbumMapper createMapper(Album album) {
        SportsAlbumMapper mapper = new SportsAlbumMapper();
        mapper.album = album;
        return mapper;
    }

    /**
     * Manual mapping by list of foto-filename and startnumber!
     * called by action to map a startnumber mapping file
     * read the arguments and trim trailing or leading whitespace
     *
     * @throws UnartigException
     */
    public void map() throws UnartigException {
        BufferedReader bufMappingStream = new BufferedReader(new InputStreamReader(mappingInputStream));
        String mappingLine;
        String[] parts;
        try {
            while (bufMappingStream.ready()) {
                mappingLine = bufMappingStream.readLine();
                _logger.debug("mappingLine = " + mappingLine);
                parts = mappingLine.split("\t");
                if (parts.length > 1) {
                    _logger.debug("parts[0] = " + parts[0]);
                    _logger.debug("parts[1] = " + parts[1]);
                    mapLine(parts[0].trim(), parts[1].trim());
                } else {
                    _logger.debug("ignoring line : " + mappingLine);
                }
            }
        } catch (IOException e) {
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
    private void mapLine(String photoFilename, String startNumber) throws UAPersistenceException {
        PhotoDAO photoDao = new PhotoDAO();
        PhotoSubjectDAO photoSubjectgDao = new PhotoSubjectDAO();

        PhotoSubject subj = photoSubjectgDao.findOrCreateSubjectByStartNumberAndFace(startNumber, album, null);
        try {
            HibernateUtil.beginTransaction();
            Photo photo = photoDao.findPhoto(album, photoFilename);
            if (photo == null) {
                _logger.warn("no photo found for given photoFilename!!");
                return;
            }
            photo.addPhotoSubject(subj);
//            photoDao.saveOrUpdate(photo);
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e) {
            _logger.error("problem saving mapped photo : ", e);
            HibernateUtil.rollbackTransaction();
        }

    }

    /**
     * map line for finish or start time mapping (according to time-keeper files)
     *
     * @param etappe - number of Sola etappe (1-14) - currently not used
     * @param startNumber - must be present
     * @param timeString - must be present
     * @param name team name - can be null
     * @throws ch.unartig.exceptions.UAPersistenceException
     */
    private void mapLine(String etappe, String startNumber, String timeString, String name) throws UAPersistenceException {
        PhotoDAO photoDao = new PhotoDAO();
        PhotoSubjectDAO photoSubjectDAO = new PhotoSubjectDAO();
        Date finishTime = null;
        // automated startnumber recognition: this might conflict with already added mappings
        // todo : check combination of both methods!!
        // there can be many subjects per startnumber
        PhotoSubject subj = photoSubjectDAO.findOrCreateSubjectByStartNumberAndFace(startNumber, album, null);

        // we will ignore the year month and day information of the date and only focus on the time part
        SimpleDateFormat simpleFormater = new SimpleDateFormat("HH:mm:ss");
        try {
            finishTime = simpleFormater.parse(timeString);
        } catch (ParseException e) {
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
        try {
            HibernateUtil.beginTransaction();
            List photos;
            photos = photoDao.findFinishTimePhotos(album, minMatchTime, maxMatchTime);


            if (photos != null) {
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
        } catch (UAPersistenceException e) {
            _logger.error("problem saving mapped photo : ", e);
            HibernateUtil.rollbackTransaction();
        } finally {
//            HibernateUtil.finishTransaction();
        }

    }

    private Date calculateMaxMatchTime(Date finishTimeDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(finishTimeDate);

        Date maxMatchTime;
        int photopointStartFinishFactor;
        int toleranceStartFinishFactor;
        if (photopointBeforeFinishTime) {
            photopointStartFinishFactor = -1;
        } else {
            photopointStartFinishFactor = +1;
        }
        toleranceStartFinishFactor = +1;

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
    private Date calculateMinMatchTime(Date finishTimeDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(finishTimeDate);

        Date minMatchTime;
        int photopointStartFinishFactor;
        int toleranceStartFinishFactor;
        if (photopointBeforeFinishTime) {
            photopointStartFinishFactor = -1;
            toleranceStartFinishFactor = -1;
        } else {
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
    private Calendar setFinishTimeDateCalendar(Date finishTime) {
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
     * Reads line by line the bip-time file and maps it
     * Values need to be delimited by tab
     * todo: extend to different delimiters used in csv format (comma, semicolon)
     *
     * @throws UnartigException
     */
    public String mapFinishOrStartTime() throws UnartigException {
        _logger.debug("@@@@@@@@@@@@@@@@@ photopointBeforeFinishtime = " + photopointBeforeFinishTime);
        _logger.debug("photopoint tolereance : " + photoPointTolerance);


        String result;
        AtomicInteger linesMapped= new AtomicInteger();
        AtomicInteger linesIgnored= new AtomicInteger();

        new BufferedReader(new InputStreamReader(mappingInputStream, StandardCharsets.UTF_8))
                .lines()
                .forEach(line -> {
                    _logger.debug("mappingLine = " + line);
                    // remove additional double-quotes:
                    line = line.replace("\"", "");


                    String[] parts;
                    parts = line.split("\t"); // todo : insert configurable delimiter (query parameter in API!)
                    if (!parts[1].trim().isEmpty() && !parts[2].trim().isEmpty()) {
                        _logger.debug("parts[0] = " + parts[0]);
                        _logger.debug("parts[1] = " + parts[1]);
                        _logger.debug("parts[2] = " + parts[2]);
                        _logger.debug("parts[3] = " + (parts.length > 3?parts[3]:"empty")); // team name optional
                        mapLine(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts.length>3?parts[3].trim():null);
                        linesMapped.getAndIncrement();
                    } else {
                        _logger.info("ignored line (emtpy time or number) : " + line);
                        linesIgnored.getAndIncrement();
                    }

                });

        result = linesMapped + " lines mapped, " + linesIgnored + " lines ignored";
        return result;
    }


    private boolean checkEtappe() {
        // todo implement if needed
        return true;
    }


    /**
     * Iterate over all photos of an album and set their photosubject-mapping to an empty set
     *
     * @throws ch.unartig.exceptions.UAPersistenceException
     * @return
     */
    public String delete() throws UAPersistenceException {
        String result;
        int photosAffected = 0;
        int photoSubjectsAffected = 0;
        PhotoDAO photoDao = new PhotoDAO();
        HibernateUtil.beginTransaction();
        try {
            // todo: make more efficient - use query
            Set<Photo> photos = album.getPhotos();
            for (Photo photo1 : photos) {
                photosAffected++;
                _logger.debug("deleting photosubject mappings for photo with id [" + photo1.getPhotoId() + "]");
                photoSubjectsAffected=photoSubjectsAffected+photo1.getPhotoSubjects().size();
                photo1.getPhotoSubjects().clear();
                photoDao.saveOrUpdate(photo1);
                _logger.debug("photo saved");

            }
            HibernateUtil.commitTransaction();
        } catch (UAPersistenceException e) {
            _logger.error("problem while deleteing photosubject mapping");
            HibernateUtil.rollbackTransaction();
            throw new UAPersistenceException("problem while deleting photosubject mappiong", e);
        }
        result = photosAffected+" photos affected, deleted "+photoSubjectsAffected+" photosubjects removed";

        return result;
    }
}

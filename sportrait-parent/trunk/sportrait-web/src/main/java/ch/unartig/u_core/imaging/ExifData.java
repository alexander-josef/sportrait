/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Oct 24, 2005$
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
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.5  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.4  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.3  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.2  2005/11/25 10:56:58  alex
 *
 * Revision 1.1  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 ****************************************************************/
package ch.unartig.u_core.imaging;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Calendar;
import java.util.Date;

public class ExifData
{
    Logger logger = Logger.getLogger(getClass().getName());
    // constant for Exif token
//    public static final String[] EXIF = {"45", "78", "69", "66", "0"};
    public static final String[] EXIF = {"45", "78", "69", "66"};
    public static final String[] DATE_TIME_TAG = {"1", "32"}; // in Motorola Byte Order
    public static final String[] Date_TIME_ORIGINAL_TAG = {"90", "3"}; // in Motorola Byte Order
    public static final int BYTE_ARRAY_SIZE = 20;
    public static final String[] MOTOROLA_BYTE_ORDER_TAG = {"4d", "4d"};
    public static final String[] INTEL_BYTE_ORDER_TAG = {"49", "49"};
    public static String[] byteOrderTag;
    // Motorola byte order:
    public static final int MOTOROLA = 0;
    // Intel byte order:
    public static final int INTEL = 1;

    // set either to Motorola or Intel byte order:
    private int byteOrder = -1;
    private BufferedInputStream buffStream = null;
    private File photoFile;

    public ExifData()
    {
    }

    /**
     * public Constructor; needs a file and sets the buffered file reader
     *
     * @param photoFile
     */
    public ExifData(File photoFile)
    {
        this.photoFile = photoFile;

        try
        {
            buffStream = new BufferedInputStream(new FileInputStream(photoFile));
            buffStream.mark(1000);
            logger.info("Reading from : " + photoFile.getName());
        } catch (FileNotFoundException e)
        {
            logger.error("Could not open File: " + photoFile);
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

    /**
     * @return
     * @throws java.io.IOException
     */
    public Date getPictureTakenDate() throws IOException
    {
        logger.info("getPictureTakenDate 1, "  + System.currentTimeMillis());

        Date date;
        // check for null file stream and non-exif file
        if (buffStream != null & setByteOrder() != -1)
        {
            BufferedInputStream in = null;
            long offset = 0;

            if (byteOrder == MOTOROLA)
            {
                // in = parseFor(buffStream,new String[]{DATE_TIME_TAG[0], DATE_TIME_TAG[1]});
                logger.debug("parsing motorola ...");
                in = parseFor(buffStream, new String[]{Date_TIME_ORIGINAL_TAG[0], Date_TIME_ORIGINAL_TAG[1]});
            } else if (byteOrder == INTEL)
            {
                // in = parseFor(buffStream,new String[]{DATE_TIME_TAG[1], DATE_TIME_TAG[0]});
                logger.debug("parsing intel ...");
                in = parseFor(buffStream, new String[]{Date_TIME_ORIGINAL_TAG[1], Date_TIME_ORIGINAL_TAG[0]});

            }
            try
            {
                in.skip(7);
            } catch (NullPointerException e)
            {
                logger.error("skip procuded null pointer exception",e);
            }
            in.mark(10);
            if (byteOrder == MOTOROLA)
            {
                offset = in.read() << 24;
                offset += in.read() << 16;
                offset += in.read() << 8;
                offset += in.read();
            } else if (byteOrder == INTEL)
            {
                offset = in.read();
                offset += in.read() << 8;
                offset += in.read() << 16;
                offset += in.read() << 24;
            }
            logger.debug("found offset: " + offset);

            date = readDateFromStream(photoFile, offset);
        } else
        {
            logger.error("could not read byte order!");
            return null;
        }
        return date;
    }

    /**
     * reading the EXIF-date according to the specifications. see www.exif.org
     *
     * @param photoFile
     * @param dateOffset
     * @return
     * @throws IOException
     */
    private Date readDateFromStream(File photoFile, long dateOffset) throws IOException
    {

        DataInputStream dataStream = null;
        Calendar calendar;
        int year = 0;
        int month = 0; // CAUTION: JANUARY: month = 0; DECEMBER: month = 11
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;

        try
        {   // data stream starts at beginning of TIFF header
            // TIFF header starts either as 0x4949 (II for Intel byte order) or as 0x4d4d (MM for Motorola Byte order)
            dataStream = new DataInputStream(parseFor(new BufferedInputStream(new FileInputStream(photoFile)), byteOrderTag));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        long skipped = dataStream.skipBytes(new Long(dateOffset - 1).intValue());
        logger.debug("Determined Offset = " + dateOffset + "; skipped the offset of " + skipped + " bytes !");

        if (dateOffset - 1 == skipped)
        { // offset successfully skipped (return of skip = bytes skipped)
            // now check if we arrived at the right place
            try
            {
                logger.debug("now reading date ... starting with year");
                char yearCharArray[] = {
                    (char) (dataStream.read()), (char) (dataStream.read()), (char) (dataStream.read()), (char) (dataStream.read())};
                String stringHelper = new String(yearCharArray);
                year = Integer.parseInt(stringHelper);
                logger.debug("year : " + year + " ; now reading month");
                dataStream.skip(1);
                month = Integer.parseInt(new String(new char[]{(char) (dataStream.read()), (char) (dataStream.read())}));
                month--; // EXIF Month format: 1 (January) - 12 (December) SEE DECLARATION OF MONTH!!
                logger.debug("got month: " + month + " ; reading day");

                dataStream.skip(1);
                day = Integer.parseInt(new String(new char[]{(char) (dataStream.read()), (char) (dataStream.read())}));
                logger.debug("got day: " + day + " ; reading hour");

                dataStream.skip(1);
                try
                {
                    hour = readNumberBySeparator(dataStream, ':');
                    logger.debug("got hour: " + hour + " ; reading minute");
                    minute = readNumberBySeparator(dataStream, ':');
                    logger.debug("got minute: " + minute + " ; reading second");
                    second = readNumberBySeparator(dataStream, ':');
                    logger.debug("got second: " + second + " ; done!");
//                    hour = Integer.parseInt(new String(new char[]{(char) (dataStream.read()), (char) (dataStream.read())}));

                } catch (NumberFormatException e)
                {
                    logger.info("time  corrupt ... setting to '0', continuing!");
                } catch (IOException e)
                {
                    logger.error("got an IO exception ....");
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                logger.info("Reading Date; year:  " + year + " month: " + month + " day : " + day + " hour " + hour + " minute : " + minute + " second : " + second);
            } catch (IOException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (NumberFormatException e)
            {
                logger.warn("Number Format Exception; cannot read date from file");
                e.printStackTrace();
                return null;
            }
        } else
        {
            logger.error("could not skip to the date fields ... end of file?");
            logger.error("Date Fields were not found in File " + photoFile.getName());
            return null;
        }
        calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        logger.debug(calendar.toString());
        return calendar.getTime();
    }

    /**
     * parses through a data stream and reads the number until the separator character is read or 0
     *
     * @param dataStream
     * @param separator
     * @return
     * @throws IOException
     */

    private int readNumberBySeparator(DataInputStream dataStream, char separator) throws IOException
    {
        int character;
        String numberAsString = "";
        while (((character = dataStream.read()) != separator) && (character != 0))
        {
            logger.debug("character as int : " + character);
            numberAsString += (char) character;
            logger.debug(" **** numberAsString  = " + numberAsString);
        }
        logger.debug("returning : " + Integer.parseInt(numberAsString));
        return Integer.parseInt(numberAsString);
    }

    /**
     * @return
     * @throws IOException
     */
    private int setByteOrder() throws IOException
    {
        logger.info("setByteOrder start, "  + System.currentTimeMillis());
        // first token to look for: Exif (on null '00' at the end)
        BufferedInputStream in = parseFor(buffStream, EXIF);
        int readValue;
        // jump to the end of the tag and read next value
        in.skip(EXIF.length + 1);
        readValue = in.read();
        logger.info("setByteOrder 2, "  + System.currentTimeMillis());

        // check if we reached end of file
        if (readValue != -1)
        {
            // stream is now AT the first two byte-order bytes
            // read out byte-order
            if (readValue == Integer.parseInt("4d", 16))
            { // looks like Motorola
                if (in.read() == Integer.parseInt("4d", 16))
                {
                    logger.info("Motorola Byte Order, "  + System.currentTimeMillis());
                    byteOrder = MOTOROLA;
                    byteOrderTag = MOTOROLA_BYTE_ORDER_TAG;
                } else
                {
                    logger.error("file error, unexpected data");
                    return -1;
                }
            } else
            {
                if (readValue == Integer.parseInt("49", 16))
                { // looks like INTEL
                    if (in.read() == Integer.parseInt("49", 16))
                    {
                        logger.info("Intel Byte Order");
                        byteOrder = INTEL;
                        byteOrderTag = INTEL_BYTE_ORDER_TAG;
                    } else
                    {
                        logger.error("file error, unexpected data");
                        return -1;
                    }
                } else
                {
                    logger.error("neither 'MM' nor 'II' found ...");
                    return -1;
                }
            }
            return 0;
        }

        return -1;
    }

    /**
     * @param in
     * @param token
     * @return
     * @throws IOException
     */
    protected BufferedInputStream parseFor(BufferedInputStream in, String[] token) throws IOException
    {
        int readValue = 0; // value at current BufferedInputStream Position
        int patternCounter = 0;


        // while not EOF and the complete pattern did not match yet:
        while (readValue != -1 && patternCounter < token.length)
        {
            patternCounter = 0;
            readValue = in.read();
            // while complete pattern did not match yet and one pattern character matches increase counter, read next value
            while (patternCounter < token.length && Integer.toHexString(readValue).toLowerCase().equals(token[patternCounter].toLowerCase()))
            {
                // set a mark at the begin of the 'suspect'
                if (patternCounter == 0)
                {
                    in.mark(10);
                }
                readValue = in.read();
                patternCounter++;
            }
            // out of the 'suspect'-loop ... jump back to start of suspect
            if (patternCounter != 0)
            {
                in.reset();
            }
        }
        if (readValue == -1)
        {
            logger.error("reached end of file withouth finding token : " + token);
        }
        return in;
    }

    /**
     * copy of method with string array using only string
     *
     * @param in
     * @param token
     * @return
     * @throws IOException
     */
    protected static BufferedInputStream parseFor(BufferedInputStream in, String token) throws IOException
    {
        /** value at current BufferedInputStream Position */
        int readValue = 0;
        int patternCounter = 0;


        while (readValue != -1 && patternCounter < token.length())
        {
            patternCounter = 0;
            readValue = in.read();
            while (patternCounter < token.length() && Integer.toHexString(readValue).equals(token.substring(patternCounter, patternCounter + 1)))
            {
                // set a mark at the begin of the 'suspect'
                if (patternCounter == 0)
                {
                    in.mark(10);
                }
                readValue = in.read();
                patternCounter++;
            }
            // out of the 'suspect'-loop ... jump back to start of suspect
            if (patternCounter != 0)
            {
                in.reset();
            }
        }
        return in;
    }

    private String toUnsignedHex(byte unsigned)
    {
        return Integer.toHexString(unsigned & 0xff);
    }


}


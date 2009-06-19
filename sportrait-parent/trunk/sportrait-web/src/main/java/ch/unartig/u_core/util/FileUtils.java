/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 6, 2005$
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
 * Revision 1.2  2007/04/17 11:03:27  alex
 * dynamic pager added
 *
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.5  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.4  2005/11/22 19:45:46  alex
 * admin actions, configurations
 *
 * Revision 1.3  2005/11/06 21:43:22  alex
 * overview, admin menu, index-photo upload
 *
 * Revision 1.2  2005/10/24 13:50:07  alex
 * upload of album
 * import in db 
 * processing of images
 *
 * Revision 1.1  2005/10/06 18:14:23  alex
 * saving new tree_items file
 *
 ****************************************************************/
package ch.unartig.u_core.util;

import ch.unartig.u_core.exceptions.UnartigException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Utility methods to deal with files
 */
public class FileUtils
{

    private static final int BUFFER_SIZE = 10000;
    static Logger _logger = Logger.getLogger("ch.unartig.u_core.util.FileUtils");

    private FileUtils()
    {
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException
    {
        copyFile(in, out, true, true);
    }

    /**
     * @param in
     * @param out
     * @param closeOutStream
     * @throws IOException
     */
    public static void copyFile(InputStream in, OutputStream out, boolean closeInStream, boolean closeOutStream) throws IOException
    {
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        try
        {
            bin = new BufferedInputStream(in);
            bout = new BufferedOutputStream(out);
            byte[] buffer = new byte[BUFFER_SIZE];
            int count;

            while ((count = bin.read(buffer)) != -1)
            {
                bout.write(buffer, 0, count);
            }
        } finally
        {
            if (bin != null && closeInStream)
            {
                try
                {
                    bin.close();
                } catch (IOException e)
                {
                    // _logger.error("Unable to close the buffered input stream", e);
                }
            }
            if (bout != null && closeOutStream)
            {
                try
                {
                    bout.close();
                } catch (IOException e)
                {
                    // _logger.error("Unable to close the buffered output stream", e);
                }
            }
        }
    }

    /**
     * copies a file from an input stream to a File
     *
     * @param source
     * @param destination
     * @throws java.io.IOException
     */
    public static void copyFile(InputStream source, File destination) throws IOException
    {
        OutputStream out = new FileOutputStream(destination);
        FileUtils.copyFile(source, out);
    }

    /**
     * copies a file to another
     *
     * @param source
     * @param destinationFileName file name (absolute)
     * @throws java.io.IOException
     */
    public static void copyFile(InputStream source,String destinationFileName) throws IOException
    {
        copyFile(source,new File(destinationFileName));
    }

    /**
     * @param source
     * @param destination
     * @param closeInStream
     * @param closeOutStream
     * @throws IOException
     */
    public static void copyFile(InputStream source, File destination, boolean closeInStream, boolean closeOutStream) throws IOException
    {
        OutputStream out = new FileOutputStream(destination);
        FileUtils.copyFile(source, out, closeInStream, closeOutStream);
    }


    /**
     * copies a file to another
     *
     * @param source
     * @param destination
     * @throws IOException
     */
    public static void copyFile(byte[] source, File destination) throws IOException
    {
        InputStream in = new ByteArrayInputStream(source);
        OutputStream out = new FileOutputStream(destination);
        FileUtils.copyFile(in, out);
    }

    /**
     * copies a file to another
     *
     * @param source
     * @param destination
     * @throws IOException
     */
    public static void copyFile(File source, File destination) throws IOException
    {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(destination);
        FileUtils.copyFile(in, out);
    }


    /**
     * copy a File from a source File to a destination output stream
     * @param source the source File
     * @param destination the destimation output stream
     * @throws IOException
     */
    public static void copyFile(File source, OutputStream destination) throws IOException
    {
        InputStream in = new FileInputStream(source);
        FileUtils.copyFile(in, destination);
    }


    /**
     * copies a file to another
     *
     * @param source      absolute path to the source file
     * @param destination absolute path to the destination file
     * @throws IOException if something goes wrong, i.e. one of the paths is a directory,
     *                     source is not readable, destination is not writable, ...
     */
    public static void copyFile(String source, String destination) throws IOException
    {
        InputStream in = new FileInputStream(new File(source));
        OutputStream out = new FileOutputStream(new File(destination));
        FileUtils.copyFile(in, out);
    }

    public static void copyFile(byte[] output, String absReportFileName) throws IOException
    {
        copyFile(output, new File(absReportFileName));
    }

    /**
     * extracts files that are in a zipped archive;
     * FILES MUST NOT BE IN DIRECTORIES !
     *
     * @throws UnartigException
     * @param uploadInputStream
     * @param targetPath
     */
    public static void extractFlatZipArchive(InputStream uploadInputStream, File targetPath) throws UnartigException {
        ZipInputStream zis = null;
        ZipEntry zipEntry;
        try
        {
            zis = new ZipInputStream(uploadInputStream);
            // loop through input stream while there are more zip entries
            while ((zipEntry = zis.getNextEntry()) != null)
            {
                File destinationFile = new File(targetPath, zipEntry.getName());
                // if zip archive contains directories, an error is thrown; check if parent directory is really the fine path!
                if (destinationFile.isDirectory() || !destinationFile.getParentFile().equals(targetPath)) // equals compares pathnames!
                {
                    _logger.error("only Files allowed in uploaded archives; files must not be in directories");
                    throw new UnartigException("only Files allowed in uploaded archives; files must not be in directories");
                } else if (!".jpg".equals(destinationFile.getName().substring(destinationFile.getName().lastIndexOf(".")).toLowerCase()))
                {
                    _logger.error("Skipping file in archive;File is not of type jpeg : " + destinationFile.getName());
                }
                targetPath.mkdirs();
                copyFile(zis, destinationFile, false, true);
                zis.closeEntry();
            }

        } catch (FileNotFoundException e)
        {
            _logger.error("File not Found", e);
            System.exit(1);
        } catch (IOException e)
        {
            _logger.error("error while reading entries in zip file", e);
            System.exit(1);
        } finally
        {
            try
            {
                zis.close();
                uploadInputStream.close();
            } catch (IOException e)
            {
                _logger.error("input stream does not close");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    /**
     * copy a directory. make sure no subdirectories exists
     * @param sourceDir
     * @param destDir
     * @throws IOException
     */
    public static void copyDir(File sourceDir, File destDir) throws IOException
    {
        copyDir(sourceDir,destDir,null);
    }

    /**
     * copy a directory, applying the passed FileFilter
     * @param sourceDir
     * @param destDir
     * @param fileFilter
     * @throws IOException
     */
    public static void copyDir(File sourceDir, File destDir,FileFilter fileFilter) throws IOException
    {
        if (!sourceDir.isDirectory() || !destDir.isDirectory())
        {
            throw new IOException("two directories neede");
        }
        File[] files;
        if (fileFilter==null)
        {
            files = sourceDir.listFiles();
        } else
        {
            files = sourceDir.listFiles(fileFilter);
        }
        for (int i = 0; i < files.length; i++)
        {
            File file = sourceDir.listFiles()[i];
            copyFile(file, new File(destDir, file.getName()));
        }
    }

    /**
     * Filter image files:
     * - jpg
     */
    public static class JpgFileFilter implements FileFilter
    {
        /**
         * check for files of type jpg (ignore case !!)
         *
         * @param file
         * @return true for jpg files false for others
         */
        public boolean accept(File file)
        {
            if (file.isFile())
            {
                if (file.getName().toLowerCase().endsWith(".jpg"))
                {
                    return true;
                }
            }
            return false;
        }
    }

}

/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 21.08.2007$
 *
 * Copyright (c) 2007 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.businesslogic.Uploader;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.SportsEvent;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Copied from the JUpload JSP "parseRequest.jsp". Receives a request from the client and stores the uploaded files.
 * <ul>
 * <li>This Servlet is called for each file to be uploaded by the Jupload Applet.
 * <li>Each file is copied in to a temporary folder that is named with the album id.
 * <li>Upon reveption of the complete file, the file will registered (and copied to the unartig file storage) with the album and deleted if the transaction was successful
 * </ul>
 */
public class JUploadAction extends Action
{
    Logger _logger = Logger.getLogger(getClass().getName());
    private static final int K = 1000; //
    private static final int M = 1000 * K; //
    private static final int _MAX_REQUEST_SIZE = 2000 * M; // max allowd size in bytes, 2 GB

     private static final int _MAX_MEMORY_SIZE = 10 * M; // IN BYTES, 10 MB
//    private static final int _MAX_MEMORY_SIZE = 100 * K; // IN BYTES, 100 KB

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
    {
        return uploadToUnartig(httpServletRequest, httpServletResponse);
        // OR
//        return jUploadSimple(httpServletRequest,httpServletResponse);


    }


    /**
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws IOException
     */
    private ActionForward uploadToUnartig(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException
    {
        Long eventId = null;
        Long photographerId = null;
        String photographerPassword = null;
        Album album;
        Long eventCategoryId = null;
        PrintWriter out = httpServletResponse.getWriter();
        //Initialization for chunk management.
        boolean bLastChunk = false;
        int numChunk = 0;


        httpServletResponse.setContentType("text/plain");
        try
        {
            // Get URL Parameters.
            Enumeration paraNames = httpServletRequest.getParameterNames();
            out.println(" ------------------------------ ");
            String pname;
            String pvalue;
            while (paraNames.hasMoreElements())
            {
                pname = (String) paraNames.nextElement();
                pvalue = httpServletRequest.getParameter(pname);
                out.println(pname + " = " + pvalue);
                if (pname.equals("jufinal"))
                {
                    bLastChunk = pvalue.equals("1");
                } else if (pname.equals("jupart"))
                {
                    numChunk = Integer.parseInt(pvalue);
                }
                //////////////////////////////
                // unartig params
                //////////////////////////////
                else if (pname.equals("eventId"))
                {
                    eventId = new Long(pvalue);
                }
                else if (pname.equals("eventCategoryId"))
                {
                    eventCategoryId = new Long(pvalue);
                }
                else if (pname.equals("photographerId"))
                {
                    photographerId = new Long(pvalue);
                }
                else if (pname.equals("photographerPassword"))
                {
                    photographerPassword = pvalue;
                }
                else
                {
                    // defensive programming:
                    throw new RuntimeException("Unexpected URL paramter : ["+pname+"] with value ["+pvalue+"]");
                }
            } // end url paramater treatment
            out.println(" ------------------------------ ");

            album = getUnartigAlbum(eventId, photographerId, eventCategoryId);

            handleFileItems(httpServletRequest, album, out, bLastChunk, numChunk);
        } catch (Exception e)
        {
            // output to client:
            out.println("Exception e = " + e.toString());
            // re-throw for server!
            throw new RuntimeException(e);
        }
        out.close();
        return null;
    }

    private Album getUnartigAlbum(Long eventId, Long photographerId, Long eventCategoryId)
    {
        SportsEvent sportsEvent;
        Album album;
        if (eventId != null && eventCategoryId != null && photographerId != null)
        {
            GenericLevelDAO glDao = new GenericLevelDAO();
            try
            {
                sportsEvent = (SportsEvent) glDao.load(eventId, SportsEvent.class);
                // now we can get an album and set the temp path
                album = sportsEvent.getSportsAlbumFor(eventCategoryId, photographerId);
            } catch (NumberFormatException e)
            {
                throw new RuntimeException("Number format exception for eventid : [" + eventId + "]", e);
            }
        } else
        {
            // defensive:
            throw new RuntimeException("Paramter missing! eventid, eventCategoryId and photographerId are mandatory!");
        }
        return album;
    }

    /**
     * File items can be form fields or 'real' mulitpart file items.
     * The code below is directly taken from the jakarta fileupload common classes
     * All informations, and download, available here : http://jakarta.apache.org/commons/fileupload/
     *
     * @param httpServletRequest
     * @param album
     * @param out
     * @param bLastChunk
     * @param numChunk
     * @throws Exception
     */
    private void handleFileItems(HttpServletRequest httpServletRequest, Album album, PrintWriter out, boolean bLastChunk, int numChunk) throws Exception
    {
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(_MAX_MEMORY_SIZE);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Set overall request size constraint
        upload.setSizeMax(_MAX_REQUEST_SIZE);

        // Parse the request

        List fileItems = upload.parseRequest(httpServletRequest);
        // Process the uploaded items
        Iterator filteItemIter = fileItems.iterator();
        FileItem fileItem;
        out.println(" Let's read input files ...");
        while (filteItemIter.hasNext())
        {
            fileItem = (FileItem) filteItemIter.next();
            if (fileItem.isFormField())
            {
                out.println(" ------------------------------ ");
                out.println(fileItem.getFieldName() + " = " + fileItem.getString());
            } else
            {
                writeFile(album, out, bLastChunk, numChunk, factory, fileItem);
            }
            // FILE HAS BEEN RECEIVED HERE ...
            out.println("SUCCESS");
        }//while
    }

    /**
     * Ok, we've got a file. Let's process it.
     * Again, for all informations of what is exactly a FileItem, please
     * have a look to http://jakarta.apache.org/commons/fileupload/
     * @param album
     * @param out
     * @param bLastChunk
     * @param numChunk
     * @param factory
     * @param fileItem
     * @return
     * @throws Exception
     */
    private File writeFile(Album album, PrintWriter out, boolean bLastChunk, int numChunk, DiskFileItemFactory factory, FileItem fileItem) throws Exception
    {
        String tempImagePath;
        File fout;
        File tmpDir;

        if (album != null)
        {
            // set temporary file in a directory named after the albumid
            // todo this temp path configurable
            tempImagePath = "/tmp/" + album.getGenericLevelId().toString();
            tmpDir = new File(tempImagePath);
            tmpDir.mkdir();
            factory.setRepository(tmpDir);
        } else
        {
            throw new RuntimeException("Album is null; Cannot create temp dir!");
        }

        out.println(" ------------------------------ ");
        out.println("FieldName: " + fileItem.getFieldName());
        out.println("File Name: " + fileItem.getName());
        out.println("ContentType: " + fileItem.getContentType());
        out.println("Size (Bytes): " + fileItem.getSize());
        //If we are in chunk mode, we add ".partN" at the end of the file, where N is the chunk number.
        String uploadedFilename = fileItem.getName() + (numChunk > 0 ? ".part" + numChunk : "");
        fout = new File(tmpDir, (new File(uploadedFilename)).getName());
        out.println("File Out: " + fout.toString());
        // write the file
        fileItem.write(fout);

        if (bLastChunk)
        {
            File targetFile = assembleChunks(tmpDir, out, numChunk, fileItem);
            unartigImport(album, out, targetFile);
        }
        // End of chunk management
        //////////////////////////////////////////////////////////////////////////////////////
        // AFTER COMPLETE UPLOAD:
        _logger.debug("deleting temporary resources for fileItem");
        fileItem.delete();
        return tmpDir;
    }

    /**
     * Chunk management: if it was the last chunk, let's recover the complete file
     * by concatenating all chunk parts.
     * @param tmpDir
     * @param out
     * @param numChunk
     * @param fileItem
     * @return
     * @throws IOException
     */
    private File assembleChunks(File tmpDir, PrintWriter out, int numChunk, FileItem fileItem) throws IOException
    {
        out.println(" Last chunk received: let's rebuild the complete file (" + fileItem.getName() + ")");
        //First: construct the final filename.
        FileInputStream fis;
        File targetFile = new File(tmpDir, fileItem.getName());
        FileOutputStream fos = new FileOutputStream(targetFile);
        int nbBytes;
        byte[] byteBuff = new byte[1024];
        String filename;
        for (int i = 1; i <= numChunk; i += 1)
        {
            filename = fileItem.getName() + ".part" + i;
            out.println("  Concatenating " + filename);
            fis = new FileInputStream(new File(tmpDir, filename));

            while ((nbBytes = fis.read(byteBuff)) >= 0)
            {
                out.println("     Nb bytes read: " + nbBytes);
                fos.write(byteBuff, 0, nbBytes);
            }
            fis.close();
        }
        fos.close();
        return targetFile;
    }

    /**
     * UNARTIG:
     *
     * copy the photos from temp to the album storage, register the new photos
     * @param album
     * @param out
     * @param targetFile
     */
    private void unartigImport(Album album, PrintWriter out, File targetFile)
    {
        Uploader uploaderThread = new Uploader(album.getGenericLevelId(), true);
        uploaderThread.uploadSingleImage(targetFile);
        out.println("****************************************************************");
        out.println("unArtig IMPORTING.... file:["+targetFile.getAbsolutePath()+"]");
        out.println("****************************************************************");
    }




    
    /**
     * copied from jupload?
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    private ActionForward jUploadSimple(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        PrintWriter out = response.getWriter();
        //Initialization for chunk management.
        boolean bLastChunk = false;
        int numChunk = 0;

        response.setContentType("text/plain");
        try{
          // Get URL Parameters.
          Enumeration paraNames = request.getParameterNames();
          out.println(" ------------------------------ ");
          String pname;
          String pvalue;
          while (paraNames.hasMoreElements()) {
            pname = (String)paraNames.nextElement();
            pvalue = request.getParameter(pname);
            out.println(pname + " = " + pvalue);
            if (pname.equals("jufinal")) {
                bLastChunk = pvalue.equals("1");
            } else if (pname.equals("jupart")) {
                numChunk = Integer.parseInt(pvalue);
            }
          }
          out.println(" ------------------------------ ");

          // Directory to store all the uploaded files
          String ourTempDirectory = "/tmp/";
          int ourMaxMemorySize  = _MAX_MEMORY_SIZE;
          int ourMaxRequestSize = _MAX_REQUEST_SIZE;

          ///////////////////////////////////////////////////////////////////////////////////////////////////////
          //The code below is directly taken from the jakarta fileupload common classes
          //All informations, and download, available here : http://jakarta.apache.org/commons/fileupload/
          ///////////////////////////////////////////////////////////////////////////////////////////////////////

          // Create a factory for disk-based file items
          DiskFileItemFactory factory = new DiskFileItemFactory();

          // Set factory constraints
          factory.setSizeThreshold(ourMaxMemorySize);
          factory.setRepository(new File(ourTempDirectory));

          // Create a new file upload handler
          ServletFileUpload upload = new ServletFileUpload(factory);

          // Set overall request size constraint
          upload.setSizeMax(ourMaxRequestSize);

          // Parse the request
          List /* FileItem */ items = upload.parseRequest(request);
          // Process the uploaded items
          Iterator iter = items.iterator();
          FileItem fileItem;
          File fout;
          out.println(" Let's read input files ...");
          while (iter.hasNext()) {
              fileItem = (FileItem) iter.next();

              if (fileItem.isFormField()) {
                  //This should not occur, in this example.
                  out.println(" ------------------------------ ");
                  out.println(fileItem.getFieldName() + " = " + fileItem.getString());
              } else {
                  //Ok, we've got a file. Let's process it.
                  //Again, for all informations of what is exactly a FileItem, please
                  //have a look to http://jakarta.apache.org/commons/fileupload/
                  //
                  out.println(" ------------------------------ ");
                  out.println("FieldName: " + fileItem.getFieldName());
                  out.println("File Name: " + fileItem.getName());
                  out.println("ContentType: " + fileItem.getContentType());
                  out.println("Size (Bytes): " + fileItem.getSize());
                  //If we are in chunk mode, we add ".partN" at the end of the file, where N is the chunk number.
                  String uploadedFilename = fileItem.getName() + ( numChunk>0 ? ".part"+numChunk : "") ;
                  fout = new File(ourTempDirectory + (new File(uploadedFilename)).getName());
                  out.println("File Out: " + fout.toString());
                  // write the file
                  fileItem.write(fout);

                  //////////////////////////////////////////////////////////////////////////////////////
                  //Chunk management: if it was the last chunk, let's recover the complete file
                  //by concatenating all chunk parts.
                  //
                  if (bLastChunk) {
                      out.println(" Last chunk received: let's rebuild the complete file (" + fileItem.getName() + ")");
                      //First: construct the final filename.
                      FileInputStream fis;
                      FileOutputStream fos = new FileOutputStream(ourTempDirectory + fileItem.getName());
                      int nbBytes;
                      byte[] byteBuff = new byte[1024];
                      String filename;
                      for (int i=1; i<=numChunk; i+=1) {
                          filename = fileItem.getName() + ".part" + i;
                          out.println("  Concatenating " + filename);
                          fis = new FileInputStream(ourTempDirectory + filename);
                          while ( (nbBytes = fis.read(byteBuff)) >= 0) {
                              out.println("     Nb bytes read: " + nbBytes);
                              fos.write(byteBuff, 0, nbBytes);
                          }
                          fis.close();
                      }
                      fos.close();
                  }
                  // End of chunk management
                  //////////////////////////////////////////////////////////////////////////////////////

                  fileItem.delete();
              }
              out.println("SUCCESS");
          }//while
        }catch(Exception e){
          out.println("Exception e = " + e.toString());
        }

        out.close();

        return null;
    }

}

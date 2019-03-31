package ch.unartig.studioserver.actions;

import ch.unartig.sportrait.imgRecognition.Startnumber;
import ch.unartig.studioserver.beans.SportsAlbumBean;
import ch.unartig.studioserver.businesslogic.SessionHelper;
import ch.unartig.studioserver.model.Photo;
import ch.unartig.studioserver.persistence.DAOs.PhotoDAO;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Struts action for handling rest service requests
 * Used by the javascript part for swiping through the display images
 */
public class RestServiceAction extends Action {
    private static final int PRELOAD_PHOTOS = 5; // number of photos to preload (for album.jsp REST call, before display is called)
    private static final int FORWARD = 5; // todo : debug values - set to 50
    private static final int BACKWARD = 4; // todo : debug values - set to 20 -- must not be 4 or higher because of swiper login in the frontend
    Logger _logger = Logger.getLogger(getClass().getName());


    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse httpServletResponse)
    {
        String method = request.getMethod();
        _logger.debug("method = " + method);
        _logger.debug("request.getRequestURI() = " + request.getRequestURI());
        Long photoId = Long.valueOf(request.getParameter("photoId"));

        String direction= request.getParameter("direction"); // fetch more from right or from left ? or no direction?

        // todo check action mapping possibilities from Struts in manual

        // todo check put / delete / get / post requests
        
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        SportsAlbumBean albumBeanInSession = (SportsAlbumBean) SessionHelper.getAlbumBeanFromSession(request);

        String jsonResponse = constructJsonResponse(albumBeanInSession,photoId);
        PrintWriter out = null;
        try {
            int contentLength = jsonResponse.getBytes(StandardCharsets.UTF_8).length;
            _logger.debug("content lenght of REST API restponse : " + contentLength);
            httpServletResponse.setContentLength(contentLength);
            out = httpServletResponse.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }



        // out.print("We are send text plain");
        out.print(jsonResponse);
        // No ActionForward for this service
        return null;
    }

    /**
     *
     * @param albumBeanInSession
     * @param photoId
     * @return
     */
    private String constructJsonResponse(SportsAlbumBean albumBeanInSession, Long photoId) {
        long timeMillisStart = System.currentTimeMillis();
        _logger.debug("before display REST call :" + timeMillisStart);

        // query db query for all photos of category (with startnumber if given)
        // simply use PhotoDAO.listSportsPhotosOnPagePlusPreview() und use '0' for items on page to receive all photos

        PhotoDAO photoDAO = new PhotoDAO();
        List photosForEventCategoryAndStartnumber;

        // todo:
        // in case photoId is given, find 1st result criteria parameter - separate query?
        if (photoId != null) {
            // get position of photo -- todo : what about startnummernsuche?
            // load photos -20 +50 of current position
            photosForEventCategoryAndStartnumber = photoDAO.listNearbySportsPhotosFor(photoId, albumBeanInSession.getEventCategory(), albumBeanInSession.getStartNumber(), BACKWARD, FORWARD);

            //photoDAO.getFirstPhotoInAlbumAndSelection()
        } else { // return first [PRELOAD_PHOTOS] photos of eventcategory (used to preload 1st photos when user accesses eventcategory?)
            photosForEventCategoryAndStartnumber = photoDAO.listSportsPhotosOnPagePlusPreview(1,albumBeanInSession.getEventCategory(), PRELOAD_PHOTOS,albumBeanInSession.getStartNumber());
        }

        StringBuilder jsonResponse= new StringBuilder();
        jsonResponse.append("[ ");

        for (Iterator iterator = photosForEventCategoryAndStartnumber.iterator(); iterator.hasNext(); ) {
            Object aPhotosForEventCategoryAndStartnumber = iterator.next();
            Photo photo = (Photo) aPhotosForEventCategoryAndStartnumber;

            // String photoElement = i + " - " + photo.getPhotoId() + " - " + photo.getDisplayUrl();
            String photoElement = "{ " +
                    "\"photoId\":\"" + photo.getPhotoId() + "\", " +
                    "\"thumbnailURL1x\":\"" + photo.getThumbnailUrl() + "\", " +
                    "\"thumbnailURL2x\":\"" + photo.getThumbnailUrl2x() + "\", " +
                    "\"thumbnailURL3x\":\"" + photo.getThumbnailUrl3x() + "\", " +
                    "\"displayURL1x\":\"" + photo.getDisplayUrl() + "\", " + // deliver srcset here with 1x 2x and 3x URLs
                    "\"displayURL2x\":\"" + photo.getDisplayUrl2x() + "\", " +
                    "\"displayURL3x\":\"" + photo.getDisplayUrl3x() + "\", " +
                    "\"displayTitle\":\"" + photo.getDisplayTitle() + "\"," + // = filename ?
                    "\"time\":\"" + photo.getShortTimeString() + "\"," +
                    "\"orientation\":\"" + (photo.isOrientationPortrait()?"portrait":"landscape") + "\"" +
                    " }"; // additional comma at the end ?
            //_logger.debug(photoElement);
            jsonResponse.append(photoElement);
            if (iterator.hasNext()) {
                jsonResponse.append(",");
            }

        }

        jsonResponse.append("]");

        long timeMillisEnd = System.currentTimeMillis();
        long timeMillistaken = timeMillisEnd-timeMillisStart;
        _logger.debug("after display REST call : " + timeMillisEnd + " -- milli seconds : " + timeMillistaken);
        _logger.debug("lenght (characters) of JSON response : " + jsonResponse.length());

        return jsonResponse.toString();
    }

}

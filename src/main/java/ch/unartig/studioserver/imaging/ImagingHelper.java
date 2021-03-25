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
 * Revision 1.9  2006/11/22 14:39:05  alex
 * small fixes
 *
 * Revision 1.8  2006/11/21 13:58:45  alex
 * small fixes
 *
 * Revision 1.7  2006/11/05 22:10:02  alex
 * credit card order works
 *
 * Revision 1.6  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.5  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.4  2005/11/25 10:56:58  alex
 *
 * Revision 1.3  2005/11/16 14:26:49  alex
 * validator works for email, new library
 *
 * Revision 1.2  2005/10/24 14:37:55  alex
 * small fixes, creating directories
 *
 * Revision 1.1  2005/10/24 13:50:07  alex
 * upload of album
 * import in db
 * processing of images
 *
 ****************************************************************/
package ch.unartig.studioserver.imaging;

import java.util.Map;
import com.imgix.URLBuilder;

public class    ImagingHelper
{

    /**
     * @param renderedOp
     * @return max width or hight of picture
     */
//    public static int getMaxWidthOrHightOf()
//    {
//        return Math.max(renderedOp.getHeight(), renderedOp.getWidth());
//    }

    /**
     * Construct signed imgix URL
     * @return signed imgix url including the needed parameters
     */
    public static String getSignedImgixUrl(Map<String, String> params, String path, String domain, String imgixSignKey)
    {
        // todo find solution to securely store sign keys for all environments
        URLBuilder builder = new URLBuilder(domain);
        builder.setUseHttps(true); // use https (mixing non-https images in a https secured web site will lead to errors in certain browsers (Chrome)
        builder.setSignKey(imgixSignKey); // set sign key
        return builder.createURL(path, params);
    }


}

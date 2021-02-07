/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 21, 2005$
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
 * Revision 1.1  2007/03/01 18:23:42  alex
 * initial commit maven setup no history
 *
 * Revision 1.5  2006/11/22 21:01:52  alex
 * small fixes, types in text
 *
 * Revision 1.4  2006/11/22 16:29:56  alex
 * small fixes
 *
 * Revision 1.3  2006/10/28 21:57:09  alex
 * reformat
 *
 * Revision 1.2  2006/10/17 08:07:06  alex
 * creating the order hashes
 *
 * Revision 1.1  2005/11/21 17:52:59  alex
 * no account action , photo order
 *
 ****************************************************************/
package ch.unartig.util;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * HTTP related helper methods. Makes heavy use of Commons HTTP-Util
 */
public class HttpUtil {


    private String address;
    private URLConnection connection = null;
    private InputStream is = null;

    public void setAddress(String address) {
        this.address = address;
    }

    public HttpUtil() {
    }

//    public HttpUtil(String url)
//    {
//        this.address = url;
//    }


    /**
     * will throw IOException if not authorized:
     * java.io.IOException: Server returned HTTP response code: 401 for URL: http://.....
     *
     * @param url
     * @return
     * @throws Exception
     */
    public InputStream downloadFileOverJavaHttpClient(URL url) throws Exception {
        connection = url.openConnection();
        connection.setDoOutput(false);
        connection.setDoInput(true);
//        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        is = connection.getInputStream();

        return is;
    }


    /**
     * @return content Encoding
     */
    public String getContentEncoding() {
        return connection.getContentEncoding();
    }

    /**
     * @return content length
     */
    public int getContentLength() {
        return connection.getContentLength();
    }



    /**
     * @return
     * @throws java.io.IOException
     */
    public int getResponseCode() throws IOException {
        if (connection instanceof HttpURLConnection) {
            return ((HttpURLConnection) connection).getResponseCode();
        }
        return 0;
    }

    /**
     * @return
     * @throws java.io.IOException
     */
    public String getResponseMessage() throws IOException {
        if (connection instanceof HttpURLConnection) {
            return ((HttpURLConnection) connection).getResponseMessage();
        }
        return null;
    }

    /**
     * compose a get-request with the given host, path and parameter values
     *
     * @param parameters
     * @param server
     * @param path
     * @return a new url with the parameters introduced
     * @throws java.io.UnsupportedEncodingException
     *
     */
    public static String composeUrlFromParameters(Map parameters, String server, String path) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer(server + path);
        sb.append('?');

        for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            String value = (String) parameters.get(key);

            sb.append(key);
            sb.append('=');
            sb.append(URLEncoder.encode(value, "utf-8"));
            if (iterator.hasNext()) {
                sb.append('&');
            }
        }
        return sb.toString();
    }

    /**
     * return a url as string that points to a download site for the order-hash that is passed
     *
     * @param orderHash the order hash
     * @param request   servlet request
     * @return String to the downloadURL
     */
    public static String getDownloadUrl(final String orderHash, HttpServletRequest request) {
        String orderUri = "/order/" + orderHash + "/download.html";
        // for non standard port ...
        String serverUrl = getWebApplicationUrl(request);
        return serverUrl + orderUri;
    }

    /**
     * Construct the URL for this web-application;
     * Example : 'http://www.sportrait.com'
     *
     * @param request The http-request from the action
     * @return The URL to the web application constructed dynamically from the
     */
    public static String getWebApplicationUrl(HttpServletRequest request) {
        return getBaseUrl(request, "https".equals(request.getScheme()));
    }

    public static String getBaseUrl(HttpServletRequest request, boolean https) {
        // for non standard port ...
        String port = "";
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {

            port = String.valueOf(":" + request.getServerPort());
        }
        // use request.getScheme() if the scheme must match the current scheme
        if (https) {
            return "https://" + request.getServerName() + port + request.getContextPath();
        } else {
            return "http://" + request.getServerName() + port + request.getContextPath();
        }
    }
}

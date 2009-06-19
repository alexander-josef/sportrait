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
package ch.unartig.u_core.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;

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
 * HTTP related helper methods. Meks heavy use of Commons HTTP-Util 
 */
public class HttpUtil
{


    private URLConnection connection = null;
    private HttpClient x_client = null;
    private InputStream is = null;


    public HttpUtil()
    {
    }

//    public HttpUtil(String url)
//    {
//        this.address = url;
//    }

    public void closeConnections()
    {
        try
        {
            if (is != null)
            {
                is.close();
            }
        } catch (Exception e)
        {
        }
        try
        {
            if (connection != null && connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection) connection).disconnect();
            }
        } catch (Exception e)
        {
        }
        try
        {
            if (x_client != null)
            {
                x_client.endSession();
            }
        } catch (Exception e)
        {
        }
    }

    /**
     * will throw IOException if not authorized:
     * java.io.IOException: Server returned HTTP response code: 401 for URL: http://.....
     *
     * @throws Exception
     * @return
     * @param url
     */
    public InputStream downloadFileOverJavaHttpClient(URL url) throws Exception
    {
        connection = url.openConnection();
        connection.setDoOutput(false);
        connection.setDoInput(true);
//        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        is = connection.getInputStream();

        return is;
    }

    /**
     * Returns the response from a http GET request as stream. 
     *
     * @throws IOException Will throw IOException if not authorized:
     * @return
     * @param url
     */
    public static InputStream getHttpResponseAsStream(String url) throws IOException
    {
        String x_new_url = url;
        String x_user = null;
        String x_pwd = null;
        if (url.indexOf('@') > 0)
        {
            String x_pwd_str =
                    url.substring(url.indexOf("://") + 3,
                            url.indexOf('@'));
            x_user =
                    x_pwd_str.substring(0, x_pwd_str.indexOf(':'));
            x_pwd =
                    x_pwd_str.substring(x_pwd_str.indexOf(':') + 1);
            x_new_url = url.substring(0, url.indexOf("://") + 3)
                    + url.substring(url.indexOf('@') + 1);
        }
        HttpClient  httpClient = new HttpClient();
        httpClient.getState().setCredentials(//null,
                null,
                new UsernamePasswordCredentials(x_user, x_pwd));
        httpClient.startSession(new URL(x_new_url));
        //	GetMethod get = new GetMethod(x_new_url);
        GetMethod get = new GetMethod(new URL(x_new_url).getPath());
        if (x_new_url.indexOf('?') > 0)
        {
            String x_queryString = x_new_url.substring(x_new_url.indexOf('?') + 1);
            get.setQueryString(x_queryString);
        }
        int x_responseCode = httpClient.executeMethod(get);
        if (x_responseCode != HttpURLConnection.HTTP_OK)
        {

            throw new IOException("Server returned HTTP response code: " + x_responseCode + " for URL: " + url);
        }
        return get.getResponseBodyAsStream();
    }

    /**
     * @return content Encoding
     */
    public String getContentEncoding()
    {
        return connection.getContentEncoding();
    }

    /**
     * @return content length
     */
    public int getContentLength()
    {
        return connection.getContentLength();
    }

    /**
     * @return content type
     */
    public String getContentType()
    {
        if (connection instanceof sun.net.www.protocol.file.FileURLConnection)
        {
            if ("application/xml".equals(connection.getContentType()))
            {
                return "text/xml";
            }
        }
        return connection.getContentType();
    }

    /**
     * @return
     * @throws java.io.IOException
     */
    public int getResponseCode() throws IOException
    {
        if (connection instanceof HttpURLConnection)
        {
            return ((HttpURLConnection) connection).getResponseCode();
        }
        return 0;
    }

    /**
     * @return
     * @throws java.io.IOException
     */
    public String getResponseMessage() throws IOException
    {
        if (connection instanceof HttpURLConnection)
        {
            return ((HttpURLConnection) connection).getResponseMessage();
        }
        return null;
    }

    /**
     * compose a get-request with the given host, path and parameter values
     * @param parameters
     * @param server
     * @param path
     * @return a new url with the parameters introduced
     * @throws java.io.UnsupportedEncodingException
     */
    public static String composeUrlFromParameters(Map parameters, String server, String path) throws UnsupportedEncodingException
    {
        StringBuffer sb = new StringBuffer(server + path);
        sb.append('?');

        for (Iterator iterator = parameters.keySet().iterator(); iterator.hasNext();)
        {
            String key = (String) iterator.next();
            String value = (String) parameters.get(key);

            sb.append(key);
            sb.append('=');
            sb.append(URLEncoder.encode(value, "utf-8"));
            if (iterator.hasNext())
            {
                sb.append('&');
            }
        }
        return sb.toString();
    }

    /**
     * return a url as string that points to a download site for the order-hash that is passed
     * @param orderHash the order hash
     * @param request servlet request
     * @return String to the downloadURL
     */
    public static String getDownloadUrl(final String orderHash, HttpServletRequest request)
    {
        String orderUri = "/order/" + orderHash + "/download.html";
        // for non standard port ...
        String serverUrl = getWebApplicationUrl(request);
        return  serverUrl + orderUri;
    }

    /**
     * Construct the URL for this web-application;
     * Example : 'http://www.sportrait.com'
     * @param request The http-request from the action
     * @return The URL to the web application constructed dynamically from the 
     */
    public static String getWebApplicationUrl(HttpServletRequest request)
    {
        String port="";
        if (request.getServerPort()!=80 && request.getServerPort()!=443)
        {

            port=String.valueOf(":"+request.getServerPort());
        }
        // use request.getScheme() if the scheme must match the current scheme
        return request.getScheme() + "://" + request.getServerName() + port + request.getContextPath();
    }
}

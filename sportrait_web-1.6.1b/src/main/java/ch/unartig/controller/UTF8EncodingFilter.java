/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Dec 29, 2005$
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
 * Revision 1.2  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/18 13:28:20  urban
 * initial check in
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.2  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.1  2006/01/04 09:33:35  alex
 * utf-8 for all pages. utf-8 filter for all incoming requests.
 * post-redirect-get PRG pattern for shoppingcart / check-out
 *
 ****************************************************************/
package ch.unartig.controller;

import javax.servlet.*;
import java.io.IOException;

public class UTF8EncodingFilter implements javax.servlet.Filter
{
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // This would be a good place to collect a parameterized
        // default encoding type.  For brevity, we're going to
        // use a hard-coded value in this example.
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        // Wrap the response object.  You should create a mechanism
        // to ensure the response object only gets wrapped once.
        // In this example, the response object will inappropriately
        // get wrapped multiple times during a forward.

// AJO: response filter not used: encoding set in struts controller
//         response = new UTF8EncodingServletResponse( (HttpServletResponse) response );

        // Specify the encoding to assume for the request so
        // the parameters can be properly decoded/.
//        System.out.println("UTF8EncodingFilter.doFilter");
//        System.out.println("Encoding : " + request.getCharacterEncoding());
        request.setCharacterEncoding("UTF-8");
//        System.out.println("Encoding : " + request.getCharacterEncoding());

        filterChain.doFilter(request, response);
    }

    public void destroy()
    {
        // no-op
    }
}
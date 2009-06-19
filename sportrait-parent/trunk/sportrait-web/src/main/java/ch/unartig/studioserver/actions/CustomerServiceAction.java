/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 17, 2005$
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
 * Revision 1.2  2007/05/05 07:42:39  alex
 * contact page
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.8  2006/02/07 14:48:53  alex
 * bug 820 and minor refactorings
 *
 * Revision 1.7  2006/01/12 23:53:42  alex
 * error zeugs
 *
 * Revision 1.6  2006/01/04 09:33:35  alex
 * utf-8 for all pages. utf-8 filter for all incoming requests.
 * post-redirect-get PRG pattern for shoppingcart / check-out
 *
 * Revision 1.5  2005/11/27 19:39:10  alex
 * fast upload
 *
 * Revision 1.4  2005/11/25 16:30:45  alex
 * order save fix
 *
 * Revision 1.3  2005/11/25 10:56:58  alex
 *
 * Revision 1.2  2005/11/18 16:41:31  alex
 * layouts cleaning up, no u t f -8 line
 *
 * Revision 1.1  2005/11/18 11:10:22  alex
 * customer service message
 *
 ****************************************************************/
package ch.unartig.studioserver.actions;

import ch.unartig.u_core.Registry;
import ch.unartig.u_core.util.MailUtil;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.actions.MappingDispatchAction;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class CustomerServiceAction extends MappingDispatchAction
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public ActionForward sendMessage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    {
        ActionMessages messages = new ActionMessages();
        DynaActionForm dynaForm = (DynaActionForm) form;
        String subject = dynaForm.getString("subject");
        String fromAddress = dynaForm.getString("fromAddress");
        String contactPhone = dynaForm.getString("contactPhone");
        String sender = dynaForm.getString("sender");
        String bodyContactPhone = "contact-phone = "+contactPhone+ "\n"+dynaForm.getString("message");

        byte[] stringBytesISO = null;
        String utf8String = null;
        try
        {
            stringBytesISO = bodyContactPhone.getBytes("ISO-8859-1");
            utf8String = new String(stringBytesISO, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        _logger.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        _logger.debug("encoding : " + request.getCharacterEncoding());
        _logger.debug("bytes : " + stringBytesISO);
        for (int i = 0; i < stringBytesISO.length; i++)
        {
            byte b = stringBytesISO[i];
            _logger.debug(Byte.toString(b));
        }
        _logger.debug("utf8String : " + utf8String);
        _logger.debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

//        try
//        {
//            String decoded = URLDecoder.decode(sender,"UTF-8");
//            System.out.println("decoded = " + decoded);
//        } catch (UnsupportedEncodingException e)
//        {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

        _logger.info("Sending customer service email : \n" + "sender = " + sender + "\n" + "fromAdrress = " + fromAddress + "\n" + "contactPhone = " + contactPhone+ "\n" + "subject = " + "sportrait.com : "+ subject + "\n" + "message = " + bodyContactPhone + "\n");

        try
        {
            _logger.debug("going to send message");
            MailUtil.sendMail(subject, bodyContactPhone, Registry.getCustomerServiceAddress(), fromAddress);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("contact.message.ok"));
            dynaForm.set("subject", null);
            dynaForm.set("message", null);
            dynaForm.set("fromAddress", null);
            dynaForm.set("contactPhone", null);
            dynaForm.set("sender", null);
        } catch (MessagingException e)
        {
            _logger.error("Problem sending Mail : ", e);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("contact.message.notok"));
        }
        // save message in session because answer will be redirected
        saveMessages(request.getSession(), messages);
        return mapping.findForward("toContactPage");
    }

}

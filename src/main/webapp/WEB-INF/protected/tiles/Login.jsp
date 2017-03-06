<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<form action="j_security_check" method="POST">
    <table class="form">
        <tr>
            <td class="formLead">Username</td>
            <td>
                <input type="text" name="j_username" value="" size="18"/>
            </td>
        </tr>
        <tr>
            <td class="formLead">Passwort</td>
            <td>
                <input type="password" name="j_password" size="18"/>
            </td>
        </tr>
        <tr>
            <td class="formLead"></td>
            <td class="checkbox">
                <input type="checkbox" name="j_rememberme" value="true" /> Remember me
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <html:image styleClass="left" 
                       page="/images/buttons/bt_login_de.gif"
                       title="Login f�r aktive User"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;
                <logic:messagesPresent message="true" >
                    <html:messages id="msg" message="true" bundle="MESSAGES" property="authError">
                        <p class="errorstyle">
                            <bean:write name="msg"/>
                        </p>
                    </html:messages>
                </logic:messagesPresent>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <%--<html:link action="/photographerRegister.html">Registrieren--%>
                <%--</html:link>--%>
            </td>
        </tr>

    </table>


    <div class="g-signin2" data-onsuccess="onSignIn"></div>
</form>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


    <table class="form">

        <tr>
            <td></td>
            <td>

                <%--@elvariable id="clientInSession" type="ch.unartig.controller.Client"--%>
                <c:if test="${clientInSession==null}">
                    <div class="g-signin2" data-onsuccess="onSignIn"></div>
                </c:if>

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



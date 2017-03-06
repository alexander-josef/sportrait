<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--@elvariable id="clientInSession" type="ch.unartig.controller.Client"--%>
<c:if test="${clientInSession!=null}">
        <div id="loginMessage">Hallo <b> ${clientInSession.photographer.fullName}!</b>&nbsp; <html:link onclick="signOut()" styleClass="expliziterLink" action="/logout">Logout</html:link>
        </div>
    <%--Todo : insert Google Logout here--%>
</c:if>
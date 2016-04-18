<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="photographers" type="java.util.Collection" scope="request"/>

<html:xhtml/>
<li class="contentCenter">

    <p>
        <html:link page="/admin/editEvents.html">Bulk Import Events</html:link>
    </p>

    <p>
        <html:link action="/admin/editCreatePhotographer"> Neuen Fotografen registrieren</html:link>

    </p>
    <ul>
        <c:forEach items="${photographers}" var="photographer">
            <li>
                <html:link action="/admin/editCreatePhotographer?photographerId=${photographer.photographerId}">
                    ${photographer.userProfile.userName}
                </html:link>
            </li>
        </c:forEach>
    </ul>
</li>
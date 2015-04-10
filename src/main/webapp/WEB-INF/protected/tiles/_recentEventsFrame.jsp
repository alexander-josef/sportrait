<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="recentEvents" type="java.util.List" scope="request"/>

<html:xhtml/>

<ul id="eventsHome">
    <!-- loop event start-->
    <c:forEach items="${recentEvents}" var="event" varStatus="forEachStatus">
        <li>
            <!--<a href="upload.html" class="rightclear">fotos hochladen</a>-->
            <html:link styleClass="listLink" action="/event?sportsEventId=${event.genericLevelId}" target="_parent">
                ${event.eventDate}, ${event.location}, ${event.navTitle}
            </html:link>
        </li>
    </c:forEach>

</ul>


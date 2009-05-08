<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="sportsEvent" type="ch.unartig.studioserver.model.SportsEvent" scope="request"/>


<div class="pageNav">
    <h1 id="pageName">${sportsEvent.longTitle}</h1>
    <%--
    <!--<html:link action="/index">Home</html:link>&nbsp;>>&nbsp;
    ${sportsEvent.longTitle}-->
    ${sportsEvent.longTitle}
    --%>
</div>
<div class="content600">
    <div class="content300Left">
        <h3>Fotos</h3>

        <c:if test="${!sportsEvent.hasPhotos}">
            <!-- wenn keine Fotos start -->
            <p>Zu diesem Anlass gibt es bisher noch keine Fotos.<br/>In der Regel sollten Sie die Fotos sp�testens 24
                Stunden nach dem Anlass
                hier finden</p>
            <!-- wenn keine Fotos end -->
        </c:if>


        <c:if test="${sportsEvent.hasPhotos}">

            <ul class="linkList">
                <c:forEach items="${sportsEvent.eventCategoriesWithPhotos}" var="eventCategory"
                           varStatus="forEachStatus">
                    <li><p class="bold">
                        <html:link
                                action="/showCategory?eventCategoryId=${eventCategory.eventCategoryId}&page=1&startNumber=">
                                ${eventCategory.title}</html:link>
                    </p>
                    </li>
                </c:forEach>
            </ul>

        </c:if>
        <!-- wenn eingeloggt als Fotograf start -->
        <!--<p>-->
            <!--<a href="#">Ich m�chte zu diesem Anlass Fotos hochladen!</a>-->
        <!--</p>-->
        <!-- wenn eingeloggt als Fotograf end -->
        <!-- wenn Fotos end -->
    </div>

</div>
<div class="content600">
    <h3>�ber den Anlass</h3>

    <p>${sportsEvent.description}<br/>
        <a href="http://${sportsEvent.weblink}" target="_blank">Zur Webseite des Veranstalters...</a></p>
</div>


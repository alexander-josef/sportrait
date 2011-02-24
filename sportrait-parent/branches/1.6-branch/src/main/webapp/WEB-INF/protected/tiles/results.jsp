<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="pageNav">
    <h1 id="pageName">FOTOS SUCHEN</h1>
</div>
<div class="content600">
    <!-- Tile "searchEvent" -->
    <form action="" method="POST">
        <table class="form">
            <tr>
                <td class="formLead"><input type="text" width="200px" value=""/></td>
                <td><html:image styleClass="left" styleId="searchEvent" alt="Search Event" page="/images/buttons/bt_searchFotosHome_de.gif"
                           title="Fotos suchen"/></td>
            </tr>
            <tr>
                <td class="formLead" colspan="2"><i>zB: Marathon, Luzern, 2008-08-15</i></td>
            </tr>
        </table>
    </form>
    <!-- Tile Ende -->
</div>

<div class="content600">
    <p><span class="bold">Suche nach: </span>Marathon, Zürich, Mai 2006</p>

    <div id="eventlist">
        <ul class="linkList">
        <%--@elvariable id="recentEvents" type="java.util.List"--%>
        <c:forEach items="${recentEvents}" var="event" varStatus="forEachStatus">
            <li><p>
                <%--@elvariable id="clientInSession" type="ch.unartig.controller.Client"--%>
                <c:if test="${clientInSession!=null}">
                    <html:link styleClass="uploadLink" href="#">
                        <html:img page="/images/arrow-upload.gif" alt=""/>
                    </html:link>
                </c:if>
                <html:link styleClass="listLink" action="/event?sportsEventId=${event.genericLevelId}" target="_parent">
                    ${event.eventDate}, ${event.location}, ${event.navTitle}
                </html:link>
                <!--<a class="uploadLink" href="upload.html" title="Fotos hochladen">fotos hochladen</a>-->
                </p>
            </li>
        </c:forEach>
        </ul>
    </div>
</div>

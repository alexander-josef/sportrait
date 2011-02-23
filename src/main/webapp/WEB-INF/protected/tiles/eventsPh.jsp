<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<jsp:useBean id="recentEvents" type="java.util.List" scope="request"/>

<html:xhtml/>
<logic:messagesPresent message="true">
    <div class="contentHome errorMessage">
        <html:messages id="msg" message="true" bundle="ERROR">
            <p class="errorstyle">
                <bean:write name="msg"/>
            </p>
        </html:messages>
    </div>
</logic:messagesPresent>


<div class="leftContentHome">
    <!--<h2>FOTOS SUCHEN</h2>
    --><!-- Tile "searchEvent" --><!--
    <form action="" method="POST">
    <table class="form">
        <tr>
            <td class="formLead"><input type="text" width="200px" value=""/></td>
            <td><input class="left" name="searchEvent" type="image" src="/images/buttons/bt_searchFotosHome_de.gif"
                                   title="Fotos suchen"/></td>
        </tr>
        <tr>
            <td class="formLead" colspan="2"><i>zB: Marathon, Luzern, 2008-08-15</i></td>
        </tr>
    </table>
    </form>
    --><!-- Tile Ende --><!---->

        <h2>AKTUELLE ANLÄSSE</h2>


    <div class="eventlist">
        <ul class="linkList">
            <c:forEach items="${recentEvents}" var="event" varStatus="forEachStatus">
                <li><p class="nobreak">
                    <c:if test="${clientInSession!=null}">
                        <html:link styleClass="uploadLink" href="#" title="Fotos hochladen zu diesem Anlass">
                            <html:img page="/images/arrow-upload.gif" alt=""/>
                        </html:link>
                    </c:if>
                    <html:link styleClass="listLink" action="/event?sportsEventId=${event.genericLevelId}"
                               target="_parent">
                        ${event.eventDate}, ${event.location}, ${event.navTitle}
                    </html:link>

                    <!--<a class="uploadLink" href="upload.html" title="Fotos hochladen">fotos hochladen</a>-->
                </p>
                </li>
            </c:forEach>
        </ul>
    </div>

        <h2>KOMMENDE ANLÄSSE</h2>

        <div class="eventlist">
            <ul class="linkList">
                <c:forEach items="${recentEvents}" var="event" varStatus="forEachStatus">
                    <li><p class="nobreak">
                        <html:link styleClass="listLink" action="/event?sportsEventId=${event.genericLevelId}"
                                   target="_parent">
                            ${event.eventDate}, ${event.location}, ${event.navTitle}
                        </html:link>
                    </p>
                    </li>
                </c:forEach>
            </ul>
        </div>
</div>

<div class="rightContentHome">

        <h2>&nbsp;</h2>
        <p style="height:205px;">In diesem Kasten werden die Events angezeigt, für die du Fotos hochladen kannst. Ab dem Tag, an dem der Anlass
        stattfindet bis eine Woche nach dem Anlass werden die Anlässe hier angezeigt.<br/>
        Über den <b>UPLOAD-LINK </b><html:img page="/images/arrow-upload.gif" alt="Fotos hochladen"/> gelangst du zur FOTO-UPLOAD Seite.
        </p>

        <h2>&nbsp;</h2>
        <p>In diesem Kasten werden die Anlässe angezeigt, die noch nicht stattgefunden haben. Die Anlässe sind chronologisch geordnet mit dem
        nächsten Anlass zuoberst. Jeeder Anlass ist ein link, der dich zu mehr Informationen über den Anlässen führt.<br/>
        Wenn du einen Anlass angemeldet hast, erscheint dieser nach ca. 1 Tag in diesem Kasetn.
        </p>

</div>


<%--<c:forEach items="${recentEvents}" var="event" varStatus="forEachStatus">
    <li>
        <html:link
                action="/event?sportsEventId=${event.genericLevelId}">${event.eventDate}
            , ${event.location}
            , ${event.navTitle}</html:link>
    </li>
</c:forEach>--%>



<%@ page import="ch.unartig.studioserver.model.Album" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--<jsp:useBean id="eventGroups" type="java.util.ArrayList" scope="request"/>--%>
<jsp:useBean id="photographerAdminBean" type="ch.unartig.studioserver.beans.PhotographerAdminBean" scope="request"/>
<li class="contentCenter">
<div class="contentLeft content-1-2">
    <h2>Liste Ihrer Alben </h2>
    <ul id="eventsAdmin">

        <c:forEach var="eventGroup" items="${photographerAdminBean.eventGroups}" >
            <!-- loop event start: get events with albums for the photographer-->
            <logic:iterate id="event" name="eventGroup" property="eventsWithAlbums(${clientInSession.photographer.photographerId})" indexId="eventIndex">
                <li class="albumAdmin">
                        <!--only for admins:-->
                        <html:link action="/admin/deleteLevel" styleClass="right padd-rl-5"
                                   onclick="return confirm('Permanently delete Event? All Photos under this Album will be deleted as well!');"
                                   paramId="genericLevelId" paramName="event" paramProperty="genericLevelId">
                            <img src="/images/admin_trash.gif" alt="trash" title="delete"/>
                        </html:link>

                        <span class="green">${event.eventDateDisplay}, ${eventGroup.city}, ${event.navTitle}</span>
                </li>
                <!-- loop album start: -->
                <logic:iterate id="album" name="event" property="photographerAlbums(${clientInSession.photographer.photographerId})" indexId="albumIndex">

                    <li class="albumAdmin">
                        <html:link action="/admin/deleteLevel" styleClass="right padd-rl-5"
                                   onclick="return confirm('Permanently delete Album? All Photos under this Album will be deleted as well!');"
                                   paramId="genericLevelId" paramName="album" paramProperty="genericLevelId">
                            <img src="/images/admin_trash.gif" alt="trash" title="delete"/>
                        </html:link>

                        <html:link action="/photographer/admin/edit?genericLevelId=${album.genericLevelId}" styleClass="listLink">
                            <c:if test="${album.publish}">
                                <span class="green">
                            </c:if>
                            <c:if test="${!album.publish}">
                                <span class="red">${album.navTitle}
                            </c:if>
                            ${album.navTitle}
                            <c:if test="${clientInSession.admin}">
                                <!--admin: show photographer info:-->
                                ${album.photographer}
                            </c:if>
                            Fotos: ${album.numberOfPhotos}
                            </span>
                        </html:link>


                    </li>
                    <!-- loop album end-->
                </logic:iterate>

                <!-- loop event end-->
            </logic:iterate>
        </c:forEach>

    </ul>
</div>
<c:if test="${photographerAdminBean.level!=null}">
<%--<jsp:useBean id="level" type="ch.unartig.studioserver.model.GenericLevel" scope="request"/>--%>
<%--<jsp:useBean id="productTypeList" type="java.util.List" scope="request"/>--%>
<html:form action="/photographer/updateAlbum">
<html:hidden property="genericLevelId" value="${photographerAdminBean.level.genericLevelId}"/>
<div class="contentBoxAdmin">
    <h2>Publikation</h2>
    <table class="form">
        <tr>
            <td class="formLead">Name:</td>
            <td>${photographerAdminBean.level.description}</td>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td class="formLead">Keywords:</td>
            <td><input name="passwordPh" type="text" size="25"/></td>
            <td>&nbsp;(mit Komma trennen)</td>
        </tr>

        <tr>
            <td>Status</td>
            <td>
                <!--<input class="rightclear" type="button" name="statusChange" value="offline schalten"/>-->
                <c:if test="${photographerAdminBean.level.publish}">
                    <span class="green leftalign">online</span>

                    <html:link action="/photographer/toggleAlbumPublishStatus?genericLevelId=${photographerAdminBean.level.genericLevelId}"
                               onclick="return confirm('Toggle Album status from online to offline?');"
                               >
                        Offline schalten
                    </html:link>
                </c:if>

                <c:if test="${!photographerAdminBean.level.publish}">
                    <span class="red leftalign">offline</span>

                    <html:link action="/photographer/toggleAlbumPublishStatus?genericLevelId=${photographerAdminBean.level.genericLevelId}"
                               onclick="return confirm('Toggle Album status from offline to online?');"
                               >
                        Online schalten
                    </html:link>
                </c:if>

            </td>
            <td></td>
        </tr>
    </table>
</div>


<div class="contentBoxAdmin">
    <h2>Produkte & Preise</h2>
    <!-- product definitions : -->
    <table class="form">

        <c:if test="${photographerAdminBean.level.albumLevel}">
            <tr>
                    <%--iterate over producttypes and show possible prices--%>
                <td colspan="2">
                    <table class="pricelist_box">
                        <tr>
                            <th>Produkt</th>
                            <th>Preis & Verfügbarkeit</th>
                        </tr>
                        <c:forEach var="productType" items="${photographerAdminBean.productTypeList}">
                            <tr>
                                <td>${productType.name}</td>
                                <td>
                                    <html:select property="productPrices(${productType.productTypeId})">
                                        <html:option value="-1">not available</html:option>
                                        <html:optionsCollection name="productType" property="prices" value="priceId" label="priceCHF"/>
                                    </html:select>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
        </c:if>
    </table>
    <br/> <input type="image" src="/images/buttons/bt_saveChanges_de.jpg"/>

    </html:form>
</div>

<!--/////////////////////////////////////////////////////////-->
<%-- Mapping todo move to separate page ////////////////////////////// --%>
<!--/////////////////////////////////////////////////////////-->

<div class="contentBoxAdmin">
    <h2>Startnummer-Zuweisung</h2>
    <!-- mapping functions : -->
    <html:form action="/admin/startnumberMapping" method="POST" enctype="multipart/form-data">
        <html:hidden property="sportsAlbumId" value="${photographerAdminBean.level.genericLevelId}"/>
        <tr>
            <th><h3 class="orange">Manuelle Starnummer-Zuweisung (über Unartig Client):</h3></th>
            <td></td>
            <td></td>
        </tr>

        <tr>
            <th><p>File</p></th>
            <td class="browsButton">
                <html:file property="mappingFile"/>
            </td>
            <td><p>(Manual mapping File aus Tool: [Filename]TAB[Startnumber])</p></td>
        </tr>
        <tr>
            <th></th>
            <td>
                <input type="image" src="/images/buttons/bt_map_de.jpg"/>
                <%--<html:submit value="Mappen" alt="" title="mappen!"/>--%>
            </td>
            <td></td>
        </tr>
    </html:form>
        <tr>
            <td colspan="2"><br/></td>
        </tr>
    <html:form action="/admin/finishtimeMapping" method="POST" enctype="multipart/form-data">
        <html:hidden property="sportsAlbumId" value="${photographerAdminBean.level.genericLevelId}"/>
        <tr>
            <th><h3 class="orange">Startnummer-Zuweisung anhand der Zeitmessung:</h3></th>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td><p>
                <html:radio property="photopointLocation" value="beforeFinishTiming"/>
                Fotopoint vor Zeit-Messung</p>

                <p>
                    <html:radio property="photopointLocation" value="afterStartTiming"/>
                    Fotopoint nach Zeit-Messung</p>
            </td>
        </tr>
        <tr>
            <th><p class="bold">Zeitdifferenz Fotopoint-Zeitmessung: </p></th>
            <td>
                <html:text styleClass="navTitle" property="photopointFinishDifference"/>&nbsp;(In Sekunden)
            </td>
        </tr>

        <tr>
            <th><p class="bold">Zeitdifferenz langsam-schnell:</p></th>
            <td>
                <html:text styleClass="navTitle" property="photopointTolerance"/>&nbsp;(In Sekunden)
            </td>
        </tr>

        <tr>
            <th><p class="bold">Datei mit Startnummer-Zeitmessung</p></th>
            <td class="browsButton">
                <html:file styleClass="navTitle" property="mappingFile"/>
            </td>
            <td>(Laufzeiten File von SOLA: [etappe]TAB[Startnumber]TAB[zeit]TAB[Mannschaft] )
                <br/>IDEE: file-struktur hier eingeben, dann koennen alle arten von Files geparsed werden
                <br/>

                <p style="color:red">Achtung: Event Datum muss mit den zu mappenden Fotos uebereinstimmen!</p>

            </td>
        </tr>

        <tr>
            <th></th>
            <td>
                <html:submit alt="File Mappen" title="mappen!" value="Mappen"/>
                <!--<input type="image" src="/images/buttons/bt_map_de.jpg"/>-->
            </td>
            <td></td>
        </tr>


    </html:form>
    <hr/>
    <html:form action="/admin/deleteFinishtimeMappings" method="POST">
        <html:hidden property="sportsAlbumId" value="${photographerAdminBean.level.genericLevelId}"/>
        <tr>
            <th><h3 class="orange">Alle Startnummer-Zuweisungen löschen!</h3></th>
            <td>
                <!--<input type="image" src="/images/buttons/bt_delete_de.jpg"/>-->
                <html:submit value="Loeschen!" onclick="return confirm('Alle Startnummern-Mappings fuer dieses Album loeschen?');"/>
                <p>Alle Startnummer-Zuweisungen in <b>${photographerAdminBean.level.description}</b> werden gelöscht!</p>
            </td>
            <td></td>
        </tr>
    </html:form>

    <%-- New for image recognition --%>

    <html:form action="/admin/postProcessMappings" method="POST">
        <html:hidden property="sportsAlbumId" value="${photographerAdminBean.level.genericLevelId}"/>
        <tr>
            <th><h3 class="orange">Post-Processing für Startnummern in Etappe <b>${photographerAdminBean.level.description}</b></h3></th>
            <td>
                <!--<input type="image" src="/images/buttons/bt_delete_de.jpg"/>-->
                <html:submit value="Start Post-Processing"/>
                <p>Queue mit Gesichtern ohne Startnummern sowie schlecht erkannte Startnummern werden gesucht</p>
            </td>
            <td></td>
        </tr>
    </html:form>


    </c:if>
</div>
</li>
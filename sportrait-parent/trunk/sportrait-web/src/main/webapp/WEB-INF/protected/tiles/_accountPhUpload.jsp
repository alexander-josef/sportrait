<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="uploadBean" type="ch.unartig.sportrait.web.beans.UploadBean" scope="request"/>
<jsp:useBean id="clientInSession" type="ch.unartig.u_core.controller.Client" scope="session"/>

<html:xhtml/>
<li class="contentCenter">
    <h2>Bilder Upload f�r:</h2><br/>

    <html:form action="/photographer/createUpload" enctype="multipart/form-data">
        <h3><span class="errorstyle">Option 1: Bestehendes Album (TODO, funktioniert noch nicht)</span></h3>
        <table class="form">
            <tr>
                <td><span class="bold">Album:</span></td>
                <td>
                        <%--wie viele events anzeigen? letzte zwei wochen? was machen fuer andere events?--%>
                    <select name="albumId">
                        <option>Engadiner Skimarathon; Start</option>
                        <option>Marathon Z�rich; Impressionen</option>
                        <option>Marathon Z?rich; M 20</option>
                        <option>CSI Luzern; Samstag</option>
                    </select>
                </td>
                <td>Bestehendes Album ausw�hlen</td>
            </tr>
            <tr>
                <td><span class="bold">Upload-Quelle:</span></td>
                <td><input type="file" name="pictureSource" size="30"/></td>
                <td>Kommentar_2</td>
            </tr>
            <tr>
                <td colspan=3><h3><br/><span class="errorstyle">Option 2: Neues Album erstellen</span></h3></td>
            </tr>
            <tr>
                <td><span class="bold">Event:</span></td>
                <td>
                        <%--wie viele events anzeigen? letzte zwei wochen? was machen fuer andere events?--%>
                    <html:select name="uploadBean" property="eventId" onchange="window.location.href ='upload.html?eventId=' + this.form.eventId.value;">
                        <html:option value="">--</html:option>
                        <html:optionsCollection name="uploadBean" property="events" label="longTitle" value="genericLevelId"/>
                    </html:select>
                </td>
                <td>Kommentar_3</td>
            </tr>
            <tr>
                <td><span class="bold">Kategorie:</span></td>
                <td>
                    <html:select name="uploadBean" property="eventCategoryId">
                        <html:option value="">-- Kategorie w�hlen --</html:option>
                        <html:optionsCollection name="uploadBean" property="eventCategories" label="title" value="eventCategoryId"/>
                    </html:select>
                </td>
                <td>&nbsp;(bestehende Kategorie w�hlen oder neue erstellen)</td>
            </tr>
            <tr>
                <td colspan=3/>
            </tr>
            <tr>
                <td><span class="bold">Upload-Quelle:</span></td>
                <td>
                    <html:file property="content"/>
                    <html:submit value="Upload starten"/>
                </td>
                <td>Entweder Zip-File waehlen...</td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td>oder...</td>
            </tr>
            <tr>
                <td>Pfad auf Server:</td>
                <td>
                    <html:text property="imagePath"/>
                    <html:submit value="Import starten"/>
                </td>
                <td>...absoluten '/fine' Pfad auf Server mit Bildern angeben</td>
            </tr>
            <tr>
                <td colspan=3/>
            </tr>
             
            <tr>
                <td><span style="color:red">NEU:</span> Import-File:</td>
                <td>
                    <html:file property="importData"/>
                    <%-- How can the property be read ?  --%>
                    <html:submit value="Foto-Daten in DB importieren"/>
                </td>
                <td>CSV-Datei mit den Importdaten (Name; Pixel Breite; Pixel Hoehe; Datum MM/dd/YY)</td>
            </tr>
            <tr>
                <td colspan=3><h3><br/><span class="errorstyle" >Upload Applet : ${clientInSession.serverUrl}</span></h3></td>
            </tr>

            <tr>
                <td colspan=3>
                    <applet code="wjhk.jupload2.JUploadApplet"
                            archive="/client-applet/sportrait_client.jar,/client-applet/xmlrpc-common.jar,/client-applet/xmlrpc-client.jar,/client-applet/ws-commons-util.jar" width="800" height="600" alt="">
                        <param name="postURL" value="${clientInSession.serverUrl}/jupload/upload.html" />
                        <param name="xmlRpcServerUrl" value="${clientInSession.serverUrl}/xmlrpc" />
                        <param name="photographerId" value="${clientInSession.photographer.photographerId}" />
                        <param name="photographerPass" value="1234" />
                        <param name="maxChunkSize" value="500000" />
                        <param name="uploadPolicy" value="ch.unartig.sportrait.client.jUploadPolicies.UnartigSportraitUploadPolicy" />
                        <param name="nbFilesPerRequest" value="1" />
                        <!-- Optionnal, see code comments -->
                        <!--<param name="maxPicHeight" value="900" />-->

                        <!-- Optionnal, see code comments -->
                        <!--<param name="maxPicWidth" value="700" />-->
                        <!-- Optionnal, see code comments -->
                        <param name="debugLevel" value="99" />
                        <!-- Optionnal, see code comments -->
                        Java 1.5 or higher plugin required.
                    </applet>
                </td>
            </tr>

        </table>
    </html:form>
</li>

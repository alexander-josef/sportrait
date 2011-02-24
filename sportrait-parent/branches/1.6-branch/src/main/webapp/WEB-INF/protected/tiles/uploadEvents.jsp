<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<li class="contentCenter">
<html:form action="/admin/bulkImportEvents" enctype="multipart/form-data" >

    Strichpunkt-getrenntes File mit Event-Daten:<br/>
    <html:file property="content"/>
    <p></p>
    Format:<br/> Sportart;Datum;PLZ;Ort;Titel;Beschreibung;Link;(Anlass-Kategorien)*<br/>

    <p></p>
    Beispiel:<br/> laufen;27.3.2007;8000;Zürich;Marathon Zürich;Am Marathon Zürich nehmen heute 12'000 Sportlerinnen und Sportler teil. Er stellt für die Marathon-Disziplin der prominenteste Anlass in der Schweiz dar\nIm Jahre 1996 standen 100 Sportlerinnen und Sportler zum ersten mal am Start des Züricher Marathons;http://www.zurichmarathon.ch/;M 18;W 18;W 20;M 20;M 30;W 30
    <br/>
    <html:submit>Importieren</html:submit>
</html:form>

</li>
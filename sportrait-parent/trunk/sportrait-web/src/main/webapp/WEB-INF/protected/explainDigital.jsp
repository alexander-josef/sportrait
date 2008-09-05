<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <!-- todo: language depending keywords -->
    <!-- todo: is this page still active? -->
    <meta name="author" content="studio unartig; --www.unartig.ch--; (c)2005-2008"/>
    <meta name="keywords" content="unartig, digital, data, original"/>
    <link href="/css/popup.css" rel="stylesheet" type="text/css"/>


    <div id="popup">
    <div class="pricelist_box">
        <bean:message key="popup.explain.digital" bundle="CONTENT"/>

<%--
        <h3>Beispiel</h3>
        <p>
            <html:link action="/downloadExample"
                       onclick="Example=window.open('/downloadExample.html','Download'); PriceList.focus(); return false;"
                    target="_parent">
                Beispiel einer Download-Seite.
            </html:link>
        </p><br/>
--%>

    </div>

    <a href="#" class="closeWindow" onclick="self.close()">
        <html:img bundle="IMAGES" srcKey="btn.close.src" altKey="btn.close.alt"/>
    </a>

</html:html>

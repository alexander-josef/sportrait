<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:xhtml/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script src="<html:rewrite page="/js/GhostBoxes.js"/>" type="text/javascript"></script>
    <script src="<html:rewrite page="/js/formPoster.js"/>" type="text/javascript"></script>
    <title><tiles:insert attribute="htmlTitle"/></title>
    <link rel="icon" type="image/x-icon" href="/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="<html:rewrite page="/css/main.css"/>?version=148"/>
</head>
<body id="iframeBody">

<tiles:insert attribute="content1"/>

</body>
</html>

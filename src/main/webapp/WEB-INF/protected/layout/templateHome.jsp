<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:xhtml/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script src="<html:rewrite page="/js/formPoster.js"/>" type="text/javascript"></script>
    <script src="<html:rewrite page="/js/loginModal.js"/>" type="text/javascript"></script>
    <tiles:insert attribute="htmlTitle"/>
    <!--<link rel="stylesheet" type="text/css" href="/css/main.css"/>-->
    <link rel="stylesheet" type="text/css" href="<html:rewrite page="/css/main.css"/>"/>
    <tiles:insert attribute="cssOverrule"/>
</head>
<body id="bodyHome">
<center id="window">
    <tiles:insert attribute="head"/>
    <div id="containerHome">
        <!-- contents -->
        <tiles:insert attribute="content1"/>
        <tiles:insert attribute="content2"/>
        <tiles:insert attribute="content3"/>
        <tiles:insert attribute="footer"/>
    </div>
</center>

<tiles:insert attribute="googleAnalytics"/>

</body>
</html>

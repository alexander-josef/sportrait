<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="ch.unartig.studioserver.Registry" %>
<html:xhtml/>
<html>
<head>
    <%--This is the tag manager snippet --%>
    <c:if test="${Registry._DevEnv}">
        <tiles:insert attribute="googleTagManagerHeadSnippetDev" ignore="true"/>
    </c:if>
    <c:if test="${Registry._IntEnv}">
        <tiles:insert attribute="googleTagManagerHeadSnippetDev" ignore="true"/>
    </c:if>
    <c:if test="${Registry._ProdEnv}">
        <tiles:insert attribute="googleTagManagerHeadSnippetDev" ignore="true"/>
    </c:if>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="https://apis.google.com/js/platform.js" async defer></script>
    <meta name="google-signin-client_id"
          content="780630173968-29smq37pmuihjn34mgpflbi7393k3dgh.apps.googleusercontent.com">
    <script src="<html:rewrite page="/js/formPoster.js"/>" type="text/javascript"></script>
    <script src="<html:rewrite page="/js/loginModal.js"/>" type="text/javascript"></script>
    <tiles:insert attribute="htmlTitle"/>
    <!--<link rel="stylesheet" type="text/css" href="/css/main.css"/>-->
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="<html:rewrite page="/css/main.css"/>"/>
    <%-- Media query to support "old" stylesheet with definitions for desktop screens --%>
    <link rel="stylesheet" href="<html:rewrite page="/css/main-wide-screen-override.css"/>"
          media="screen and (min-width: 40em)"/>
    <tiles:insert attribute="cssOverrule"/>
    <tiles:insert attribute="googleAnalytics"/>

    <script>

        function onSignIn(googleUser) {

            // window.alert("log in attempt! Google User Id : " + googleUser.getId());
            console.log("log in attempt! Google User Id : " + googleUser.getId());
            var id_token = googleUser.getAuthResponse().id_token;

            var xhr = new XMLHttpRequest();
            xhr.open('POST', '<html:rewrite action="/tokensignin" />');
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onload = function () {
                if (xhr.responseText == "unauthorized") {
                    console.log('not authorized, logging out');
                    window.alert("your user is not registered");
                    signOut();
                } else {
                    console.log('Signed in');
                }
            };
            xhr.send('idtoken=' + id_token);

        }

        function signOut() {
            var auth2 = gapi.auth2.getAuthInstance();
            auth2.signOut().then(function () {
                console.log('User signed out.');
            });
        }
    </script>

</head>
<body id="bodyHome">
<%--This is the tag manager snippet --%>
<c:if test="${Registry._DevEnv}">
    <tiles:insert attribute="googleTagManagerBodySnippetDev" ignore="true"/>
</c:if>
<c:if test="${Registry._IntEnv}">
    <tiles:insert attribute="googleTagManagerBodySnippetInt" ignore="true"/>
</c:if>
<c:if test="${Registry._ProdEnv}">
    <tiles:insert attribute="googleTagManagerBodySnippetProd" ignore="true"/>
</c:if>

<div id="window">
    <header role="banner">
        <tiles:insert attribute="head"/>
    </header>
    <div id="containerHome">
        <!-- contents -->
        <tiles:insert attribute="content1"/>
        <tiles:insert attribute="content2"/>
        <tiles:insert attribute="content3"/>
        <footer role="contentinfo">
            <tiles:insert attribute="footer"/>
        </footer>
    </div>
</div>

</body>
</html>

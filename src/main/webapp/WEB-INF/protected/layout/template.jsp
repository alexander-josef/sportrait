<%--@elvariable id="display" type="ch.unartig.studioserver.beans.DisplayBean"--%>
<%--@elvariable id="albumBean" type="ch.unartig.studioserver.beans.SportsAlbumBean"--%>
<%--@elvariable id="sportsEvent" type="ch.unartig.studioserver.model.SportsEvent"--%>
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
<script>
        dataLayer = [{
            'pageTitle' : '<tiles:getAsString name="pageTitle" ignore="true"/>',
            'pageURL':'',

            'pageCategory': '',
            'visitorType': '',

            'eventTitle':'${sportsEvent.longTitle}',
            'eventYear':'${sportsEvent.eventDateYear}',
            'eventId' : '${sportsEvent.genericLevelId}',

            'eventCategoryId' : '${albumBean.eventCategoryId}',
            'eventCategoryTitle' : '${albumBean.eventCategory.title}',
            'thumbnailPage' : '${albumBean.page}',
            'startNumber' : '${albumBean.startNumber}',

            'photoId':'${display.displayPhotoId}',
            'photoTitle': '${display.displayPhoto.displayTitle}',
            'photoOrientation': '${display.displayPhoto.orientationSuffix}',
            'photoPhotographer': '${display.displayPhoto.photographer.fullName}',
            'photoURL' : '${display.displayPhoto.highResUrl}',
            'photoPictureTakenDate' : '${display.displayPhoto.pictureTakenDate}',


        }];
    </script>

    <%--
        START Google Tag Manager Head Snippet
    --%>
    <c:if test="${Registry._DevEnv}">
        <tiles:insert attribute="googleTagManagerHeadSnippetDev" ignore="true"/>
    </c:if>
    <c:if test="${Registry._IntEnv}">
        <tiles:insert attribute="googleTagManagerHeadSnippetInt" ignore="true"/>
    </c:if>
    <c:if test="${Registry._ProdEnv}">
        <tiles:insert attribute="googleTagManagerHeadSnippetProd" ignore="true"/>
    </c:if>
    <%--
        Google Tag Manager END
    --%>

    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%-- <script src="<html:rewrite page="/js/imgix.min.js"/>"></script> --%>
    <meta property="ix:host" content="dev-sportrait.imgix.net">
    <meta property="ix:useHttps" content="true">
    <script src="https://apis.google.com/js/platform.js" async defer></script>
    <meta name="google-signin-client_id"
          content="780630173968-29smq37pmuihjn34mgpflbi7393k3dgh.apps.googleusercontent.com">
    <script src="<html:rewrite page="/js/formPoster.js"/>" type="text/javascript"></script>
    <script src="<html:rewrite page="/js/loginModal.js"/>" type="text/javascript"></script>
    <tiles:insert attribute="cssLinks" ignore="true"/>
    <tiles:insert attribute="htmlTitle"/>
    <!--<link rel="stylesheet" type="text/css" href="/css/main.css"/>-->
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="<html:rewrite page="/css/main.css"/>?version=148"/>
    <%-- Media query to support "old" stylesheet with definitions for desktop screens --%>
    <link rel="stylesheet" href="<html:rewrite page="/css/main-wide-screen-override.css"/>"
          media="screen and (min-width: 40em)"/>
    <tiles:insert attribute="cssOverrule" ignore="true"/>
    <%-- Google Analytics snippet not inserted in template anymore - comes with Google Tag Manager --%>
    <tiles:insert attribute="twitterCard" ignore="true"/>

    <%-- Google Sign-In js functions
         Function called by Google Sign-In button on success
     --%>
    <script>


        function onSignIn(googleUser) {

            // window.alert("log in attempt! Google User Id : " + googleUser.getId());
            console.log("log in attempt! Google User Id : " + googleUser.getId());

            // check if this can be empty
            var id_token = googleUser.getAuthResponse().id_token;
            console.log("using token : " + id_token);
            var xhr = new XMLHttpRequest();
            xhr.open('POST', '<html:rewrite action="/tokensignin" />');
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onload = function () {
                signinResponse = xhr.responseText;
                console.log("response: " + signinResponse);
                if (signinResponse == "unauthorized") {
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
<body id="body">

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

<%-- Facebook SDK--%>


<div id="fb-root"></div>
<script>(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/de_DE/sdk.js#xfbml=1&version=v2.10";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>

<%-- END Facebook SDK --%>

<%-- Twitter JS --%>

<%--
<script>window.twttr = (function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0],
        t = window.twttr || {};
    if (d.getElementById(id)) return t;
    js = d.createElement(s);
    js.id = id;
    js.src = "https://platform.twitter.com/widgets.js";
    fjs.parentNode.insertBefore(js, fjs);

    t._e = [];
    t.ready = function(f) {
        t._e.push(f);
    };

    return t;
}(document, "script", "twitter-wjs"));</script>
--%>

<%-- END Twitter JS--%>


<%-- todo bessere loesung:
    * z.b. verschachtelte tiles; nur das open tag fuer body zu extrahieren macht wenig sinn und ist fehleranfaellig
 --%>


<div id="window">
    <header role="banner">
        <tiles:insert attribute="head"/>
    </header>
    <div id="container">
        <!-- contents -->
        <tiles:insert attribute="content1"/>
        <tiles:insert attribute="content2"/>
        <tiles:insert attribute="content3"/>
        <footer role="contentinfo">
            <tiles:insert attribute="footer"/>
        </footer>
    </div>
</div>
<%-- Placing scripts at the bottom of the <body> element improves the display speed, because script compilation slows down the display. --%>
<tiles:insert attribute="jsScripts" ignore="true"/>
</body>
</html>

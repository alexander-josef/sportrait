<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:xhtml/>
<html>
<head>

    <!-- Google Tag Manager -->
    <script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
            new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
        j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
        'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
    })(window,document,'script','dataLayer','GTM-MM75LH6');</script>
    <!-- End Google Tag Manager -->

    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="<html:rewrite page="/js/imgix.min.js"/>"></script>
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
    <link rel="stylesheet" type="text/css" href="<html:rewrite page="/css/main.css"/>"/>
    <%-- Media query to support "old" stylesheet with definitions for desktop screens --%>
    <link rel="stylesheet" href="<html:rewrite page="/css/main-wide-screen-override.css"/>"
          media="screen and (min-width: 40em)"/>
    <tiles:insert attribute="cssOverrule"/>
    <tiles:insert attribute="googleAnalytics"/>
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

<!-- Google Tag Manager (noscript) -->
<noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-MM75LH6"
                  height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->

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

<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8"%>

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

    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="https://apis.google.com/js/platform.js" async defer ></script>
    <meta name="google-signin-client_id" content="780630173968-29smq37pmuihjn34mgpflbi7393k3dgh.apps.googleusercontent.com">
    <script src="<html:rewrite page="/js/formPoster.js"/>" type="text/javascript"></script>
    <script src="<html:rewrite page="/js/loginModal.js"/>" type="text/javascript"></script>
    <tiles:insert attribute="htmlTitle"/>
    <!--<link rel="stylesheet" type="text/css" href="/css/main.css"/>-->
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="<html:rewrite page="/css/main.css"/>"/>
    <%-- Media query to support "old" stylesheet with definitions for desktop screens --%>
    <link rel="stylesheet" href="<html:rewrite page="/css/main-wide-screen-override.css"/>" media="screen and (min-width: 40em)" />
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
                if (xhr.responseText=="unauthorized") {
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

<!-- Google Tag Manager (noscript) -->
<noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-MM75LH6"
                  height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->

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

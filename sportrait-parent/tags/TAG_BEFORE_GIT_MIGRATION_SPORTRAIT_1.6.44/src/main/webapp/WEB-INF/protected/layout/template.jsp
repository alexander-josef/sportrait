<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:xhtml/>
<html>
<head>
    <script src="https://apis.google.com/js/platform.js" async defer></script>
    <meta name="google-signin-client_id"
          content="780630173968-29smq37pmuihjn34mgpflbi7393k3dgh.apps.googleusercontent.com">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script src="<html:rewrite page="/js/formPoster.js"/>" type="text/javascript"></script>
    <script src="<html:rewrite page="/js/loginModal.js"/>" type="text/javascript"></script>
    <tiles:insert attribute="htmlTitle"/>
    <link rel="stylesheet" type="text/css" href="<html:rewrite page="/css/main.css"/>"/>
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
            var id_token = googleUser.getAuthResponse().id_token;

            var xhr = new XMLHttpRequest();
            xhr.open('POST', '<html:rewrite action="/tokensignin" />');
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onload = function () {
                signinResponse = xhr.responseText;
                console.log("response: " + signinResponse);
                if (signinResponse=="unauthorized") {
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
<%-- Facebook SDK--%>
<div id="fb-root"></div>
<script>(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/de_DE/sdk.js#xfbml=1&version=v2.10";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>
<%-- END Facebook SDK --%>

<%-- Twitter JS --%>
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

<%-- END Twitter JS--%>


<%-- todo bessere loesung:
    * z.b. verschachtelte tiles; nur das open tag fuer body zu extrahieren macht wenig sinn und ist fehleranfaellig
 --%>

<center id="window">
    <tiles:insert attribute="head"/>
    <div id="container">
        <!-- contents -->
        <tiles:insert attribute="content1"/>
        <tiles:insert attribute="content2"/>
        <tiles:insert attribute="content3"/>
        <tiles:insert attribute="footer"/>
    </div>
</center>

</body>
</html>

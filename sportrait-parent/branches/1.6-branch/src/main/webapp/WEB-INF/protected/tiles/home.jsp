<%@ page contentType="text/html; charset=UTF-8"%>

<%--@elvariable id="clientInSession" type="ch.unartig.controller.Client"--%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<jsp:useBean id="recentEvents" type="java.util.List" scope="request"/>

<html:xhtml/>
<%-- Do we need the error messages here? Be careful ... authentication messages are also in the error bundle ...--%>


<%-- We leave this out for now. Should there be a need to display standard messages on the homepage, use a property to filter them, for example from the authentication messages --%>
<%--

<logic:messagesPresent message="true" >
    <!--messages present:-->
    <div class="contentHome errorMessage">
        <html:messages id="msg" message="true" bundle="ERROR" >
            <p class="errorstyle">
                <bean:write name="msg"/>
            </p>
        </html:messages>
    </div>
</logic:messagesPresent>
<logic:messagesPresent message="false" >
    <!--errors present:-->
    <div class="contentHome errorMessage">
        <html:messages id="msg" message="false" bundle="ERROR" >
            <p class="errorstyle">
                <bean:write name="msg"/>
            </p>
        </html:messages>
    </div>
</logic:messagesPresent>

--%>
<div class="leftContentHome">
    <!--<h2>FOTOS SUCHEN</h2>
    --><!-- Tile "searchEvent" --><!--
    <form action="" method="POST">
    <table class="form">
        <tr>
            <td class="formLead"><input type="text" width="200px" value=""/></td>
            <td><input class="left" name="searchEvent" type="image" src="/images/buttons/bt_searchFotosHome_de.gif"
                                   title="Fotos suchen"/></td>
        </tr>
        <tr>
            <td class="formLead" colspan="2"><i>zB: Marathon, Luzern, 2008-08-15</i></td>
        </tr>
    </table>
    </form>
    --><!-- Tile Ende --><!---->
<%--

    <c:if test="${clientInSession==null}">
        <h2>AKTUELLE FOTOS</h2>
    </c:if>
--%>
        <h2>AKTUELLE ANLÄSSE</h2>

   
    <div class="eventlist">
        <ul class="linkList">
            <c:forEach items="${recentEvents}" var="event" varStatus="forEachStatus">
                <li><p>

                    <html:link styleClass="listLink" action="/event?sportsEventId=${event.genericLevelId}"
                               target="_parent">
                        ${event.eventDate}, ${event.location}, ${event.navTitle}
                    </html:link>
                    <c:if test="${clientInSession!=null}">
                        <html:link styleClass="uploadLink" href="#" title="Fotos hochladen zu diesem Anlass">
                            <html:img page="/images/arrow-upload.gif" alt="Fotos hochladen zu diesem Anlass"
                                      style="height:11px;width:47px;"/>
                        </html:link>
                    </c:if>
                    <!--<a class="uploadLink" href="upload.html" title="Fotos hochladen">fotos hochladen</a>-->
                </p>
                </li>
            </c:forEach>
        </ul>
    </div>

    <h2>WAS IST SPORTRAIT</h2>

    <p>SPORTRAIT wurde von den Machern von <a href="http://www.unartig.ch">unArtig</a> entwickelt mit dem Ziel, den
        Sportfotoservice attraktiver zu gestalten.</p>
    <%--<p>SPORTRAIT lebt von der Initiative vieler unabhängiger Profi- und Amateurfotografen, die Fotos von Sportanlässe an Athletinnen und Athleten verkaufen.</p>--%>
    <%--    <p>SPORTRAIT wurde 2006 von den Machern von <a href="#">unArtig</a> gegründet mit dem Ziel, den Sportfotoservice in
vielerlei Hinsicht attraktiver zu gestalten.</p>--%>

</div>

<div class="rightContentHome">
    <ul class="box_A box" id="loginBoxHome">
        <li class="top"><html:img page="/images/box_A_top.gif" alt=""/></li>
        <li class="middle">
            <h2>LOGIN <span class="normalWeight">FÜR FOTOGRAFEN</span></h2>

            <tiles:insert attribute="login"/>
            <!-- Tile:Login -->

        </li>

        <li class="bottom"><html:img page="/images/box_A_bottom.gif" alt=""/></li>

        <%--   <li class="subLinks"><a href="/photographerRegister.html">Registrieren</a><span class="linkSepartor">|</span><a href="#">Passwort
        vergessen</a></li>--%>
    </ul>
    <br/>

    <%--Re-enable if sportrait is ready for photographers:--%>

    <%--<h2>F?R SPORTLER</h2>--%>

    <%--<p>Hier finden Sie Ihre Sportfotos zu fairen Preisen. <b>Ist Ihr Anlass auch dabei?</b>--%>
    <%--</p>--%>

    <%--<h2>F?R FOTOGRAFEN</h2>--%>

    <%--<p>Sportfotos effizient vermarkten in <html:link action="/photographerInfo">drei Schritten:</html:link></p>--%>
    <%--<html:link action="/photographerInfo" styleId="stepsBox">--%>
        <%--<html:img page="/images/3_steps_home.gif" alt="drei Schritte"/>--%>
    <%--</html:link>--%>
    <%----%>
    
</div>


<%--<c:forEach items="${recentEvents}" var="event" varStatus="forEachStatus">
    <li>
        <html:link
                action="/event?sportsEventId=${event.genericLevelId}">${event.eventDate}
            , ${event.location}
            , ${event.navTitle}</html:link>
    </li>
</c:forEach>--%>



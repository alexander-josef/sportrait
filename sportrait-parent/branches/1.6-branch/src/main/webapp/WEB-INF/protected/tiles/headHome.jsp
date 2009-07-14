<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<div id="headerHome">

    <tiles:insert attribute="hallo"/>

    <html:img styleId="fotostack" page="/images/fotostack.jpg" alt="fotostack_1"/>

    <div id="tabnavigationHome">

        <!-- Tile: Tabnavigation Home -->
        <ul class="tabnavigation">
            <%--@elvariable id="clientInSession" type="ch.unartig.controller.Client"--%>
            <c:if test="${clientInSession!=null}">
                <li>
                    <html:link action="/photographer/main-zul"><html:img page="/images/tabs/tab_toMyAccount_de.gif"
                                                                    alt=""/></html:link>
                </li>
            </c:if>
            <c:if test="${clientInSession!=null}">
                <li>
                    <html:link action="/photographerFaq"><html:img page="/images/tabs/tab_help_de.gif"
                                                              alt="hilfe / faq"/></html:link>
                </li>
            </c:if>
            <c:if test="${clientInSession==null}">
                <li>
                    <html:link action="/faq"><html:img page="/images/tabs/tab_help_de.gif" alt="hilfe / faq"/></html:link>
                </li>
            </c:if>
            <c:if test="${clientInSession==null}">
                <li>
                    <html:link action="/toShoppingCart"><html:img page="/images/tabs/tab_shoppingcart_de.gif"
                                                             alt=""/></html:link>
                </li>
            </c:if>
            <c:if test="${clientInSession==null}">
                <li><html:img page="/images/tabs/ON/tab_fotos_de.gif" alt="fotos"/></li>
            </c:if>
            <c:if test="${clientInSession!=null}">
                <li><html:link action="/photographer/events"><html:img page="/images/tabs/tab_events_de.gif" alt="fotos"/></html:link></li>
            </c:if>
        </ul>
        <!-- Tile-End -->

    </div>
    <html:link action="/index"><html:img styleId="logo" page="/images/logoHome.jpg" alt="logo"/></html:link>

    <h1 id="solo">sportfotos online</h1>
</div>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<!--todo: tab code duplicated over all tabs-->
<ul class="tabnavigation">
    <%--@elvariable id="clientInSession" type="ch.unartig.u_core.controller.Client"--%>
    <c:if test="${clientInSession==null}">
        <li><html:image styleId="Button1"  page="/images/tabs/tab_loginPh_de.gif" onclick="revealModal('modalPage')"/>
        </li>
    </c:if>
    <c:if test="${clientInSession!=null}">
        <li>
            <html:link action="/photographer/main-zul"><html:img page="/images/tabs/tab_toMyAccount_de.gif"
                                                            alt="mein Konto"/></html:link>
        </li>
    </c:if>
    <c:if test="${clientInSession!=null}">
        <li>
            <html:link action="/photographerFaq"><html:img page="/images/tabs/tab_help_de.gif" alt="hilfe / faq"/></html:link>
        </li>
    </c:if>
    <c:if test="${clientInSession==null}">
        <li>
            <html:link action="/faq"><html:img page="/images/tabs/tab_help_de.gif" alt="hilfe / faq"/></html:link>
        </li>
    </c:if>
    <li>
        <html:link action="/toShoppingCart"><html:img page="/images/tabs/tab_shoppingcart_de.gif"
                                                 alt="warenkorb"/></html:link>
    </li>
        <li>
            <html:link action="/showCategory"><html:img page="/images/tabs/tab_fotos_de.gif" alt="fotos"/></html:link>
        </li>
</ul>
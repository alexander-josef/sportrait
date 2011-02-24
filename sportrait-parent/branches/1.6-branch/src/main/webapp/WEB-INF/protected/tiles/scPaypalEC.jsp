<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="shoppingCart" type="ch.unartig.studioserver.beans.ShoppingCart" scope="session"/>
<html:xhtml/>

<center id="checkoutBar">
    <table id="checkoutProgress">
        <tr>
            <td>
                <html:img page="/images/buttons/Checkout_step_1_off.gif" alt="Schritt 1"/><br/>
                <html:link action="/coWizard_page1">Adresse erfassen</html:link>
            </td>
            <td class="iamhere">
                <html:img page="/images/buttons/Checkout_step_2.gif" alt="Schritt 2"/><br/>
                Zahlungsmethode wählen
            </td>
            <td>
                <html:img page="/images/buttons/Checkout_step_3_off.gif" alt="Schritt 3"/><br/>
                Bestätigen
            </td>
        </tr>
    </table>
</center>

<html:form action="/checkOutPaypalEC">

<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
        <li class="navList-content">
<%--
            <html:image  styleClass="right" page="/images/buttons/bt_next_de.gif"/>
            <span class="right">Mit einer sicheren Verbindung&nbsp;</span>
--%>
            <html:link action="/startCheckOut" styleClass="left">
                <html:img page="/images/buttons/bt_back_de.gif" alt="zurück"/>
            </html:link>
        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>

<div class="content">
<h2>Zahlung via Paypal</h2>
<%--
<logic:messagesPresent>
    <li class="contentCenter" class="errorMessage">
        <html:messages id="error" bundle="ERROR">
          <p class="errorstyle">
              <bean:write name="error" bundle="ERROR"/>
          </p>
        </html:messages>
    </li>
</logic:messagesPresent>
--%>
<logic:messagesPresent message="true">
        <p class="errorstyle"><span>BITTE BEACHTEN<br> Bei der Abwicklung traten Probleme auf:</span>
        </p>
        <html:messages id="msg" message="true" bundle="ERROR">
            <p class="errorstyle">
                <bean:write name="msg"/>
            </p>
        </html:messages>
</logic:messagesPresent>

<html:hidden property="page" value="1"/>

<logic:messagesPresent>
        <ul>
            <html:messages id="error">
                <li><p class="errorstyle">
                    <bean:write name="error"/>
                </p>
                </li>
            </html:messages>
        </ul>
</logic:messagesPresent>

<c:if test="${shoppingCart.onlyDigitalProducts}">
    <p><b>
    <%--<bean:message key="message.payment.onlyDigitalProducts" bundle="CONTENT"/><br/>--%>
        <bean:message key="payment.paypal.explanation" bundle="CONTENT"/>
    </b>
    </p>
</c:if>

<!--debug:-->
    <%--${shoppingCart.onlyDigitalProducts} --- ${shoppingCart.invoiceAvailableCountry} --- ${shoppingCart.customerCountry}--%>

<!--no digital products but also no invoice available:-->
<%--
    not relevant for paypal
--%>
<%--
<c:if test="${!shoppingCart.onlyDigitalProducts && !shoppingCart.invoiceAvailableCountry}">
    <p><b>Für die gewählte Rechnungsadresse bieten wir nur die Bezahlung per Kreditkarte an.</b>
    </p>
</c:if>
--%>
<%--

<table class="form">
    <tr>
        <td colspan="2"><p><i>Mit einem
            <span class="must">*</span> versehene Felder müssen ausgefüllt werden.</i></p><br/></td>
    </tr>
    <!--Invoice is possible for non-digital orders and if the country supports invoicing-->
    <c:if test="${!shoppingCart.onlyDigitalProducts && shoppingCart.invoiceAvailableCountry}">

        <tr>
            <td>
                <html:radio property="paymentMethod" value="invoice" styleId="paymentMethodInvoice"
                            errorStyleClass="inputError"/>
            </td>
            <td><label for="paymentMethodInvoice">Rechnung</label></td>
        </tr>
    </c:if>

    <tr>
        <td>
            <html:radio property="paymentMethod" value="creditCard" styleId="paymentMethodCreditCard"
                        onchange="windowShow('billingCmustitdata')" errorStyleClass="inputError"/>

        </td>
        <td><label for="paymentMethodCreditCard">Kreditkarte</label></td>
    </tr>
</table>
--%>

<br/>
<table class="form">
    <tr>
        <td class="formLead">
            <html:image bundle="IMAGES" srcKey="btn.paypal.expresscheckout" altKey="btn.paypal.expresscheckout.alt"/>
        </td>
    </tr>
</table>
</div>

<div class="content">
<b>Eine Buchung wird erst mit dem Abschicken der Bestellung auf der letzten Seite des
    Bestellvorganges vorgenommen.</b>

</div>


<div class="content">

    <p class="leftclear leftalign comment">Die Daten werden über eine
        <span style="font-weight:bold;">verschlüsselte Verbindung</span> übertragen. Wir behandeln Ihre persönlichen
        Daten mit grösster Sorgfalt. Lesen Sie
        dazu auch unsere
        <html:link action="/privacy" target="_blank">Datenschutzbestimmungen</html:link>
        .</p>
</div>

</html:form>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="shoppingCart" type="ch.unartig.u_core.beans.ShoppingCart" scope="session"/>
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

<html:form action="/checkOutOverview">

<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
        <li class="navList-content">
            <html:image  styleClass="right" page="/images/buttons/bt_next_de.gif"/>
            <span class="right">Mit einer sicheren Verbindung&nbsp;</span>
            <html:link action="/startCheckOut" styleClass="left">
                <html:img page="/images/buttons/bt_back_de.gif" alt="zurück"/>
            </html:link>
        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>

<div class="content">
<h2>Zahlungsmethode wählen</h2>
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
    <p><b> <%--<bean:message key="message.payment.onlyDigitalProducts" bundle="CONTENT"/><br/>--%>
        Alle Ihre Produkte sind Digitalbilder. Für "Downloadprodukte" bieten wir nur die Zahlung via Kreditkarte an.</b>
    </p>
</c:if>

<!--debug:-->
    <%--${shoppingCart.onlyDigitalProducts} --- ${shoppingCart.invoiceAvailableCountry} --- ${shoppingCart.customerCountry}--%>

<!--no digital products but also no invoice available:-->
<c:if test="${!shoppingCart.onlyDigitalProducts && !shoppingCart.invoiceAvailableCountry}">
    <p><b>Für die gewählte Rechnungsadresse bieten wir nur die Bezahlung per Kreditkarte an.</b>
    </p>
</c:if>

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
<br/>
<table class="form">
    <tr>
        <td class="formLead"><label for="creditCardType">Karte:</label><span class="must">*</span></td>
        <td colspan="2">
            <html:select property="creditCardType" styleId="creditCardType">
                <html:option value="visa">Visa</html:option>
                <html:option value="masterCard">Master Card</html:option>
            </html:select>

        </td>
    </tr>
    <tr>
        <td class="formLead"><label for="creditCardNumber">Kartennummer:</label><span class="must">*</span></td>
        <td>
            <html:text property="creditCardNumber" styleId="creditCardNumber"
                       errorStyleClass="inputError" style="width:200px"/>
        </td>
        <td>&nbsp;<i>(keine Leer- oder Trennzeichen)</i></td>
    </tr>
    <tr>
        <td class="formLead">Gültig bis:<span class="must">*</span></td>
        <td><label for="creditCardExpiryMonth">Monat:</label>
            <html:select property="creditCardExpiryMonth" styleId="creditCardExpiryMonth" errorStyleClass="inputError">
                <html:option value="**"/>
                <html:option value="01"/>
                <html:option value="02"/>
                <html:option value="03"/>
                <html:option value="04"/>
                <html:option value="05"/>
                <html:option value="06"/>
                <html:option value="07"/>
                <html:option value="09"/>
                <html:option value="08"/>
                <html:option value="09"/>
                <html:option value="10"/>
                <html:option value="11"/>
                <html:option value="12"/>
            </html:select>
        </td>
        <td>
            <label for="creditCardExpiryYear">Jahr:</label>
            <html:select property="creditCardExpiryYear" styleId="creditCardExpiryYear" errorStyleClass="inputError">
                <html:option value="****"/>
                <html:option value="2007"/>
                <html:option value="2008"/>
                <html:option value="2009"/>
                <html:option value="2010"/>
                <html:option value="2011"/>
                <html:option value="2012"/>
                <html:option value="2013"/>
                <html:option value="2014"/>
                <html:option value="2015"/>
                <html:option value="2016"/>
                <html:option value="2017"/>
                <html:option value="2018"/>
            </html:select>
        </td>
    </tr>
    <tr>
        <td class="formLead"><label for="creditCardHolderName">Karteninhaber:<span class="must">*</span></label></td>
        <td colspan="2">
            <html:text styleId="creditCardHolderName" property="creditCardHolderName" style="width:200px"/>
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

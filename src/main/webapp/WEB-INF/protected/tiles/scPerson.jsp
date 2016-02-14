<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="shoppingCart" type="ch.unartig.studioserver.beans.ShoppingCart" scope="session"/>
<html:xhtml/>

<!--Form must enclose all input elements! -->
<html:form action="/checkOutBillingMethod">

<%--suppress HtmlDeprecatedTag --%>
<%--suppress HtmlDeprecatedTag --%>
    <center id="checkoutBar">
    <table id="checkoutProgress">
        <tr>
            <td class="iamhere">
                <html:img page="/images/buttons/Checkout_step_1.gif" alt="Schritt 1"/><br/>
                Adresse erfassen
            </td>
            <td class="undone">
                <html:img page="/images/buttons/Checkout_step_2_off.gif" alt="Schritt 2"/><br/>
                Zahlungsmethode wählen
            </td>
            <td class="undone">
                <html:img page="/images/buttons/Checkout_step_3_off.gif" alt="Schritt 3"/><br/>
                Bestätigen
            </td>
        </tr>
    </table>

</center>


<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
        <li class="navList-content">
            <html:image styleClass="right" page="/images/buttons/bt_next_de.gif"/>
            <html:link action="/toShoppingCart" styleClass="left">
                <html:img page="/images/buttons/bt_back_de.gif" alt="zur�ck"/>
            </html:link>
        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>

<div class="content">
<h2>Adresse erfassen</h2>


<logic:messagesPresent>
    <html:messages id="error" bundle="ERROR">
        <p class="errorstyle">
            <bean:write name="error" bundle="ERROR"/>
        </p>
    </html:messages>
</logic:messagesPresent>

    <%--<logic:messagesPresent message="true">
            <p class="errorstyle"><span>BITTE BEACHTEN<br> Bei der Abwicklung traten Probleme auf:</span>
            </p>
            <html:messages id="msg" message="true" bundle="ERROR">
                <p class="errorstyle">
                    <bean:write name="msg"/>
                </p>
            </html:messages>
    </logic:messagesPresent>--%>

<html:hidden property="page" value="0"/>

    <%--<logic:messagesPresent>
            <ul>
                <html:messages id="error">
                    <li><p class="errorstyle">
                        <bean:write name="error"/>
                    </p>
                    </li>
                </html:messages>
            </ul>
    </logic:messagesPresent>--%>


<p><i>Mit einem
    <span class="must">*</span> versehene Felder müssen ausgefüllt werden.</i>
</p>
<table class="form">
    <tr>
        <td class="formLead">E-Mail<span class="must">*</span>:</td>
        <td>
            <!--<input name="email" type="text" style=""/>-->
            <html:text property="email" style="width: 190px" errorStyleClass="inputError"/>
        </td>
    </tr>
    <tr>
        <td class="formLead">E-Mail wiederholen<span class="must">*</span>:</td>
        <td class="formLead">
            <!--<input name="email" type="text" style=""/>-->
            <html:text property="emailConfirmation" style="width: 190px" errorStyleClass="inputError"/>
        </td>
    </tr>
    <tr>
        <td class="formLead">Vorname<span class="must">*</span>:</td>
        <td>
                <%--<input name="firstName" type="text" style="width: 190px"/>--%>
            <html:text property="firstName" style="width: 190px" errorStyleClass="inputError"/>
        </td>
    </tr>
    <tr>
        <td class="formLead">Nachname<span class="must">*</span>:</td>
            <%--<td><input name="lastName" type="text" style="width: 190px"/></td>--%>
        <td>
                <%--<input name="firstName" type="text" style="width: 190px"/>--%>
            <html:text property="lastName" style="width: 190px" errorStyleClass="inputError"/>
        </td>

    </tr>
    <tr>
        <td class="formLead">Adresse 1<span class="must">*</span>:</td>
            <%--<td><input name="address1" type="text" style="width: 190px"/></td>--%>
        <td>
                <%--<input name="firstName" type="text" style="width: 190px"/>--%>
            <html:text property="addr1" style="width: 190px" errorStyleClass="inputError"/>
        </td>

    </tr>
    <tr>
        <td class="formLead">Adresse 2:</td>
            <%--<td><input name="address2" type="text" style="width: 190px"/></td>--%>
        <td>
                <%--<input name="firstName" type="text" style="width: 190px"/>--%>
            <html:text property="addr2" style="width: 190px" errorStyleClass="inputError"/>
        </td>

    </tr>
    <tr>
        <td class="formLead">PLZ / Ort<span class="must">*</span>:</td>
            <%--<td>--%> <%--<input name="plz" type="text" style="width: 80px"/>--%> <%--<input name="city" type="text" style="width: 100px"/>--%> <%--</td>--%>
        <td>
                <%--<input name="firstName" type="text" style="width: 190px"/>--%>
            <html:text property="zipCode" style="width: 80px" errorStyleClass="inputError"/>
            <html:text property="city" style="width: 100px" errorStyleClass="inputError"/>
        </td>

    </tr>
    <tr>
        <td class="formLead">Land<span class="must">*</span>:</td>
        <td>


            <html:select property="country">
                <html:option value="CHE">
                    Schweiz<%--<bean:message bundle="CONTENT" key="country.switzerland"/>--%>
                </html:option>
                <!--plz 4 stellig-->
                <html:option value="DEU">
                    Deutschland<%--<bean:message bundle="CONTENT" key="country.germany"/>--%>
                </html:option>
                <html:option value="AUT">
                    Österreich
                </html:option>
            </html:select>

        </td>
    </tr>
</table>
</div>


<div class="content">
    <p class="leftclear leftalign comment">Die Daten werden über eine
        verschlüsselte Verbindung übertragen. Wir behandeln Ihre persönlichen Daten mit grösster Sorgfalt. Lesen Sie
        dazu auch unsere
        <html:link action="/privacy" target="_blank">Datenschutzbestimmungen</html:link>
        .</p>
</div>

</html:form>
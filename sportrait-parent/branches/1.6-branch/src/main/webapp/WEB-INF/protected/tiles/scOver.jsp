<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:useBean id="shoppingCart" type="ch.unartig.studioserver.beans.ShoppingCart" scope="session"/>
<jsp:useBean id="checkOutForm" type="ch.unartig.studioserver.beans.CheckOutForm" scope="session"/>
<html:xhtml/>

<%--script for preventing double-submissions : --%>
<script language="Javascript" type="text/javascript">
    <!--
    var isSubmitted = false;
    function confirmOrder()
    {
        if (isSubmitted)
        {
            return false;
        }
        else
        {
            isSubmitted = true;
            return true;
        }
    }
    //-->
</script>


<center id="checkoutBar">
    <table id="checkoutProgress">
        <tr>
            <td>
                <html:img page="/images/buttons/Checkout_step_1_off.gif" alt="Schritt 1"/><br/>
                <html:link action="/coWizard_page1">Adresse erfassen</html:link>
            </td>
            <td>
                <html:img page="/images/buttons/Checkout_step_2_off.gif" alt="Schritt 2"/><br/>
                <html:link action="/coWizard_page3">Zahlungsmethode wählen</html:link>
            </td>
            <td class="iamhere">
                <html:img page="/images/buttons/Checkout_step_3.gif" alt="Schritt 3"/><br/>
                Bestätigen
            </td>
        </tr>
    </table>
</center>

<logic:messagesPresent message="true">
    <div class="content">
        <ul>
            <html:messages id="error" bundle="ERROR" message="true">
                <li><p class="errorstyle">
                    <bean:write name="error" bundle="ERROR"/>
                </p></li>
            </html:messages>
        </ul>
    </div>
</logic:messagesPresent>

<logic:messagesPresent>
    <div class="content">
        <ul>
            <html:messages id="error" bundle="ERROR">
                <li><p class="errorstyle">
                    <bean:write name="error" bundle="ERROR"/>
                </p></li>
            </html:messages>
        </ul>
    </div>
</logic:messagesPresent>

<html:form action="/checkOutConfirm" onsubmit="return(confirmOrder());">
<html:hidden property="page" value="1"/>

<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
        <li class="navList-content">
            <html:image  styleClass="right" page="/images/buttons/bt_sendOrder_de.gif"/>
            <span class="right">Ich akzeptiere die <html:link action="/agb" target="_blank">allgemeinen
                Geschäftsbedingungen
            </html:link>.
            <html:checkbox errorStyleClass="inputError" property="acceptTermsCondition" value="true"/>&nbsp;</span>
            <html:link action="/checkOutBillingMethod" styleClass="left">
                <html:img page="/images/buttons/bt_back_de.gif" alt="zurück"/>
            </html:link>
        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>

<div class="content">
    <div class="modalitySc">
        <p><span class="bold">Rechnungs- und Zustelladresse: </span>
            <html:link action="/coWizard_page1">..ändern</html:link>
            <br/>
                ${checkOutForm.firstName} ${checkOutForm.lastName}<br/>
                ${checkOutForm.addr1}<br/>
            <c:if test="${ ! empty checkOutForm.addr2}">
                ${checkOutForm.addr2}<br/>
            </c:if>
                ${checkOutForm.zipCode} ${checkOutForm.city}<br/>
                ${checkOutForm.country}<br/>
                E-Mail: ${checkOutForm.email}
        </p>
    </div>
    <div class="modalitySc">
        <p><span class="bold">Zahlungsmethode:</span>
            <html:link action="/coWizard_page3">..ändern</html:link>
            <br/>
            <c:if test="${checkOutForm.paymentMethodInvoice}">Rechnung</c:if>
            <c:if test="${checkOutForm.paymentMethodCreditCard}">Kreditkarte<br/>
                Karteninhaber : ${checkOutForm.creditCardHolderName}<br/>
                Kartennummer : ${checkOutForm.obfuscatedCreditCardNumber}
            </c:if>
        </p>
    </div>
</div>

<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
        <li class="navList-content">
            <table class="scTableDescription bold">
                <tr>
                    <td class="scFirstcol">
                        Artikel
                    </td>
                    <td class="scSecondcol">
                        Format / Produkt
                    </td>
                    <td class="scThirdcol">
                        Menge
                    </td>
                    <td class="scFourthcol">
                        &nbsp;
                    </td>
                    <td class="scFifthcol">
                        Preis
                    </td>
                </tr>
            </table>
        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>

<!-- start loop foto -->
<logic:iterate name="shoppingCart" property="scItemsForConfirmation" id="scItem" indexId="scItemIndex">
    <bean:define id="product" name="shoppingCart" property="productMapped(${scItem.productId})" toScope="page"/>

    <%-- IF photo changed:  --%>
    <c:if test="${scItem.photoId != lastPhotoId}">

        <!--we are not at start and a new photo began ... first, finish of last photo if necessary-->
        <c:if test="${scItemIndex != 0}">
            <%--@elvariable id="photo" type="ch.unartig.studioserver.model.Photo"--%>
            <!--end of photo if-->
            <table class="scTableFormats">
                <tr>
                    <td class="leftalign" colspan="4">
                            <%--
                             <c:if test="${!empty photo.photo.photographer}">
                            © by Fotograf: &nbsp;${photo.displayPhoto.photographer.fullName}&nbsp;--&nbsp;
                            </c:if>
                            --%>
                        <p style="width:743px;padding:1px 0;margin-bottom:10px;"> © by Fotograf: <span class="bold">SPORTRAIT</span>
                            --
                            Filename: <span class="bold">${photo.displayTitle}</span></p>
                    </td>
                </tr>
            </table>
        </c:if>

        <div class="content">

        <%-- create page variable 'photo' --%>
        <bean:define id="photo" name="shoppingCart" property="photoMapped(${scItem.photoId})" toScope="page"/>


        <ul class="slide sliceSc margTB10">
            <li class="slideTop"></li>
            <li class="slideImage">
                <!--<a href="display.html">-->
                <html:img styleClass="${photo.orientationSuffix}" src="${photo.thumbnailUrl}"
                          title="${photo.displayTitle}" alt="${photo.displayTitle}"/>
                <!--</a>-->
            </li>
            <li class="slideBottom"></li>
        </ul>
    </c:if>

    <!-- start loop format -->
    <table class="scTableFormats margTB3">
        <tr>
            <td class="scSecondcol"><p>${product.productType.name}</p></td>
            <td class="scThirdcol"><p>${scItem.quantity}</p></td>
            <td class="scFourthcol"><p></p></td>
            <td class="scFifthcol"><p>${scItem.price} &nbsp;${shoppingCart.currency}</p></td>
        </tr>
    </table>
    <!-- end loop format -->

    <bean:define id="lastPhotoId" value="${scItem.photoId}" toScope="page"/>

</logic:iterate>
<!-- end loop foto -->
<%-- closing element at the end of each photo for the last element in the loop : --%>
<table class="scTableFormats">
    <tr>
        <td class="scSecondcol leftalign">
            &nbsp;
            <!--<p class="left">Anbieter: <span class="bold">Mathias Mühlemann</span></p>-->
        </td>
    </tr>
</table>

</div>

<div class="content">
    <table class="scTableDescription">
        <tr class="bold">
            <td class="scFirstcol" colspan="3">Subtotal</td>
            <td class="scFourthcol"></td>
            <td class="scFifthcol">${shoppingCart.formattedSubtotalPhotos} &nbsp;${shoppingCart.currency}</td>
        </tr>
        <tr>
            <td class="scSecondcol"></td>
            <td class="scThirdcol"></td>
            <td class="scFourthcol"></td>
            <td class="scFifthcol"></td>
        </tr>
            <%--
                    <tr>
                        <td class="scFirstcol" colspan="3"><p>Bilder</p></td>
                        <td class="scFourthcol"><p></p></td>
                        <td class="scFifthcol"><p>${shoppingCart.formattedSubtotalPhotos} &nbsp;${shoppingCart.currency}</p></td>
                    </tr>
            --%>
        <tr>
            <td class="scFirstcol" colspan="3">Versandkosten</td>
            <td class="scFourthcol"></td>
            <td class="scFifthcol">${shoppingCart.formattedShippingPrice} &nbsp;${shoppingCart.currency}</td>
        </tr>

    </table>
</div>

<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
        <li class="navList-content">

            <table class="scTableDescription">
                <tr class="bold emphasize">
                    <td class="scFirstcol">TOTAL</td>
                    <td class="scSecondcol" colspan="2">MWST inbegriffen.</td>
                    <td class="scFourthcol"></td>
                    <td class="scFifthcol">${shoppingCart.formattedTotal} &nbsp;${shoppingCart.currency}</td>
                </tr>
            </table>

        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>
<div class="content">
    <!-- This button must not be an input type="submit" ... this sends the form to the action. we only need an onclick handler -->
    <%--<button type="button" onclick="window.print()"><html:img class="right" src="/images/buttons/bt_printOrder_de.gif" alt="Bestellung drucken"></button>--%>

    <%--<input type="button" class="right" src="/images/buttons/bt_printOrder_de.gif" onclick="window.print()"/>--%>
    <html:img page="/images/buttons/bt_printOrder_de.gif" styleClass="right"  alt="Bestellung drucken" onclick="window.print()"/>
</div>


</html:form>
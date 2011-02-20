<%@ page import="ch.unartig.studioserver.Registry" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="shoppingCart" type="ch.unartig.studioserver.beans.ShoppingCart" scope="session"/>

<html:form action="/updateShoppingCart">

<div class="pageNav">
    <h1 id="pageName">Warenkorb</h1>
    <logic:messagesPresent message="true">
        <div class="contentHome errorMessage">
            <html:messages id="msg" message="true" bundle="ERROR">
                <p class="errorstyle">
                    <bean:write name="msg"/>
                </p>
            </html:messages>
        </div>
    </logic:messagesPresent>

</div>

<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
        <li class="navList-content">
            <html:image styleClass="right" page="/images/buttons/bt_checkout_de.gif"
                   onclick="postSimpleForm('checkOut',0)"/>
            <span class="right">Mit einer sicheren Verbindung&nbsp;</span>

            <!--todo duplizierter code, siehe ende dieses files-->

        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>

<div class="content">
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
                Preis (SFr.)
            </td>
<%--
            <td class="scFifthcol">
                Preis (Euro)
            </td>
--%>
        </tr>
    </table>
</div>

<!-- start loop foto -->
<logic:iterate name="shoppingCart" property="scItems" id="scItem" indexId="scItemIndex">
    <%-- IF index 0 oder teilbar durch 4: create index photo --%>
    <c:if test="${scItemIndex % 4 == 0}">

        <div class="content">
        <%-- create page variable 'photo' --%>
        <bean:define id="photo" name="shoppingCart" property="photoMapped(${scItem.photoId})" toScope="page"/>
        <bean:define id="album" name="photo" property="album" toScope="page"/>


        <ul class="slide sliceSc">
            <li class="slideTop"></li>
            <li class="slideImage">
                <!--<a href="display.html">-->
                <html:img styleClass="${photo.orientationSuffix}" src="${photo.thumbnailUrl}"
                          title="${photo.displayTitle}"/>
                <!--</a>-->

            </li>
            <li class="slideBottom">
                <html:link action="/removeFromCart" paramId="<%=Registry._NAME_ORDERED_PHOTO_ID_PARAM%>"
                           paramName="scItem" paramProperty="photoId">
                    lšschen
                </html:link>
            </li>
        </ul>
    </c:if>

    <!-- start loop format -->
    <table class="scTableFormats margTB3">
        <tr>
            <td class="scSecondcol">
                <html:select styleClass="left" name="scItem" property="productId" indexed="true"
                             onchange="postSimpleForm('update',0)">
                    <html:option value="-1">
                        <%--<bean:message bundle="BUTTONS" key="product.choose"/>--%>Format/Produkt wählen
                    </html:option>
                    <html:optionsCollection name="album" property="products" label="productType.name"
                                            value="productId"/>
                </html:select>
                <c:if test="${!scItem.digitalOrderItem}">

                    <%--<html:link action="/explainDigital"
                               onclick="PriceList=window.open('/explainDigital.html','Preisliste','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,width=400px,height=450px,left=200px,top=100px'); PriceList.focus(); return false;">
                        <html:img page="/images/questionmark.gif" alt="help"/>
                    </html:link>--%>

                </c:if>
            </td>
            <td class="scThirdcol">
                <!-- pre-set quantity to one for first item is done in shopping cart business logic-->
                <c:if test="${scItem.digitalOrderItem}">
                    <html:text name="scItem" property="quantity" indexed="true" size="3" disabled="true" value="1"/>
                </c:if>
                <c:if test="${!scItem.digitalOrderItem}">
                    <html:text name="scItem" property="quantity" indexed="true" size="3"/>
                </c:if>
            </td>
            <!--<td class="scFourthcol"><p>16.00 (SFr.) </p></td>-->
            <td class="scFourthcol">
                    ${scItem.formattedItemPriceCHF} (SFr.)

<%--
            </td>
            <!--<td class="scFifthcol"><p>10.00 (Euro) </p></td>-->
            <td class="scFifthcol">
                    ${scItem.formattedItemPriceEUR} (Euro)
            </td>
--%>

        </tr>
    </table>
    <!-- end loop format -->

    <%-- END Loop order items --%>

    <%--IF INDEX modulo 4 = 3   (end of sc-items loop for one photo reached)  --%>
    <c:if test="${scItemIndex % 4 == 3}">


        <table class="scTableFormats">
            <tr>
                <td class="leftalign" colspan="4">
                        <%-- <c:if test="${!empty photo.photo.photographer}">
                        ? by Fotograf: &nbsp;${photo.displayPhoto.photographer.fullName}&nbsp;--&nbsp;
                        </c:if>
                        --%>
                    <p style="width:743px;padding:1px 0;margin-bottom:10px;"> © by Fotograf: <span class="bold">unartig AG</span>
                        --
                        Filename: <span class="bold">${photo.displayTitle}</span></p>
                </td>
            </tr>
        </table>

        </div>

    </c:if>
</logic:iterate>
<!-- end loop foto -->
<div class="content" style="text-align:right;">
    <html:image styleClass="btRight" page="/images/buttons/bt_recalculate_de.gif"
           onclick="postSimpleForm('update',0)"/>
</div>

<div class="content">
    <table class="scTableDescription">
        <tr class="bold">
            <td class="scFirstcol" colspan="3">Subtotal</td>
            <td class="scFourthcol">${shoppingCart.formattedSubtotalPhotosCHF} (SFr.)</td>
<%--
            <td class="scFifthcol">${shoppingCart.formattedSubtotalPhotosEUR} (Euro)</td>
--%>
        </tr>
        <tr>
            <td class="scSecondcol"></td>
            <td class="scThirdcol"></td>
            <td class="scFourthcol"></td>
<%--
            <td class="scFifthcol"></td>
--%>
        </tr>
        <tr>
            <td class="scFirstcol" colspan="3">Versandkosten</td>
            <td class="scFourthcol">${shoppingCart.formattedShippingCHE} (SFr.)</td>
<%--
            <td class="scFifthcol">${shoppingCart.formattedShippingInternationalEUR} (Euro)</td>
--%>
        </tr>
    </table>
</div>

<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
        <li class="navList-content">

            <table class="scTableDescription">
                <tr class="bold">
                    <td class="scFirstcol">TOTAL</td>
                    <td class="scSecondcol normal">MWST inbegriffen</td>
                    <td class="scThirdcol"></td>
                    <td class="scFourthcol">${shoppingCart.formattedTotalCHE} (SFr.)</td>
<%--
                    <td class="scFifthcol">${shoppingCart.formattedTotalEUR} (Euro)</td>
--%>
                </tr>
            </table>

        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>

<div class="content">
    <b>Lieferfristen:</b><br/>
<%--
    2-3 Tage fŸr PapierabzŸge; bis 10 Tage fŸr Poster; Digitale Produkte werden unverzŸglich per E-Mail zugestellt.
--%>
    Nach der Bezahlung mit Kreditkarte kšnnen die Dateien unmittelbar heruntergeladen werden.
</div>
<!-- set default actionParam to checkOut to support non js browser -->
<html:hidden property="actionParam" value="checkOut"/>

</html:form>
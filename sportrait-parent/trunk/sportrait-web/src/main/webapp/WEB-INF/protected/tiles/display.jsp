<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="display" type="ch.unartig.studioserver.beans.DisplayBean" scope="request"/>
<html:xhtml/>
<%--
IMPORTANT: albumBean is a misleading name: it means the bean that supports the thumbnail-overview.
There may be pictures from many albums in the thumbnailOverview
todo refactor name albumBean as fast as possible
--%>
<jsp:useBean id="albumBean" type="ch.unartig.studioserver.beans.SportsAlbumBean" scope="session"/>

<div class="pageNav">
    <ul id="fotoNav">
        <li>
            <html:link action="/showCategory" styleClass="toAlbum">
                zur &Uuml;bersicht
            </html:link>
        </li>
    </ul>
    <h1 id="pageName">${albumBean.sportsEvent.longTitle}</h1>
</div>


<div class="pageNav">
    <ul>
        <li class="navList-top"/>
        <li class="navList-content">

            <html:form action="/placeInCartPreview" target="shoppingcart_frame">
                <table id="displaySelectionBar">
                    <tr>
                        <td class="leftalign" id="fotoInfoDisplay">
                            Zeit:&nbsp;<b>${display.displayPhoto.shortTimeString}</b>&nbsp;&nbsp;&nbsp;Foto:&nbsp;<b>${display.displayPhoto.filename}</b>
                        </td>

                        <td>
                            <table id="formatSelectDisplay">
                                <tr>
                                    <td class="rightalign">Format:</td>
                                    <td class="leftalign">
                                        <!-- Dynamic List of products: -->

                                        <select name="orderedProductId">
                                            <option value="-1">Format/Produkt w&auml;hlen :</option>

                                            <!-- Start digital products -->
                                            <!--todo : refactor the getAlbumProducts in many methods: one for each product category-->
                                            <c:forEach items="${display.albumFromPhoto.products}" var="product"
                                                       varStatus="forEachStatus">
                                                <c:if test="${product.digitalProduct}">
                                                    <option value="${product.productId}" ${display.defaultProductId==product.productId?' selected=selected':' '}>
                                                            ${product.productType.name}
                                                        - ${product.formattedPriceCHF} CHF
                                                        / ${product.formattedPriceEUR} EUR
                                                    </option>
                                                </c:if>
                                            </c:forEach>

                                            <!-- And then paper products -->
                                            <c:forEach items="${display.albumFromPhoto.products}" var="product"
                                                       varStatus="forEachStatus">
                                                <c:if test="${!product.digitalProduct}">
                                                    <option value="${product.productId}" ${display.defaultProductId==product.productId?' selected=selected':' '}>
                                                            ${product.productType.name} - ${product.formattedPriceCHF} CHF
                                                                / ${product.formattedPriceEUR} EUR
                                                    </option>
                                                </c:if>
                                            </c:forEach>

                                        </select>
                                    </td>
                                    <td class="rightalign">
                                        <input type="hidden" name="orderedPhotoId" value="${display.displayPhoto.photoId}"/>
                                        <html:image alt="" page="/images/buttons/bt_inShoppingcart_de.gif"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </html:form>

        </li>
        <li class="navList-bottom"/>
    </ul>
</div>

<div class="content">
<table id="displayContainer">
<tr>
<td class="displayPreview">

    <c:if test="${ ! empty display.previousPhoto}">
        <ul class="slide">
            <li class="slideTop"/>
            <li class="slideImage">
                <html:link action="/display/${display.previousPhoto.photoId}/display.html" name="display"
                           property="previousPhotoLinkParams" title="vorheriges Foto">
                    <img class="${display.previousPhoto.orientationSuffix}" src="${display.previousPhoto.thumbnailUrl}"
                         alt="vorheriges Foto"/>
                </html:link>
            </li>
            <li class="slideBottom">
                <html:link action="/display/${display.previousPhoto.photoId}/display.html" name="display"
                           property="previousPhotoLinkParams" title="vorheriges Foto">zurück
                </html:link>
            </li>
        </ul>
    </c:if>

</td>
<td id="displayCenter">

    <table>
        <tr>
            <th class="frameTopleft"/>
            <th class="frameTop">

            </th>
            <th class="frameTopright"/>
        </tr>
        <tr>
            <td class="frameLeft"/>
            <td id="displayImageholder_${display.displayPhoto.orientationSuffix}">
                <img src="${display.displayPhoto.displayUrl}"
                     alt="${display.displayPhoto.filename} -- ${display.displayPhoto.displayUrl}"/>
            </td>
            <td class="frameRight"/>
        </tr>
        <tr>
            <th class="frameBottomleft"/>
            <th class="frameBottom"/>
            <th class="frameBottomright"/>
        </tr>
        <tr>
            <td colspan="3" id="filename">
                <c:if test="${!empty display.displayPhoto.photographer}">
                    © by Fotograf: &nbsp;${display.displayPhoto.photographer.fullName} <br/>
                </c:if>
                ${display.displayPhoto.filename}&nbsp;--&nbsp;${display.displayPhoto.shortTimeString}</td>
        </tr>
    </table>

</td>

<!-- Preview of the Shopping Cart -->
<td class="displayPreview">

    <c:if test="${ ! empty display.nextPhoto}">
        <ul class="slide right">
            <li class="slideTop"/>
            <li class="slideImage">
                <html:link action="/display/${display.nextPhoto.photoId}/display.html" name="display"
                           property="nextPhotoLinkParams" title="nächstes Foto"><img
                        class="${display.nextPhoto.orientationSuffix}" src="${display.nextPhoto.thumbnailUrl}"
                        alt="nächstes Foto"/></html:link>
            </li>
            <li class="slideBottom">
                <html:link action="/display/${display.nextPhoto.photoId}/display.html" name="display"
                           property="nextPhotoLinkParams" title="nächstes Foto">weiter
                </html:link>
            </li>
        </ul>
    </c:if>

</td>
<td id="displaySpacer"/>
<td id="displayShop">


<ul class="box_B box">
    <li class="top"><html:img page="/images/box_B_top_de.gif" alt=""/></li>
    <li class="middle">

        <iframe src="<html:rewrite action="/shoppingCartPreviewFrame"/>" id="shoppingcartPreview" name="shoppingcart_frame">
            <p>Ihr Browser kann leider keine eingebetteten Frames anzeigen: Sie k&ouml;nnen aber direkt zum
                Einkaufswagen gehen:
                <html:link action="/toShoppingCart">zum Einkaufswagen</html:link>
            </p>
        </iframe>

    </li>
    <li class="bottom"><html:img page="/images/box_B_bottom.gif" alt=""/></li>
</ul>


</td>
</tr>
</table>
</div>
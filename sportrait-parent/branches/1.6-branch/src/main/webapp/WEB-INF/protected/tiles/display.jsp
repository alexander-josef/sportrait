<%@ page contentType="text/html; charset=UTF-8" %>
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
                zur Übersicht
            </html:link>
        </li>
    </ul>
    <h1 id="pageName">${albumBean.sportsEvent.longTitle}</h1>

    <h2 id="eventCategory">${albumBean.eventCategory.title}</h2>
</div>


<div class="pageNav">
    <ul>
        <li class="navList-top"></li>
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

                                        <%-- free download on display page? --%>
                                    <c:if test="${display.albumFromPhoto.hasFreeHighResDownload}">
                                        <td class="rightalign">

                                            <html:link action="/downloadPhoto?photoId=${display.displayPhotoId}"
                                                       title="BILD HERUNTERLADEN -- Datei wird nur als gratis Download angeboten"
                                                       onclick="_gaq.push(['_trackEvent', '${display.albumFromPhoto.event.longTitle} / ${display.albumFromPhoto.longTitle}', 'download_free_highres', 'album_ID', ${display.albumFromPhoto.genericLevelId}]);">
                                                <html:img page="/images/buttons/bt_download_picture_de.gif" />
                                            </html:link>

                                        </td>
                                    </c:if>

                                    <c:if test="${!display.albumFromPhoto.hasFreeHighResDownload}">

                                        <td class="rightalign">Produkt:</td>
                                        <td class="leftalign">
                                            <!-- Dynamic List of products: -->

                                            <select name="orderedProductId">
                                                <option value="-1">Format/Produkt wählen :</option>

                                                <!-- Start digital products -->
                                                <!--todo : refactor the getAlbumProducts in many methods: one for each product category-->
                                                <c:forEach items="${display.albumFromPhoto.activeProducts}"
                                                           var="product"
                                                           varStatus="forEachStatus">
                                                    <c:if test="${product.digitalProduct}">
                                                        <option value="${product.productId}" ${display.defaultProductId==product.productId?' selected=selected':' '}>
                                                                ${product.productType.name}
                                                            - ${product.formattedPriceCHF} CHF
                                                                <%--
                                                                                                                        / ${product.formattedPriceEUR} EUR
                                                                --%>
                                                        </option>
                                                    </c:if>
                                                </c:forEach>

                                                <!-- And then paper products -->
                                                <c:forEach items="${display.albumFromPhoto.activeProducts}"
                                                           var="product"
                                                           varStatus="forEachStatus">
                                                    <c:if test="${!product.digitalProduct}">
                                                        <option value="${product.productId}" ${display.defaultProductId==product.productId?' selected=selected':' '}>
                                                                ${product.productType.name}
                                                            - ${product.formattedPriceCHF} CHF
                                                                <%--
                                                                                                                                / ${product.formattedPriceEUR} EUR
                                                                --%>
                                                        </option>
                                                    </c:if>
                                                </c:forEach>

                                            </select>
                                        </td>
                                        <td class="rightalign">
                                            <input type="hidden" name="orderedPhotoId"
                                                   value="${display.displayPhoto.photoId}"/>
                                            <html:image alt="" page="/images/buttons/bt_inShoppingcart_de.gif"/>
                                        </td>
                                    </c:if>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </html:form>

        </li>
        <li class="navList-bottom"></li>
    </ul>
</div>

<div class="content">
    <table id="displayContainer">
        <tr>
            <td class="displayPreview">

                <c:if test="${ ! empty display.previousPhoto}">
                    <ul class="slide">
                        <li class="slideTop"></li>
                        <li class="slideImage">
                            <html:link action="/display/${display.previousPhoto.photoId}/display.html" name="display"
                                       property="previousPhotoLinkParams" title="vorheriges Foto">
                                <img class="${display.previousPhoto.orientationSuffix}"
                                     src="${display.previousPhoto.thumbnailUrl}"
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
                        <th class="frameTopleft"></th>
                        <th class="frameTop">

                        </th>
                        <th class="frameTopright"></th>
                    </tr>
                    <tr>
                        <td class="frameLeft"></td>
                        <td id="displayImageholder_${display.displayPhoto.orientationSuffix}">
                            <c:if test="${clientInSession!=null}">
                                <div>
                                    <html:link action="/admin/deleteDisplayPhoto?photoId=${display.displayPhotoId}&nextPhotoId=${display.nextPhoto.photoId}"  >
                                        delete
                                    </html:link>
                                </div>
                            </c:if>

                            <img src="${display.displayPhoto.displayUrl}"
                                 alt="${display.displayPhoto.filename}"/>
                            <c:if test="${clientInSession!=null}">
                                <div>
                                    <html:link action="/admin/deletePhoto?photoId=${display.displayPhotoId}"  >
                                        delete
                                    </html:link>
                                </div>                            </c:if>
                        </td>
                        <td class="frameRight"></td>
                    </tr>
                    <tr>
                        <th class="frameBottomleft"></th>
                        <th class="frameBottom"></th>
                        <th class="frameBottomright"></th>
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
                        <li class="slideTop"></li>
                        <li class="slideImage">
                            <html:link action="/display/${display.nextPhoto.photoId}/display.html" name="display"
                                       property="nextPhotoLinkParams" title="nächstes Foto"><img
                                    class="${display.nextPhoto.orientationSuffix}"
                                    src="${display.nextPhoto.thumbnailUrl}"
                                    alt="nächstes Foto"/></html:link>
                        </li>
                        <li class="slideBottom">
                            <html:link action="/display/${display.nextPhoto.photoId}/display.html" name="display"
                                       property="nextPhotoLinkParams" title="n?chstes Foto">weiter
                            </html:link>
                        </li>
                    </ul>
                </c:if>

            </td>
            <td id="displaySpacer"></td>
            <td id="displayShop">


                <ul class="box_B box">
                    <li class="top"><html:img page="/images/box_B_top_de.gif" alt=""/></li>
                    <li class="middle">

                        <iframe src="<html:rewrite action="/shoppingCartPreviewFrame"/>" id="shoppingcartPreview"
                                name="shoppingcart_frame">
                            <p>Ihr Browser kann leider keine eingebetteten Frames anzeigen: Sie können aber direkt zum
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
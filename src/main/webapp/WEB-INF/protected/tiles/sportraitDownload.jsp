<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="downloadBean" type="ch.unartig.studioserver.beans.DownloadImageBean" scope="request"/>
<html:xhtml/>

<div class="content">
    <h2>Download der bestellten Fotos</h2>
</div>

<c:forEach var="downloadableItem" items="${downloadBean.downloadableOrderItems}">
    <div class="content">

        <ul class="slide sliceSc" style="margin:6px 20px 0 0;float:left;">
            <li class="slideTop"></li>
            <li class="slideImage">
                <html:img styleClass="${downloadableItem.photo.orientationSuffix}"
                          src="${downloadableItem.photo.thumbnailUrl}"/>

            </li>
            <li class="slideBottom">
            </li>
        </ul>

            <p>Dateiname:&nbsp;<b>${downloadableItem.photo.displayTitle}</b><br/>
                <c:if test="${downloadableItem.product.digitalProduct}">
                    ${downloadableItem.product.productName}
                </c:if>
                <c:if test="${!downloadableItem.product.digitalProduct}">
                    <b>Bonus Digi Foto 400x600 zu Ihrer Print-Bestellung</b>
                </c:if>
            </p>

            <p>
                <b>WICHTIG, bitte Anleitung lesen: </b> Klicken Sie auf den "Download >>" Link, und benutzen Sie die Speicher-Funktion ('Save', 'Save to Disk') des Browsers bevor Sie das Foto öffnen!
                <%--<bean:message bundle="CONTENT" key="instructions.download"/>--%>
                <br/>

                    <%--<html:link href="${downloadBean.downloadUrl}?phId=${downloadableItem.photo.photoId}">--%>
                <html:link href="${downloadBean.downloadUrl}?OIID=${downloadableItem.orderItemId}">
                    <br/><span class="bold">Download >> </span>
                </html:link>
            </p>

    </div>
    <%--
                    <td class="EcardCol">
                        <img src="/images/button/ecard.gif" alt=""/>

                        <p><br/>
                            <bean:message bundle="CONTENT" key="instructions.ecard"/>
                        </p>
                    </td>
    --%>

</c:forEach>


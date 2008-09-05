<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="albumBean" type="ch.unartig.studioserver.beans.SportsAlbumBean" scope="session"/>


<div class="pageNav">
    <ul id="fotoNav">
        <li>
            <html:link action="/event?sportsEventId=${albumBean.sportsEvent.genericLevelId}" styleClass="arrowUp">zum
                Anlass
            </html:link>
        </li>
    </ul>
    <h1 id="pageName">${albumBean.sportsEvent.longTitle}</h1>
</div>


<div class="pageNav">
    <ul>
        <li class="navList-top"/>
        <li class="navList-content">

            <html:form action="/showCategory" method="get">
                <html:hidden property="page" value="1"/>
                <table class="form">
                    <tr>
                        <td>Startnummer:</td>
                        <td>
                            <html:text styleClass="txt" property="startNumber" size="6" maxlength="6"
                                       title="Startnummer eingeben"/>
                        </td>
                        <td>
                            <html:link action="/showCategory?page=1&startNumber=" styleClass="noUnderline">
                                <html:img page="/images/buttons/x.gif" alt=""/>&nbsp;
                            </html:link>
                        </td>
                        <!-- Kategorie start -->
                        <td>&nbsp;Kategorie:</td>
                        <td>
                            <html:select property="eventCategoryId">
                                <html:optionsCollection name="albumBean" property="eventCategories" label="title"
                                                        value="eventCategoryId"/>
                            </html:select>
                        </td>
                        <!-- Kategorie end -->
                        <td>
                            <html:image styleClass="left"   page="/images/buttons/bt_searchFoto_de.gif"
                                   title="Fotos suchen"/>
                        </td>
                    </tr>
                </table>
            </html:form>

        </li>
        <li class="navList-bottom"/>
    </ul>
</div>

<div class="content">
    <c:if test="${albumBean.size==0}">
        <%--<bean:message bundle="CONTENT" key="error.album.photos.notavailable" />--%>
        <p>1) Entweder sind diese Fotos noch nicht verfügbar, oder <br/>
            2) Das gesuchte Foto wurde nicht gefunden. >> Probieren Sie's mit der Startnummer <span
                class="bold">'999'</span>.
        </p>
    </c:if>
    <ul class="album">
        <c:forEach var="thumbnail" items="${albumBean.photosOnPage}">
            <bean:define id="thisPhotoId" value="${thumbnail.photoId}" toScope="page"/>
            <li>
                <ul class="slide">
                    <li class="slideTop">
                        <%
                            //show slide for ordered photo ?
                            if (albumBean.getOrderedPhotos().containsKey(new Long(thisPhotoId))) { %>

                        <html:link action="/toShoppingCart">
                            <html:img styleClass="sc" page="/images/sc_orange_small.gif" alt=""
                             title="Befindet sich bereits im Einkaufswagen" style="height:12px;width:11px;"/>
                        </html:link>
                        <% } %>

                    </li>

                    <li class="slideImage">
                        <html:link action="/display/${thumbnail.photoId}/${thumbnail.album.navTitle}/display.html">
                            <html:img styleClass="${thumbnail.orientationSuffix}" src="${thumbnail.thumbnailUrl}"
                                 alt="${thumbnail.displayTitle}" title="${thumbnail.displayTitle}"/>
                        </html:link>
                    </li>
                    <li class="slideBottom">${thumbnail.shortTimeString}</li>
                </ul>
            </li>
        </c:forEach>
    </ul>

</div>
<div class="pageNav">

    <ul>
        <li class="navList-top"/>
        <li class="navList-content">

            <ul class="navList">

                <!--time navigation in steps by 10-pages:-->
                <li>
                    <html:link action="${albumBean.actionString}" title="back" name="albumBean"
                               property="jumpBackParams">
                        <html:img page="/images/arrow-a.gif" alt=""/>
                    </html:link>
                </li>
                <!--previous page:-->
                <li>
                    <html:link action="${albumBean.actionString}" title="zurueck" name="albumBean"
                               property="previousPageParams">
                        <html:img page="/images/arrow-b.gif" alt=""/>
                    </html:link>
                </li>
                <!--not all page-number will fit-->
                <c:if test="${albumBean.numberOfPages > albumBean.maxPageNumbersFitOnNavigation}">
                    <!--first page link is shown if page is higher than maxpagenumberfitonnavigation -2 -->
                    <c:if test="${albumBean.page >= albumBean.maxPageNumbersFitOnNavigation-2}">
                        <html:link action="${albumBean.actionString}?page=1">
                            <li>1...</li>
                        </html:link>
                    </c:if>
                    <!--middle part-->
                    <c:forEach var="i" begin="${albumBean.pagerMiddlepartStart}" end="${albumBean.pagerMiddlepartEnd}">
                        <li>
                            <!--page nr is not current page aaa-->
                            <c:if test="${i != albumBean.page}">
                                <html:link action="${albumBean.actionString}" paramId="page"
                                           paramName="i"> ${i} </html:link>
                            </c:if>
                            <!--page nr = current page-->
                            <c:if test="${i == albumBean.page}">${i}</c:if>
                        </li>
                    </c:forEach>
                    <!--last page link-->
                    <c:if test="${albumBean.pagerMiddlepartEnd < albumBean.numberOfPages}">
                        <li>
                            <html:link action="${albumBean.actionString}" paramId="page" paramName="albumBean"
                                       paramProperty="numberOfPages">
                                ...${albumBean.numberOfPages}
                            </html:link>
                        </li>
                    </c:if>

                </c:if>

                <!--all page numbers fit in page-nav-->
                <c:if test="${albumBean.numberOfPages <= albumBean.maxPageNumbersFitOnNavigation}">
                    <c:forEach var="i" begin="1" end="${albumBean.numberOfPages}">
                        <li>
                            <!--page nr is not current page-->
                            <c:if test="${i != albumBean.page}">
                                <html:link action="${albumBean.actionString}" paramId="page"
                                           paramName="i"> ${i} </html:link>
                            </c:if>
                            <!--page nr = current page-->
                            <c:if test="${i == albumBean.page}">${i}</c:if>
                        </li>
                    </c:forEach>
                </c:if>
                <!--next page:-->
                <li>
                    <html:link action="${albumBean.actionString}" title="naechste Seite" name="albumBean"
                               property="nextPageParams">
                        <html:img page="/images/arrow-c.gif" alt=""/>
                    </html:link>
                </li>
                <!--time navigation in steps by 10 pages:-->
                <c:if test="${'notime' != albumBean.type}">
                    <li>
                        <html:link action="${albumBean.actionString}" title="jump forward" name="albumBean"
                                   property="jumpForwardParams">
                            <html:img page="/images/arrow-d.gif" alt=""/>
                        </html:link>
                    </li>
                </c:if>

            </ul>
            <!--DYNAMIC Pager END-->

        </li>
        <li class="navList-bottom"/>
    </ul>

</div>

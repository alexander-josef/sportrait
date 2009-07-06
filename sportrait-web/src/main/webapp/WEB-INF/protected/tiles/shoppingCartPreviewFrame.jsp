<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="shoppingCart" type="ch.unartig.u_core.beans.ShoppingCart" scope="session"/>


<!-- loop foto in SC start -->
<logic:iterate id="photo" property="photosInShoppingcart" name="shoppingCart">

        <html:link action="/toShoppingCart" target="_parent">
            <img src="${photo.thumbnailUrl}" alt="${photo.displayTitle}"
                 title="${photo.displayTitle}"/><br/><nobr>${photo.filename}</nobr>
        </html:link><br/>


</logic:iterate>
<!-- loop foto in SC end -->

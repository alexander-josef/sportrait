<%--chech here : https://support.google.com/tagmanager/answer/6107169?hl=en for changing to a dataLayer approach with Google Tag Manager--%>

<%@ page import="java.util.Enumeration" %>
<%@ page import="ch.unartig.studioserver.businesslogic.PhotoOrderIF" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    PhotoOrderIF test = (PhotoOrderIF)session.getAttribute("photoOrder");
//    System.out.println("test = " + test);
    Enumeration enumeration = request.getAttributeNames();
//    System.out.println("enumeration = " + enumeration);
%>
<jsp:useBean id="sc" scope="session" type="ch.unartig.studioserver.beans.ShoppingCart" />
<jsp:useBean id="GAorderId" scope="session" type="Long" />
<jsp:useBean id="GAorderItems" scope="session" type="java.util.Collection<ch.unartig.studioserver.model.OrderItem>" />
<jsp:useBean id="GAcustomerCity" scope="session"  type="String"/>
<html:xhtml/>

<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-385263-2']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

        _gaq.push(['_addTrans',
            // we only know an order id if processed immediatly with credit card, use unartig order id
            '${GAorderId}', // Order ID
            'sportrait', // Affiliation
            '${sc.totalPhotosCHF}', // Total
            '0', // Tax
            '${sc.shippingHandlingCHE}', // Shipping
            '${GAcustomerCity}', // City
            'n/a', // State
            '${sc.customerCountry}'  // Country
        ]);

    // loop over product-type/price consolidated items
    <c:forEach items="${GAorderItems}" var="item" varStatus="forEachStatus" >

/*

    Think about how to handle this. We don't want to count based on individual photos, but on products, or better, product types.
    Maybe add a helper method to consolidate the product types.
    - add item per individual producttype-price combination.
    - category is the product type.

*/

        _gaq.push(['_addItem',
                '${GAorderId}', // Order ID
                '${item.product.productName}', // SKU (unique stock keeping unit)
                '${item.product.productName}', // Product Name
                '${item.product.productType.name}', // Category
                '${item.product.price.priceCHF}', // Price
                '${item.quantity}'  // Quantity
        ]);

        _gaq.push(['_trackTrans']);

    </c:forEach>

</script>
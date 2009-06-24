<%@ page import="java.util.Enumeration" %>
<%@ page import="ch.unartig.u_core.ordering.PhotoOrderIF" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    PhotoOrderIF test = (PhotoOrderIF)session.getAttribute("photoOrder");
    System.out.println("test = " + test);
    Enumeration enumeration = request.getAttributeNames();
    System.out.println("enumeration = " + enumeration);
%>
<jsp:useBean id="sc" scope="session" type="ch.unartig.studioserver.beans.ShoppingCart" />
<jsp:useBean id="GAorderId" scope="session" type="Long" />
<jsp:useBean id="GAorderItems" scope="session" type="java.util.Collection<ch.unartig.u_core.model.OrderItem>" />
<jsp:useBean id="GAcustomerCity" scope="session"  type="String"/>
<html:xhtml/>
<%--
Google analytics script for the eCommerce functions after a transaction
--%>
<script type="text/javascript">
    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
    var pageTracker = _gat._getTracker("UA-385263-2");
    pageTracker._initData();
    pageTracker._trackPageview();


    pageTracker._addTrans(
            // we only know an order id if processed immediatly with credit card, use unartig order id
            "${GAorderId}", // Order ID
            "sportrait", // Affiliation
            "${sc.totalPhotosCHF}", // Total
            "0", // Tax
            "${sc.shippingHandlingCHE}", // Shipping
            "${GAcustomerCity}", // City
            "n/a", // State
            "${sc.customerCountry}"                                       // Country
            );

    // loop over product-type/price consolidated items
    <c:forEach items="${GAorderItems}" var="item" varStatus="forEachStatus" >

    <%--
    Think about how to handle this. We don't want to count based on individual photos, but on products, or better, product types.
    Maybe add a helper method to consolidate the product types.
    - add item per individual producttype-price combination.
    - category is the product type.
    --%>

        pageTracker._addItem(
                "${GAorderId}", // Order ID
                "${item.product.productName}", // SKU (unique stock keeping unit)
                "${item.product.productName}", // Product Name
                "${item.product.productType.name}", // Category
                "${item.product.price.priceCHF}", // Price
                "${item.quantity}"  // Quantity
                );

        pageTracker._trackTrans();

    </c:forEach>

</script>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<li class="contentCenter">
    <ul id="tabMenu">
        <li class="tab"><html:link action="/photographer/main">Mein Konto</html:link></li>
        <li class="tab"><html:link action="/photographer/admin/eventlist">Liste von Anlässen</html:link></li>
        <li class="tab"><html:link action="/photographer/admin/upload">Bilder hochladen</html:link></li>
        <li class="tab"><html:link action="/photographer/admin/edit">Alben verwalten</html:link></li>
        <%--@elvariable id="clientInSession" type="ch.unartig.u_core.controller.Client"--%>
        <c:if test="${clientInSession.admin}">
            <li class="tab"><html:link action="/admin/listPhotographers">SPORTrait Administration</html:link></li>
        </c:if>
    </ul>
</li>
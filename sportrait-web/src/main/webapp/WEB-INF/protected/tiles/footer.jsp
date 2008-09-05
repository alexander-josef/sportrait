<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="footerLinks">
    <ul>
        <li><html:link action="/index">Home</html:link></li>
        <li><html:link action="/about">Sportrait</html:link></li>
        <li><html:link action="/copyright">© unartig AG</html:link></li>
        <li><html:link action="/agb">AGB</html:link></li>
        <li><html:link action="/privacy">Datenschutz</html:link></li>
        <li><html:link action="/contact">Kontakt</html:link></li>
        <li><a href="http://bugz.unartig.ch/?sportrait-forum" target="_blank">Sportrait-Forum</a>
        </li>
    </ul>
</div>
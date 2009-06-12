<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:xhtml/>
<div class="pageNav">
    <h1 id="pageName">Besten Dank, wir haben Ihre Bestellung erhalten.</h1>
</div>
<div class="content">

    <p class="leftclear errorstyle">WICHTIG, bitte lesen:</p>

    <p class="leftclear leftalign">In den n�chsten Minuten erhalten Sie von uns eine <span style="font-weight:bold">Best�tigung</span>
        Ihrer Bestellung per E-Mail.
        Dieses E-Mail enth�lt auch den <span style="font-weight:bold">Download-Link f�r Ihre Digitalfotos,</span>
        sollten Sie welche bestellt haben.
    </p>

    <p class="leftclear leftalign"><span style="font-weight:bold">Falls Sie keine Best�tigung erhalten,</span>
        �berpr�fen Sie bitte zuerst Ihren Spam-Filter.
        <html:link action="/contact" title="Zur Kontaktseite"> Kontaktieren</html:link>
        Sie uns, wenn Sie kein Best�tigungs-E-Mail bekommen haben.
    </p>

    <p class="leftclear leftalign">Vielen Dank f�r Ihren Einkauf bei SPORTrait und viel Spass mit den Fotos!</p>

</div>


<%--<div class="content">--%>
    <%--<html:form action="/...OptIn">--%>
        <%--<table class="left_button">--%>
            <%--<tr>--%>
                <%--<td>--%>
                    <%--<html:checkbox property="noSportraitEmails" value="true"/>--%>
                <%--</td>--%>
                <%--<td><p class="checkboxtext">SPORTRAIT darf mir gerne aktuelle Informationen per E-Mail zustellen.</p>--%>
                <%--</td>--%>
            <%--</tr>--%>
                        <%--<tr>--%>
                    <%--<td><html:checkbox property="noCoplaEmails" value="true"/></td>--%>
                    <%--<td><p class="checkboxtext"></p></td>--%>
                <%--</tr>--%>
        <%--</table>--%>
    <%--</html:form>--%>
<%--</div>--%>

<div class="content">
    <html:link action="/index" styleClass="left">
        <html:img page="/images/buttons/bt_toHome_de.gif" alt=""/>
    </html:link>
</div>

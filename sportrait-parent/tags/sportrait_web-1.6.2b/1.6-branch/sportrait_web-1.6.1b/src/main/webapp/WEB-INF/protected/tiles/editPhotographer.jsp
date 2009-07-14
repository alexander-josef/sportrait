<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html:xhtml/>
<li class="contentCenter">
<html:form action="/admin/saveUpdatePhotographer">
<c:if test="${!empty photographerId}">
    <html:hidden property="photographerId"/>
</c:if>
<logic:messagesPresent message="true">
    <html:messages id="error" bundle="ERROR" message="true">
        <p class="errorstyle">
            <bean:write name="error" bundle="ERROR"/>
        </p>
    </html:messages>
</logic:messagesPresent>

<table>
    <tr>
        <td>Username</td>
        <td>
            <html:text property="userName"/>
        </td>
    </tr>
    <tr>
        <td>E-Mail-Adresse</td>
        <td>
            <html:text property="emailAddress"/>
        </td>
    </tr>
    <tr>
        <td>Passwort</td>
        <td>
            <html:password property="password"/>
        </td>
    </tr>
    <tr>
        <td>Passwort bestätigen</td>
        <td>
            <html:password property="passwordConfirm"/>
        </td>
    </tr>
    <tr>
        <td>Anrede:</td>
        <td>
            <select name="gender">
                <option value="m">Herr</option>
                <option value="f">Frau</option>
            </select>
        </td>
    </tr>

    <tr>
        <td>Vorname</td>
        <td>
            <html:text property="firstName"/>
        </td>
    </tr>
    <tr>
        <td>Name</td>
        <td>
            <html:text property="lastName"/>
        </td>
    </tr>
    <tr>
        <td>Adresse</td>
        <td>
            <html:text property="addr1"/>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td>
            <html:text property="addr2"/>
        </td>
    </tr>
    <tr>
        <td>PLZ / Ort</td>
        <td>
            <html:text property="zipCode"/>
            <html:text property="city"/>
        </td>
    </tr>
    <tr>
        <td>Land</td>
        <td>
            <html:text property="country"/>
        </td>
    </tr>
    <tr>
        <td>Telefon</td>
        <td>
            <html:text property="phone"/>
        </td>
    </tr>
    <tr>
        <td>Mobiltelefon</td>
        <td>
            <html:text property="phoneMobile"/>
        </td>
    </tr>
    <tr>
        <td>Kamera</td>
        <td>
            <html:text property="cameraModel"/>
        </td>
    </tr>
    <tr>
        <td>Website</td>
        <td>
            <html:text property="website"/>
        </td>
    </tr>
</table>

<c:if test="${!empty photographerId}">
    <html:submit value="Update"/>
</c:if>
<c:if test="${empty photographerId}">
    <html:submit value="Speichern"/>
</c:if>
</html:form>
</li>
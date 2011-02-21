<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<logic:messagesPresent message="true">
    <div class="content">
        <p class="errorstyle"><span></span></p>
        <ul>
            <html:messages id="msg" message="true" bundle="ERROR">
                <li><p class="errorstyle">
                    <bean:write name="msg"/>
                </p></li>
            </html:messages>
        </ul>
    </div>
</logic:messagesPresent>

<logic:messagesPresent>
    <div class="content">
        <p class="errorstyle"><span></span></p>
        <ul>
            <html:messages id="validationError" bundle="ERROR">
                <li><p class="errorstyle">
                    <bean:write name="validationError"/>
                </p></li>
            </html:messages>
        </ul>
    </div>
</logic:messagesPresent>

<div class="content">

<h2 class="orange"><br/>Kontaktformular</h2>
<html:form action="/sendCustomerServiceMessage" method="post">
<br/>
<table class="form leftclear">


<c:if test="${clientInSession!=null}">
    <tr>
        <td class="formLead">Name:</td>
        <td class="leftalign" colspan="2">
            <b>${clientInSession.userProfile.firstName} ${clientInSession.userProfile.lastName}</b>
        </td>
    </tr>

    <tr>
        <td class="formLead">Kunden Nr.:</td>
        <td class="leftalign" colspan="2">
            <b>${clientInSession.userProfile.userProfileId}</b>
        </td>
    </tr>
    <tr>
        <td colspan="3">&nbsp;</td>
    </tr>
    <tr>
        <td colspan="3">
            Bitte kontaktiert mich via:
        </td>
    </tr>
    <tr>
        <td></td>
        <td class="leftalign" width="20">
            <html:radio property="fromContactPhotographer" value="contactEmail" styleId=""
                        errorStyleClass="inputError"/>
        </td>
        <td>E-Mail:&nbsp;<b>${clientInSession.userProfile.emailAddress}</b></td>
    </tr>
    <tr>
        <td></td>
        <td class="leftalign">
            <html:radio property="fromContactPhotographer" value="contactPhone" styleId=""
                        errorStyleClass="inputError"/>
        </td>
        <td>Telefon:&nbsp;<b>${clientInSession.userProfile.phone}</b></td>
    </tr>
    <tr>
        <td></td>
        <td class="leftalign">
            <html:radio property="fromContactPhotographer" value="contactPhoneMobile" styleId=""
                        errorStyleClass="inputError"/>
        </td>
        <td>Mobile:&nbsp;<b>${clientInSession.userProfile.phoneMobile}</b></td>
    </tr>
    <tr>
        <td></td>
        <td class="leftalign">
            <html:radio property="fromContactPhotographer" value="contactPhoneElternative" styleId=""
                        errorStyleClass="inputError"/>
        </td>
        <td>
            <html:text property="fromContactPhotographer" style="width: 190px" value=""/>
            &nbsp;<i>E-Mail oder Telefonnummer</i></td>
    </tr>
    <tr>
        <td colspan="3">&nbsp;</td>
    </tr>

</c:if>
<c:if test="${clientInSession==null}">
    <tr>

        <td class="formLead">Name<span class="must">*</span>:</td>
        <td class="leftalign" colspan="2">
            <html:text property="sender" style="width: 190px"/>
        </td>
    </tr>
    <tr>
        <td class="formLead">E-Mail:<span class="must">*</span>:</td>
        <td class="leftalign" colspan="2">
            <html:text property="fromAddress" style="width: 190px"/>
        </td>
    </tr>
    <tr>
        <td class="formLead">E-Mail bestätigen:<span class="must">*</span>:</td>
        <td class="leftalign" colspan="2">
            <html:text property="fromAddressConfirm" style="width: 190px"/>
        </td>
    </tr>
    <tr>
        <td class="formLead">Telefon:</td>
        <td class="leftalign" colspan="2">
            <html:text property="contactPhone" style="width: 190px"/>
        </td>
    </tr>
    <tr>
        <td class="leftalign" colspan="3">&nbsp;</td>
    </tr>
</c:if>
<c:if test="${clientInSession==null}">
    <tr>
        <td class="formLead">Betreff:</td>
        <td class="leftalign" colspan="2">
            <html:text property="subject" style="width: 190px"/>
        </td>
    </tr>
</c:if>
<c:if test="${clientInSession!=null}">
    <tr>
        <td class="formLead">Betreff:</td>
        <td class="leftalign" colspan="2">
            <html:select property="subject">
                <html:option value="Anlass anmelden">
                    Anlass anmelden
                </html:option>
                <html:option value="allgemeine">
                    <bean:message bundle="CONTENT" key="contact.issue.general"/>
                </html:option>
                <html:option styleClass="kontaktfeld" value="Anlass abwickeln">
                    <bean:message bundle="CONTENT" key="contact.issue.search"/>
                </html:option>
                <html:option styleClass="kontaktfeld" value="Fotos nachbearbeiten">
                    <bean:message bundle="CONTENT" key="contact.issue.search"/>
                </html:option>
                <html:option styleClass="kontaktfeld" value="Fotos hochladen">
                    <bean:message bundle="CONTENT" key="contact.issue.book.unartig"/>
                </html:option>
            </html:select>
        </td>

    </tr>
</c:if>
<tr>
    <td class="formLead" style="">Mitteilung:</td>
    <td colspan="2">
        <html:textarea property="message" rows="8" cols="50"/>

    </td>
</tr>

<tr>
    <td></td>
    <td class="leftalign" colspan="2">
        <html:submit property="Button1" value="Senden"/>
    </td>
</tr>
<tr>
    <td colspan="3"><br/></td>
</tr>
<tr>
    <td></td>
    <td class="formLead" colspan="2">SPORTRAIT, unartig AG, Clausiusstrasse 34, 8006 Zürich, Tel: +41 (0)79 600 3561</td>
</tr>
</table>
</html:form>
</div>

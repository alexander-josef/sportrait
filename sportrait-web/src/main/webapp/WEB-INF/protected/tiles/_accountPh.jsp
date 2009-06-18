<%--@elvariable id="clientInSession" type="ch.unartig.controller.Client"--%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<li class="contentCenter">
    <div class="contentLeft content-1-2">
        <table class="form">
            <tr>
                <td><span class="bold">Pseudonym:</span></td>
                <td>Foti-Heiri</td>
            </tr>
            <tr>
                <td><span class="bold">Bewertung:</span></td>
                <td><img src="/images/rating_5.gif"alt=""/></td>
            </tr>
            <tr>
                <td><span class="bold">Anzahl Bestellungen:</span></td>
                <td>530</td>
            </tr>
            <tr>
                <td><span class="bold">Verkaufte Bilder:</span></td>
                <td>2&#39;305</td>
            </tr>
            <tr>
                <td><span class="bold">erzielter Umsatz:</span></td>
                <td>8&#39;345 SFr.</td>
            </tr>
<%--            <tr>
                <td>
                    <html:link page="/admin/editEvents.html" styleClass="left">
                    <img src="/images/buttons/bt_bulkimport.jpg" alt="bulkimport"
                    </html:link>
                </td>
                <td></td>
            </tr>--%>


        </table>
    </div>

    <div class="contentRight content-1-2">
        <table class="form">
            <tr>
                <td>Vorname:</td>
                <td>${clientInSession.userProfile.firstName}</td>
            </tr>
            <tr>
                <td>Name:</td>
                <td>${clientInSession.userProfile.lastName}</td>
            </tr>
            <tr>
                <td>Adresse:</td>
                <td>${clientInSession.userProfile.addr1}</td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>${clientInSession.userProfile.addr2}</td>
            </tr>
            <tr>
                <td>PLZ / Ort:</td>
                <td>${clientInSession.userProfile.zipCode}&nbsp; ${clientInSession.userProfile.city}</td>
            </tr>
            <tr>
                <td>Konto Moneybookers:</td>
                <td>******</td>
            </tr>
            <tr>
                <td/>
                <td>
                    <html:link href="#" onclick="windowShow('editPhPers')" styleClass="left">
                        <img src="/images/buttons/bt_edit_de.jpg" alt="anpassen"/>
                    </html:link>
                </td>
            </tr>
        </table>
    </div>
</li>
<li class="contentCenter" id="editPhPers">
    <table>
        <tr>
            <td colspan="3">
                <h3>Persönliche Daten bearbeiten</h3>
            </td>
        </tr>
        <tr>
            <td class="formLead">Vorname:</td>
            <td colspan="2">Peter</td>
        </tr>
        <tr>
            <td class="formLead">Name:</td>
            <td colspan="2">Moser</td>
        </tr>
        <tr>
            <td class="formLead">Adresse-1:</td>
            <td colspan="2"><input name="addressPh" type="text" style="width: 180px"/></td>
        </tr>
        <tr>
            <td class="formLead">Adresse-2:</td>
            <td colspan="2"><input name="address2Ph" type="text" style="width: 180px"/></td>
        </tr>
        <tr>
            <td class="formLead">PLZ / Ort:</td>
            <td colspan="2">
                <input name="plzPh" type="text" style="width: 40px"/>&nbsp;&nbsp;
                <input name="cityPh" type="text" style="width: 125px"/>
            </td>

        </tr>
        <tr>
            <td class="formLead">Land:</td>
            <td colspan="2"><select name="Select1">
                <option>Schweiz</option>
                <option>Deutschland</option>
                <option>Österreich</option>
            </select></td>
        </tr>
        <tr>
            <td class="formLead">Konto<span class="must">*</span>:</td>
            <td colspan="2"><input name="bankAccount" type="text" style="width: 125px"/>&nbsp;(Konto bei Moneybookers)
            </td>
        </tr>
        <tr>
            <td><br/></td>
            <td colspan="2"/>
        </tr>

        <tr>
            <td/>
            <td colspan="2">
                    <html:link href="#" onclick="windowHide('editPhPers')" styleClass="left">
                        <img src="/images/buttons/bt_saveChanges_de.gif" alt="Aenderungen seichern"
                    </html:link>
            </td>
        </tr>
        <tr>
            <td colspan="3"><br/><span class="must">*</span>&nbsp;Sie können den Service von SPORTrait ab sofort
                benutzen,
                benötigen aber ein Konto bei Moneybookers, damit wir Ihnen Ihr Guthaben überweisen können.
            </td>
        </tr>
    </table>
</li>
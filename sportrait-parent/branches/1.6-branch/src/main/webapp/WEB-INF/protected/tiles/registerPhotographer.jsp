<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<div class="content">
    <!--
        todo:entweder den einen oder den anderen button anzeigen<br/>
        <input type="button" name="recommendEvent" value="Ich möchte einen neuen Anlass anmelden"/>
        <input type="button" name="toContactForm" value="zurück zum Kontaktformular"/>
        <br/>todo: Bei Eventanmeldung die zusätzlichen Felder anzeigen, den Titel ändern und den Betreff fixieren...<br/>

    -->
    <h2 class="orange"><br/>Pilot-Phase</h2>

    <p>Vielen Dank für dein Interesse an SPORTRAIT. Zurzeit befindet sich SPORTRAIT noch in
        der <b>Pilot-Phase</b>.<br/>
        Wenn du interessiert bist, in der Pilot-Phase aktiv mitzuhelfen, die Funktionaliät und die Benutzerfreundlichkeit von SPORTRAIT
        zu optimieren, und du ein ambitionierter und engagierter Sportfotograf bist (Hobby oder Beruf), dann kannst du dich gerne über
        das <html:link action="/contact">Kontaktformular</html:link> an uns wenden.
    </p>
    <p>Alle anderen Fotografen bitten wir um etwas Geduld, bis SPORTRAIT seine Tore für eine breite Öffentlichkeit öffnet.</p>

   <%-- <form action="MAILTO:register@sportrait.com?subject=SPORTRAIT-Registrierung" method="post" enctype="text/plain">

        <table class="form leftclear">
            <tr>
                <td class="formLead">Am Pilot teilnehmen <span class="must">* </span>:</td>
                <td class="leftalign" colspan="2">
                    <input type="radio" name="Pilot" value="nein" checked="checked"> Nein
                    <input type="radio" name="Pilot" value="yes"> Ja
                </td>
            </tr>
            <tr>

                <td class="formLead">Vorname:</td>
                <td class="leftalign" colspan="2">
                    <input type="text" name="Vorname" size="20">
                </td>
            </tr>
            <tr>                                                           

                <td class="formLead">Name:</td>                                     
                <td class="leftalign" colspan="2">
                    <input type="text" name="Name" size="20">
                </td>
            </tr>
            <tr>
                <td class="formLead">Telefon:</td>
                <td class="leftalign" colspan="2">
                    <input type="text" name="Telefon" size="20">
                </td>
            </tr>
            <tr>
                <td class="formLead">Meine Webseite:</td>
                <td class="leftalign" colspan="2">
                    <input type="text" name="Website" size="20">
                </td>
            </tr>
            <tr>
                <td class="formLead">Kommentar:</td>
                <td colspan="2">
                    <input type="text" name="Kommentar" size="20">
                </td>
            </tr>
            <tr>
                <td></td>
                <td class="leftalign" colspan="2">
                    <input type="submit" value="Jetzt Registrieren"/>
                </td>
            </tr>

        </table>

    </form>--%>

</div>

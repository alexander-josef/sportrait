<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--@elvariable id="clientInSession" type="ch.unartig.controller.Client"--%>
<div class="pageNav">
    <h1 id="pageName">Hilfestellung f�r Kunden</h1>
</div>

<div class="content">
    <p>Hier finden Sie Antworten auf die meisten Ihrer Fragen betreffend das Suchen und die Bestellung von Fotos bei
        SPORTRAIT. Wenn wir an dieser Stelle Ihre Frage nicht beantworten k�nnen, dann kontaktieren Sie uns bitte �ber
        unser
        <html:link action="/contact">Kontaktformular</html:link>
        .</p>
    <c:if test="${clientInSession!=null}">
        <html:link action="/photographerFaq"><html:img page="/images/buttons/bt_faqForPh_de.gif" alt=""/></html:link>
    </c:if>
</div>
<div class="content faq">

    <h3><span class="orange">Fotos Suchen und Finden</span></h3>
    <ul class="faqList">
        <li><a href="#020" class="expliziterLink bold">Wie finde ich einen Sportanlass?</a></li>
        <li><a href="#021" class="expliziterLink bold">Wie gelange ich zu den Fotos?</a></li>
        <li><a href="#022" class="expliziterLink bold">Wie kann ich meine Fotos anhand der Startnummer finden?</a></li>
        <li><a href="#023" class="expliziterLink bold">Ich kann meine Fotos nicht finden. Wie gehe ich vor?</a></li>
        <li><a href="#024" class="expliziterLink bold">Gibt es eine Grossansicht der Fotos?</a></li>
    </ul>

    <h3><span class="orange">Fotos bestellen</span></h3>
    <ul class="faqList">
        <li><a href="#030" class="expliziterLink bold">Wie kann ich ein Foto in den Einkaufswagen legen?</a></li>
        <li><a href="#031" class="expliziterLink bold">Wo kann ich die Produkte (Formate) und Quantit�ten w�hlen?</a>
        </li>
        <li><a href="#032" class="expliziterLink bold">Wie kann ich erkennen, ob sich ein Foto bereits im Warenkorb
            befindet?</a></li>
        <li><a href="#033" class="expliziterLink bold">Wie kann ich ein Foto aus meiner Bestellung entfernen?</a></li>
    </ul>

    <h3><span class="orange">Produkte & Preise</span></h3>
    <ul class="faqList">
        <li><a href="#040" class="expliziterLink bold">Welche Produkte (Formate) sind erh�ltlich?</a></li>
        <li><a href="#042" class="expliziterLink bold">Warum variieren Preise und Produkte-Angebot?</a></li>
        <li><a href="#043" class="expliziterLink bold">Was sind digitale Daten?</a></li>
    </ul>

    <h3><span class="orange">Checkout (Bestellung abschliessen)</span></h3>
    <ul class="faqList">
        <li><a href="#050" class="expliziterLink bold">Wie kann ich eine Bestellung abschliessen?</a></li>
        <li><a href="#051" class="expliziterLink bold">Kann ich meine Bestellung noch �ndern?</a></li>
        <li><a href="#051" class="expliziterLink bold">Wann ist eine Bestellung abgeschlossen?</a></li>
    </ul>

    <h3><span class="orange">Nach der Bestellung</span></h3>
    <ul class="faqList">
        <li><a href="#060" class="expliziterLink bold">Wie erfahre ich, ob meine Bestellung erfolgreich abgeschickt und
            erfasst wurde?</a></li>
        <li><a href="#061" class="expliziterLink bold">Ich habe kein Best�tigungs-E-Mail erhalten. Ist meine Bestellung
            fehlgeschlagen?</a></li>
        <li><a href="#062" class="expliziterLink bold">Wie lange dauert es, bis die Bestellung bei mir eintrifft?</a>
        </li>
        <%--    <li><a href="#063" class="expliziterLink bold">Meine Bestellung, die ich bereits abgeschickt habe, ist fehlerhaft.
       Was tun?</a></li>--%>
    </ul>

</div>
<div class="content faq">
<!--<h2 class="orange">Allgemein</h2>

trifft noch nich zu, auskommenntiert!

<p>
    <a name="010"></a><b>Was ist SPORTrait?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    SPORTrait ist ein Marktplatz f�r Sportfotografie. Ambitionierte Fotografen bieten hier ihre Fotos den Sportlern an.
    Sportler k�nnen Fotos von Sportanl�ssen einfach finden, betrachten und bestellen.
</p>-->

<h2 class="orange">Fotos Suchen und Finden</h2>

<p><a name="020"/><b>Wie finde ich einen Sportanlass?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Auf der
    <html:link action="/index" target="blank">Homepage von SPORTRAIT</html:link>
    befindet sich auf der linken Seite eine Liste aktueller Anl�sse.
    Zuoberst in der Liste finden Sie die j�ngsten Anl�sse. Jede Zeile in dieser Liste ist ein Link, der Sie zu den Fotos
    und zu Informationen �ber den Anlass f�hrt.<!-- �ber die Suchfunktion oberhalb der Anlass-Liste
              k�nnen Sie einen Anlass anhand des Austragungsdatums, des Autragungsortes oder anhand von Stihworten suchen. Nachdem Sie die
              Suche gestartet haben, erscheint eine Liste von Anl�ssen, die Ihre Suchkriterien erf�llen. W�hlen Sie den Anlass aus dieser
               Liste aus. K�nnen Sie den Anlass nach wiederholter Suche nicht finden, dann haben Sie die M�glichkeit, uns zu
               <a href="/contact.html">kontaktieren</a>, falls Sie m�chten, dass dieser Anlass bei SPORTrait eingetregen wird.-->
</p>

<p><a name="021"/><b>Wie gelange ich zu den Fotos?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Befolgen Sie zuerst die Anleitungen zu <a href="#020" class="expliziterLink bold">"Wie finde ich einen
    Sportanlass?"</a> und lesen Sie dann hier weiter.<br/>
    Auf der Anlass-Seite finden Sie die Links zu den Fotos, Informationen zum Anlass und einen Link zum Veranstalter.
    Rechnen Sie damit, dass es nach dem Anlass oft 24 Stunden und l�nger dauern kann, bevor Sie hier die Fotos finden.
    Meistens werden die Fotos in Kategorien unterteilt. Die Bezeichnung der Links sollte selbsterkl�rend sein.
    Klicken Sie auf die Kategorie, unter der Sie Ihre Fotos erwarten und Sie gelangen zur Foto-�bersicht der gew�hlten
    Kategorie. </p>

<p><a name="022"/><b>Wie kann ich meine Fotos anhand der Startnummer finden?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Befolgen Sie zuerst die Anleitungen zu <a href="#020" class="expliziterLink bold">"Wie finde ich einen
    Sportanlass?"</a>
    und <a href="#021" class="expliziterLink bold">"Wie gelange ich zu den Fotos?"</a> und lesen Sie dann hier
    weiter.<br/>
    Sie befinden sich jetzt in der Foto-�bersicht der gew�hlten Kategorie.
    Sie haben die M�glichkeit, andere Kategorien anzuw�hlen und die Fotos mittels der Startnummer zu suchen. Geben sie
    dazu Ihre
    Startnummer in dem entsprechenden Feld ein, w�hlen Sie die Kategorie, in der Sie Ihre Fotos erwarten, und
    starten Sie die Suche mit "Fotos suchen" (siehe nachfolgende Abbildung). Wenn Sie alle Fotos der gew�hlten Kategorie
    sehen m�chten, dann
    klicken Sie auf den Knopf mit dem Kreuz (nach dem Startnummerfeld). Somit wird die Startnummer aus dem
    Startnummerfeld gel�scht
    und sofort werden alle Fotos der gew�hlten Kategorie angezeigt.</p>
<html:img page="/images/faq/faq_022_de.gif" alt=""/>

<p><a name="023"/><b>Ich kann mein Foto nicht finden. Wie gehe ich vor?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Gehen Sie wie folgt vor, wenn Ihre erste Suche nach einem Foto erfolglos war:<br/>
    1. Suchen Sie das Foto in den anderen Kategorien. Es ist m�glich, dass die Fotos nicht der richtigen
    Kategorie zugewiesen wurden.<br/>
    2. Lesen Sie die Hinweise, die anstelle der Fotos erscheinen, wenn Ihre Suche erfolglos war. Vielleicht wurde Ihr
    Foto einer falschen Startnummer zuzuweisen; Die Hinweise k�nnen Ihnen helfen, ein Foto trotzdem zu finden.<br/>
    3. Am Schluss k�nnen Sie in der Foto-�bersicht oder in der Foto-Grossansicht Bild um Bild durchsehen und ein Foto
    anhand der Zeitangabe suchen. In der Foto-�bersicht befindet sich die Zeit unter jedem einzelnen Foto. In der
    Foto-Grossansicht
    steht die Zeit oben links im grauen Balken.<br/>
    4. Es ist nat�rlich auch m�glich, dass das von Ihnen gesuchte Foto nicht existiert. Bei grossen Sportanl�ssen ist es
    fast unm�glich, jede Teilnehmerin und jeden Teilnehmer fotografisch festzuhalten. Vielleicht klappt es beim n�chsten
    mal...</p>

<p><a name="024"/><b>Gibt es eine Grossansicht der Fotos?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Ja. Wenn Sie in der Foto-�bersicht auf ein einzelnes Foto klicken, gelangen Sie zur Grossansicht dieses Fotos. Sie
    haben auch in diesem Modus die M�glichkeit vor- und zur�ckzubl�ttern (kleine Fotos rechts und links der
    Grossansicht). Gr�sser als in dieser Ansicht k�nnen die Fotos nicht angezeigt werden.
</p>

<h2 class="orange">Fotos bestellen</h2>

<p><a name="030"/><b>Wie kann ich ein Foto in den Warenkorb legen?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Sie haben in der Foto-�bersicht ein Foto gew�hlt und betrachten nun das Foto in der Grossansicht:<br/>
    Im hellgrauen Balken �ber dem Foto k�nnen Sie das Format w�hlen und den Knopf mit der Bezeichnung "In den Warenkorb"
    klicken, um das Foto, welches in der Grossansicht zu sehen ist, in den Warenkorb zu legen (siehe nachfolgende
    Abbildung).
    In dem grauen Kasten auf der rechten Seite erscheint nun das Foto, welches Sie gerade in den Warenkorb gelegt haben.<br/>
    Das Format bestimmen Sie gleich hier. Im Warenkorb k�nnen Sie das Format jederzeit �ndern und die Menge bestimmen.
    In den Warenkorb gelangen Sie, indem Sie in der Navigation oben rechts auf der Seite
    das Register "Warenkorb" w�hlen oder direkt im grauen Kasten rechts auf das entsprechende Foto klicken.
</p>
<html:img page="/images/faq/faq_030_de.gif" alt=""/>


<p><a name="031"/><b>Wo kann ich die Produkte (Formate/Produkte) und Mengen w�hlen?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Wenn Sie ein Foto in den Warenkorb legen, w�hlen Sie bereits ein Format oder Produkt. Im Warenkorb k�nnen sie zu
    jedem
    Foto das Format oder Produkt �ndern und die Menge bestimmen.
    In den Warenkorb gelangen Sie, indem Sie in der Navigation oben rechts auf der Seite
    das Register "Warenkorb" w�hlen oder direkt im grauen Kasten rechts auf das entsprechende Foto klicken.<br/>
    Sie k�nnen von einem Fotos mehrere verschiedene Formate und Quantit�ten w�hlen. Bedienen Sie dazu jeweils eines der
    vier Drop-Down-Men�s unter "Format/Produkt" (siehe nachfolgende Abbildung). Klicken Sie auf den Pfeil, der sich
    rechts vom Feld f�r
    "Format/Produkt" befindet, um die verschiedenen Produkte zu sehen. W�hlen Sie jetzt das Produkt (Format) aus. Um die
    Quantit�t zu bestimmen, tragen Sie die Menge, in der Sie dieses Produkt bestellen wollen, in das nebenstehende Feld
    f�r "Menge" ein. Sie k�nnen pro Foto vier verschiedene Produkte zu beliebigen Quantit�ten ausw�hlen, indem Sie f�r
    jedes neue Produkt eine neue Zeile mit den Auswahlfeldern "Format/Produkt" und "Menge" benutzen.
</p>
<html:img page="/images/faq/faq_031_de.gif" alt=""/>

<p>W�hlen Sie ein digitales Produkt, so wird die Menge automatisch auf '1' gesetzt und kann nicht angepasst werden.</p>

<p><a name="032"/><b>Wie kann ich erkennen, dass sich ein Foto bereits im Warenkorb befindet?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    In der Foto-�bersicht befindet sich im oberen linken Rand ein kleiner Einkaufswagen, wenn
    das Foto bereits im Einkaufswagen liegt (siehe nachfolgende Abbildung <b>a</b>). In der Grossansicht zeigt die Box
    auf der rechten Seite "Vorschau Warenkorb" die
    Fotos an, die bereits im Einkaufswagen liegen (siehe nachfolgende Abbildung <b>b</b>). Schlussendlich
    k�nnen Sie jederzeit �ber den Navigationspunkt "Warenkorb" im oberen Men� zum Warenkorb wechseln, um ihre Bestellung
    einzusehen.
</p>
<html:img page="/images/faq/faq_032_de.jpg" alt=""/>

<p><a name="033"/><b>Wie kann ich ein Foto aus meiner Bestellung entfernen?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Gehen Sie zum Einkaufswagen. Unter jedem einzelnen Foto finden Sie eine Funktion "l�schen". Bet�tigen Sie diese, um
    ein einzelnes Foto aus dem Einkaufswagen zu entfernen (Siehe letzte Abbildung weiter oben auf diser Seite).
</p>

<h2 class="orange">Preise und Produkte</h2>

<p><a name="040"/><b>Welche Formate und Produkte werden angeboten?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Welche Produkte zur Auswahl stehen, entscheidet der Fotograf, der die Fotos publiziert und zum Kauf anbietet. Das
    m�gliche Angebot
    reicht von Papierabz�gen verschiedener Formate �ber Poster, T-Shirts, Mouse-Pads bis hin zu den elekronischen
    Bilddaten (JPG)
    (ca. 6 MegaPixel oder 400x600 pixel). Das Angebot kann nicht nur zwischen den Anl�ssen, sondern auch innerhalb eines
    Anlasses variieren, da oft mehrere Fotografen an einem Anlass fotografieren, aber nicht dieselben Produkte anbieten
    wollen.<br/>
    In der Foto-Grossansicht, wo Sie das Foto in den Warenkorb legen, werden die zur Auswahl stehenden Formate und
    Produkte
    im Auswahlmen� angezeigt. Wenn Sie den auf den Pfeil rechts des Feldes "Format" klicken, werden alle zur Verf�gung
    stehenden
    Formate und Produkte angezeigt (sie nachfolgende Abbildung). Die Prise sind jeweils hinter jedem Produkt in
    Schweizer Franken und EURO angegeben.
</p>
<html:img page="/images/faq/faq_040_de.gif" alt=""/>

<p><a name="042"/><b>Warum variieren Preise und Produkte-Angebot?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Jeder Fotograf kann selbst bestimmen, welche Produkte er zu welchem Preis anbieten will. Da an Grossanl�ssen oft
    mehrere Fotografen anwesend sind, und jeder Fotograf das Angebot an Produkten und deren Preis selbst bestimmen kann,
    variieren das Angebot und die Preise auch f�r Fotos desselben Anlasses.
</p>

<p><a name="043"/><b>Was sind digitale Daten?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Unter den digitalen Bildprodukten sind die elektronischen Daten eines Fotos zu verstehen.<br/>
    Es stehen zwei digitale Bildprodukte zur Verf�gung: <b>digital (hochaufgel�st)</b> und <b>digital (400x600)</b>.
    Bei "digital (hochaufgel�st)" handelt es sich um eine Datei im JPG-Format in einer f�r die angebotenen
    Produkte gen�gend hohen Aufl�sung (ca. 6 MegaPixel).<br/>
    Bei "digital (400x600)" handelt es sich um eine Datei im JPG-Format in einer Aufl�sung von 400x600 Pixeln.
    Diese Datei ist f�r die Reproduktion von Printprodukten nicht geeignet. Sie ist aber ideal f�r den Versand per
    E-Mail.
</p>

<h2 class="orange">Checkout (Bestellung abschliessen)</h2>

<p><a name="050"/><b>Wie kann ich eine Bestellung abschliessen?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Gehen Sie zum Warenkorb. Wenn Sie alle Fotos ausgew�hlt und deren Produkte (Formate) und Mengen bestimmt
    haben, dann klicken Sie auf den
    Knopf "zur Kasse" oben rechts auf der Seite. Auf der folgenden Seite werden Sie aufgefordert, die notwendigen
    Angaben zu Ihrer Person zu machen.
    Felder, die mit einem * versehen sind, m�ssen ausgef�llt werden, damit die Bestellung abgeschlossen werden
    kann.<br/>
    Auf der n�chsten Seite (�ber den Knopf "weiter") k�nnen Sie die Zahlungsmethode w�hlen. Machen Sie die
    entsprechenden Angaben zur Kreditkarte, wenn Sie sich f�r dieses Zahlungsmittel entschieden haben, oder w�hlen Sie
    die Zahlungsmethode "Rechnung".<br/>
    Auf der letzten Seite der Bestellung (�ber den Knopf "weiter") k�nnen Sie Ihre Bestellung noch einmal pr�fen.
    Kontrollieren Sie f�r jedes Foto die Produkte (Formate), Mengen und Preise. Sie k�nnen die Bestellung f�r sich
    ausdrucken, indem Sie den Knopf "Bestellung drucken" klicken.<br/>
    Sie schliessen die Bestellung definitiv ab, wenn Sie die allgemeinen Gesch�ftsbestimmungen akzeptieren und den Knopf
    "Bestellung abschicken" klicken.<br/>
    Beachten Sie die Fehlermeldungen oben an der Seite, wenn Sie nicht weiterkommen.<br/>
    Eine Fortschrittsanzeige in drei Punkten oben an der Seite, f�hrt Sie durch den Checkout-Prozess und zeigt Ihnen an,
    wie nahe vor dem Abschluss Ihre Bestellung steht. Bevor Sie
    auf der Seite 3 dieses Prozesses den Knopf "Bestellung abschicken" bet�tigt haben, k�nnen Sie den Inhalt der
    Bestellung, Ihre pers�nlichen Angaben und die Zahlungsmethode jederzeit �ndern.
</p>

<p><a name="051"/><b>Kann ich meine Bestellung noch �ndern?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Sie k�nnen den Inhalt und somit die Auswahl der Fotos, deren Formate und Mengen anpassen, bis Sie sich auf der
    Seite 3 des Checkout-Prozesses (siehe Fortschrittsanzeige in drei Punkten oben an der Seite) mit den allgemeinen
    Gesch�ftsbestimmungen einverstanden erkl�rt und den
    Knopf "Bestellung abschicken" bet�tigt haben. Solange dies nicht geschieht, k�nnen Sie den Inhalt der Bestellung,
    Ihre pers�nlichen Angaben und die Zahlungsmethode jederzeit �ndern.
</p>

<p><a name="052"/><b>Wann ist eine Bestellung definitiv abgeschlossen?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Sobald Sie sich auf der Seite 3 des Checkout-Prozessen (siehe Fortschrittsanzeige in drei Punkten oben an der Seite)
    mit den allgemeinen
    Gesch�ftsbestimmungen einverstanden erkl�rt und den Knopf "Bestellung abschicken" bet�tigt haben, wird die
    Bestellung definitiv und verbindlich.<br/>
    Nach Abschicken der Bestellung gelangen Sie auf die Best�tigungsseite. Sie erhalten als Best�tigung eine E-Mail
    mit den Angaben Ihrer Bestellung.
</p>

<h2 class="orange">Nach der Bestellung</h2>

<p><a name="060"/><b>Wie erfahre ich, ob meine Bestellung erfolgreich abgeschickt und erfasst wurde?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Kurz nachdem Sie die Bestellung abgeschickt haben gelangen Sie auf die Best�tigungsseite. Die Seite zeigt Ihnen an,
    dass die Bestellung erfolgreich war. Sie erhalten
    eine E-Mail an die von Ihnen angegebene E-Mail-Adresse. In diesem E-Mail wird Ihnen der Inhalt Ihrer Bestellung noch
    einmal angezeigt. Es kann sein, dass es einige Minuten dauert, bis das E-Mail bei Ihnen eintrifft.
</p>

<p><a name="061"/><b>Ich habe kein Best�tigungs-E-Mail erhalten. Ist meine Bestellung fehlgeschlagen?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Nicht unbedingt.
    Wenn Sie die Bestellung abgeschickt haben und die Best�tigungsseite erscheint, Sie aber innerhalb von ein paar Minuten
    kein E-Mail bekommen, kann das folgende Gr�nde haben. <br/>
    1. Die von Ihnen angegebene E-Mail Adresse existiert nicht oder kann nicht erreicht werden.<br/>
    2. Das Best�tigungs-E-Mail blieb bei Ihnen im Spam-Filter h�ngen.<br/>
    3. Oder die eher unwahrscheinliche M�glichkeit einer technischen St�rung auf der SPORTRAIT-Plattform.
    Wir empfehlen Ihnen daher, bei Nicht erhalt des E-Mails die Bestellung nicht zu wiederholen, sondern uns �ber das <a
        href="/contact.html">Kontaktformular</a>
    eine entsprechende Nachricht zukommen zu lassen. Wir werden uns innerhalb von 24 Stunden bei Ihnen melden.
</p>

<p><a name="062"/><b>Wie lange dauert es, bis die Bestellung bei mir eintrifft?</b>&nbsp;
    <a href="#">nach oben</a><br/>
    Bei der Bestellung von digitalen Daten wird Ihnen wenige Minuten nach Ihrer
    Bestellung per E-Mail ein Link zugestellt, �ber den Sie das bestellte Foto
    herunterladen k�nnen. Falls Sie Abz�ge (Prints) bestellen, werden Ihre Fotos sp�testens am folgenden Arbeitstag nach
    der Bestellung verarbeitet und Ihnen per priorit�re Brief- bzw. Paketpost zugesandt. Die durchschnittliche
    Bef�rderungszeit betr�gt 2 bis 4 Tage.
    Poster und andere Produkte wie T-Shirts etc. haben l�ngere Lieferfristen von bis zu einer Woche. Bei einer
    gemischten Bestellung (zum Beispiel Abz�ge und ein Poster) werden Abz�ge und Poster separat versandt und haben
    somit auch unterschiedliche Lieferfristen.
</p>

<!--<p><a name="063"></a><b>Meine Bestellung, die ich bereits abgeschickt habe, ist fehlerhaft. Was tun?</b>&nbsp;<a
        href="#">nach oben</a><br/>
    Sie stellen anhand des Best�tigungs-E-Mails, das Sie kurz nach der Bestellung erhalten haben, fest, dass Ihnen bei
    der Bestellung ein Fehler unterlaufen ist.
    Grunds�tzlich ist die Bestellung von Ihnen inhaltlich kontrolliert und best�tigt worden und ist somit
    verbindlich.<br/>
    Sie haben aber die M�glichkeit, sich an unser Labor zu wenden (<a
        href="http://www.colorplaza.ch">www.colorplaza.ch</a>):<br/>
    Bedenken Sie dabei, dass Ihre Bestellung das Labor erst in der Nacht nach der Bestellung erreichen wird.
</p>-->


</div>
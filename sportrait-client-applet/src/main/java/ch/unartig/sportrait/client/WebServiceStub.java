package ch.unartig.sportrait.client;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import java.io.InputStream;

/**
 * Todo factor out the web service stub code into this class
 */
public class WebServiceStub {


    /**
     * The representation of the events comes in an XML stream. We use a DOM parser and XPath expression to read the
     * nodes.
     * @param xmlStream
     */
    public void readEventsFromXmlFile(InputStream xmlStream) {

        Document document;

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(xmlStream);
            // select all elements 'event'
//            NodeList events = document.getElementsByTagName("event");

            // Using an XPath expression to select the 'event' nodes. This is safer then the getElementsByName approach.
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList eventNodes = (NodeList) xPath.evaluate("//events/event", document, XPathConstants.NODESET);

            // iterate over events
            for (int i = 0; i < eventNodes.getLength(); i++) {
                NamedNodeMap event = eventNodes.item(i).getAttributes();
                System.out.println("event.getNamedItem(\"date\") = " + event.getNamedItem("date"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}

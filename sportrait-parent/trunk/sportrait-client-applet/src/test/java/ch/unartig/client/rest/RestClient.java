package ch.unartig.client.rest;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import java.io.*;

import ch.unartig.sportrait.client.WebServiceStub;

/**
 * Created by IntelliJ IDEA.
 * User: alexanderjosef
 * Date: Oct 13, 2008
 * Time: 1:25:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class RestClient {

    public static void main(String[] args) throws FileNotFoundException {
        File xmlFile = new File("/Users/alexanderjosef/WORK/sportrait-parent/sportrait-client-applet/src/test/resources/events.xml");
        new WebServiceStub().readEventsFromXmlFile(new FileInputStream(xmlFile));
    }

}

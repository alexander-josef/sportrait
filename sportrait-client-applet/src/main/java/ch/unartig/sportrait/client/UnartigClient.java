/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 15.08.2007$
 *
 * Copyright (c) 2007 Alexander Josef,unartig AG; All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 ****************************************************************/
package ch.unartig.sportrait.client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.policies.UploadPolicy;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import ch.unartig.sportrait.client.jUploadPolicies.UnartigSportraitUploadPolicy;
import ch.unartig.sportrait.client.forms.UnartigClientAppletForm;

/**
 * This class acts as a client to unartig. It can produce a panel containing the events and categories.
 * The class abstracts the xml-rpc / web service calls to the unartig server.
 */
public class UnartigClient implements ActionListener
{
    private UploadPolicy uploadPolicy;
    private static final String _PHOTOGRAPHER_PASS = "photographerPass";
    private static final String _PHOTOGRAPHER_ID = "photographerId";
    private static final String _XML_RPC_SERVER_URL = "xmlRpcServerUrl";
    private HashMap eventCategoryMap;
    private HashMap eventMap;
    private DefaultListModel sportraitEventListModel;
    private DefaultListModel sportraitEventCategoryListModel;
    private UnartigClientAppletForm form;



    public JPanel getUnartigUploadClientPanel(UploadPolicy uploadPolicy)
    {
        this.uploadPolicy = uploadPolicy;
        form = new UnartigClientAppletForm();


        sportraitEventListModel = new DefaultListModel();

        sportraitEventCategoryListModel = new DefaultListModel();
        form.chooseYourEventList.setModel(sportraitEventListModel);
        form.chooseYourEventList.setCellRenderer(new EventRenderer());

        form.chooseYourCategoryList.setModel(sportraitEventCategoryListModel);
        form.chooseYourCategoryList.setCellRenderer(new EventCategoryRenderer());

        form.chooseYourCategoryList.addListSelectionListener(new EventCategotyListSelectionListener());
        form.chooseYourEventList.addListSelectionListener(new EventListSelectionListener());

        try
        {
            this.loadEventList();
        } catch (Exception e)
        {
            throw new RuntimeException("Can not initialize events");
        }

        return form.eventCategoryChooserPanel;
    }




    private void debugRedBorder(JComponent targetLabel) {
        targetLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED),targetLabel.getBorder()));
    }

    public void loadEventList()
    {
        // insert events in list
        Object [] events;
        events = getSportraitEvents();

        for (Object event : events)
        {
            String eventKey = (String) event;
            sportraitEventListModel.addElement(eventKey);
        }
    }

    /**
     * Load the event categories from the server and reset the list-model with the result.
     * @param eventId id of event that has been selected
     */
    private void loadEventCategoryList(String eventId)
    {
        // insert events in list
        Object [] eventCategories;
        eventCategories = getSportraitEventCategories(eventId);
        System.out.println("eventCategories = " + Arrays.toString(eventCategories));
        sportraitEventCategoryListModel.clear();
        for (Object o : eventCategories)
        {
            String eventCategoryKey = (String) o;
            sportraitEventCategoryListModel.addElement(eventCategoryKey);
        }

        if (sportraitEventCategoryListModel.size() <1)
        {
            sportraitEventCategoryListModel.addElement("-1");
        }

    }

    /**
     * Make a call to the xmlRpc getSportraitEvents method; todo move 
     * @return the key-set of the map retrieved by the xml rpc service. the keys are the eventids.
     * @throws MalformedURLException
     * @throws XmlRpcException
     */
    private Object[] getSportraitEvents()
    {
        Object xmlRpcResult = getXmlRpcResult("AdminServices.getEvents",null);

        System.out.println("xmlRpcResult.getClass().getName() = " + xmlRpcResult.getClass().getName());

        // the xmlprc result is a map in the form: eventId:title
        eventMap = (HashMap) xmlRpcResult;
        System.out.println("albumMap.size() = " + eventMap.size());
        // debug output:
        for (Iterator iterator = eventMap.keySet().iterator(); iterator.hasNext();)
        {
            String key = (String) iterator.next();
            System.out.println("key = " + key);
            String eventName = (String) eventMap.get(key);
            System.out.println("eventName = " + eventName);
        }


        Object[] keys = eventMap.keySet().toArray();
        System.out.println("keys.getClass().getName() = " + keys.getClass().getName());
        return keys;
    }

    /**
     * Todo better routine name: this routine queries the server, sets the map with <categoryID,title> and return the keyset as array 
     * @return
     * @param eventId
     */
    private Object[] getSportraitEventCategories(String eventId)
    {
        java.util.List parameters = new ArrayList();
        parameters.add(eventId);
        Object xmlRpcResult = getXmlRpcResult("AdminServices.getEventCategories", parameters);
        System.out.println("xmlRpcResult.getClass().getName() = " + xmlRpcResult.getClass().getName());

        // the xmlprc result is a map:
        eventCategoryMap = (HashMap) xmlRpcResult;
        System.out.println("albumMap.size() = " + eventCategoryMap.size());
        for (Iterator iterator = eventCategoryMap.keySet().iterator(); iterator.hasNext();)
        {
            String key = (String) iterator.next();
            System.out.println("key = " + key);
            String eventName = (String) eventCategoryMap.get(key);
            System.out.println("eventName = " + eventName);
        }
        Object[] keys = eventCategoryMap.keySet().toArray();
        System.out.println("keys.getClass().getName() = " + keys.getClass().getName());
        return keys;
    }

    /**
     * Using the method name in the parameter, make an xmlrpc call.
     * @param xmlRpcMethod
     * @param parameterList
     * @return
     */
    private Object getXmlRpcResult(String xmlRpcMethod,java.util.List parameterList)
    {
        String photographerId = uploadPolicy.getApplet().getParameter(UnartigSportraitUploadPolicy.PROP_PHOTOGRAPHER_ID);
        String password = uploadPolicy.getApplet().getParameter(_PHOTOGRAPHER_PASS);
        String xmlRpcServerUrl = uploadPolicy.getApplet().getParameter(_XML_RPC_SERVER_URL);
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        final Object xmlRpcResult;
        try {
            config.setServerURL(new URL(xmlRpcServerUrl));
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            java.util.List xmlRpcParameterList = new ArrayList();
            xmlRpcParameterList.add(photographerId);
            xmlRpcParameterList.add(password);
            if (parameterList!=null)
            {
                for (int i = 0; i < parameterList.size(); i++)
                {
                    Object parameter = parameterList.get(i);
                    xmlRpcParameterList.add(parameter);
                }
            }
            System.out.println("going to execute xmlrpc-method");
            xmlRpcResult = client.execute(xmlRpcMethod, xmlRpcParameterList);
        } catch (Exception e) {
            System.out.println("error executing xml rpc");
            e.printStackTrace();
            throw new RuntimeException("Error executing xml rpc call, check stack trace",e);
        }
        return xmlRpcResult;
    }



    /**
     * Upon selecting or changing the album, transmit the id to the ... upload policy??
     * todo this needs to change. the parameters must be eventid and category id and then use the already existing routine for creating an album.
     * @param e
     */
    public void actionPerformed(ActionEvent e)
    {
        try
        {
            // todo adjust to event Id and category Id
            uploadPolicy.setProperty(UploadPolicy.PROP_ALBUM_ID,(String)form.chooseYourEventList.getSelectedValue());
        } catch (JUploadException e1)
        {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        }
    }
    
    private class EventListSelectionListener implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            System.out.println("UnartigClient$EventListSelectionListener.valueChanged");
            String eventId = (String)form.chooseYourEventList.getSelectedValue();
            System.out.println("event id = " + eventId);
            try
            {
                uploadPolicy.setProperty(UnartigSportraitUploadPolicy.PROP_EVENT_ID,eventId);
            } catch (JUploadException e1)
            {
                throw new RuntimeException("Error updating parameter");
            }
            // call loadCategoryList with event id
            loadEventCategoryList(eventId);
        }
    }

    private class EventCategotyListSelectionListener implements ListSelectionListener
    {

        public void valueChanged(ListSelectionEvent e)
        {
            try
            {
                uploadPolicy.setProperty(UnartigSportraitUploadPolicy.PROP_EVENT_CATEGORY_ID,(String)form.chooseYourCategoryList.getSelectedValue());
            } catch (JUploadException e1)
            {
                throw new RuntimeException("Problem choosing the event category");
            }
        }
    }




    /**
     * Renderer for the entry in the sportrait event list.
     */
    class EventRenderer implements ListCellRenderer{
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            System.out.println("renderer called");
            JLabel label;
            if (Long.parseLong((String) value) > 0)
            {
                label = new JLabel(eventMap.get(value) + " -- " + value);
                if (isSelected)
                {
                    label.setOpaque(true);
                    System.out.println("is selected");
                    label.setBackground(new Color(200,200,200));
                }
            } else
            {
                label = new JLabel("--Bitte wählen--");
                label.setHorizontalAlignment(SwingConstants.CENTER);
            }
            System.out.println("label.getBackground()" + label.getBackground());
            return label;
        }
    }

    /**
     * Renderer for the entry in the sportrait event-category list.
     */
    class EventCategoryRenderer implements ListCellRenderer{
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        {
            System.out.println("category renderer called");
            JLabel label;
            String valueString = (String) value;
            if (Long.parseLong(valueString) > 0)
            {
                label = new JLabel(eventCategoryMap.get(value) + " -- " + value);
                if (isSelected)
                {
                    label.setOpaque(true);
                    System.out.println("is selected");
                    label.setBackground(new Color(200,200,200));
                }
            } else
            {
                label = new JLabel("--Keine Kategorie vorhanden--");
                label.setHorizontalAlignment(SwingConstants.CENTER);
            }
            System.out.println("label.getBackground()" + label.getBackground());
            return label;
        }
    }

}

/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 03.07.2007$
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
package ch.unartig.u_core.beans;

import ch.unartig.u_core.controller.Client;
import ch.unartig.u_core.model.GenericLevel;

import java.util.List;

/**
 * Module Refactoing:
 *  + sportrait only; But may be refactored to contain common logic
 */
public class PhotographerAdminBean
{
    private Client client;
    private List productTypeList;
    private GenericLevel level;
    private List eventGroups;

    public PhotographerAdminBean(Client client)
    {

        this.client = client;
    }

    public void setProductTypeList(List productTypeList)
    {
        this.productTypeList = productTypeList;
    }

    public void setLevel(GenericLevel level)
    {
        this.level = level;
    }

    public void setEventGroups(List eventGroups)
    {
        this.eventGroups = eventGroups;
    }


    public Client getClient()
    {
        return client;
    }

    public List getProductTypeList()
    {
        return productTypeList;
    }

    public GenericLevel getLevel()
    {
        return level;
    }

    public List getEventGroups()
    {
        return eventGroups;
    }
}

/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since 10.10.2007$
 *
 * Copyright (c) 2006 Alexander Josef,unartig AG; All rights reserved
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
package ch.unartig.sportrait.zk.components;

import ch.unartig.controller.Client;
import ch.unartig.exceptions.NotAuthorizedException;
import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.studioserver.model.Album;
import ch.unartig.studioserver.model.Price;
import ch.unartig.studioserver.model.ProductType;
import ch.unartig.studioserver.model.Product;
import ch.unartig.studioserver.persistence.DAOs.GenericLevelDAO;
import ch.unartig.studioserver.persistence.DAOs.ProductTypeDAO;
import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Component to render an album configuration
 *
 * @author <a href=mailto:alexander.josef@unartig.ch>Alexander Josef</a>, (c) 2007 unartig AG
 */
@SuppressWarnings({"unchecked"})
public class ZAlbumComponent extends Div
{

    final Logger _logger = Logger.getLogger(this.getClass());
    private Album album;
    private Client client;
    private Grid albumInfoGrid;
    private Grid albumProductGrid;
    private Label productTitleLabel;

    public void onCreate()
    {
        _logger.debug("ZAlbumComponent.onCreate");
    }


    /**
     * Upon selection of an album, show the album edit div in the view
     * This method sets up the div to edit the album (Publish status, Products)
     *
     * @param albumId albumid
     */
    public void renderAlbumConfiguration(Long albumId)
    {
        try
        {
            GenericLevelDAO glDao = new GenericLevelDAO();

            this.album = (Album) glDao.load(albumId, Album.class);
            // make sure there's nothing on the page:
            cleanUp();

            albumInfoGrid = new Grid();
            Rows rows = new Rows();
            albumInfoGrid.appendChild(rows);

            // Album Details Info
            appendLabelRow(rows, "Anlass", album.getEvent().getLongTitle());
            appendLabelRow(rows, "Album", album.getLongTitle());

            // publish status
            renderPublishStatus(rows);
            this.appendChild(albumInfoGrid);

            renderProducts();
        } catch (Exception e)
        {
            _logger.error("error rendering album", e);
            throw new RuntimeException("Error rendering album!",e);
        }

    }

    private void renderProducts()
    {
        productTitleLabel = new Label("Produkte zu diesem Album");
        productTitleLabel.setSclass("sportraitTitle");
        this.appendChild(productTitleLabel);
        albumProductGrid = new Grid();
        Rows productRows = new Rows();
        albumProductGrid.appendChild(productRows);
        appendProductChildren(productRows);
        this.appendChild(albumProductGrid);
    }

    /**
     * For this album, rows for each possible product.
     *
     * @param productRows rows object ot attach the row s
     */
    private void appendProductChildren(Rows productRows)
    {

        ProductTypeDAO ptDao = new ProductTypeDAO();
        List productTypeList = ptDao.listProductTypes();

        Row row;


        for (Object aProductType : productTypeList)
        {
            final ProductType productType = (ProductType) aProductType;
            row = new Row();
            productRows.appendChild(row);
            row.appendChild(new Label(productType.getName()));
            renderPricesListbox(row, productType);
        }

    }

    private void renderPricesListbox(Row row, final ProductType productType)
    {
        final Listbox pricesListbox = new Listbox();
        pricesListbox.setMold("select");
        pricesListbox.setWidth("300px");
        // sort pricelist by price chf : done in mapping
        ArrayList priceList = new ArrayList(productType.getPrices());
        priceList.add(0, "Nicht verfügbar");
        final SimpleListModel simpleListModel = new SimpleListModel(priceList);
        pricesListbox.setModel(simpleListModel);
        // select the first entry, "not available"
        pricesListbox.setSelectedIndex(0);

        renderItem(productType, pricesListbox);
        pricesListbox.addEventListener(Events.ON_SELECT,new EventListener()
        {
            /**
             * Update album acording to selection
             * @param event fired zk event
             * @throws Exception
             */
            @SuppressWarnings({"unchecked"})
            public void onEvent(Event event) throws Exception
            {
                Listbox listbox = (Listbox) event.getTarget();
                Object price = listbox.getSelectedItem().getValue();
                Product product = album.getProductFor(productType.getProductTypeId());
                if (product == null )
                {
                    // create new product (if a price has been selected)
                    if (price instanceof Price)
                    {
                        Product newProduct = new Product(productType.getProductTypeId(),((Price)price).getPriceId(),album);
                        album.getProducts().add(newProduct);
                    }
                    else
                    {
                        // price has been set to not available and the product is null ... should not happen
                        throw new RuntimeException("unexpected state for updating products ...");
                    }
                }
                else
                {
                    // product exists
                    if (price instanceof Price)
                    {
                        // adjust price
                        product.setPrice((Price) price);
                    }
                    else
                    {
                        // price has been set to not available. Delete product
                        album.getProducts().remove(product);
                    }
                }
                GenericLevelDAO glDao = new GenericLevelDAO();
                glDao.saveOrUpdate(album);
                HibernateUtil.commitTransaction();
            }
        });
        row.appendChild(pricesListbox);
    }

    /**
     * Render a list box item, attach a listener
     * @param productType ProductType
     * @param pricesListbox Listbox containing the Price objects
     */
    private void renderItem(final ProductType productType, Listbox pricesListbox)
    {
        pricesListbox.setItemRenderer(new ListitemRenderer()
        {
            public void render(Listitem item, Object data) throws Exception
            {
                if (data instanceof Price)
                {
                    final Price price = (Price) data;
                    item.setLabel(price.getPriceLabel());
                    // set the price as value (it's not set just because the listmodel is of elements 'price')
                    item.setValue(price);
                    // check if price is selected:
                    final Product albumProduct = album.getProductFor(productType.getProductTypeId());
                    if (albumProduct != null && price.equals(albumProduct.getPrice()))
                    {
                        item.getListbox().clearSelection();
                        item.setSelected(true);
                    }
                } else
                {
                    item.setLabel("Nicht verf�gbar");
                }

            }
        });
    }

    /**
     * Detach all components that are possibly on this page
     */
    private void cleanUp()
    {
        if (albumInfoGrid != null)
        {
            albumInfoGrid.detach();
        }
        if (albumProductGrid != null)
        {
            albumProductGrid.detach();
        }

        if (productTitleLabel!=null)
        {
            productTitleLabel.detach();
        }
    }

    private void renderPublishStatus(Rows rows)
    {
        Hbox hbox = new Hbox();
        Label statusValue = new Label();
        hbox.appendChild(statusValue);
        Button button = new Button();
        hbox.appendChild(button);
        if (album.getPublish())
        {
            statusValue.setValue("online");
            statusValue.setStyle("background-color:green");
            button.setLabel("Offline schalten");
        } else
        {
            statusValue.setValue("offline");
            statusValue.setStyle("background-color:red");
            button.setLabel("Online schalten");
        }

        // ANONYMOUS INNER FOR EVENT HANDLING
        button.addEventListener(Events.ON_CLICK, new EventListener()
        {
            // the change publish status button has been pushed; change and update page
            public void onEvent(Event event) throws Exception
            {
                // todo notification about saved changes
                GenericLevelDAO glDao = new GenericLevelDAO();
                HibernateUtil.beginTransaction();
                Album innerAlbum = null;
                try
                {
                    innerAlbum = (Album) glDao.load(album.getGenericLevelId(), Album.class);
                    innerAlbum.toggleLevelPublishStatus(client);
                    glDao.saveOrUpdate(innerAlbum);
                    HibernateUtil.commitTransaction();
                } catch (NotAuthorizedException e)
                {
                    Messagebox.show("Not Authorized!!");
                    HibernateUtil.rollbackTransaction();
                } catch (UAPersistenceException e)
                {
                    HibernateUtil.rollbackTransaction();
                    Messagebox.show("Server Error, can not save album.");
                }
                // and then start a new transaction:
//                HibernateUtil.beginTransaction();
                renderAlbumConfiguration(innerAlbum.getGenericLevelId());
            }
        });
        appendLabelRow(rows, "Status", hbox);
    }

    private void appendLabelRow(Rows rows, String labelString, Component component)
    {
        Row row = new Row();
        Label labelLabel = new Label(labelString);
        labelLabel.setSclass("sportraitLabel");
        // width?
        row.appendChild(labelLabel);
        row.appendChild(component);
        rows.appendChild(row);
    }

    /**
     * Helper for appending a label/value row
     *
     * @param rows zk-rows
     * @param labelString label
     * @param valueString value
     */
    private void appendLabelRow(Rows rows, String labelString, String valueString)
    {
        Row row = new Row();
        Label labelLabel = new Label(labelString);
        labelLabel.setSclass("sportraitLabel");
        // width?
        row.appendChild(labelLabel);
        Label valueLabel = new Label(valueString);
        labelLabel.setSclass("sportraitInputEntry");
        row.appendChild(valueLabel);
        rows.appendChild(row);
    }


    public void setClient(Client client)
    {
        this.client = client;
    }
}

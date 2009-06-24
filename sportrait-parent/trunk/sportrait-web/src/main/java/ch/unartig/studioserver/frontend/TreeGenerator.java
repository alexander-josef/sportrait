/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Oct 6, 2005$
 *
 * Copyright (c) 2005 unartig AG  --  All rights reserved
 *
 * STATUS  :
 *    $Revision$, $State$, $Name$
 *
 *    $Author$, $Locker$
 *    $Date$
 *
 *************************************************
 * $Log$
 * Revision 1.1  2007/03/27 15:54:28  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.14  2006/11/12 13:32:49  alex
 * dynamic album ads
 *
 * Revision 1.13  2006/04/30 16:21:27  alex
 * removing system.outs
 *
 * Revision 1.12  2006/04/29 23:32:07  alex
 * many sola features, bugs, hibernate config
 *
 * Revision 1.11  2006/04/19 21:31:53  alex
 * session will be restored with album-bean (i.e. for bookmarked urls or so...)
 *
 * Revision 1.10  2006/02/16 17:13:46  alex
 * admin interface: deletion of levels works now
 *
 * Revision 1.9  2005/11/25 10:56:58  alex
 *
 * Revision 1.8  2005/11/08 13:22:58  alex
 * rename tree items. tree items now not in cvs ... generated at startup time
 *
 * Revision 1.7  2005/11/08 10:05:02  alex
 * tree items i18n, backend
 *
 * Revision 1.6  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.5  2005/11/04 17:12:18  alex
 * tree refactoring
 *
 * Revision 1.4  2005/10/24 13:50:07  alex
 * upload of album
 * import in db
 * processing of images
 *
 * Revision 1.3  2005/10/06 18:14:23  alex
 * saving new tree_items file
 *
 * Revision 1.2  2005/10/06 14:30:09  alex
 * generating the nav tree recursivly works
 *
 * Revision 1.1  2005/10/06 11:06:33  alex
 * generating the nav tree
 *
 ****************************************************************/
package ch.unartig.studioserver.frontend;

import ch.unartig.u_core.exceptions.UAPersistenceException;
import ch.unartig.u_core.exceptions.UnartigException;
import ch.unartig.u_core.Registry;
import ch.unartig.u_core.model.EventGroup;
import ch.unartig.u_core.model.Category;
import ch.unartig.u_core.persistence.DAOs.GenericLevelDAO;
import ch.unartig.u_core.util.FileUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class goes through all events and creates the javascript and static links for navigating the events categories and events
 */
public class TreeGenerator
{
    Logger _logger = Logger.getLogger(getClass().getName());

    /**
     * generate all the i18n ized tree items files!
     *
     * @throws ch.unartig.u_core.exceptions.UAPersistenceException
     */
    public void generateTreeItems() throws UnartigException {
        GenericLevelDAO glDao = new GenericLevelDAO();
        StringBuffer treeItems;
        StringBuffer staticNoscriptLinks = new StringBuffer();
        StringBuffer sb;
        List categories = glDao.listGenericLevel(Category.class);

        String[] langSuffixes = Registry._LANG_SUFFIXES;
        for (int langIndex = 0; langIndex < langSuffixes.length; langIndex++)
        {
            String langSuffix = langSuffixes[langIndex];
            treeItems = new StringBuffer();

            _logger.debug("number of categories in tree : " + categories.size());
            treeItems.append("[\n" + "\t['',null,null,");

            for (int catIndex = 0; catIndex < categories.size(); catIndex++)
            {
                Category category = (Category) categories.get(catIndex);
                _logger.debug("nav tree for : " + category.getLongTitle());
                sb = new StringBuffer();
                treeItems.append(category.composeTreeItem(sb, langSuffix));
            }
            treeItems.append("\t]\n" + "]");

            storeTree(treeItems, langSuffix);
        }

        // create the static links for the <noscript> insert:
        for (int i = 0; i < categories.size(); i++)
        {
            Category category = (Category) categories.get(i);
            staticNoscriptLinks
                    .append("<h1><span><a href=\"")
                    .append(category.getIndexNavLink())
                    .append("\">")
                    .append(category.getNavTitle())
                    .append("</a></span></h1>\n");
            for (Iterator iterator = category.getEventGroups().iterator(); iterator.hasNext();)
            {
                EventGroup eventGroup = (EventGroup) iterator.next();
                staticNoscriptLinks
                        .append("<h1 class=\"indent\"><a href=\"")
                        .append(eventGroup.getIndexNavLink())
                        .append("\">")
                        .append(eventGroup.getNavTitle())
                        .append("</a></h1>\n");
            }

        }

        _logger.debug("static noscript links:");
        _logger.debug(staticNoscriptLinks.toString());

        storeStaticEventLinks(staticNoscriptLinks);

    }

    private void storeStaticEventLinks(StringBuffer staticNoscriptLinks) throws UnartigException
    {
        File generatedStaticEventLinks = new File(Registry.frontendDirectory, "WEB-INF/protected/generatedStaticEventLinks.jsp");

        try
        {
            _logger.debug("going to write : " + generatedStaticEventLinks.getAbsolutePath());
            FileUtils.copyFile(new ByteArrayInputStream(staticNoscriptLinks.toString().getBytes()), generatedStaticEventLinks);
        } catch (IOException e)
        {
            _logger.error("cannot write tree items File!");
            throw new UnartigException("cannot write tree items file", e);
        }
    }


    private void storeTree(StringBuffer treeItems, String langSuffix) throws UAPersistenceException
    {
        StringBuffer fileStringBuffer = new StringBuffer();
        fileStringBuffer.append("/* Auto-Generated  [").append(new Date()).append("]  for Tiagra Tree Menu Pro */ ");
        fileStringBuffer.append(System.getProperty("line.separator"));
        fileStringBuffer.append("var TREE_ITEMS =");
        fileStringBuffer.append(treeItems);

        File jsTreeItemDir = new File(Registry.frontendDirectory, Registry.jsTreeDirectory);
        File jsTreeItemFile = new File(jsTreeItemDir, Registry.getTreeItemsFilePrefix() + langSuffix + Registry._JAVA_SCRIPT_SUFFIX);

        try
        {
            _logger.debug("going to write : " + jsTreeItemFile.getAbsolutePath());
            FileUtils.copyFile(new ByteArrayInputStream(fileStringBuffer.toString().getBytes()), jsTreeItemFile);
        } catch (IOException e)
        {
            _logger.error("cannot write tree items File!");
            throw new UAPersistenceException("cannot write tree items file", e);
        }

    }

}

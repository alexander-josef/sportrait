/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$
 *    @since Sep 22, 2005$
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
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.2  2007/03/14 02:41:01  alex
 * initial checkin
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.27  2006/03/03 16:54:56  alex
 * minor fixes
 *
 * Revision 1.26  2006/02/23 14:37:42  alex
 * admin tool: new category works now
 *
 * Revision 1.25  2006/02/16 17:13:46  alex
 * admin interface: deletion of levels works now
 *
 * Revision 1.24  2006/02/15 15:57:03  alex
 * bug [968] fixed. admin interface does that now
 *
 * Revision 1.23  2006/02/13 16:15:28  alex
 * but [968]
 *
 * Revision 1.22  2006/02/10 14:21:49  alex
 * admin tool: edit level partly ...
 *
 * Revision 1.21  2006/02/06 13:37:44  alex
 * merged from r1.5
 *
 * Revision 1.20  2006/01/30 17:28:41  alex
 * merged from r1.5
 *
 * Revision 1.19.4.1  2006/01/30 17:22:02  alex
 * tool tipps removed, st auf 1 (state = open)
 *
 * Revision 1.19  2005/11/30 10:47:38  alex
 * bug fixes
 *
 * Revision 1.18  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.17  2005/11/25 10:56:58  alex
 *
 * Revision 1.16  2005/11/23 21:58:43  alex
 * bug-fixes
 *
 * Revision 1.15  2005/11/20 21:24:32  alex
 * side navigation fixes
 *
 * Revision 1.14  2005/11/19 11:08:20  alex
 * index navigation works, (extended GenericLevel functions)
 * wrong calculation of fixed checkout overview eliminated
 *
 * Revision 1.13  2005/11/08 10:05:02  alex
 * tree items i18n, backend
 *
 * Revision 1.12  2005/11/08 09:33:45  alex
 * languages
 *
 * Revision 1.11  2005/11/07 21:57:43  alex
 * admin interface refactored
 *
 * Revision 1.10  2005/11/07 17:38:26  alex
 * admin interface refactored
 *
 * Revision 1.9  2005/11/05 21:41:58  alex
 * overview und links in tree menu
 *
 * Revision 1.8  2005/11/04 23:02:54  alex
 * shopping cart session
 *
 * Revision 1.7  2005/11/04 17:28:35  alex
 * tree refactoring
 *
 * Revision 1.6  2005/11/04 17:12:18  alex
 * tree refactoring
 *
 * Revision 1.5  2005/10/06 14:30:09  alex
 * generating the nav tree recursivly works
 *
 * Revision 1.4  2005/10/06 11:06:33  alex
 * generating the nav tree
 *
 * Revision 1.3  2005/10/04 11:36:48  alex
 * level imports
 *
 * Revision 1.2  2005/10/03 14:50:25  alex
 * first daos
 *
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 ****************************************************************/
package ch.unartig.studioserver.model;

import ch.unartig.exceptions.UAPersistenceException;
import ch.unartig.exceptions.UnartigException;
import ch.unartig.studioserver.Registry;
import ch.unartig.studioserver.businesslogic.GenericLevelVisitor;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 */
public class Category extends GeneratedCategory
{
    Logger _logger = Logger.getLogger(getClass().getName());

    public Category()
    {
    }

    public void accept(GenericLevelVisitor visitor)
    {
        try
        {
            visitor.visit(this);
        } catch (UnartigException e)
        {
            _logger.error("visitor threw exception",e);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public Category(String navTitle, String longTitle, String description)
    {
        setNavTitle(navTitle);
        setLongTitle(longTitle);
        setDescription(description);
    }

    /**
     * return all eventGroups for this category
     *
     * @return list of eventGroups
     */
    public List listChildrenForNavTree()
    {
        return listChildren();
    }


    /**
     * overridden for category: return 0 as link
     * @return the link to be shown in the nav tree
     */
//    public String getTreeNavLink()
//    {
//        return "0";
//    }

    public List listChildren()
    {
        return new ArrayList(getEventGroups());
    }

    public Class getParentClazz()
    {
        return null;
    }

    public String getLevelType()
    {
        return "Category";
    }

    public void setParentLevel(GenericLevel parentLevel)
            throws UnartigException {
        throw new UnartigException("No Parent Level for Category!");

    }

    public GenericLevel getParentLevel()
    {
        // category has no parent event. category is root
        // return this as parent?
        return this;
    }

    public String[] getIndexNavEntry()
    {
        return new String[]{getIndexNavLink(),getNavTitle()};
    }

    public String getEventDateDisplay()
    {
        return "";
    }


    /**
     * overriden nav-Title method; return empty string in apostrophes .... text is in images
     *
     * @return String for the category nav title which is ''
     */
    public String getTreeNavTitle()
    {
        return "";
    }

    /**
     * custom category implemention using the overwritten item scope seetings
     * <ul>
     * <li>DON'T FORGET TO INCLUDE THE "'" character
     * <li>DON'T FORGET: NO !!! no comma in the last item scope settings line
     * </ul>
     * the name of the category is represented with an image:<br>
     * image names follow this schema:<br>
     * [nav Title]_[icon-state]_[language depending suffix].gif<br>
     * example: sport_anlaesse_closed_de.gif  (for category with nav title 'sport_anlaesse')
     * @return the item scope settings as string
     * @param langSuffix
     * @noinspection StringConcatenationInsideStringBufferAppend
     */
    protected String getItemScopeSettings(String langSuffix)
    {
        StringBuffer sb = new StringBuffer();

        String languageDepImageSuffix="_"+langSuffix+".gif";
        sb.append("{'st' : 1,\n" +
                "\t\t\t\t\t'sb' : '"+ this.getLongTitle()+"', \n" +
                "\t\t\t\t\t'i0' : '"+ Registry._TREE_ICONS_PATH +getNavTitle()+Registry._ICON_FOR_ITEM +languageDepImageSuffix+"', \n" +
                "\t\t\t\t\t'i4' : '"+ Registry._TREE_ICONS_PATH +getNavTitle()+Registry._ICON_FOR_SELECTED_ITEM +languageDepImageSuffix+"', \n" +
                "\t\t\t\t\t'i8' : '"+ Registry._TREE_ICONS_PATH +getNavTitle()+Registry._ICON_FOR_OPENED_ITEM +languageDepImageSuffix+"', \n" +
                "\t\t\t\t\t'i12' : '"+ Registry._TREE_ICONS_PATH +getNavTitle()+Registry._ICON_FOR_SELECTED_OPEN_ITEM +languageDepImageSuffix+"', \n" +
                "\t\t\t\t\t'i64' : '"+ Registry._TREE_ICONS_PATH +getNavTitle()+Registry._ICON_FOR_ITEM_MOUSE_OVER +languageDepImageSuffix+"', \n" +
                "\t\t\t\t\t'i68' : '"+ Registry._TREE_ICONS_PATH +getNavTitle()+Registry._ICON_FOR_SELECTED_ITEM_MOUSE_OVER +languageDepImageSuffix+"', \n" +
                "\t\t\t\t\t'i72' : '"+ Registry._TREE_ICONS_PATH +getNavTitle()+Registry._ICON_FOR_OPENED_ITEM_MOUSE_OVER +languageDepImageSuffix+"', \n" +
                "\t\t\t\t\t'i76' : '"+ Registry._TREE_ICONS_PATH +getNavTitle()+Registry._ICON_FOR_SELECTED_OPENED_ITEM_MOUSE_OVER +languageDepImageSuffix+"', \n" +
                "\t\t\t\t\t's4' : '"+Registry._PSEUDO_ROOT_CLASS+"', \n" +
                "\t\t\t\t\t's12' : '"+Registry._PSEUDO_ROOT_CLASS+"', \n" +
                "\t\t\t\t\t's68' : '"+Registry._PSEUDO_ROOT_CLASS+"', \n" +
                "\t\t\t\t\t's76' : '"+Registry._PSEUDO_ROOT_CLASS+"' \n" +
                "\t\t\t\t\t}");

        return sb.toString();
    }

    public boolean isCategoryLevel()
    {
        return true;
    }

    /**
     * no delete logic needed for category; loop through all eventgroups and delete
     */
    public void deleteLevel() throws UAPersistenceException
    {
        _logger.debug("Category.deleteLevel");
        for (Iterator iterator = getEventGroups().iterator(); iterator.hasNext();)
        {
            EventGroup eventGroup = (EventGroup) iterator.next();
            eventGroup.deleteLevel();
        }
    }

}
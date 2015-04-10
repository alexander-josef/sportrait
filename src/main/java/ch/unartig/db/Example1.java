package ch.unartig.db;

import java.sql.*;

/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author $USER$
 *    @since $DATE$
 *
 * Copyright (c) 2003 Alexander Josef All rights reserved
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
 * Revision 1.1  2005/09/26 18:37:45  alex
 * *** empty log message ***
 *
 * Revision 1.1  2004/09/27 19:23:48  alex
 * hbm2dll installed
 *
 * Revision 1.1  2004/09/20 14:39:13  alex
 * new module importet. the unartig-struts branch now is in module unartig/unartig-gallery
 *
 * Revision 1.2  2004/01/05 14:32:03  alex
 * new file template
 *
 ****************************************************************/

public class Example1
{

    public Example1()
    {
    }

    public static Connection c = null;

    public static void checkConnection() throws SQLException
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            System.out.println(" * found Driver....");
        } catch (ClassNotFoundException cnfe)
        {
            System.out.println("Database driver not found");
            cnfe.printStackTrace();
            //System.exit(1);
        }

        System.out.println("Registered the driver ok, let's make a connection.");


        System.out.println("********** trying to connect  ***************");
        try
        {
            c = DriverManager.getConnection("jdbc:postgresql://127.0.0.1/unartigdb", "unartig", "unartig");
        } catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        if (c != null)
        {
            System.out.println("Bingo! ...connected to the database!");
        }
        else
        {
            System.out.println("....huärä Scheiss... not connected!");
        }
        System.out.print("releasing connection .....");
        try
        {
            while (!c.isClosed())
            {
                c.close();
            }
        } catch (SQLException se)
        {
        }
        System.out.println("closed!");
    }

    private void insertSingle()
    {
        Statement s = null;
        try
        {
            s = c.createStatement();
        } catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        int m = 0;

        try
        {
            m = s.executeUpdate("INSERT INTO project (name,comment) VALUES ('zweites','kommentar')");
        } catch (SQLException e)
        {
            System.out.println("INSERT failed");
            e.printStackTrace();
        }
        System.out.println("Success ! Modified " + m + " rows");
    }

    private void insertMultiple()
    {
        PreparedStatement ps = null;
        try
        {
            ps = c.prepareStatement("INSERT INTO project (name,comment) VALUES (?,?)");

        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        try
        {
            ps.setString(1, "drittes");
            ps.setString(2, "anotherComment");
            ps.executeUpdate();
        } catch (SQLException e)
        {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
}

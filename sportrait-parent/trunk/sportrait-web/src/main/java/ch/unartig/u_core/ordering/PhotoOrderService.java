/*-*
 *
 * FILENAME  :
 *    $RCSfile$
 *
 *    @author alex$             
 *    @since Nov 21, 2005$
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
 * Revision 1.2  2007/05/04 16:56:00  alex
 * some fixes
 *
 * Revision 1.1  2007/03/27 15:54:27  alex
 * initial commit sportrait code base
 *
 * Revision 1.1  2007/03/01 18:23:41  alex
 * initial commit maven setup no history
 *
 * Revision 1.6  2005/11/25 13:22:24  alex
 * resources
 *
 * Revision 1.5  2005/11/25 11:09:09  alex
 * removed system outs
 *
 * Revision 1.4  2005/11/23 21:22:57  alex
 * bug-fixes
 *
 * Revision 1.3  2005/11/23 20:52:10  alex
 * bug-fixes
 *
 * Revision 1.2  2005/11/22 21:33:16  alex
 * ordering process
 *
 * Revision 1.1  2005/11/21 09:58:23  alex
 * init. version
 *
 ****************************************************************/
package ch.unartig.u_core.ordering;

import ch.unartig.u_core.Registry;
import ch.unartig.u_core.ordering.TimedOrderProcess;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * This singelton starts the daily order process to order all open photo-orders
 */
public class PhotoOrderService
{
    public static final int _SEC = 1000;
    public static final int _MINUTE = 60 * _SEC;
    public static final int _HOUR = 60 * _MINUTE;

    private static Timer timer;
    private static final PhotoOrderService _instance = new PhotoOrderService();
    private static final String _NAME = "TimedOrderProcess";

    
    public static final long PERIOD = 24 * _HOUR;// 86400000 one day
    public  Date firstRun; // 10 sec
    private static final long _TEST_DELAY = 10000;
    private static final long _TEST_PERIOD = 10 * _MINUTE;

    private PhotoOrderService()
    {
    }

    /**
     * get a singelton instance of this class
     *
     * @return a photo order service
     */
    public static PhotoOrderService getInstance()
    {
        return _instance;
    }

    public boolean startService()
    {
        timer = new Timer();
        setFirstRun();
        startTimedOrderProcess();
        return true;
    }

    /**
     * upon start of the service, calculate the new time when the service runs first<br/>
     * it will be 5:07 AM of the following day
     */
    private void setFirstRun()
    {
        Date timeNow = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(timeNow);
        c.add(Calendar.DAY_OF_YEAR,1);
        c.set(Calendar.HOUR_OF_DAY,5);
        c.set(Calendar.MINUTE,7);
        setFirstRun(c.getTime());

    }


    public boolean stopService()
    {
        timer.cancel();
        return true;
    }


    private void startTimedOrderProcess()
    {
        TimedOrderProcess orderProcess = new TimedOrderProcess();
        if (Registry.isDemoOrderMode())
        {
            timer.scheduleAtFixedRate(orderProcess,_TEST_DELAY,_TEST_PERIOD);
        } else
        {
            timer.scheduleAtFixedRate(orderProcess, getFirstRun(), PERIOD);
        }
    }

    public Date getFirstRun()
    {
        return firstRun;
    }

    public void setFirstRun(Date firstRun)
    {
        this.firstRun = firstRun;
    }

}

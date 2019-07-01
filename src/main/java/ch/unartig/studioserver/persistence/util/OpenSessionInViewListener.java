package ch.unartig.studioserver.persistence.util;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.util.ExecutionCleanup;
import org.zkoss.zk.ui.util.ExecutionInit;

import java.util.List;

/**
 * Writing a custom listener after hibernate 5.4 migration
 */
public class OpenSessionInViewListener implements ExecutionInit, ExecutionCleanup {
    private final Logger _logger = Logger.getLogger(this.getClass());

    public void init(Execution exec, Execution parent) {
        if (parent == null) { //the root execution of a servlet request
            _logger.debug("Started a database transaction (zk listener): "+exec);
            // transaction should be open with every call to currentSession()
            HibernateUtil.currentSession();
        }
        // todo: does this make sense?
        // HibernateUtil.currentSession().clear();
    }

    public void cleanup(Execution exec, Execution parent, List errs) {
        if (parent == null) { //the root execution of a servlet request
            if (errs == null || errs.isEmpty()) {
                _logger.debug("Committing the database transaction after zk unit of work: "+exec);
                HibernateUtil.currentSession().getTransaction().commit();
            } else {
                final Throwable ex = (Throwable) errs.get(0);
                rollback(exec, ex);
            }
        }
    }

    private void rollback(Execution exec, Throwable ex) {
        _logger.info("****** ROLLBACK ********");
        _logger.info(Thread.currentThread().getStackTrace());
        try {
            if (HibernateUtil.currentSession().getTransaction().isActive()) {
                _logger.info("Trying to rollback database transaction after exception:"+ex);
                HibernateUtil.currentSession().getTransaction().rollback();
            }
        } catch (Throwable rbEx) {
            _logger.error("Could not rollback transaction after exception! Original Exception:\n"+ex, rbEx);
        }
    }
}
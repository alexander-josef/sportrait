package com.sportrait.importrs;

import ch.unartig.studioserver.persistence.util.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class HibernateResponseServerFilter implements ContainerResponseFilter {

    private final Logger _logger = Logger.getLogger(getClass().getName());

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        // responseContext.getHeaders().add("X-Test", "Filter test");

        // make sure Hibernate Transaction is committed, session is flushed and closed:
        if (!requestContext.getMethod().equals(HttpMethod.OPTIONS)) { // ignore OPTIONS requests (see CORS)
            try {
                _logger.debug("Jersey Response - Going to finish Hibernate Transaction/Sessions");
                HibernateUtil.currentSession().getTransaction().commit();
                HibernateUtil.currentSession().flush();
                _logger.debug("... Session flushed");
            } catch (HibernateException e) {
                _logger.info("Exception during transaction commit - rolling back.");
                HibernateUtil.rollbackTransaction();
            } finally {
                HibernateUtil.currentSession().close();
                _logger.debug("... Session closed");
            }
        } else {
            _logger.debug("Ignoring request with method ["+requestContext.getMethod()+"]");
        }

    }
}
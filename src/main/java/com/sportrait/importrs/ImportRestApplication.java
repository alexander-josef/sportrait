package com.sportrait.importrs;

import ch.unartig.studioserver.Registry;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.glassfish.jersey.server.monitoring.ApplicationEvent.Type.INITIALIZATION_FINISHED;

@ApplicationPath("/api/import")
public class ImportRestApplication extends Application implements ApplicationEventListener {

    /**
     * check for events and initialize the REST API application
     *
     * @param applicationEvent
     */
    @Override
    public void onEvent(ApplicationEvent applicationEvent) {

        // this gets called after Jersey has finished initialisation //  good place for init methods?
        if (applicationEvent.getType() == INITIALIZATION_FINISHED) {
            try {
                Registry.init();
                System.out.println("***** Registry.init() from Jersey API Import App ******");
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return null;
    }
}
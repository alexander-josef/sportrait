package com.sportrait.importrs;

import ch.unartig.studioserver.Registry;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.MultiPartProperties;
import org.glassfish.jersey.server.ResourceConfig;
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
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("com.sportrait.importrs").register(MultiPartFeature.class);
    }
}
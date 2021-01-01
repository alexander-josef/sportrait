package com.sportrait.importrs;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/import")
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages("com.sportrait.importrs").register(MultiPartFeature.class);
    }
}
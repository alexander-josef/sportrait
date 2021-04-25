package com.sportrait.importrs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * CORS request servlet filter to add the necessary response headers
 * todo : NOT A GOOD SOLUTION !!!
 * Add here the correct Origin or make sure CORS is not an issue
 * And read about preflight tests
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {
    private final Logger _logger = LogManager.getLogger();

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        _logger.debug("adding CORS headers - CAUTION : adding * for access-control-allow-origin");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
    }
}
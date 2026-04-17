package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * JAX-RS filter for logging all incoming requests and outgoing responses.
 * 
 * Implements both ContainerRequestFilter and ContainerResponseFilter to
 * provide a cross-cutting logging concern that applies to ALL endpoints
 * without modifying individual resource methods.
 * 
 * This follows the Single Responsibility Principle — resource methods
 * focus on business logic while this filter handles logging as a
 * separate, orthogonal concern.
 * 
 * Annotated with @Provider for automatic discovery by the JAX-RS runtime.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Logs the incoming HTTP request method and URI.
     * Called BEFORE the request reaches the resource method.
     *
     * @param requestContext the request context
     * @throws IOException if an I/O exception occurs
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        LOGGER.info("→ Request: " + method + " " + path);
    }

    /**
     * Logs the outgoing HTTP response status code.
     * Called AFTER the resource method has processed the request.
     *
     * @param requestContext  the request context
     * @param responseContext the response context
     * @throws IOException if an I/O exception occurs
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getRequestUri().getPath();
        LOGGER.info("← Response: " + status + " | " + method + " " + path);
    }
}

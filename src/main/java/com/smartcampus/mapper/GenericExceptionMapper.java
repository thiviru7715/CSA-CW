package com.smartcampus.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global "safety net" exception mapper that catches ALL unhandled exceptions.
 * 
 * Returns a generic HTTP 500 Internal Server Error response with a safe JSON
 * body that does not leak internal stack traces or sensitive information to
 * the client. The actual exception details are logged server-side for debugging.
 * 
 * If the exception is a JAX-RS WebApplicationException (e.g. NotFoundException),
 * the mapper preserves the original HTTP status code while still returning
 * a consistent JSON error format.
 * 
 * This follows OWASP best practices for error handling — never expose
 * internal implementation details to external API consumers.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {

        // If it's a JAX-RS WebApplicationException (e.g. NotFoundException, BadRequestException),
        // preserve the original status code instead of defaulting to 500
        if (exception instanceof WebApplicationException) {
            WebApplicationException webEx = (WebApplicationException) exception;
            int status = webEx.getResponse().getStatus();

            LOGGER.log(Level.WARNING, "JAX-RS exception: {0} (HTTP {1})",
                    new Object[]{exception.getMessage(), status});

            String json = String.format(
                    "{\"error\": \"%s\", \"code\": %d, \"message\": \"%s\", \"timestamp\": %d}",
                    Response.Status.fromStatusCode(status) != null
                            ? Response.Status.fromStatusCode(status).getReasonPhrase().toUpperCase().replace(" ", "_")
                            : "ERROR",
                    status,
                    exception.getMessage().replace("\"", "\\\""),
                    System.currentTimeMillis()
            );

            return Response.status(status)
                    .entity(json)
                    .type("application/json")
                    .build();
        }

        // For all other unexpected exceptions → 500 Internal Server Error
        LOGGER.log(Level.SEVERE, "Unhandled exception caught by GenericExceptionMapper: " + exception.getMessage(), exception);

        String json = String.format(
                "{\"error\": \"INTERNAL_SERVER_ERROR\", \"code\": 500, \"message\": \"An unexpected error occurred. Please contact the administrator.\", \"timestamp\": %d}",
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(json)
                .type("application/json")
                .build();
    }
}

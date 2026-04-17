package com.smartcampus.mapper;

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
 * This follows OWASP best practices for error handling — never expose
 * internal implementation details to external API consumers.
 */
@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GenericExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // Log the full exception server-side for debugging
        LOGGER.log(Level.SEVERE, "Unhandled exception caught by GenericExceptionMapper: " + exception.getMessage(), exception);

        // Return a generic error response — no stack trace, no internal details
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

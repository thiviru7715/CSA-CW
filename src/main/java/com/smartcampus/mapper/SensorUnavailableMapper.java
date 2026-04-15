package com.smartcampus.mapper;

import com.smartcampus.exception.SensorUnavailableException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for SensorUnavailableException.
 * Maps to HTTP 403 Forbidden.
 */
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        String json = String.format(
                "{\"error\": \"FORBIDDEN\", \"code\": 403, \"message\": \"%s\", \"timestamp\": %d}",
                exception.getMessage().replace("\"", "\\\""),
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.FORBIDDEN)
                .entity(json)
                .type("application/json")
                .build();
    }
}

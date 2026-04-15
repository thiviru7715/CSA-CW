package com.smartcampus.mapper;

import com.smartcampus.exception.LinkedResourceNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for LinkedResourceNotFoundException.
 * Maps to HTTP 422 Unprocessable Entity.
 */
@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        String json = String.format(
                "{\"error\": \"UNPROCESSABLE_ENTITY\", \"code\": 422, \"message\": \"%s\", \"timestamp\": %d}",
                exception.getMessage().replace("\"", "\\\""),
                System.currentTimeMillis()
        );

        return Response.status(422) // 422 Unprocessable Entity
                .entity(json)
                .type("application/json")
                .build();
    }
}

package com.smartcampus.mapper;

import com.smartcampus.exception.RoomNotEmptyException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for RoomNotEmptyException.
 * Maps to HTTP 409 Conflict.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        String json = String.format(
                "{\"error\": \"CONFLICT\", \"code\": 409, \"message\": \"%s\", \"timestamp\": %d}",
                exception.getMessage().replace("\"", "\\\""),
                System.currentTimeMillis()
        );

        return Response.status(Response.Status.CONFLICT)
                .entity(json)
                .type("application/json")
                .build();
    }
}

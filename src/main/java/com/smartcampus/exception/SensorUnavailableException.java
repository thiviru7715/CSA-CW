package com.smartcampus.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class SensorUnavailableException extends WebApplicationException {

    public SensorUnavailableException(String message) {
        super(Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\": \"FORBIDDEN\", \"message\": \"" + message + "\"}")
                .type("application/json")
                .build());
    }
}

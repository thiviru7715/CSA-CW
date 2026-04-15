package com.smartcampus.exception;

/**
 * Exception thrown when posting a reading to a sensor in MAINTENANCE mode.
 * This should be mapped to an HTTP 403 Forbidden.
 */
public class SensorUnavailableException extends RuntimeException {

    public SensorUnavailableException(String message) {
        super(message);
    }
}

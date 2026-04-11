package com.smartcampus.exception;

/**
 * Exception thrown when a resource references another resource that does not exist.
 * For example, when creating a sensor that references a non-existent room ID.
 * This should be mapped to an HTTP 422 Unprocessable Entity.
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}

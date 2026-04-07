package com.smartcampus.exception;

/**
 * Exception thrown when attempting to delete a room that still has active sensors assigned to it.
 * This should be mapped to an HTTP 409 Conflict.
 */
public class RoomNotEmptyException extends RuntimeException {

    public RoomNotEmptyException(String message) {
        super(message);
    }
}

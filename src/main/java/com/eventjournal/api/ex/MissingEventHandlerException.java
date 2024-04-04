package com.eventjournal.api.ex;

/**
 * Thrown when an event handler (apply method) is missing.
 */
public class MissingEventHandlerException extends RuntimeException {

    public MissingEventHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}

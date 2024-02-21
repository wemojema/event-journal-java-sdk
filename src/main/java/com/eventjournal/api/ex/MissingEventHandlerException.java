package com.eventjournal.api.ex;

public class MissingEventHandlerException extends RuntimeException {
    public MissingEventHandlerException(String message) {
        super(message);
    }

    public MissingEventHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}

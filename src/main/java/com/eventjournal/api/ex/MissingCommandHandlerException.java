package com.eventjournal.api.ex;

public class MissingCommandHandlerException extends RuntimeException {
    public MissingCommandHandlerException(String message) {
        super(message);
    }
}

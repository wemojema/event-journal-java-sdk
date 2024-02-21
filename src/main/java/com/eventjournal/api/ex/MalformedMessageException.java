package com.eventjournal.api.ex;

public class MalformedMessageException extends RuntimeException {
    public MalformedMessageException(String message) {
        super(message);
    }

    public MalformedMessageException(String message, Throwable cause) {
        super(message, cause);
    }

}

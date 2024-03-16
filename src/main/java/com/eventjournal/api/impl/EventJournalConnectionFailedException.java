package com.eventjournal.api.impl;

public class EventJournalConnectionFailedException extends RuntimeException {
    public EventJournalConnectionFailedException(String message) {
        super(message);
    }

    public EventJournalConnectionFailedException(String message, Exception e) {
        super(message, e);
    }
}

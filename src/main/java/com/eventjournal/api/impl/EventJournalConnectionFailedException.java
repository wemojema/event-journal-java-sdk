package com.eventjournal.api.impl;

/**
 * Exception thrown when the connection to the event journal host fails.
 * This can be due to network issues, invalid API keys, or other reasons.
 */
public class EventJournalConnectionFailedException extends RuntimeException {
    public EventJournalConnectionFailedException(String message) {
        super(message);
    }

    public EventJournalConnectionFailedException(String message, Exception e) {
        super(message, e);
    }
}

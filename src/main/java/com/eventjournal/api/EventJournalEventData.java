package com.eventjournal.api;

/**
 * Event data for an event journal event.
 */
public class EventJournalEventData {
    private String serializedMessage;

    public EventJournalEventData(String serializedMessage) {
        this.serializedMessage = serializedMessage;
    }

    private EventJournalEventData() {
    }

    @Override
    public String toString() {
        return getSerializedMessage();
    }

    public String getSerializedMessage() {
        return serializedMessage;
    }
}

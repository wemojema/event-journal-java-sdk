package com.eventjournal.api;

/**
 * Event data for an event journal event.
 * Implements the CloudEventData interface.
 */
public class EventJournalEventData {
    private String serializedMessage;

    public EventJournalEventData(String serializedMessage) {
        this.serializedMessage = serializedMessage;
    }

    private EventJournalEventData() {
    }

    public byte[] toBytes() {
        return serializedMessage.getBytes();
    }

    @Override
    public String toString() {
        return getSerializedMessage();
    }

    public String getSerializedMessage() {
        return serializedMessage;
    }
}

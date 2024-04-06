package com.eventjournal.api;

import io.cloudevents.CloudEventData;

/**
 * Event data for an event journal event.
 * Implements the CloudEventData interface.
 */
public class EventJournalEventData implements CloudEventData {
    private String serializedMessage;

    public EventJournalEventData(String serializedMessage) {
        this.serializedMessage = serializedMessage;
    }

    private EventJournalEventData() {
    }

    @Override
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

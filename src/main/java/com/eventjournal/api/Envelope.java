package com.eventjournal.api;


import com.eventjournal.api.impl.EventJournal;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * The Envelope is a CloudEvent implementation that wraps a Message.
 * It is the primary data structure used by the EventJournal API, and contains
 * the MessageHeader and the serialized Message. The Envelope is used to
 * carry the Message across applications and services.
 */
public class Envelope {
    private Header header;
    private EventJournalEventData data;


    private Envelope() {
    }

    public static Envelope wrap(Message message) {
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(message.header(), "Message header cannot be null");
        Objects.requireNonNull(message.timestamp(), "Message timestamp cannot be null");
        Objects.requireNonNull(message.messageCategory(), "Message category cannot be null");
        Objects.requireNonNull(message.streamId(), "Message streamId cannot be null");
        return new Envelope(EventJournal.Toolbox.serialize(message), message.header());
    }

    /**
     * @param header            the MessageHeader of this message
     * @param serializedMessage the serialized message (typically stringified json)
     */
    private Envelope(String serializedMessage, Header header) {
        this.header = header;
        this.data = new EventJournalEventData(serializedMessage);
    }

    public String streamId() {
        return header.get("streamId")
                .map(Object::toString)
                .orElseThrow(() -> new RuntimeException("streamId is missing from the MessageHeader!"));
    }

    @JsonIgnore // ignored here because it's stored in the header and not required in the cloudEvent interface
    public long getSequence() {
        return header.getSequence();
    }

    public Header getHeader() {
        return header;
    }

    public EventJournalEventData getData() {
        return data;
    }
}

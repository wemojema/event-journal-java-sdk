package com.eventjournal.api;


import com.eventjournal.api.impl.EventJournal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class Envelope implements CloudEvent {
    @JsonIgnore
    private static final Logger log = LoggerFactory.getLogger(Envelope.class);
    private SpecVersion specVersion = SpecVersion.V1;
    private String subject;
    private String type;
    private String id;
    private URI source;
    private URI dataSchema;
    private OffsetDateTime time;
    private Set<String> attributeNames;
    private Set<String> extensionNames;
    private String dataContentType;
    private Header header;
    private EventJournalEventData data;


    private Envelope() {
    }

    public static Envelope of(Message message) {
        return new Envelope(EventJournal.Toolbox.serialize(message), message.header());
    }

    /**
     * @param header            the MessageHeader of this message
     * @param serializedMessage the serialized message (typically stringified json)
     */
    private Envelope(String serializedMessage, Header header) {
        this.header = header;
        this.id = header.getMessageId();
        this.type = header.getMessageType();
        this.data = new EventJournalEventData(serializedMessage);
        this.source = header.producer.source();
        this.dataSchema = URI.create(header.producer.source() + "/schema/" + header.messageType);
        this.subject = header.streamId.split("/")[0];
        this.time = header.timestamp.atOffset(OffsetDateTime.now().getOffset());
        this.dataContentType = "application/json";
        this.extensionNames = new HashSet<>();
        this.attributeNames =
                Stream.concat(CloudEvent.super.getAttributeNames().stream(),
                        Stream.of("streamId", "messageId", "version", "category", "timestamp"))
                        .collect(HashSet::new, Set::add, Set::addAll);
    }

    public String streamId() {
        return header.get("streamId")
                .map(Object::toString)
                .orElseThrow(() -> new RuntimeException("streamId is missing from the MessageHeader!"));
    }

    @JsonIgnore // ignored here because it's stored in the header and not required in the cloudEvent interface
    public int getSequence() {
        return header.getSequence();
    }

    public Message.MessageCategory messageCategory() {
        return header.category();
    }

    public Instant timestamp() {
        return header.timestamp();
    }


    @Override
    public EventJournalEventData getData() {
        return data;
    }

    @Override
    public SpecVersion getSpecVersion() {
        return specVersion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public URI getSource() {
        return this.source;
    }

    @Override
    public String getDataContentType() {
        return this.dataContentType;
    }

    @Override
    public URI getDataSchema() {
        return this.dataSchema;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public OffsetDateTime getTime() {
        return this.time;
    }

    @Override
    @JsonIgnore
    public Object getAttribute(String attributeName) throws IllegalArgumentException {
        return keyBelongsToEnvelope(attributeName)
                .map(this::getField)
                .or(() -> header.get(attributeName))
                .orElseThrow(() -> new IllegalArgumentException("Attribute " + attributeName + " is missing from the MessageHeader!"));
    }

    private Object getField(Field f) {
        try {
            return f.get(this);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not access attribute: " + f.getName(), e);
        }
    }

    private Object getAttributeFromEnvelope(String attributeName) {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> field.getName().equalsIgnoreCase(attributeName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Attribute " + attributeName + " is missing from the Envelope!"));
    }

    private Optional<Field> keyBelongsToEnvelope(String attributeName) {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter(field -> field.getName().equalsIgnoreCase(attributeName))
                .findAny();
    }

    @Override
    public Set<String> getAttributeNames() {
        return attributeNames;
    }

    @Override
    @JsonIgnore
    public Object getExtension(String extensionName) {
        // todo figure out what the extensions are.
        return null;
    }

    @Override
    @JsonIgnore
    public Set<String> getExtensionNames() {
        return this.extensionNames;
    }
}

package com.eventjournal.api;

import java.time.Instant;
import java.util.*;

public class Header {
    String streamId;
    String messageType;
    Message.MessageCategory category;
    Instant timestamp;
    Integer sequence;
    String causationId;
    String messageId;
    String correlationId;
    Message.Producer producer = Message.Producer.ANONYMOUS;
    Map<String, Object> customHeader;

    public Header() {
    }

    public Header(String streamId,
                  String messageType,
                  Message.MessageCategory category,
                  Instant timestamp,
                  Integer sequence,
                  String causationId,
                  String messageId,
                  String correlationId) {
        establishProps(streamId, messageType, category, timestamp, sequence, causationId, messageId, correlationId);
    }

    private void establishProps(String streamId, String messageType, Message.MessageCategory category, Instant timestamp, Integer version, String causationId, String messageId, String messageChainId) {
        this.streamId = Objects.requireNonNull(streamId, "Stream ID is required");
        this.messageType = Objects.requireNonNull(messageType, "Message Type is required");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp is required!");
        this.category = Objects.requireNonNull(category, "Category is required");
        this.sequence = version;
        this.causationId = Objects.requireNonNull(causationId, "CausationId is required");
        this.messageId = Objects.requireNonNull(messageId, "Message ID is required!");
        this.correlationId = Objects.requireNonNull(messageChainId, "Message Chain ID is required!");
        this.customHeader = new HashMap<>();
    }

    /**
     * Use this static convenience constructor when you need to create
     * a message header that is a direct result of another message.
     * The resulting message will be in the same correlation chain as the original message
     * and populate the same Stream as the original message.
     *
     * @param header      the original message header
     * @param messageType the type of the new message
     * @return a new MessageHeader
     */
    public static Header resultingFrom(Header header,
                                       Class<? extends Message> messageType) {
        return new Header(
                header.streamId,
                messageType.getSimpleName(),
                Message.MessageCategory.of(messageType),
                Instant.now(),
                header.sequence + 1,
                header.messageId,
                UUID.randomUUID().toString(),
                header.correlationId
        );
    }

    /**
     * Use this static convenience constructor when you need to create
     * a message header as a side effect of a message.
     * "Side Effects" are typically commands that are raised as a result of an event.
     * These messages will appear in a correlation chain, but can be related to aggregates other than the
     * original aggregate for which the Cause is related.
     *
     * @param cause           the message that caused this side effect
     * @param targetAggregate the aggregate that this side effect is related to
     * @param messageType     the type of message that this side effect is
     * @return a new MessageHeader
     */
    public static Header sideEffectOf(Message cause, Aggregate targetAggregate, Class<? extends Message> messageType) {
        return new Header(
                targetAggregate.streamId(),
                messageType.getSimpleName(),
                Message.MessageCategory.COMMAND, // side effects should always be commands
                Instant.now(),
                targetAggregate.version(),
                cause.header().messageId,
                UUID.randomUUID().toString(),
                cause.header().correlationId
        );
    }

    /**
     * Use this static convenience constructor when you need to create
     * a message header for the first message in a chain
     *
     * @param aggregateType the Class of the Aggregate
     * @param aggregateId   the ID of the Aggregate
     * @param messageType   the Class of the Message
     * @return a new MessageHeader
     */
    public static Header headOfChain(Class<? extends Aggregate> aggregateType, String aggregateId, Class<? extends Message> messageType) {
        return new Header(StreamId.of(aggregateType, aggregateId), messageType, 0);
    }

    /**
     * Use this constructor when you need full control over the entire header including the timestamp
     *
     * @param streamId      the Stream ID this Message belongs to
     * @param messageType   the MessageType (some implementation of a Command or Event class.simpleName())
     * @param timestamp     the Instant that this message was raised
     * @param sequence      the aggregate version for which this message was raised
     * @param causationId   the event or command that caused this event or command
     * @param messageId     the id for this event or command
     * @param correlationId the id identifying all events or commands in a single chain of events for full traceability
     */
    public Header(String streamId, String messageType, Instant timestamp, int sequence, String causationId, String messageId, String correlationId) {
        this.streamId = Objects.requireNonNull(streamId, "Stream ID is required");
        this.messageType = Objects.requireNonNull(messageType, "Message Type is required");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp is required!");
        this.sequence = sequence;

        this.causationId = Objects.requireNonNull(causationId, "CausationId is required");
        this.messageId = Objects.requireNonNull(messageId, "Message ID is required!");
        this.correlationId = Objects.requireNonNull(correlationId, "Message Chain ID is required!");
        this.customHeader = new HashMap<>();
    }

    /**
     * Use this constructor when this is the first event or command in a chain.
     *
     * @param streamId    the Stream ID this Message belongs to
     * @param messageType the MessageType (some implementation of a Command or Event class.simpleName())
     * @param sequence    the aggregate version for which this message was raised
     * @param causationId the event or command that caused this event or command
     */
    public Header(String streamId, Class<? extends Message> messageType, int sequence, String causationId) {
        this.streamId = Objects.requireNonNull(streamId, "Stream ID is required");
        this.messageType = Objects.requireNonNull(messageType, "Message Type is required").getSimpleName();
        this.sequence = sequence;
        this.causationId = Objects.requireNonNull(causationId, "CausationId is required");
        this.category = Message.Command.class.isAssignableFrom(messageType) ? Message.MessageCategory.COMMAND : Message.MessageCategory.EVENT;
        this.timestamp = Instant.now();
        this.messageId = this.causationId;
        this.correlationId = this.causationId;
        this.customHeader = new HashMap<>();
    }

    /**
     * Use this constructor when this is the first event or command in a chain,
     * and you don't need to assign the causation ID.
     *
     * @param streamId    the Stream ID this Message belongs to
     * @param messageType the MessageType (some implementation of a Command or Event class.simpleName())
     * @param sequence    the aggregate version for which this message was raised
     */
    public Header(String streamId, Class<? extends Message> messageType, int sequence) {
        this.streamId = Objects.requireNonNull(streamId, "Stream ID is required");
        this.messageType = Objects.requireNonNull(messageType, "Message Type is required").getSimpleName();
        this.sequence = sequence;
        this.category = Message.Command.class.isAssignableFrom(messageType) ? Message.MessageCategory.COMMAND : Message.MessageCategory.EVENT;
        this.causationId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.messageId = this.causationId;
        this.correlationId = this.causationId;
        this.customHeader = new HashMap<>();
    }

    public Object put(String key, Object value) {
        if (keyIsReserved(key))
            throw new IllegalArgumentException(String.format("the Key: %s is a reserved Keyword of the MessageHeader", key));
        if (customHeader == null)
            customHeader = new HashMap<>();
        return customHeader.put(key, value);
    }

    private boolean keyIsReserved(String key) {
        return Arrays.stream(this.getClass().getDeclaredFields()).anyMatch(field -> field.getName().equals(key));
    }

    public Optional<Object> get(String key) {
        try {
            if (keyIsReserved(key)) {
                return Optional.ofNullable((String) Header.class.getDeclaredField(key).get(this));
            }
            return Optional.ofNullable(customHeader.get(key));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            // todo log a warning here
            return Optional.empty();
        }
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public Instant timestamp() {
        return Instant.ofEpochMilli(timestamp.toEpochMilli());
    }

    public Message.MessageCategory category() {
        return Message.MessageCategory.valueOf(category.name());
    }

    public String getStreamId() {
        return streamId;
    }

    public String getMessageType() {
        return messageType;
    }

    public Message.MessageCategory getCategory() {
        return Message.MessageCategory.valueOf(category.name());
    }

    public Instant getTimestamp() {
        return Instant.ofEpochMilli(timestamp.toEpochMilli());
    }

    public Integer getSequence() {
        return sequence;
    }

    public String getCausationId() {
        return causationId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public Map<String, Object> getCustomHeader() {
        return new HashMap<>(customHeader);
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                ", streamId='" + streamId + '\'' +
                ", messageType='" + messageType + '\'' +
                ", category=" + category +
                ", timestamp=" + timestamp +
                ", version=" + sequence +
                ", causationId='" + causationId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", messageChainId='" + correlationId + '\'' +
                ", customHeader=" + customHeader +
                '}';
    }
}

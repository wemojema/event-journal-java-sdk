package com.eventjournal.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * A Message is the primary unit of communication in the EventJournal API.
 * Messages are either Commands or Events. Commands are requests for an action to be taken, while
 * Events are notifications that an action has been taken and represent a change in state.
 * <p>
 * Messages are identified by their category, streamId, timestamp, and sequence number.
 * The streamId is a unique identifier for a stream of messages, typically representing an aggregate.
 * The timestamp is the time the message was emitted. The sequence number is the order in which the
 * message was emitted in the stream, and is used to determine the order of events when replaying
 * events to rehydrate an aggregate.
 * <p>
 * Note: changing the classname of a Message implementation can cause deserialization to be impacted.
 * Using the provided @JsonTypeName annotation to provide a stable type name for a Message in the event of a classname change.
 * (eg: @JsonTypeName("SomethingHappened"))
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "@type")
@JsonTypeIdResolver(Message.MessageTypeIdResolver.class)
public interface Message {
    String streamId();

    Instant timestamp();

    long sequence();

    Header header();

    MessageCategory messageCategory();

    /**
     * A Command is a request for an action to be taken.
     * Commands are messages that can be handled by an Aggregate.
     * They are typically used to record a request for a change in state.
     * Commands are not guaranteed to result in an Event, they must first be validated by the Aggregate.
     * If the Command is valid, the Aggregate will emit an Event in response to the Command.
     */
    abstract class Command implements Message {
        Header header;

        public Command() {
        }

        public Command(Header header) {
            this.header = header;
            Objects.requireNonNull(header, "The Message Header cannot be null!");
            Objects.requireNonNull(header.streamId, "Stream ID cannot be null!");
            Objects.requireNonNull(header.timestamp, "Timestamp cannot be null!");
        }

        @Override
        public MessageCategory messageCategory() {
            return MessageCategory.COMMAND;
        }

        @Override
        public String streamId() {
            return header.streamId;
        }

        @Override
        public Instant timestamp() {
            return header.timestamp;
        }

        @Override
        public long sequence() {
            return header.sequence;
        }

        @Override
        public Header header() {
            return header;
        }

        public Command withHeader(Header header) {
            this.header = header;
            return this;
        }
    }

    /**
     * An Event is a notification that an action has been taken.
     * Events are messages that represent a change in state.
     * They are typically used to record the result of a Command and are emitted by an Aggregate.
     * They carry an important sequence number that is used to determine the order of events when replaying
     * That sequence number aligns with the version of the Aggregate that emitted the event.
     * For instance an Aggregate with version 3 will emit an event with sequence 3.
     * <p>
     * Events are immutable and cannot be modified after they are emitted.
     * They are the source of truth for the state of the system.
     * They are the only messages that can be replayed to rehydrate an Aggregate.
     * They are the only messages that can be used to determine the state of the system at a given point in time.
     */
    abstract class Event implements Message {
        Header header;

        public Event() {
        }

        public Event(Header header) {
            this.header = header;
            Objects.requireNonNull(header, "The Message Header cannot be null!");
            Objects.requireNonNull(header.streamId, "Stream ID cannot be null!");
            Objects.requireNonNull(header.timestamp, "Timestamp cannot be null!");
        }

        @Override
        public MessageCategory messageCategory() {
            return MessageCategory.EVENT;
        }

        @Override
        public String streamId() {
            return header.streamId;
        }

        @Override
        public Instant timestamp() {
            return header.timestamp;
        }

        @Override
        public long sequence() {
            return header.sequence;
        }

        @Override
        public Header header() {
            return header;
        }

        public Event withHeader(Header header) {
            this.header = header;
            return this;
        }
    }

    enum MessageCategory {
        EVENT,
        COMMAND,
        ;

        public static MessageCategory of(Class<? extends Message> messageType) {
            return messageType.getSuperclass()
                    .getSimpleName()
                    .equals(Event.class.getSimpleName())
                    ? EVENT
                    : COMMAND;
        }

    }

    /**
     * A class representing the producer of a message.
     */
    class Producer {
        String name;
        String version;

        /**
         * Creates a new Producer with the given name and version.
         *
         * @param name    the name of the producer
         * @param version the version of the producer
         *                <p>This is helpful for traceability, especially in distributed systems.
         *                It is recommended to use the name of the service and the version of the service.
         *                For example, "my-service" and "1.0.0"</p>
         */
        public Producer(String name, String version) {
            this.name = name;
            this.version = version;
        }

        private Producer() {

        }

        /**
         * The default producer, used when the producer is unknown.
         */
        public static final Producer ANONYMOUS = new Producer("anonymous", "unknown-version");
    }

    class MessageTypeIdResolver implements TypeIdResolver {
        private static final Logger log = LoggerFactory.getLogger(MessageTypeIdResolver.class);
        private static JavaType superType;
        private static Map<String, Class<? extends Message>> typeMap;

        public static void scanForTypes(ObjectMapper mapper) {
            superType = mapper.getTypeFactory().constructType(Message.class);
            typeMap = new HashMap<>();

            log.trace("Scanning for Event and Command types");

            Arrays.stream(Package.getPackages())
                    .map(p -> p.getName().split("\\.")[0])
                    .distinct()
                    .filter(p -> p != null && !p.isEmpty())
                    .peek(p -> log.trace("Scanning Package: {}", p))
                    .flatMap(topLevelPackage -> identifyMessageTypes(topLevelPackage).stream())
                    .peek(c -> log.trace("Found Message Type: {}", c.getSimpleName()))
                    .forEach(MessageTypeIdResolver::addType);

            log.trace("Scanning for Event and Command types complete, found: {} types.", MessageTypeIdResolver.availableTypes().size());
        }


        private static Set<Class<? extends Message>> identifyMessageTypes(String topLevelPackage) {
            return new Reflections(topLevelPackage).getSubTypesOf(Message.class);
        }

        public static void addType(Class<? extends Message> clazz) {
            typeMap.put(clazz.getSimpleName(), clazz);
        }

        public static Collection<Object> availableTypes() {
            return Arrays.asList(typeMap.values().toArray());
        }


        @Override
        public void init(JavaType javaType) {

        }

        @Override
        public String idFromBaseType() {
            return superType.getClass().getSimpleName();
        }

        @Override
        public String getDescForKnownTypeIds() {
            return typeMap.toString();
        }

        @Override
        public String idFromValue(Object value) {
            return idFromValueAndType(value, value.getClass());
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return suggestedType.getSimpleName();
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String id) throws IOException {
            Class<? extends Message> clazz = typeMap.get(id);
            if (clazz == null) {
                throw new InvalidTypeIdException(null, "Unknown type id: " + id, superType, id);
            }
            return context.constructSpecializedType(superType, clazz);
        }

        @Override
        public JsonTypeInfo.Id getMechanism() {
            return JsonTypeInfo.Id.CUSTOM;
        }
    }
}

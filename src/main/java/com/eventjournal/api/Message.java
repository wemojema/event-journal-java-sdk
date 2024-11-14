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

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "@type")
@JsonTypeIdResolver(Message.MessageTypeIdResolver.class)
public interface Message {
    String streamId();

    Instant timestamp();

    long sequence();

    Header header();

    MessageCategory messageCategory();

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

    class Producer {
        String name;
        String version;

        public Producer(String name, String version) {
            this.name = name;
            this.version = version;
        }
        private Producer() {

        }
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

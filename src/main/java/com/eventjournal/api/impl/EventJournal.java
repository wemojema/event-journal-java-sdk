package com.eventjournal.api.impl;

import com.eventjournal.api.Aggregate;
import com.eventjournal.api.Envelope;
import com.eventjournal.api.Message;
import com.eventjournal.api.StreamId;
import com.eventjournal.api.ex.IncompleteAggregateException;
import com.eventjournal.api.ex.MissingEventHandlerException;
import com.eventjournal.auth.APIKeys;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class EventJournal {
    private static final Logger log = LoggerFactory.getLogger(EventJournal.class);

    private final EventStoreClient client;

    EventJournal(EventStoreClient client) {
        this.client = client;
    }

    public EventJournal(String publicKey, String secretKey) {
        APIKeys keys = new APIKeys(publicKey, secretKey);
        this.client = new Client(keys);
        ((Client)this.client).checkConnection();
    }

    public <T extends Aggregate> T playback(String streamId, Class<T> clazz) {
        return this.playback(streamId, clazz, Instant.now());
    }

    public <T extends Aggregate> T playback(String streamId, Class<T> clazz, Instant timestamp) {
        try {
            T aggregate = clazz.getDeclaredConstructor().newInstance();

            List<Message.Event> events = client
                    .stream(streamId)
                    .events();

            if (log.isTraceEnabled())
                log.trace("Streamed Events: \n" + Toolbox.serialize(events));

            events.stream()
                    .filter(e -> e.timestamp().isBefore(timestamp) || e.timestamp().equals(timestamp))
                    .sorted(Comparator.comparing(Message.Event::version))
                    .forEach(e -> applyEvent(aggregate, e));

            return aggregate;

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new IncompleteAggregateException("The Aggregate class provided is incomplete", e);
        }
    }

    private static <T extends Aggregate> void applyEvent(T aggregate, Message.Event e) {
        try {
            aggregate.getClass().getMethod("apply", e.getClass()).invoke(aggregate, e);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new MissingEventHandlerException("Failed to invoke the apply method for Event: " + e.getClass().getSimpleName() + " on Aggregate: " + aggregate.getClass().getSimpleName(), ex);
        }
    }

    public void record(Message message) {
        client.save(Envelope.of(message));
    }

    public void record(Collection<Message> messages) {
        messages.forEach(this::record);
    }

    public <T extends Aggregate> T playback(Class<T> aggregateType, String aggregateId) {
        return this.playback(StreamId.of(aggregateType, aggregateId), aggregateType);
    }

    public static class Toolbox {
        private static final Logger logger = LoggerFactory.getLogger(Toolbox.class);
        public static final ObjectMapper mapper = new ObjectMapper();

        static {
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.registerModule(new JavaTimeModule());
        }

        public static String serialize(Object obj) {
            try {
                return mapper.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("A JsonProcessing Exception Occurred while trying to serialize!", e);
            }
        }

        public static <T> T deserialize(String string, Class<T> clazz) {
            try {
                return mapper.readValue(Objects.requireNonNull(string, "jsonString must not be null"),
                        Objects.requireNonNull(clazz, "Provided Class must not be null"));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("A JsonProcessing Exception Occurred while trying to deserialize!", e);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public static <T> T deserialize(String json, Class<?> objectType, Class<? extends Collection> listType) {
            try {
                return mapper.readValue(Objects.requireNonNull(json, "json must not be null"),
                        mapper.getTypeFactory().constructCollectionType(Objects.requireNonNull(listType, "ListType must not be null!"),
                                Objects.requireNonNull(objectType, "Object type must not be null")));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

}

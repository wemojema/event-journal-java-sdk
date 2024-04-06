package com.eventjournal.api.impl;

import com.eventjournal.api.*;
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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * The EventJournal is the primary interface for interacting with the EventJournal API.
 * It provides methods for recording and playing back events. It also provides a method for
 * instantiating an Aggregate and applying events to it.
 * <p>
 * To obtain API Keys visit: <a href="https://event-journal.com">event-journal.com</a>
 * <p>
 * example usage:
 * <pre>
 * EventJournal ej = new EventJournal("publicKey","secretKey");
 * ej.record(new SomethingHappened()); // SomethingHappened is an implementation of Message.Event
 * </pre>
 * Instantiating an EventJournal requires API Keys or a custom EventStoreClient implementation,
 * it is recommended to use the API Keys constructor. However, in circumstances where
 * the EventJournal library is used for its utility and conventions related to Event Sourcing
 * but the EventJournal API is not used, a custom EventStoreClient can be provided.
 * For instance, if the organization has outgrown the EventJournal API and wishes to implement
 * their own event store, it's possible to do so and continue using this library for its
 * functionality in rehydrating Aggregates and guarding against StaleAggregates, as well
 * as the opinions it takes regarding the Event Data structures and protections it provides for them.
 */
public class EventJournal {
    private static final Logger log = LoggerFactory.getLogger(EventJournal.class);

    private final EventStoreClient client;
    private Message.Producer producer = Message.Producer.ANONYMOUS;

    EventJournal(EventStoreClient client) {
        this.client = client;
    }

    public EventJournal(String publicKey, String secretKey) {
        APIKeys keys = new APIKeys(publicKey, secretKey);
        this.client = new Client(keys);
        ((Client) this.client).checkConnection();
    }

    public EventJournal withProducer(Message.Producer producer) {
        this.producer = producer;
        return this;
    }

    public <T extends Aggregate> T playback(String streamId, Class<T> clazz) {
        return this.playback(streamId, clazz, Instant.now());
    }

    public <T extends Aggregate> T playback(String streamId, Class<T> clazz, Instant timestamp) {
        T aggregate = instantiate(clazz);

        List<Message.Event> events = client
                .stream(streamId)
                .events();

        if (log.isTraceEnabled())
            log.trace("Streamed Events: \n" + Toolbox.serialize(events));

        events.stream()
                .filter(e -> e.timestamp().isBefore(timestamp) || e.timestamp().equals(timestamp))
                .sorted(Comparator.comparing(Message.Event::sequence))
                .forEach(e -> applyEvent(aggregate, e));

        return aggregate;
    }

    private <T extends Aggregate> T instantiate(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IncompleteAggregateException(String.format("Unable to instantiate %s", clazz.getSimpleName()), e);
        }
    }

    private static <T extends Aggregate> void applyEvent(T aggregate, Message.Event e) {
        try {
            aggregate.getClass().getMethod("apply", e.getClass()).invoke(aggregate, e);
            if(aggregate instanceof VersionedAggregate versionedAggregate) {
                versionedAggregate.incrementVersion();
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new MissingEventHandlerException("Failed to invoke the apply method for Event: " + e.getClass().getSimpleName() + " on Aggregate: " + aggregate.getClass().getSimpleName(), ex);
        }
    }

    public void record(Message message) {
        client.save(Envelope.of(message));
    }

    public void record(Collection<Message> messages) {
        List<Envelope> envelopes = messages.stream().map(Envelope::of).toList();
        client.save(envelopes);
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
            Message.MessageTypeIdResolver.scanForTypes(mapper);
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
                return mapper.readValue(Objects.requireNonNull(maybeDecode(string), "jsonString must not be null"),
                        Objects.requireNonNull(clazz, "Provided Class must not be null"));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("A JsonProcessing Exception Occurred while trying to deserialize!", e);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public static <T> T deserialize(String string, Class<?> objectType, Class<? extends Collection> listType) {
            try {
                return mapper.readValue(Objects.requireNonNull(maybeDecode(string), "json must not be null"),
                        mapper.getTypeFactory().constructCollectionType(Objects.requireNonNull(listType, "ListType must not be null!"),
                                Objects.requireNonNull(objectType, "Object type must not be null")));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (NullPointerException e) {
                throw new IllegalArgumentException(e);
            }
        }

        private static String maybeDecode(String string) {
            if (string.startsWith("{") || string.startsWith("[")) {
                return string;
            } else {
                log.debug("The provided String appears to be encoded. Assuming it is Base64 and decoding it.");
                return new String(Base64.getDecoder().decode(string.getBytes(StandardCharsets.UTF_8)));
            }
        }

    }

}

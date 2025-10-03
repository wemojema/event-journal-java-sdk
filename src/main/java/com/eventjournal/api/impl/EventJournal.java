package com.eventjournal.api.impl;

import com.eventjournal.api.Envelope;
import com.eventjournal.api.Message;
import com.eventjournal.api.StreamId;
import com.eventjournal.api.ex.IncompleteAggregateException;
import com.eventjournal.api.ex.MissingEventHandlerException;
import com.eventjournal.auth.APIKeys;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

/**
 * The EventJournal is the primary interface for interacting with the EventJournal API.
 * It provides methods for recording and playing back events. It also provides a method for
 * instantiating an VersionedAggregate and applying events to it.
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
 * functionality in rehydrating VersionedAggregates and guarding against StaleAggregates, as well
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

    /**
     * Sets the producer for the EventJournal.
     *
     * @param producer the producer to set
     * @return the EventJournal with the producer set
     */
    public EventJournal withProducer(Message.Producer producer) {
        this.producer = producer;
        return this;
    }

    private <T extends VersionedAggregate> T playbackAtPointInTime(String streamId, Class<T> clazz) {
        return this.playbackAtPointInTime(streamId, clazz, Instant.now());
    }

    private <T extends VersionedAggregate> T playbackAtPointInTime(String streamId, Class<T> clazz, Instant timestamp) {
        T aggregate = instantiate(clazz);

        List<Message.Event> events = client
                .stream(streamId)
                .events();

        if (log.isTraceEnabled())
            log.trace("Streamed Events: \n{}", Toolbox.serialize(events));

        events.stream()
                .filter(e -> e.timestamp().isBefore(timestamp) || e.timestamp().equals(timestamp))
                .sorted(Comparator.comparing(Message.Event::sequence))
                .forEach(e -> applyEvent(aggregate, e));

        return aggregate;
    }

    private <T extends VersionedAggregate> T instantiate(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IncompleteAggregateException(String.format("Unable to instantiate %s", clazz.getSimpleName()), e);
        }
    }

    private static <T extends VersionedAggregate> void applyEvent(T aggregate, Message.Event e) {
        try {
            aggregate.getClass().getMethod("apply", e.getClass()).invoke(aggregate, e);
            aggregate.incrementVersion();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new MissingEventHandlerException("Failed to invoke the apply method for Event: " + e.getClass().getSimpleName() + " on Aggregate: " + aggregate.getClass().getSimpleName(), ex);
        }
    }

    public JournalEntry record(Message message) {
        Envelope envelope = Envelope.wrap(message);
        client.save(envelope);
        return new JournalEntry(message, envelope.getHeader());
    }

    public void record(Collection<Message> messages) {
        List<Envelope> envelopes = messages.stream().map(Envelope::wrap).toList();
        client.save(envelopes);
    }

    public <T extends VersionedAggregate> T playback(Class<T> aggregateType, String aggregateId) {
        return (T) this.playbackAtPointInTime(StreamId.of(aggregateType, aggregateId), aggregateType)
                .withEventJournal(this)
                .withId(aggregateType, aggregateId);
    }

    /**
     * Plays back the aggregate, or if the event store contains no events for the aggregate, throws the exception supplied.
     *
     * @param aggregateType the type of the aggregate
     * @param aggregateId   the id of the aggregate
     * @param orElseThrow   the supplier to throw if the aggregate is not found
     * @param <X>           the exception to throw
     * @param <T>           the type of the aggregate
     * @return the aggregate
     * @throws X if the aggregate is not found
     */
    public <X extends Throwable, T extends VersionedAggregate> T playbackOrElseThrow(Class<T> aggregateType,
                                                                                     String aggregateId,
                                                                                     Supplier<? extends X> orElseThrow) throws X {
        T versionedAggregate = this.playbackAtPointInTime(StreamId.of(aggregateType, aggregateId), aggregateType);
        if (versionedAggregate.id == null) {
            throw orElseThrow.get();
        }
        return (T) versionedAggregate
                .withEventJournal(this)
                .withId(aggregateType, aggregateId);
    }

    public static class Toolbox {
        private static final Logger logger = LoggerFactory.getLogger(Toolbox.class);
        public static final ObjectMapper mapper = new ObjectMapper();

        static {
            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

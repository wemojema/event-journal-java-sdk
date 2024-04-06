package com.eventjournal.api.impl;

import com.eventjournal.api.Envelope;
import com.eventjournal.api.Message;

import java.util.List;

/**
 * Interface for a client that can interact with an event store.
 * Enabling IOC for the EventJournal. To implement your own Event Storage
 * mechanism this is the class to override. Simply implement the methods
 * and provide the implementation to the package private EventJournal constructor.
 */
interface EventStoreClient {

    void save(Envelope envelope);

    void save(List<Envelope> envelopeList);

    EventStream stream(String streamId);

    record EventStream(List<Message.Event> events) {
    }
}

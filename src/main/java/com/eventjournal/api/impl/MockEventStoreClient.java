package com.eventjournal.api.impl;

import com.eventjournal.api.Envelope;
import com.eventjournal.api.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A mock implementation of the EventStoreClient interface.
 * Useful for testing the EventJournal without needing to connect to a real event store.
 */
class MockEventStoreClient implements EventStoreClient {

    private final List<Envelope> savedMessages = new ArrayList<>();


    @Override
    public void save(Envelope envelope) {
        this.savedMessages.add(envelope);
    }

    @Override
    public void save(List<Envelope> envelopeList) {
        this.savedMessages.addAll(envelopeList);
    }

    @Override
    public EventStream stream(String streamId) {

        return new EventStream(
                savedMessages
                        .stream()
                        .filter(e -> e.streamId().equals(streamId))
                        .sorted(Comparator.comparing(Envelope::getSequence))
                        .map(envelope -> EventJournal.Toolbox.deserialize(envelope.getData().getSerializedMessage(), Message.Event.class))
                        .collect(Collectors.toList())
        );

    }
}

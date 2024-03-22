package com.eventjournal.api.impl;

import com.eventjournal.api.Envelope;
import com.eventjournal.api.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class MockEventStoreClient implements EventStoreClient {

    private final List<Envelope> saves = new ArrayList<>();


    @Override
    public void save(Envelope envelope) {
        this.saves.add(envelope);
    }

    @Override
    public void save(List<Envelope> envelopeList) {
        this.saves.addAll(envelopeList);
    }

    @Override
    public EventStream stream(String streamId) {

        return new EventStream(
                saves
                        .stream()
                        .filter(e -> e.streamId().equals(streamId))
                        .sorted(Comparator.comparing(Envelope::getSequence))
                        .map(envelope -> EventJournal.Toolbox.deserialize(envelope.getData().getSerializedMessage(), Message.Event.class))
                        .collect(Collectors.toList())
        );

    }
}

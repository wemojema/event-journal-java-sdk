package com.eventjournal.api.testing.fixtures;

import com.eventjournal.api.Envelope;
import com.eventjournal.api.EventStoreClient;
import com.eventjournal.api.Message;
import com.eventjournal.api.impl.EventJournal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MockEventStoreClient implements EventStoreClient {

    private final List<Envelope> publishedEnvelopes = new ArrayList<>();


    @Override
    public void save(Envelope envelope) {
        this.publishedEnvelopes.add(envelope);
    }

    @Override
    public void save(List<Envelope> envelopeList) {
        this.publishedEnvelopes.addAll(envelopeList);
    }

    @Override
    public EventStream stream(String streamId) {

        return new EventStream(
                publishedEnvelopes
                        .stream()
                        .filter(e -> e.streamId().equals(streamId))
                        .sorted(Comparator.comparing(Envelope::getSequence))
                        .map(envelope -> EventJournal.Toolbox.deserialize(envelope.getData().getSerializedMessage(), Message.Event.class))
                        .collect(Collectors.toList())
        );

    }
}

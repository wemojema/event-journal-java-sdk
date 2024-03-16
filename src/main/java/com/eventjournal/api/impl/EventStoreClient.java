package com.eventjournal.api.impl;

import com.eventjournal.api.Envelope;

import java.util.List;

interface EventStoreClient {

    void save(Envelope envelope);

    void save(List<Envelope> envelopeList);

    EventStream stream(String streamId);

}

package com.eventjournal.api.impl;

import com.eventjournal.api.Envelope;
import com.eventjournal.api.Message;

import java.util.ArrayList;
import java.util.List;

interface EventStoreClient {

    void save(Envelope envelope);

    void save(List<Envelope> envelopeList);

    EventStream stream(String streamId);

    class EventStream {

        private final List<Message.Event> events;

        public EventStream(List<Message.Event> events) {
            this.events = events;
        }

        public List<Message.Event> events() {
            return new ArrayList<>(events);
        }

    }
}

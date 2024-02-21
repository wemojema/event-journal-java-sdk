package com.eventjournal.api;

import java.util.ArrayList;
import java.util.List;

public interface EventStoreClient {

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

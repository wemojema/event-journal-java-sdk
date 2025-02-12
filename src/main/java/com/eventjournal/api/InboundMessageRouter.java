package com.eventjournal.api;

import java.util.List;

public interface InboundMessageRouter {

    void routeMessages(List<Envelope> envelopes);
    <T extends Message> void subscribeReactor(Class<T> messageClass, Reactor<T> reactor);

}

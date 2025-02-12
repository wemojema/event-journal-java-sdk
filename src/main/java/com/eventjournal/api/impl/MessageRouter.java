package com.eventjournal.api.impl;

import com.eventjournal.api.*;
import com.eventjournal.api.reactor.SimpleReactorRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class MessageRouter implements InboundMessageRouter {
    private static final String HOW_TO_SUBSCRIBE = "Subscribe by calling messageRouter.subscribeReactor(MyMessage.class, myReactorInstance);";
    private final Logger log = LoggerFactory.getLogger(MessageRouter.class);
    private final ReactorRegistrar reactorRegistrar;
    private final EventJournal eventJournal;

    public MessageRouter(EventJournal eventJournal) {
        this.eventJournal = eventJournal;
        this.reactorRegistrar = new SimpleReactorRegistrar();
    }

    @Override
    public <T extends Message> void subscribeReactor(Class<T> messageClass, Reactor<T> reactor) {
        log.info("Registering reactor for message: {}", messageClass.getSimpleName());
        reactorRegistrar.subscribeReactor(messageClass, reactor);
    }

    @Override
    public void routeMessages(List<Envelope> envelopes) {
        envelopes.forEach(this::handle);
    }

    private void handle(Envelope e) {
        handleMessage(EventJournal.Toolbox.deserialize(e.getData().getSerializedMessage(), Message.class));
    }

    private <T extends Message> void handleMessage(T message) {
        String messageClass = message.getClass().getSimpleName();
        List<Reactor<T>> reactors = reactorRegistrar
                .reactorsFor(messageClass);

        if (reactors.isEmpty()) {
            log.warn("No reactors found subscribed to message: {}. {}", messageClass, HOW_TO_SUBSCRIBE);
            return;
        }

        if (reactors.size() > 1)
            logMultipleReactorsMessage(messageClass);

        List<Message> sideEffects = reactors
                .parallelStream()
                .map((r) -> r.handle(message))
                .flatMap(outcome -> outcome.messages.stream())
                .toList();

        eventJournal.record(sideEffects);
    }

    private void logMultipleReactorsMessage(String messageType) {
        log.warn("Discovered multiple reactors for {}. By default, each reactor must handle this message successfully for any outcomes to be recorded. Any reactor that throws an exception will cause other reactors to either not receive the subscribed message or to have their outcomes discarded. Additionally, reactors could be executed in any order.", messageType);
    }


}

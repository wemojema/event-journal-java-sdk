package com.eventjournal.api.reactor;

import com.eventjournal.api.Message;
import com.eventjournal.api.Reactor;
import com.eventjournal.api.ReactorRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * A simple implementation of a ReactorRegistrar.
 */
public class SimpleReactorRegistrar implements ReactorRegistrar {
    Logger log = LoggerFactory.getLogger(SimpleReactorRegistrar.class);
    Map<String, List<Reactor<? extends Message>>> reactors = new HashMap<>();

    /**
     * Subscribes a Reactor to a specific message type.
     * @param messageClass the message type
     * @param reactor the reactor to subscribe
     * @param <T> the type of message
     */
    @Override
    public <T extends Message> void subscribeReactor(Class<T> messageClass, Reactor<T> reactor) {
        reactors.merge(messageClass.getSimpleName(), Arrays.asList(reactor),
                (oldValue, newValue) -> {
                    log.warn("A Reactor is already subscribed for {}. Multiple Reactors for the same Message Type is not recommended.", messageClass.getSimpleName());
                    oldValue.addAll(newValue);
                    return oldValue;
                });
    }

    /**
     * Returns a list of Reactors that are subscribed to a specific message type.
     * @param messageClass the message type
     * @return a list of Reactors (empty if no Reactors are subscribed)
     * @param <T> the type of message
     */
    @Override
    public <T extends Message> List<Reactor<T>> reactorsFor(String messageClass) {
        return Optional.ofNullable(reactors.get(messageClass))
                .orElseGet(ArrayList::new)
                .stream()
                .map(r -> (Reactor<T>) r)
                .toList();

    }
}

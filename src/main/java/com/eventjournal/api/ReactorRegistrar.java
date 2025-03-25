package com.eventjournal.api;

import java.util.List;

/**
 * A ReactorRegistrar is a component that manages Reactors.
 * It allows for subscribing Reactors to specific message types and
 * querying for Reactors that are interested in a specific message type.
 */
public interface ReactorRegistrar {

    <T extends Message> void subscribeReactor(Class<T> messageClass, Reactor<T> reactor);

    <T extends Message> List<Reactor<T>> reactorsFor(String messageClass);
}

package com.eventjournal.api;

import com.eventjournal.api.impl.Outcome;

/**
 * A reactor is a component that processes messages of type T.
 * The outcome of processing a message is encapsulated in an Outcome object.
 * @param <T>
 */
public interface Reactor<T extends Message> {

    Outcome handle(T message);

}

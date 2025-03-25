package com.eventjournal.api.impl;

import com.eventjournal.api.Message;
import com.eventjournal.api.Reactor;

/**
 * Abstract base class for all reactors that handle messages of type T.
 *
 * @param <T> the type of message this reactor handles
 */
public abstract class AbstractReactor<T extends Message> implements Reactor<T> {

    protected EventJournal eventJournal;

    /**
     * Constructs a new AbstractReactor with the specified EventJournal.
     *
     * @param eventJournal the event journal to be used by this reactor
     */
    public AbstractReactor(EventJournal eventJournal) {
        this.eventJournal = eventJournal;
    }

    @Override
    public abstract Outcome handle(T message);

}

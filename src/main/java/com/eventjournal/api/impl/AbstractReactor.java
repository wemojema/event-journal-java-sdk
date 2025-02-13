package com.eventjournal.api.impl;

import com.eventjournal.api.Message;
import com.eventjournal.api.Reactor;

public abstract class AbstractReactor<T extends Message> implements Reactor<T> {

    protected EventJournal eventJournal;

    public AbstractReactor(EventJournal eventJournal) {
        this.eventJournal = eventJournal;
    }

    @Override
    public abstract Outcome handle(T message);

}

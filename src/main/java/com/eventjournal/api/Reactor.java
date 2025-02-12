package com.eventjournal.api;

import com.eventjournal.api.impl.Outcome;

public interface Reactor<T extends Message> {

    Outcome handle(T message);

}

package com.eventjournal.api;

public interface Reactor<T extends Message> {

    Outcome handle(T message);

}

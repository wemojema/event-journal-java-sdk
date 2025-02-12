package com.eventjournal.api;

import java.util.List;

public interface ReactorRegistrar {

    <T extends Message> void subscribeReactor(Class<T> messageClass, Reactor<T> reactor);

    <T extends Message> List<Reactor<T>> reactorsFor(String messageClass);
}

package com.eventjournal.api.reactor;

import com.eventjournal.api.Message;
import com.eventjournal.api.Reactor;
import com.eventjournal.api.ReactorRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SimpleReactorRegistrar implements ReactorRegistrar {
    Logger log = LoggerFactory.getLogger(SimpleReactorRegistrar.class);
    Map<String, List<Reactor<? extends Message>>> reactors = new HashMap<>();

    @Override
    public <T extends Message> void subscribeReactor(Class<T> messageClass, Reactor<T> reactor) {
        reactors.merge(messageClass.getSimpleName(), List.of(reactor),
                (oldValue, newValue) -> {
                    log.warn("A Reactor is already subscribed for {}. Multiple Reactors for the same Message Type is not recommended.", messageClass.getSimpleName());
                    oldValue.addAll(newValue);
                    return oldValue;
                });
    }


    @Override
    public <T extends Message> List<Reactor<T>> reactorsFor(String messageClass) {
        return Optional.ofNullable(reactors.get(messageClass))
                .orElseGet(ArrayList::new)
                .stream()
                .map(r -> (Reactor<T>) r)
                .toList();

    }
}

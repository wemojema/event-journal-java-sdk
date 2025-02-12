package com.eventjournal.api.impl;

import com.eventjournal.api.Message;

import java.util.function.Function;

public class Entry {

    private Message message;

    public Entry(Message message) {
        this.message = message;
    }

    public Entry andThen(Function<Message, Entry> consumer) {
        return consumer.apply(message);
    }

}

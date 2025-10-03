package com.eventjournal.api.impl;

import com.eventjournal.api.Header;
import com.eventjournal.api.Message;

import java.util.function.Consumer;
import java.util.function.Function;

public class JournalEntry {

    private Message message;
    private Header header;

    public JournalEntry(Message message, Header header) {
        this.message = message;
        this.header = header;
    }

    public JournalEntry andThen(Function<Message, JournalEntry> consumer) {
        return consumer.apply(message);
    }

    public void andThen(Consumer<Message> consumer) {
        consumer.accept(message);
    }

    public Header header() {
        return header;
    }

}

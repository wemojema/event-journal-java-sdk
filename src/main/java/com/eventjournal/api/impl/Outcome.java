package com.eventjournal.api.impl;

import com.eventjournal.api.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Outcome {

    List<Message> messages = new ArrayList<>();

    private Outcome(Message message) {
        this.messages.add(message);
    }

    private Outcome() {
    }

    /**
     * Use this method if the Outcome of handling a command or event is inert.
     * As in, no further messages should be recorded as a result of handling a message.
     *
     * @return an Outcome object representing an inert outcome (contains no further messages).
     */
    public static Outcome inert() {
        return new Outcome();
    }

    public static Outcome of(Message... event) {
        Outcome outcome = new Outcome();
        outcome.messages.addAll(Arrays.asList(event));
        return outcome;
    }

    public static Outcome of(List<Message> messages) {
        Outcome outcome = new Outcome();
        outcome.messages.addAll(messages);
        return outcome;
    }

    public void add(Message message) {
        this.messages.add(message);
    }

}

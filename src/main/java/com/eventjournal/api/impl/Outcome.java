package com.eventjournal.api.impl;

import com.eventjournal.api.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Represents the outcome of handling a command or event.
 */
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

    /**
     * A convenient factory method to create an Outcome object with a single message or
     * several Messages that are not already collected.
     * @param message the message to be included in the Outcome
     * @return an Outcome object containing the specified message
     */
    public static Outcome of(Message... message) {
        Outcome outcome = new Outcome();
        outcome.messages.addAll(Arrays.asList(message));
        return outcome;
    }

    /**
     * A convenient factory method to create an Outcome with a collection of messages.
     * @param messages the messages to be included in the Outcome
     * @return an Outcome object containing the specified messages
     */
    public static Outcome of(Collection<Message> messages) {
        Outcome outcome = new Outcome();
        outcome.messages.addAll(messages);
        return outcome;
    }

    /**
     * Adds a message to the Outcome.
     * @param message the message to be added to the Outcome
     */
    public void add(Message message) {
        this.messages.add(message);
    }

}

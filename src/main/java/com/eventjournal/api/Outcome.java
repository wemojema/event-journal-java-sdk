package com.eventjournal.api;

public class Outcome {

    Message message;

    private Outcome(Message message) {
        this.message = message;
    }

    private Outcome() {
    }

    /**
     * Use this method if the Outcome of handling a command or event is inert.
     * As in, no further messages should be recorded as a result of handling a message.
     * @return an Outcome object representing an inert outcome (contains no further messages).
     */
    public static Outcome inert() {
        return new Outcome();
    }

    public Outcome of(Message event) {
        return new Outcome(event);
    }

}

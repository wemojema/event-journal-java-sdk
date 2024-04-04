package com.eventjournal.api.ex;

/**
 * Thrown when an aggregate is incomplete. This can happen when an aggregate is
 * missing a no Args Constructor, or when an aggregate is missing or has
 * protected access to an event handler.
 */
public class IncompleteAggregateException extends RuntimeException {
    public IncompleteAggregateException(String s, ReflectiveOperationException e) {
        super(s, e);
    }
}

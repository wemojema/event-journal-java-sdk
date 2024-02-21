package com.eventjournal.api.ex;

public class IncompleteAggregateException extends RuntimeException {
    public IncompleteAggregateException(String s, ReflectiveOperationException e) {
        super(s, e);
    }
}

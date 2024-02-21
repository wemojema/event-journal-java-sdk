package com.eventjournal.api.ex;

public class StaleAggregateException extends RuntimeException {
    public StaleAggregateException(String s) {
        super(s);
    }

    public static void throwIf(boolean condition) {
        if (condition)
            throw new StaleAggregateException("Command executed against a stale aggregate, refresh aggregate!");
    }

}

package com.eventjournal.api;

public interface Aggregate {
    String getId();

    default String streamId() {
        return StreamId.of(getClass(), getId());
    }

    default Integer version() { return 0; };
}

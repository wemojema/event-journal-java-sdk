package com.eventjournal.api;

public interface Aggregate {
    String getId();

    default String streamId() {
        return StreamId.of(getClass(), getId());
    }

    default long version() { return 0L; };
}

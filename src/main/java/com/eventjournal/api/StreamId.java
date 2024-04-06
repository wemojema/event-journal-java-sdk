package com.eventjournal.api;

import java.util.Objects;

public class StreamId {

    public static final String STREAM_ID_DELIMITER = "/";

    public static <T extends Aggregate> String of(Class<T> aggregateClass, String aggregateId) {
        return Objects.requireNonNull(aggregateClass, "The Class provided is null!").getSimpleName() +
                STREAM_ID_DELIMITER +
                Objects.requireNonNull(aggregateId, "Aggregate ID is required!");
    }

    public static String of(Aggregate aggregate) {
        return of(aggregate.getClass(), aggregate.getId());
    }

}

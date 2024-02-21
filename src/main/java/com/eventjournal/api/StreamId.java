package com.eventjournal.api;

import java.util.Objects;

public class StreamId {
    public static <T extends Aggregate> String of(Class<T> aggregateClass, String aggregateId) {
        return Objects.requireNonNull(aggregateClass, "The Class provided is null!").getSimpleName() +
                "/" +
                Objects.requireNonNull(aggregateId, "Aggregate ID is required!");
    }
}

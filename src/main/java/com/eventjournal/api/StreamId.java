package com.eventjournal.api;

import java.util.Objects;

/**
 * A utility class to generate a streamId for a given aggregate.
 * The streamId is used to identify an event stream.
 * By default, the streamId is the simple name of the aggregate class
 * concatenated with the aggregate id, separated by a delimiter.
 * However, this is only a convention, feel free to customize the
 * StreamId to any String format such as a simple UUID.
 */
public class StreamId {

    public static final String STREAM_ID_DELIMITER = "/";

    public static <T extends Aggregate> String of(Class<T> aggregateClass, String aggregateId) {
        return Objects.requireNonNull(aggregateClass, "The Class provided is null!").getSimpleName() +
                STREAM_ID_DELIMITER +
                Objects.requireNonNull(aggregateId, "Aggregate ID is required!");
    }

    public static String of(Aggregate aggregate) {
        return of(aggregate.getClass(), aggregate.id());
    }

}

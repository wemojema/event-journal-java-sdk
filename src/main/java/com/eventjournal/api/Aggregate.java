package com.eventjournal.api;

/**
 * An interface representing an aggregate,
 * an interface to label a class as an aggregate.
 * Aggregates should know their streamId, id, and version.
 */
public interface Aggregate {
    /**
     * The id of the aggregate
     * @return the String id of the aggregate
     */
    String id();

    /**
     * The streamId of the aggregate
     * @return the String streamId of the aggregate,
     * used to identify an event stream.
     */
    default String streamId() {
        return StreamId.of(getClass(), id());
    }

    /**
     * The version of the aggregate
     * @return the long version of the aggregate
     * used to determine the sequence of events
     * when replaying events to hydrate the aggregate.
     */
    default long version() { return 0L; };
}

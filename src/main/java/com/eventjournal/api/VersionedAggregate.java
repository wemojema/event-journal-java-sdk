package com.eventjournal.api;

/**
 * Base class for all versioned aggregates.
 * conveniently provides the id and version fields required by the Aggregate interface.
 * <p>
 * Additionally, during playback, the version field is incremented for each event applied to the aggregate.
 * removing the necessity for an aggregate to manage its own versioning, rather it can focus
 * on the logic associated to the events it handles.
 */
public abstract class VersionedAggregate implements Aggregate {

    protected String id;
    private long version;

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final String streamId() {
        return StreamId.of(this.getClass(), id);
    }

    @Override
    public final long version() {
        return this.version;
    }

    public final void incrementVersion() {
        this.version++;
    }

}

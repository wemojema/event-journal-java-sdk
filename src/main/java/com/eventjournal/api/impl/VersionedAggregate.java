package com.eventjournal.api.impl;

import com.eventjournal.api.Aggregate;
import com.eventjournal.api.Header;
import com.eventjournal.api.Message;
import com.eventjournal.api.StreamId;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @JsonIgnore
    private EventJournal eventJournal;

    @Override
    public final String id() {
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

    /**
     * Emits a new event with the header set to the current aggregate id and version.
     *
     * @param event the event to emit
     * @param cause the command that caused the event
     */
    protected final void emit(Message.Event event, Message.Command cause) {
        event.withHeader(Header.resultingFrom(cause, this, event.getClass()));
        eventJournal.record(event);
    }

    /**
     * Emits a new event with the header set to the current aggregate id and version.
     *
     * @param event the event to emit
     * @param cause the event that caused the event
     */
    protected final void emit(Message.Event event, Message.Event cause) {
        eventJournal.record(event.withHeader(Header.resultingFrom(cause, this, event.getClass())));
    }

    /**
     * Emits a new command with the header set to the current aggregate id and version.
     *
     * @param command the command to emit
     * @param cause   the event that caused the command
     * @return the command with the header properly constructed
     */
    protected final Message.Command emit(Message.Command command, Message.Event cause) {
        return command.withHeader(Header.sideEffectOf(cause, this, command.getClass()));
    }

    /**
     * Emits a new command with the header set to the current aggregate id and version.
     *
     * @param command the command to emit
     * @param cause   the command that caused the command
     * @return the command with the header properly constructed
     */
    protected final Message.Command emit(Message.Command command, Message.Command cause) {
        return command.withHeader(Header.sideEffectOf(cause, this, command.getClass()));
    }


    public <T extends VersionedAggregate> T withId(Class<T> clazz, String aggregateId) {
        if(clazz != this.getClass()) {
            throw new IllegalArgumentException("Cannot change the type of an aggregate");
        }
        this.id = aggregateId;
        return (T) this;
    }

    public VersionedAggregate withEventJournal(EventJournal eventJournal) {
        this.eventJournal = eventJournal;
        return this;
    }
}

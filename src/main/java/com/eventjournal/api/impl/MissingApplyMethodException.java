package com.eventjournal.api.impl;

import com.eventjournal.api.Message;

public class MissingApplyMethodException extends RuntimeException {
    public MissingApplyMethodException(Class<? extends VersionedAggregate> aggregateClass, Class<? extends Message.Event> eventClass) {
        super(aggregateClass.getSimpleName() + " attempted to emit an event of type " + eventClass.getSimpleName() + " but no apply method was found for that event type.");
    }
}

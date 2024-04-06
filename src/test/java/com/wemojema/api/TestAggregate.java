package com.wemojema.api;

import com.eventjournal.api.Aggregate;
import com.eventjournal.api.StreamId;
import com.wemojema.fixtures.TestEvent;

public class TestAggregate implements Aggregate {
    int version;

    @Override
    public String getId() {
        return "1";
    }

    @Override
    public String streamId() {
        return StreamId.of(TestAggregate.class, getId());
    }

    @Override
    public long version() {
        return version;
    }

    public void apply(TestEvent event) {
        version++;
        // apply the event data to the model
    }

}

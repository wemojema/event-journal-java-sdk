package com.wemojema.api;

import com.eventjournal.api.Aggregate;
import com.eventjournal.api.Outcome;
import com.eventjournal.api.StreamId;
import com.wemojema.fixtures.TestEvent;

public class TestAggregate implements Aggregate {
    int version;

    @Override
    public String id() {
        return "1";
    }

    @Override
    public String streamId() {
        return StreamId.of(TestAggregate.class, id());
    }

    @Override
    public long version() {
        return version;
    }

    public Outcome apply(TestEvent event) {
        version++;
        return Outcome.inert();
    }

}

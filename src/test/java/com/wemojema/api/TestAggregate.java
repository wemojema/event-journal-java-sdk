package com.wemojema.api;

import com.eventjournal.api.Aggregate;
import com.eventjournal.api.StreamId;

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
    public Integer version() {
        return version;
    }
}

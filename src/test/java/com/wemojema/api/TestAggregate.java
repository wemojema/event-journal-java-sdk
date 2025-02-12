package com.wemojema.api;

import com.eventjournal.api.impl.Outcome;
import com.eventjournal.api.impl.VersionedAggregate;
import com.wemojema.fixtures.TestEvent;

public class TestAggregate extends VersionedAggregate {

    public Outcome apply(TestEvent event) {
        return Outcome.inert();
    }

}

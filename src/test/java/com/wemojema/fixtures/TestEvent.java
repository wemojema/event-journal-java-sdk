package com.wemojema.fixtures;

import com.eventjournal.api.Header;
import com.eventjournal.api.Message;

public class TestEvent extends Message.Event {

    public TestEvent(Header header) {
        super(header);
    }

    private TestEvent() {
    }
}

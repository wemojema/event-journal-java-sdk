package com.wemojema.api;

import com.eventjournal.api.Header;
import com.eventjournal.api.StreamId;
import com.eventjournal.api.testing.fixtures.MockEventStoreClient;
import com.wemojema.BaseTest;
import com.eventjournal.api.impl.EventJournal;
import com.wemojema.fixtures.TestEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Recording extends BaseTest {

    EventJournal eventJournal;
    MockEventStoreClient fakeEventStoreClient;


    @BeforeEach
    public void setup() {
        fakeEventStoreClient = new MockEventStoreClient();
        eventJournal = new EventJournal(fakeEventStoreClient);
    }

    @Test
    public void should_record_into_stream_provided() {
        TestEvent event = new TestEvent(Header.headOfChain(TestAggregate.class, "1", TestEvent.class));
        eventJournal.record(event);
        Assertions.assertEquals(1, fakeEventStoreClient.stream(StreamId.of(TestAggregate.class, "1")).events().size());
    }

}

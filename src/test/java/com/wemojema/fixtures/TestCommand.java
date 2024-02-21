package com.wemojema.fixtures;

import com.eventjournal.api.Header;
import com.wemojema.api.TestAggregate;
import com.eventjournal.api.Message;

import java.time.Instant;

public class TestCommand extends Message.Command {

    String streamId;
    Instant timestamp;
    int version;
    Header header;

    public TestCommand(String streamId, Instant timestamp, int version, Header header) {
        super(header);
        this.streamId = streamId;
        this.timestamp = timestamp;
        this.version = version;
        this.header = header;
    }

    @Override
    public String streamId() {
        return streamId == null ? "TestAggregate|1" : streamId;
    }

    @Override
    public Instant timestamp() {
        return timestamp == null ? Instant.now() : timestamp;
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public Header header() {
        return header == null ? Header.headOfChain(TestAggregate.class, "1", TestCommand.class) : header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }


}

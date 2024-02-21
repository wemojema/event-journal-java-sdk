package com.eventjournal.api;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.net.URI;
import java.time.Instant;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public interface Message {
    String streamId();

    Instant timestamp();

    int version();

    Header header();

    MessageCategory messageCategory();

    abstract class Command implements Message {
        Header messageHeader;

        public Command() {
        }

        public Command(Header header) {
            this.messageHeader = header;
            Objects.requireNonNull(header, "The Message Header cannot be null!");
            Objects.requireNonNull(header.sequence, "version cannot be null!");
            Objects.requireNonNull(header.streamId, "Stream ID cannot be null!");
            Objects.requireNonNull(header.timestamp, "Timestamp cannot be null!");
        }

        @Override
        public MessageCategory messageCategory() {
            return MessageCategory.COMMAND;
        }

        @Override
        public String streamId() {
            return messageHeader.streamId;
        }

        @Override
        public Instant timestamp() {
            return messageHeader.timestamp;
        }

        @Override
        public int version() {
            return messageHeader.sequence;
        }

        @Override
        public Header header() {
            return messageHeader;
        }

    }

    abstract class Event implements Message {
        Header header;

        public Event() {
        }

        public Event(Header header) {
            this.header = header;
            Objects.requireNonNull(header, "The Message Header cannot be null!");
            Objects.requireNonNull(header.sequence, "version cannot be null!");
            Objects.requireNonNull(header.streamId, "Stream ID cannot be null!");
            Objects.requireNonNull(header.timestamp, "Timestamp cannot be null!");
        }

        @Override
        public MessageCategory messageCategory() {
            return MessageCategory.COMMAND;
        }

        @Override
        public String streamId() {
            return header.streamId;
        }

        @Override
        public Instant timestamp() {
            return header.timestamp;
        }

        @Override
        public int version() {
            return header.sequence;
        }

        @Override
        public Header header() {
            return header;
        }

    }

    enum MessageCategory {
        EVENT,
        COMMAND,
        ;

        public static MessageCategory of(Class<? extends Message> messageType) {
            return messageType.getSuperclass()
                    .getSimpleName()
                    .equals(Event.class.getSimpleName())
                    ? EVENT
                    : COMMAND;
        }

    }

    record Producer(String name, URI source){
        public static final Producer ANONYMOUS = new Producer("anonymous", URI.create("http://localhost"));
    }

}

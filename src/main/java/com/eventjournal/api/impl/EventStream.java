package com.eventjournal.api.impl;

import com.eventjournal.api.Message;

import java.util.ArrayList;
import java.util.List;

public class EventStream {

    private List<Message.Event> events;

    public EventStream(List<Message.Event> events) {
        this.events = new ArrayList<>(events);
    }

    public List<Message.Event> events() {
        return new ArrayList<>(events);
    }

}

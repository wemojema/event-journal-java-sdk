package com.eventjournal.api.impl;

import com.eventjournal.api.Message;

import java.util.ArrayList;
import java.util.List;

public record EventStream(List<Message.Event> events) {

    @Override
    public List<Message.Event> events() {
        return new ArrayList<>(events);
    }

}

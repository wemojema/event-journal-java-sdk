package com.eventjournal.api.impl;

import com.eventjournal.api.Aggregate;
import com.eventjournal.api.Message;
import com.eventjournal.api.StreamId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart implements Aggregate {

    String id;
    List<String> items = new ArrayList<>();
    int version;

    public Cart() {
    }

    public Cart(String id) {
        this.id = id;
    }

    @Override
    public Integer version() {
        return version;
    }

    @Override
    public String streamId() {
        return StreamId.of(Cart.class, id);
    }

    @Override
    public String getId() {
        return id;
    }

    public Message.Event handle(AddItem command) {
        return new ItemAdded(this, command);
    }

    public List<Message> apply(ItemAdded event) {
        this.items.add(event.itemId);
        return Collections.emptyList();
    }

}

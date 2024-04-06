package com.eventjournal.api.impl;

import com.eventjournal.api.Message;
import com.eventjournal.api.VersionedAggregate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart extends VersionedAggregate {

    List<String> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(String id) {
        this.id = id;
    }

    public Message.Event handle(AddItem command) {
        return new ItemAdded(this, command);
    }

    public List<Message> apply(ItemAdded event) {
        this.items.add(event.itemId);
        return Collections.emptyList();
    }

}

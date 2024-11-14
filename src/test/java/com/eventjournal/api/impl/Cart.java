package com.eventjournal.api.impl;

import com.eventjournal.api.Message;
import com.eventjournal.api.Outcome;
import com.eventjournal.api.VersionedAggregate;

import java.util.ArrayList;
import java.util.List;

public class Cart extends VersionedAggregate {

    List<String> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(String id) {
        this.id = id;
    }

    public Message.Event handle(AddItem command) {
        return emit(new ItemAdded(command.itemId), command);
    }

    public Outcome apply(ItemAdded event) {
        this.items.add(event.itemId);
        return Outcome.inert();
    }

}

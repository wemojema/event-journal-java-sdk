package com.eventjournal.api.impl;

import com.eventjournal.api.Header;
import events.SomethingHappened;

import java.util.ArrayList;
import java.util.List;

public class Cart extends VersionedAggregate {

    List<String> items = new ArrayList<>();

    public Cart() {
    }

    public Cart(String id) {
        this.id = id;
    }

    public void handle(AddItem command) {
        super.emit(new ItemAdded(command.itemId), command);
    }

    public void apply(ItemAdded event) {
        this.items.add(event.itemId);
    }

    public void emitEventForWhichTheCartDoesNotHaveAnApplyMethod() {
        super.emit(new SomethingHappened(Header.headOfChain(this, SomethingHappened.class), "", "", "", ""), new ItemAdded(""));
    }
}

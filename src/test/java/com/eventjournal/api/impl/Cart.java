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
        super.emit(new ItemAdded(this.id, command.itemId), command);
    }

    public void apply(ItemAdded event) {
        if(id == null)
            id = event.cartId;
        this.items.add(event.itemId);
    }

    public void emitEventForWhichTheCartDoesNotHaveAnApplyMethod() {
        super.emit(new SomethingHappened(Header.headOfChain(this, SomethingHappened.class), "", "", "", ""), new ItemAdded("",""));
    }
}

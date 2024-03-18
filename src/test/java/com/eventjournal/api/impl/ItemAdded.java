package com.eventjournal.api.impl;

import com.eventjournal.api.Header;
import com.eventjournal.api.Message;

public class ItemAdded extends Message.Event {

    String itemId;

    public ItemAdded(Cart cart, AddItem command) {
        super(Header.resultingFrom(command, cart, ItemAdded.class));
        this.itemId = command.itemId;
    }

    public ItemAdded() {
    }
}

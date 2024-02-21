package com.example;

import com.eventjournal.api.Header;
import com.eventjournal.api.Message;

public class ItemAdded extends Message.Event {

    String itemId;

    public ItemAdded(Cart cart, AddItem command) {
        super(Header.resultingFrom(command.header(), ItemAdded.class));
        this.itemId = command.itemId;
    }

    public ItemAdded() {
    }
}

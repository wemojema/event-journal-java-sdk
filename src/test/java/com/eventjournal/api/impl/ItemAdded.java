package com.eventjournal.api.impl;

import com.eventjournal.api.Header;
import com.eventjournal.api.Message;

import java.util.UUID;

public class ItemAdded extends Message.Event {

    String cartId;
    String itemId;

    public ItemAdded(Cart cart, AddItem command) {
        super(Header.resultingFrom(command, cart, ItemAdded.class));
        this.cartId = cart.id() == null ? UUID.randomUUID().toString() : cart.id();
        this.itemId = command.itemId;
    }

    private ItemAdded() {

    }

    public ItemAdded(String cartId, String itemId) {
        this.cartId = cartId;
        this.itemId = itemId;
    }

}

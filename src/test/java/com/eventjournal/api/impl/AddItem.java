package com.eventjournal.api.impl;

import com.eventjournal.api.Header;
import com.eventjournal.api.Message;

public class AddItem extends Message.Command {

    String itemId;

    public AddItem(Header header, String itemId) {
        super(header);
        this.itemId = itemId;
    }

}

package com.eventjournal.api.impl;

import com.eventjournal.api.Header;
import com.wemojema.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CartTest extends BaseTest {

    EventJournal eventJournal = new EventJournal(new MockEventStoreClient());


    @Test
    void should_result_with_a_Cart_with_items() {
        Cart cart = eventJournal.playback(Cart.class, faker.idNumber().valid());

        cart.handle(new AddItem(Header.headOfChain(Cart.class, cart.id(), ItemAdded.class, 0), faker.idNumber().valid()));

        Cart playbackCart = eventJournal.playback(Cart.class, cart.id());

        Assertions.assertFalse(playbackCart.items.isEmpty());
    }


}

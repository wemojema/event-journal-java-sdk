package com.example;

import com.eventjournal.api.Header;
import com.eventjournal.api.Message;
import com.eventjournal.api.impl.EventJournal;
import com.eventjournal.api.testing.fixtures.MockEventStoreClient;
import com.wemojema.BaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CartTest extends BaseTest {

    EventJournal eventJournal = new EventJournal(new MockEventStoreClient());


    @Test
    void should_result_with_a_Cart_with_items() {
        Cart cart = new Cart(faker.idNumber().valid());
        Message.Event result = cart.handle(new AddItem(Header.headOfChain(Cart.class, cart.id, ItemAdded.class), faker.idNumber().valid()));
        eventJournal.record(result);

        Cart playbackCart = eventJournal.playback(Cart.class, cart.id);

        Assertions.assertFalse(playbackCart.items.isEmpty());
    }


}

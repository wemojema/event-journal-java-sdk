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
        Assertions.assertEquals(1, playbackCart.version());
    }

    @Test
    void should_cause_a_MissingApplyMethodException_when_a_cart_emits_an_event_it_does_not_have_an_apply_method_for() {
        Cart cart = eventJournal.playback(Cart.class, faker.idNumber().valid());

        Assertions.assertThrows(MissingApplyMethodException.class, cart::emitEventForWhichTheCartDoesNotHaveAnApplyMethod);
        Cart cartPlayback = eventJournal.playback(Cart.class, cart.id());
        Assertions.assertEquals(0, cartPlayback.version());
    }


}

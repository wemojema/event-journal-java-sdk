package com.eventjournal.api.impl;

import com.eventjournal.api.Header;
import com.wemojema.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
class EventJournalTest extends BaseTest {

    private EventJournal uut;

    @BeforeEach
    void setUp() {
        uut = new EventJournal(new MockEventStoreClient());
    }

    @Test
    void should_throw_the_supplied_exception_when_the_aggregate_is_not_found() {
        assertThrows(IllegalArgumentException.class, () -> uut.playbackOrElseThrow(Cart.class, faker.idNumber().valid(), IllegalArgumentException::new));
    }

    @Test
    void should_not_throw_the_supplied_exception_when_the_aggregate_is_found() {
        Cart cart = uut.playback(Cart.class, faker.idNumber().valid());

        cart.handle(new AddItem(Header.headOfChain(Cart.class, cart.id(), AddItem.class, 0), faker.idNumber().valid()));

        assertDoesNotThrow(() -> uut.playbackOrElseThrow(Cart.class, cart.id(), IllegalArgumentException::new));
    }

}
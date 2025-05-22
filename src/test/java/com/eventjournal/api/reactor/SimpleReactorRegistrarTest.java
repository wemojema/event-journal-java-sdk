package com.eventjournal.api.reactor;

import com.eventjournal.api.impl.Outcome;
import com.wemojema.BaseTest;
import com.wemojema.fixtures.TestEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleReactorRegistrarTest extends BaseTest {

    SimpleReactorRegistrar uut;

    @BeforeEach
    void setUp() {
        uut = new SimpleReactorRegistrar();
    }


    @Test
    void should_not_throw_exception_when_subscribing_a_reactor() {
        Assertions.assertDoesNotThrow(() -> uut.subscribeReactor(TestEvent.class, m -> Outcome.inert()));
    }

    @Test
    void should_not_throw_exception_when_subscribing_multiple_reactors_for_the_same_messag_type() {
        Assertions.assertDoesNotThrow(() -> {
            uut.subscribeReactor(TestEvent.class, m -> Outcome.inert());
            uut.subscribeReactor(TestEvent.class, m -> Outcome.inert());
        });
    }

}
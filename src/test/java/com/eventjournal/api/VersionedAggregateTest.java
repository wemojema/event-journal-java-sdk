package com.eventjournal.api;

import com.eventjournal.api.impl.VersionedAggregate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionedAggregateTest {


    private static class TestAgg extends VersionedAggregate {
        public TestAgg() {
            this.id = "1";
        }
    }

    TestAgg uut;

    @BeforeEach
    void setUp() {
        uut= new TestAgg();
    }

    @Test
    void should_identify_the_correct_StreamID_for_this_agg() {
        assertEquals("TestAgg/1", uut.streamId());
    }


}
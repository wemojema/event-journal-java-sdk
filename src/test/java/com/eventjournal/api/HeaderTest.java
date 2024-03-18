package com.eventjournal.api;

import com.wemojema.BaseTest;
import com.wemojema.api.TestAggregate;
import com.wemojema.fixtures.TestCommand;
import com.wemojema.fixtures.TestEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class HeaderTest extends BaseTest {
    Header uut;

    @BeforeEach
    void setUp() {
        uut = Header.headOfChain(TestAggregate.class, faker.idNumber().valid(), TestCommand.class);
    }

    @Test
    void should_be_of_message_type_COMMAND() {
        uut = Header.headOfChain(TestAggregate.class, faker.idNumber().valid(), TestCommand.class);
        Assertions.assertEquals(Message.MessageCategory.COMMAND, uut.category);
    }

    @Test
    void should_be_of_message_type_EVENT() {
        TestAggregate testAggregate = new TestAggregate();
        TestCommand testCommand = new TestCommand("TestAggregate|1", Instant.now(), 0, Header.headOfChain(TestAggregate.class, "1", TestCommand.class));
        // act
        uut = Header.resultingFrom(testCommand, testAggregate, TestEvent.class);
        // assert
        Assertions.assertEquals(Message.MessageCategory.EVENT, uut.category);
    }

//    @Test
//    void should_give_me_a_version_bump() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//
//        MessageHeader resultingHeader = MessageHeader.resultingFrom(TestAggregate.class, command.header(), TestEvent.class);
//
//        Assertions.assertEquals(1, resultingHeader.version());
//    }

//    @Test
//    void should_correlate_to_the_original_correlation_id() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//
//        MessageHeader resultingHeader = MessageHeader.resultingFrom(TestAggregate.class, command.header(), TestEvent.class);
//
//        Assertions.assertEquals(uut.messageChainId, resultingHeader.messageChainId);
//    }
//
//    @Test
//    void should_be_caused_by() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//
//        MessageHeader resultingHeader = MessageHeader.resultingFrom(TestAggregate.class, command.header(), TestEvent.class);
//
//        Assertions.assertEquals(uut.messageId, resultingHeader.causationId);
//    }
//
//    @Test
//    void should_not_have_the_same_message_id() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//
//        MessageHeader resultingHeader = MessageHeader.resultingFrom(TestAggregate.class, command.header(), TestEvent.class);
//
//        Assertions.assertNotEquals(uut.messageId, resultingHeader.messageId);
//    }
//
//    @Test
//    void should_have_the_EVENT_category() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//
//        MessageHeader resultingHeader = MessageHeader.resultingFrom(TestAggregate.class, command.header(), TestEvent.class);
//
//        Assertions.assertEquals(MessageCategory.EVENT, resultingHeader.category);
//    }
//
//    @Test
//    void should_know_the_message_type_of_the_resulting_event() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//
//        MessageHeader resultingHeader = MessageHeader.resultingFrom(TestAggregate.class, command.header(), TestEvent.class);
//
//        Assertions.assertEquals(TestEvent.class.getSimpleName(), resultingHeader.messageType);
//    }
//
//    @Test
//    void should_have_a_timestamp_after_the_causal_commands_timestamp() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//
//        MessageHeader resultingHeader = MessageHeader.resultingFrom(TestAggregate.class, command.header(), TestEvent.class);
//
//        Assertions.assertTrue(uut.timestamp.isBefore(resultingHeader.timestamp));
//    }
//
//    @Test
//    void should_share_a_stream_id() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//
//        MessageHeader resultingHeader = MessageHeader.resultingFrom(TestAggregate.class, command.header(), TestEvent.class);
//
//        Assertions.assertEquals(uut.streamId, resultingHeader.streamId);
//    }
//
//    @Test
//    void should_set_all_properties_of_the_header() {
//        Assertions.assertEquals(MessageCategory.COMMAND, uut.category);
//    }
//
//    @Test
//    void should_be_a_SideEffectOf() {
//        TestCommand command = new TestCommand();
//        command.setHeader(uut);
//        MessageHeader sideEffectOfHeader = MessageHeader.sideEffectOf(command, new AggregateRootTarget(UUID.randomUUID().toString(), TestAggregate.class, null), TestEvent.class);
//        Assertions.assertEquals(MessageCategory.COMMAND, sideEffectOfHeader.category);
//
//    }

}
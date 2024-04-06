import com.eventjournal.api.Header;
import com.eventjournal.api.Message;
import com.eventjournal.api.impl.EventJournal;
import com.wemojema.api.TestAggregate;
import com.wemojema.fixtures.TestEvent;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class TestDrive {
    public static void main(String[] args) {
        integrationTest___StoreAndReadEvent();
    }


    //Public Key: F5I655ECVD5H4YEFHS7M1QYVK38X8G0V
    //Secret Key: h>OlTIqpyM1b9B2zxHtcS>ZkB/IyZl2<zJ<oGGe+<j430TUXZDpt/14MjXTHj8xn
    public static void integrationTest___StoreAndReadEvent() {
        EventJournal ej = new EventJournal("F5I655ECVD5H4YEFHS7M1QYVK38X8G0V", "h>OlTIqpyM1b9B2zxHtcS>ZkB/IyZl2<zJ<oGGe+<j430TUXZDpt/14MjXTHj8xn");
        String AGG_ID = UUID.randomUUID().toString();

        List<Message> events = IntStream.range(0, 10)
                .mapToObj(i ->
                        new TestEvent(Header.headOfChain(TestAggregate.class, AGG_ID, TestEvent.class, i))
                )
                .map(e -> (Message) e)
                .toList();

        ej.record(events);
        TestAggregate testAggregate = ej.playback(TestAggregate.class, AGG_ID);
        System.out.println(EventJournal.Toolbox.serialize(testAggregate));
    }

}
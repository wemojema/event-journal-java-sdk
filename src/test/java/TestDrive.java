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

    public static void integrationTest___StoreAndReadEvent() {
        EventJournal ej = new EventJournal("IG8DPC910RFHG9DVPIUOKREZEE52T7QD", "tv3bvRM4mqbza14BiEkbD3D>1LFg8z6xjuskaG/zfq<NDHbPW/s1ljiC5Xlh8kC<");
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
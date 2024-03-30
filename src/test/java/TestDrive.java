import com.eventjournal.api.Header;
import com.eventjournal.api.Message;
import com.eventjournal.api.impl.EventJournal;
import com.wemojema.api.TestAggregate;
import com.wemojema.fixtures.TestEvent;

import java.util.List;
import java.util.stream.IntStream;

public class TestDrive {
    public static void main(String[] args) {
        integrationTest_verify_connection();
    }

    public static void integrationTest_verify_connection() {
        EventJournal ej = new EventJournal("DQJBOAZ0IKHPQGURCEBSRG0NDR5VC9UG", "fMl5oTG6zNHjLUGJvzTALJkJZgngi4GA9AxZX+N0kGBj2<op81UP7EQLdK07t87v");
        List<Message> events = IntStream.range(0, 100)
                .mapToObj(i ->
                        new TestEvent(Header.headOfChain(TestAggregate.class, "0.057", TestEvent.class, i))
                )
                .map(e -> (Message) e)
                .toList();

        ej.record(events);
    }

}
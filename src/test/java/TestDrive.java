import com.eventjournal.api.Header;
import com.eventjournal.api.Message;
import com.eventjournal.api.impl.EventJournal;
import com.wemojema.api.TestAggregate;
import com.wemojema.fixtures.TestEvent;

public class TestDrive {
    public static void main(String[] args) {
        integrationTest_verify_connection();
    }

    public static void integrationTest_verify_connection() {
        // setting up a new EventJournal will automatically check the connection with the host.
        new EventJournal("test-key","test-secret");

        TestEvent event = new TestEvent(Header.headOfChain(TestAggregate.class, "1", TestEvent.class));
        String json = EventJournal.Toolbox.serialize(event);
        System.out.println(json);
        Message message = EventJournal.Toolbox.deserialize(json, Message.class);
        System.out.println(message.getClass().getName());
    }

}
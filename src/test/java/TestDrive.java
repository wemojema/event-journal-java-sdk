import com.eventjournal.api.Header;
import com.eventjournal.api.impl.EventJournal;
import com.wemojema.api.TestAggregate;
import com.wemojema.fixtures.TestEvent;

public class TestDrive {
    public static void main(String[] args) {
        integrationTest_verify_connection();
    }

    public static void integrationTest_verify_connection() {
        EventJournal ej = new EventJournal("TESTAPIKEYOYHLQ6U43Q7IZR2605A1MOU", "73de3b95cf69495f85fa8ae6f011fb0973de3b95cf69495f85fa8ae6f011fb09");
        TestEvent event = new TestEvent(Header.headOfChain(TestAggregate.class, "1", TestEvent.class, 1));
        ej.record(event);
    }

}
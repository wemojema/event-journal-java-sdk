import com.eventjournal.api.impl.EventJournal;
import com.eventjournal.api.impl.EventStream;

import java.util.ArrayList;

public class TestDrive {
    public static void main(String[] args) {
        integrationTest_verify_connection();
    }

    public static void integrationTest_verify_connection() {
        // setting up a new EventJournal will automatically check the connection with the host.
        new EventJournal("test-key","test-secret");
        EventStream eventStream = new EventStream(new ArrayList<>());

    }

}
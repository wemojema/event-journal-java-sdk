import com.eventjournal.api.impl.EventJournal;

public class TestDrive {
    public static void main(String[] args) {
        integrationTest_verify_connection();
    }

    public static void integrationTest_verify_connection() {
        // setting up a new EventJournal will automatically check the connection with the host.
        new EventJournal("test-key","test-secret");

    }

}
import com.eventjournal.api.impl.EventJournal;

public class TestDrive {
    public static void main(String[] args) {
        integrationTest_verify_connection();
    }

    public static void integrationTest_verify_connection() {
        // setting up a new EventJournal will automatically check the connection with the host.

        new EventJournal("GO4XP6LM9QSYZYCKKOZRQCWN9Q8BJRKU","Pv2NIFYq<y55tC4AVoKl8eXWLp95x14FEnFpYEtNVAW4FNSyzG27sFbOZHMzCEKA");

//        TestEvent event = new TestEvent(Header.headOfChain(TestAggregate.class, "1", TestEvent.class, 0));
//        String json = EventJournal.Toolbox.serialize(event);
//        System.out.println(json);
//        Message message = EventJournal.Toolbox.deserialize(json, Message.class);
//        System.out.println(message.getClass().getName());
//        event.header().put("testKey", "testValue");
//        Envelope envelope = Envelope.of(event);
//        envelope.getExtensionNames()
//                .forEach(extensionName -> {
//                    System.out.println(extensionName + ": " + envelope.getExtension(extensionName).toString());
//                });
    }

}
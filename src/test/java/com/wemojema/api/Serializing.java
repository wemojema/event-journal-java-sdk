package com.wemojema.api;

import com.eventjournal.api.Header;
import com.example.AddItem;
import com.example.Cart;
import com.example.ItemAdded;
import com.wemojema.BaseTest;
import com.eventjournal.api.Envelope;
import com.eventjournal.api.Message;
import com.eventjournal.api.impl.EventJournal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Serializing extends BaseTest {


    @Test
    public void should_deserialize_event_using_minimal_classname_types() {
        Cart cart = new Cart(faker.idNumber().valid());
        ItemAdded itemAdded = new ItemAdded(cart, new AddItem(Header.headOfChain(Cart.class, cart.getId(), AddItem.class), faker.idNumber().valid()));
        Envelope envelope = Envelope.of(itemAdded);


        String packed = EventJournal.Toolbox.serialize(envelope);
        System.out.println(packed);
        Assertions.assertTrue(packed.contains("@type\\\":\\\"com.example.ItemAdded\\\""));
        Assertions.assertDoesNotThrow(() -> EventJournal.Toolbox.deserialize(packed, Envelope.class));
        Assertions.assertDoesNotThrow(() -> EventJournal.Toolbox.deserialize(EventJournal.Toolbox.deserialize(packed, Envelope.class).getData().getSerializedMessage(), Message.class));
    }

}

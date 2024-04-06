# Recording Messages
When recording messages to a stream, event journal will automatically assign a sequence number to the message. This sequence number is used to order the events in the stream. The sequence number is a long value that is unique to the stream. The Sequence number and the Aggregate version() are closely tied together. In fact, the version() of the aggregate is used to derive the sequence of an event or command produced by the aggregate.

    
## Java

Command classes should extend the `Message.Command` class. Which has a single constructor requiring a `Header` object.

```java
public class ShipOrder extends Message.Command {
    private String id;
    public ShipOrder(Header header, String id) {
        super(header);
        this.id = id;
    }
}
```

Likewise, event classes should extend the `Message.Event` class. Which has a single constructor requiring a `Header` object.

```java

public class OrderShipped extends Message.Event {
    // any serializable properties are permitted here
    private String id;
    private String trackingId;
    public OrderShipped(Header header, String id, String trackingId) {
        super(header);
        this.id = id;
        this.trackingId = trackingId;
    }
}
```
Imagine the system just received the `ShipOrder` command. The `Order` aggregate would handle the command and produce an `OrderShipped` event. 
> 💡<br>
> Before handling a `Command`, record it. This is helpful especially in tracing and debugging complex systems.

After the Command is handled successfully the byproduct of which is the `OrderShipped` event. A new call to Event Journal is made to record the event.

```java
Order order = eventJournal.playback(Order.class, "1234");
ShipOrder command = new ShipOrder(Header.headOfChain(order, ShipOrder.class), "1234");
eventJournal.record(order.handle(command));
OrderShipped event = order.handle(command);
eventJournal.record(event);
```

Internally the Order aggregate would look something like this:

```java
public class Order implements Aggregate {

    private String id;
    private boolean paid;
    private ShipmentStatus status;

    public void handle(ShipOrder command) {
        if(!paid) {
            throw new RuntimeException("Order has not been paid for");
        }
        if(status == ShipmentStatus.SHIPPED) {
            throw new IllegalStateException("Order has already been shipped");
        }
        
        // do order shipping things
        
        return new OrderShipped(Header.sideEffectOf(command, this), "trackingId");
    }
    public void apply(OrderShipped event) {
        status = ShipmentStatus.SHIPPED;
    }
    
    // ... other methods
}
```
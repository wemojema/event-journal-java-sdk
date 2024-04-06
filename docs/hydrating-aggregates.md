# Hydrating Aggregates from an Event Stream
TLDR;
- Events are the source of truth in the system.
- Events are immutable facts that represent a change in the system.
- Aggregates are hydrated from events.
- Aggregate, Model, and Projection are all terms that are used (for the most part) interchangeably in this documentation.

## Event Streams
The primary purpose of reading events is to hydrate a model of some sort. The Aggregate should have apply() methods for each of the event types that it produces. The apply() method should be responsible for updating the state of the Aggregate based on the event that is being applied.
For example:
```java
public class OrderShipment implements Aggregate {

    private String id;
    private boolean paid;
    private ShipmentStatus status;

    public OrderInitiated handle(InitiateOrder command) {
        // some business rule validation
        return new OrderInitiated(command.getId());
    }

    public void apply(OrderInitiated event) {
        this.id = event.getId();
        this.status = ShipmentStatus.PENDING;
    }

    public void handle(ShipOrder command) {
        if(status == ShipmentStatus.SHIPPED) {
            throw new IllegalStateException("Order has already been shipped");
        }
        return new OrderShipped(id);
    }
    
    public void apply(OrderPaymentReceived event) {
        this.paid = true;
    }
    
    public void apply(OrderShipped event) {
        status = ShipmentStatus.SHIPPED;
    }
    
}
```

You can see here that the Aggregate has 0 dependencies. It is a simple POJO that has a few methods that are responsible for handling commands and applying events. The apply() method is responsible for updating the state of the Aggregate based on the event that is being applied.

## Hydrating Order number 1234
```java
EventJournal eventJournal = new EventJournal("publicKey", "privateKey");
Order order = eventJournal.playback(Order.class, "1234");
```
There is also a Time Machine capability here. You can hydrate the Order as it was at a specific point in time.

```java
Instant lastWeek = Instant.now().minus(Duration.ofDays(7));
Order order = eventJournal.playback(Order.class, "1234", lastWeek);
```
It is important to note that using a "point in time" Aggregate is not always safe to use for handling new commands.
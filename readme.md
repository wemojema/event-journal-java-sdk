# EventJournal SDK

## Overview

The EventJournal SDK provides a set of classes and methods to handle event-sourcing messaging within your application. It includes utilities for creating message headers, generating stream IDs, and recording and playing back events.

## Installation

To include the EventJournal SDK in your project, add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation 'com.eventjournal:api:${eventjournalversion}'
}
```

## Usage

Event Journal is intended to be used with an event-journal API account. Before you get started, create an account at [https://event-journal.com](https://event-journal.com). Once you have an account, application, and environment setup generate your API keys and use them to initialize the SDK. Like this:

```java
import com.eventjournal.api.EventJournal;

EventJournal ej = new EventJournal("your-api-key", "your-api-secret");
```
You are now ready to record your first event. Here is an example of how to record an event:

---

## VersionedAggregates
A Versioned aggregate is an Abstract class that all Event Sourced Aggregates must extend.
Each time an event is applied to a Versioned Aggregate, the version of the aggregate is incremented. Any new messages that are emitted from this aggregate use the aggregate's version as their sequence number.
The state of a Versioned Aggregate is deterministic so long as in the apply methods of the aggregate nothing non-deterministic is happening.

**Versioned Aggregates** should implement apply() methods for any events that can alter its state.

Example:
```java
public class Room extends VersionedAggregate {

    public String roomNumber;
    public List<Bed> beds;
    public Guest currentGuest;

    public Outcome apply(RoomRegistered event) {
        this.roomNumber = event.getRoomNumber();
        this.beds = event.getBeds();
        return Outcome.intert();
    }
    
    public Outcome apply(GuestCheckedIn event) {
        this.currentGuest = event.getGuest();
        return Outcome.intert();
    }
}
```

## Messages
`Messages` are the communication mechanism of your application. There are two types of Messages that can be recorded in Event Journal: `Events` and `Commands`. Events are deterministically & sequentially ordered objects that are serialized and stored in a Stream index (provided by an application). While Commands are deterministically & sequentially ordered objects that are serialized and stored in that same Stream Index. Commands are used to trigger side effects in your application, while Events are used to record changes in your application's state. Recording Commands is optional.

### Recording
Any part of an application can produce a command, but typically Events are produced by the application's Domain layer, in what's known as an `Aggregate`. A typical flow might look something like this:
- A user interacts with the application's UI, triggering a Command.
- The Command is sent to the application's Domain layer, where it is validated by an Aggregate.
- If the Command is valid, the Aggregate emits an Event.

It's not always a User that triggers a command, it could be a SideEffect of an Event, a scheduled task, or any other part of the application.

### Commands
Commands are immutable representations of an intent to change the state of your application. They are the building blocks of Event Journal. Commands are (optionally) serialized and stored in the Event Journal database. They can be replayed at any time to recreate the state of your application at any point in time.

```java
Cart cart = ej.playback("cartId", Cart.class);

Header header = Header.headOfChain(cart, AddItemToCart.class);

AddItemToCart command = new AddItemToCart(header, "itemId", "cartId");

ej.record(command); // optional
// the cart should throw an exception if the command is invalid, causing the application to halt this flow and return an error to the user.
ItemAddedToCart event = cart.handle(command); 

ej.record(event);
```

## Events
Events are immutable representations of something that happened in your application. They are the building blocks of Event Journal. Events are serialized and stored in the Event Journal database. They can be replayed at any time to recreate the state of your application at any point in time.
> ⚠️ **Warning**: Once an event is recorded, the schema of that originally recorded event will live forever, the properties of an event should only be changed after careful consideration. You can follow the Weak Schema principle here by adding new properties to an event, but removing or renaming properties could cause serialization issues. You can use the @JsonAlias annotation if a property is renamed for backwards compatability. 

> **Tip** When creating events, it is a best practice to use primitive types for fields. Custom types embedded in the event are likely to change over time and in doing so can cause a maintainer to inadvertently change the schema of an event. If you must use a custom type, consider placing that type as a subclass of the event, this way the schema of this type is clearly tied to the schema of the event and a maintainer would be able to easily see that by changing the schema of the type they are also changing the schema of the event.

An application can record an event at any time, like this:
```java

Header header = Header.headOfChain(cart, ItemAddedToCart.class);

ItemAddedToCart event = new ItemAddedToCart(header, "itemId", "cartId");

ej.record(event);

```

### Recording an Event
First - create an event:
```java
/**
 * All events should extend Message.Event, an Abstract class that implements the Message interface.
 */
public class ItemAddedToCart extends Message.Event {

    /*
     * This is the event class. It should extend Message.Event and have a constructor that takes a Header object.
     * You can add any fields you want to the event class, but they should be serializable. As a best practice, stick with primitive types. DTOs and Models are likely to change over time.
     * In this example, we have two fields, itemId and cartId, which are both String.
     */

    String itemId;
    String cartId;

    /**
     * This is the constructor for the event. It should take a Header as an argument and call the super constructor with that header.
     * @param header The header object for the event
     * @param itemId
     * @param cartId
     */
    public ItemAddedToCart(Header header, String itemId, String cartId) {
        super(header);
        this.itemId = itemId;
        this.cartId = cartId;
    }

    /**
     * This is the default constructor. It is required for deserialization.
     * As a best practice, keep it private so that it is not accidentally called in code since a Header is required to record an event.
     */
    private ItemAddedToCart() {

    }
    
}

```

### Reactors
Reactors are classes that listen for events and publish and Outcome in response to those events. They are used primarily as asynchronous handlers of messages. They are a good place to put side effects that are triggered by events. They are also a good place to put code that is non-deterministic, such as code that interacts with the file system or network, as they are configurable to have a retry mechanism.

```java
# Getting Started
Thanks for trying out Event Journal. This guide will help you get started.


## Java

### EventJournal SDK
Bring in the Client dependency using your preferred dependency management tool.

```xml
<!--maven-->
<dependency>
    <groupId>com.eventjournal</groupId>
    <artifactId>client</artifactId>
    <version>0.11.5</version>
</dependency>
```

```groovy
// gradle:
implementation("com.eventjournal:client:0.11.5")
```

### The Basics
The `EventJournal` class is the primary interface for interacting with Event Journal. It is used to record and playback events.

Instantiate the `EventJournal` class with your public and private keys. If you don't already have them, get them [here](https://event-journal.com). Each Application can have multiple Environments, and each Environment can have multiple API Keys. Setup Environments and API Keys in the [Event Journal Console](https://event-journal.com/dashboard).

```java
EventJournal ej = new EventJournal("publicKey","privateKey");
```

### Recording Events
Record Events to Event Journal using the `record` method.

```java
MyEvent myEvent = new MyEvent(Header.headOfChain(StreamId.of(MyAggregate.class,"10000"), MyEvent.class), "1234");
ej.record(myEvent);
```

The result will be a new event in the Event Journal. The next time you playback the stream, the new event will be included.
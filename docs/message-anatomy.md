# Anatomy of a Message
In EventJournal, Events are Messages and Commands are Messages. An Event is a representation of a state mutation, while a command is typically a request to perform a state mutation.

An event is a representation of a change in state. It is a record of something that has happened in the past. Events are immutable and are stored in the event journal. An event is composed of the following parts:

## Header
The Message Header is a container of meta data about the embedded message.
> All Messages (events and commands) must have a Header.
### Stream ID
The Stream ID identifies the stream that the message belongs to. Streams are consistent internally. Typically, a Stream ID is represented as a combination of an Aggregate ID and an Aggregate Type. The Stream ID is used to group events that are related to the same aggregate.
ie: Customer/1234 or Order/5678.
### Message Type
The message type is a string that identifies the type (schema) of message, and helps Typed languages map the payload to an object in memory. It can also be used to route the message to asynchronous handlers that might be downstream consumers.
### Message Category
an enum that identifies the category of the message. Currently supported Categories: `COMMAND`, `EVENT`
### Timestamp
The timestamp is a date and time that the event occurred. The timestamp is used to order events in the event journal. By default, the Timestamp is serialized to `Milliseconds.Nanoseconds` since the epoch.
### Sequence
The sequence number is a long value that is unique to the stream, used to order the events in the stream, and closely tied to the Aggregate version. In fact, the version of the aggregate is used to derive the sequence of an event or command produced by the aggregate.
### Message ID
The event ID is a unique identifier for the event. The event ID is used to uniquely identify an event in the event journal. The event ID is used to prevent duplicate events from being recorded in the event journal.
The Message ID of a Message that's at the Head of a Chain will also be the Correlation ID.
### Correlation ID
The correlation ID is an identifier that is shared among a set of Messages to relate them to each other. The correlation ID is used to group events that are related to the same business transaction.
### Causation ID
The causation ID is a pointer to a Message ID. The causation ID is used to group events that are related to the same business transaction.
### Producer
The producer is a container of meta data about the producer of the message. Provided fields are: `name` and `source`.
### Custom Header
The custom header is a container of custom meta data about the message. The custom header is used to store additional meta data about the message that a custom application might need to store about events. For instance, a use case might be to store the IP address of the client that produced the event, or the user ID that produced the event.

## Header Examples
Event Example:
```json lines
  "header": {
    "streamId": "Customer/123456",
    "messageType": "CustomerRegistered",
    "category": "EVENT",
    "timestamp": 1712310309.974000000,
    "sequence": 1,
    "causationId": "35a01280-9e2d-44d1-91dd-fa0373fddfbd",
    "messageId": "35a01280-9e2d-44d1-91dd-fa0373fddfbd",
    "correlationId": "35a01280-9e2d-44d1-91dd-fa0373fddfbd",
    "producer": {
      "name": "anonymous",
      "source": "http://localhost"
    },
    "customHeader": {}
  },
  "data": {}
```
Command Example:
```json lines
  "header": {
    "streamId": "User/a3cdb66a2e874c06809056115667fa76",
    "messageType": "SendEmailConfirmation",
    "category": "COMMAND",
    "timestamp": 1712309412.001000000,
    "sequence": 1,
    "causationId": "10514d64-4763-4ede-885a-651d1e4d5275",
    "messageId": "4660a369-2552-4f61-af22-abae215afbcf",
    "correlationId": "10514d64-4763-4ede-885a-651d1e4d5275",
    "producer": {
      "name": "anonymous",
      "source": "http://localhost"
    },
    "customHeader": {}
  }
```

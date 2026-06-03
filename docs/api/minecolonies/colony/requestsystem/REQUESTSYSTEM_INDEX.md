# colony/requestsystem Package Index

`com.minecolonies.api.colony.requestsystem` — the MineColonies demand/supply coordination system.

## Subdirectories

- [[data/]] — `IDataStoreManager` and data store interfaces
- [[factory/]] — `IFactoryController`, serialization factories for request objects
- [[location/]] — `ILocation` interface for request positions
- [[management/]] — internal management helpers
- [[manager/]] — `IRequestManager` — the colony-level request coordinator
- [[request/]] — `IRequest`, `RequestState` enum
- [[requestable/]] — `IRequestable` subtypes: `Stack`, `Tool`, `IDeliverable`, `RequestTag`
- [[requester/]] — `IRequester` interface
- [[resolver/]] — `IRequestResolver`, player and retrying resolver interfaces
- [[token/]] — `IToken` opaque request identifier

## Files

### StandardFactoryController.java
**Summary:** Singleton factory controller for serializing and deserializing request system objects.
**Source:** [[docs/api/minecolonies/colony/requestsystem/StandardFactoryController.java]]

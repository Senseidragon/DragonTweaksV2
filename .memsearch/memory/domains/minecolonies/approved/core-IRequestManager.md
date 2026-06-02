---
title: MineColonies — IRequestManager interface (colony request and delivery system)
domain: minecolonies
fact: IRequestManager is the colony demand/supply coordination system. Citizens and buildings submit IRequest objects for items, tools, or services; resolvers fulfil them by routing deliveries from warehouses or triggering crafting. Mod code places requests indirectly through IBuilding.createRequest or ICitizenData methods. Key methods: createRequest (returns IToken), createAndAssignRequest, assignRequest, reassignRequest, getRequestForToken, updateRequestState, overruleRequest, getResolverForToken, onProviderAddedToColony, onProviderRemovedFromColony, getPlayerResolver, getRetryingRequestResolver, getDataStoreManager, getFactoryController.
confidence: 0.95
usefulness: high
supersedes: core-IRequestManager.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: 6f60b28a73d0d923460b55e00d8e417d64c389dbc8fdb368a1544f42c13f2a8d
validated_at: 2026-05-31T23:43:31.911808+00:00
approval_route: auto
---

`IRequestManager` is the colony's demand/supply coordination system. Citizens and buildings submit `IRequest` objects for items, tools, or services; resolvers fulfil them by routing deliveries from warehouses or triggering crafting. Mod code that needs to place a request on behalf of a citizen or building uses this interface indirectly through `IBuilding.createRequest`.

**Key API surfaces:**
- `createRequest(IRequester, IRequestable)` — create a new request token; returns `IToken`
- `createAndAssignRequest(IRequester, IRequestable)` — create and immediately try to assign
- `assignRequest(IToken)` — attempt to assign an existing unresolved request to a resolver
- `reassignRequest(IToken, Collection<IToken>)` — re-route a request, blacklisting specific resolvers
- `getRequestForToken(IToken)` — retrieve the live `IRequest` by its token
- `updateRequestState(IToken, RequestState)` — move a request through its lifecycle
- `overruleRequest(IToken, ItemStack)` — manually complete a request with a specific stack
- `getResolverForToken(IToken)` — find which resolver owns a token
- `onProviderAddedToColony(IRequestResolverProvider)` — register a building's resolvers
- `onProviderRemovedFromColony(IRequestResolverProvider)` — unregister a building's resolvers
- `getPlayerResolver()` — the `IPlayerRequestResolver` that routes unresolvable requests to players
- `getRetryingRequestResolver()` — resolver that retries failed requests on a schedule
- `getDataStoreManager()` — internal data storage for the request system
- `getFactoryController()` — serialization/deserialization factory for request objects

**Useful for:** monitoring request state from event handlers, manually overruling stuck requests, diagnosing delivery failures, implementing custom request resolvers.

**Does not prove:** how to create requests from citizen AI (use `AbstractEntityAIBasic` utility methods); how resolvers are prioritised; crafting resolver behaviour.

**Source:** [[docs/api/minecolonies/colony/requestsystem/manager/IRequestManager.java]]

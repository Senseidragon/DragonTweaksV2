# colony Package Index

`com.minecolonies.api.colony` — core colony interfaces.

## Subdirectories

- [[buildingextensions/]] — building extension registry and plantation modules
- [[buildings/]] — `IBuilding`, building modules, registry, settings, worker buildings
- [[claim/]] — chunk claim data
- [[colonyEvents/]] — colony event types and descriptions
- [[connections/]] — inter-colony connections and diplomacy
- [[guardtype/]] — guard type (knight, ranger, druid) registry
- [[interactionhandling/]] — citizen interaction and chat priority
- [[jobs/]] — `IJob`, job views, `ModJobs`, `JobEntry` registry
- [[managers/]] — citizen, grave, visitor, raider, reproduction, event, stats managers
- [[modules/]] — colony-level module interfaces
- [[permissions/]] — `IPermissions`, rank system
- [[requestsystem/]] — full request/delivery system
- [[savedata/]] — colony save data helpers
- [[workorders/]] — `IWorkOrder`, work manager, builder orders

## Files

### CitizenNameFile.java
**Summary:** Data class describing the citizen name file used by a colony for name generation.
**Source:** [[docs/api/minecolonies/colony/CitizenNameFile.java]]

### ColonyProgressType.java
**Summary:** Enum of colony progression milestones.
**Source:** [[docs/api/minecolonies/colony/ColonyProgressType.java]]

### ColonyState.java
**Summary:** Enum of colony active/inactive/loading states; returned by `IColony.getState()`.
**Source:** [[docs/api/minecolonies/colony/ColonyState.java]]

### CompactColonyReference.java
**Summary:** Lightweight serializable reference to a colony by ID and dimension.
**Source:** [[docs/api/minecolonies/colony/CompactColonyReference.java]]

### GraveData.java
**Summary:** Data holder for a citizen grave (position, name, equipment).
**Source:** [[docs/api/minecolonies/colony/GraveData.java]]

### IAnimalData.java
**Summary:** Interface for colony-tracked animal data.
**Source:** [[docs/api/minecolonies/colony/IAnimalData.java]]

### ICitizen.java
**Summary:** Minimal citizen identity interface (ID, name).
**Source:** [[docs/api/minecolonies/colony/ICitizen.java]]

### ICitizenData.java
**Summary:** Full server-side citizen data: job, buildings, handlers, family, sleep, saturation.
**Source:** [[docs/api/minecolonies/colony/ICitizenData.java]]

### ICitizenDataManager.java
**Summary:** Manager for creating and deserialising citizen data objects.
**Source:** [[docs/api/minecolonies/colony/ICitizenDataManager.java]]

### ICitizenDataView.java
**Summary:** Client-side read-only view of citizen data.
**Source:** [[docs/api/minecolonies/colony/ICitizenDataView.java]]

### ICivilianData.java
**Summary:** Common interface shared by `ICitizenData` and visitor data.
**Source:** [[docs/api/minecolonies/colony/ICivilianData.java]]

### IColony.java
**Summary:** Central colony interface: identity, all managers, world lifecycle, NBT persistence.
**Source:** [[docs/api/minecolonies/colony/IColony.java]]

### IColonyManager.java
**Summary:** Singleton for colony lookup, creation, and deletion; access via `getInstance()`.
**Source:** [[docs/api/minecolonies/colony/IColonyManager.java]]

### IColonyRelated.java
**Summary:** Marker interface for objects that have a colony reference.
**Source:** [[docs/api/minecolonies/colony/IColonyRelated.java]]

### IColonyView.java
**Summary:** Client-side colony proxy returned by `IColonyManager` on the client.
**Source:** [[docs/api/minecolonies/colony/IColonyView.java]]

### IGraveData.java
**Summary:** Interface for grave data access.
**Source:** [[docs/api/minecolonies/colony/IGraveData.java]]

### IVisitorData.java
**Summary:** Server-side data for a visiting (non-colonist) citizen.
**Source:** [[docs/api/minecolonies/colony/IVisitorData.java]]

### IVisitorViewData.java
**Summary:** Client-side view of visitor data.
**Source:** [[docs/api/minecolonies/colony/IVisitorViewData.java]]

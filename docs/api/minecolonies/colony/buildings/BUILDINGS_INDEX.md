# colony/buildings Package Index

`com.minecolonies.api.colony.buildings` — building interfaces, modules, registry, worker buildings.

## Subdirectories

- [[modules/]] — building module interfaces (settings, crafting, assignment, ticking, persistence)
- [[registry/]] — `BuildingEntry`, `IBuildingDataManager`, `IBuildingRegistry`
- [[views/]] — client-side `IBuildingView`, `IModuleContainerView`
- [[workerbuildings/]] — specific building interfaces: `ITownHall`, `IWareHouse`, `IBuildingDeliveryman`

## Files

### HiringMode.java
**Summary:** Enum for citizen hiring mode (automatic vs. manual assignment).
**Source:** [[docs/api/minecolonies/colony/buildings/HiringMode.java]]

### IBuilding.java
**Summary:** Core building interface: level, construction state, modules, requests, citizen assignment.
**Source:** [[docs/api/minecolonies/colony/buildings/IBuilding.java]]

### IBuildingContainer.java
**Summary:** Interface for buildings that hold inventory or item storage.
**Source:** [[docs/api/minecolonies/colony/buildings/IBuildingContainer.java]]

### IBuildingWorker.java
**Summary:** Extended building interface for worker buildings that assign citizens to jobs.
**Source:** [[docs/api/minecolonies/colony/buildings/IBuildingWorker.java]]

### IBuildingWorkerModule.java
**Summary:** Module interface for per-worker-type logic within a building.
**Source:** [[docs/api/minecolonies/colony/buildings/IBuildingWorkerModule.java]]

### IBuildingWorkerView.java
**Summary:** Client-side view for worker building data.
**Source:** [[docs/api/minecolonies/colony/buildings/IBuildingWorkerView.java]]

### ICommonBuilding.java
**Summary:** Shared building interface subset present on both server and client views.
**Source:** [[docs/api/minecolonies/colony/buildings/ICommonBuilding.java]]

### IGuardBuilding.java
**Summary:** Interface for guard-type buildings (tower, barracks).
**Source:** [[docs/api/minecolonies/colony/buildings/IGuardBuilding.java]]

### IMysticalSite.java
**Summary:** Interface for the mystical site building.
**Source:** [[docs/api/minecolonies/colony/buildings/IMysticalSite.java]]

### IRSComponent.java
**Summary:** Marker interface for buildings that participate in the request system as providers.
**Source:** [[docs/api/minecolonies/colony/buildings/IRSComponent.java]]

### ISchematicProvider.java
**Summary:** Interface for objects that can provide a structure schematic reference.
**Source:** [[docs/api/minecolonies/colony/buildings/ISchematicProvider.java]]

### ModBuildings.java
**Summary:** Registry of all built-in MineColonies building entries as static `DeferredHolder` fields.
**Source:** [[docs/api/minecolonies/colony/buildings/ModBuildings.java]]

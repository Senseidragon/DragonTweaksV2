# colony/workorders Package Index

`com.minecolonies.api.colony.workorders` — building construction and repair work orders.

## Files

### IBuilderWorkOrder.java
**Summary:** Extended work order interface specific to builder hut construction tasks.
**Source:** [[docs/api/minecolonies/colony/workorders/IBuilderWorkOrder.java]]

### IServerWorkOrder.java
**Summary:** Server-only work order data not present in the client view.
**Source:** [[docs/api/minecolonies/colony/workorders/IServerWorkOrder.java]]

### IWorkManager.java
**Summary:** Manager that holds and distributes work orders to builders; obtained via `IColony.getWorkManager()`.
**Source:** [[docs/api/minecolonies/colony/workorders/IWorkManager.java]]

### IWorkOrder.java
**Summary:** Single construction/repair/upgrade task: level, schematic path, claim state, bounding box.
**Source:** [[docs/api/minecolonies/colony/workorders/IWorkOrder.java]]

### IWorkOrderView.java
**Summary:** Client-side read-only work order view for GUI display.
**Source:** [[docs/api/minecolonies/colony/workorders/IWorkOrderView.java]]

### WorkOrderType.java
**Summary:** Enum: BUILD, UPGRADE, REPAIR, REMOVE — the type of construction work.
**Source:** [[docs/api/minecolonies/colony/workorders/WorkOrderType.java]]

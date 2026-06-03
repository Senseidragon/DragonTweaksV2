# colony/jobs Package Index

`com.minecolonies.api.colony.jobs` — citizen job interfaces and registry.

## Subdirectories

- [[registry/]] — `JobEntry`, `IJobRegistry`, `IJobDataManager`, `ModJobs`

## Files

### IJob.java
**Summary:** Server-side job contract: `generateAI()`, action counter, lifecycle hooks, assignment.
**Source:** [[docs/api/minecolonies/colony/jobs/IJob.java]]

### IJobView.java
**Summary:** Client-side job view interface for display and GUI purposes.
**Source:** [[docs/api/minecolonies/colony/jobs/IJobView.java]]

### IJobWithColonyFlag.java
**Summary:** Marker interface for jobs that use the colony flag in their UI.
**Source:** [[docs/api/minecolonies/colony/jobs/IJobWithColonyFlag.java]]

### IJobWithExternalWorkStations.java
**Summary:** Interface for jobs that work at positions outside the home building.
**Source:** [[docs/api/minecolonies/colony/jobs/IJobWithExternalWorkStations.java]]

### ModJobs.java
**Summary:** Registry of all built-in MineColonies job entries as static `DeferredHolder` fields.
**Source:** [[docs/api/minecolonies/colony/jobs/ModJobs.java]]

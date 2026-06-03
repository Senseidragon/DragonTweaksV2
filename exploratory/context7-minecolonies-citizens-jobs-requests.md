# Context7 — MineColonies: Citizens, Jobs, Requests
# Query: citizen jobs AI worker AbstractEntityAIBasic AbstractJob request system crafting inventory saturation happiness

Source: https://context7.com/ldtteam/minecolonies/llms.txt
Fetched: 2026-06-02

---

## ICitizenData - Individual Citizen State

Holds the persistent data (job, home/work building, saturation, sleep, family tree, quests, custom texture, etc.) for a single citizen. Not the entity itself — get the live entity via `citizenData.getEntity()`.

### Methods
- `getName()`: Returns the citizen's name.
- `getId()`: Returns the citizen's unique ID.
- `getUniqueID()`: Returns the citizen's UUID.
- `getJob()`: Returns the citizen's current job.
- `getWorkBuilding()`: Returns the building where the citizen works.
- `getHomeBuilding()`: Returns the citizen's home building.
- `setJob(IJob<?> job)`: Assigns or unassigns a job to the citizen.
- `setHomeBuilding(IBuilding home)`: Assigns a home building to the citizen.
- `getSaturation()`: Returns the citizen's current saturation level.
- `setSaturation(double saturation)`: Sets the citizen's saturation level.
- `getJobStatus()`: Returns the citizen's current job status.
- `setJobStatus(JobStatus status)`: Sets the citizen's job status.
- `getStatus()`: Returns the citizen's visible status icon.
- `setVisibleStatus(VisibleCitizenStatus status)`: Sets the citizen's visible status.
- `isAsleep()`: Checks if the citizen is asleep.
- `setAsleep(boolean asleep)`: Sets the citizen's sleep state.
- `setBedPos(BlockPos pos)`: Sets the citizen's bed position.
- `getPartner()`: Returns the citizen's partner.
- `getChildren()`: Returns a list of the citizen's children's IDs.
- `isRelatedTo(ICitizenData other)`: Checks if the citizen is related to another citizen.
- `doesLiveWith(ICitizenData other)`: Checks if the citizen lives with another citizen.
- `scheduleRestart(ServerPlayer player)`: Forces a restart of the citizen's AI.
- `hasQuestAssignment()`: Checks if the citizen has an active quest.
- `setCustomTexture(UUID uuid)`: Sets a custom texture for the citizen.

```java
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.entity.ai.JobStatus;
import com.minecolonies.api.entity.citizen.VisibleCitizenStatus;

ICitizenData cit = colony.getCitizen(2);

// ── Identity / name ───────────────────────────────────────────────────────────
String name  = cit.getName();
int    id    = cit.getId();
UUID   uuid  = cit.getUniqueID();

// ── Job & buildings ───────────────────────────────────────────────────────────
IJob<?>  job   = cit.getJob();
IBuilding work = cit.getWorkBuilding();
IBuilding home = cit.getHomeBuilding();
cit.setJob(null);                                         // fire the citizen
cit.setHomeBuilding(colony.getServerBuildingManager()
        .getBuilding(new BlockPos(108, 64, 210)));

// ── Status / saturation ───────────────────────────────────────────────────────
double sat   = cit.getSaturation();   // 0–20
cit.setSaturation(ICitizenData.MAX_SATURATION);

JobStatus status = cit.getJobStatus();            // IDLE, WORKING, ...
cit.setJobStatus(JobStatus.IDLE);

VisibleCitizenStatus vis = cit.getStatus();
cit.setVisibleStatus(VisibleCitizenStatus.SLEEP);

// ── Sleep state ───────────────────────────────────────────────────────────────
boolean asleep = cit.isAsleep();
cit.setAsleep(true);
cit.setBedPos(new BlockPos(108, 64, 211));

// ── Family relations ──────────────────────────────────────────────────────────
ICitizenData partner = cit.getPartner();
List<Integer> children = cit.getChildren();
boolean related = cit.isRelatedTo(partner);
boolean together = cit.doesLiveWith(partner);

// ── Force a restart of the citizen AI ────────────────────────────────────────
cit.scheduleRestart(serverPlayer);

// ── Quest status ──────────────────────────────────────────────────────────────
boolean hasQuest = cit.hasQuestAssignment();

// ── Custom skin ──────────────────────────────────────────────────────────────
cit.setCustomTexture(UUID.fromString("a1b2c3d4-..."));
```

---

## ICitizenManager - Citizen Lifecycle

Sub-manager of `IColony` responsible for spawning, tracking, and removing citizen NPCs. Access via `colony.getCitizenManager()`.

### Methods
- **getCurrentCitizenCount()**: Returns the number of living citizens currently in the colony.
- **getMaxCitizens()**: Returns the maximum number of citizens the colony can support (based on beds).
- **getPotentialMaxCitizens()**: Returns the potential maximum including guard-tower beds.
- **calculateMaxCitizens()**: Forces a recalculation of the maximum citizen count after a building change.
- **getCitizens()**: Returns a list of `ICitizenData` for all citizens in the colony.
- **spawnOrCreateCitizen()**: Spawns a new citizen or creates data for one if needed.
- **spawnOrCreateCitizen(Object data, World world, BlockPos pos)**: Spawns a citizen at a specific location.
- **resurrectCivilianData(CompoundTag savedNBT, boolean resetId, World world, BlockPos pos)**: Resurrects a dead citizen from saved NBT.
- **checkCitizensForHappiness()**: Updates citizen happiness levels.
- **updateCitizenMourn(ICitizenData citizen, boolean mourn)**: Updates mourning status for a citizen.
- **getCivilian(int id)**: Retrieves a specific citizen by integer ID.
- **getJoblessCitizen()**: Returns the first available jobless citizen, or null.
- **getRandomCitizen()**: Returns a random citizen from the colony.

```java
import com.minecolonies.api.colony.managers.interfaces.ICitizenManager;

ICitizenManager cm = colony.getCitizenManager();

int current   = cm.getCurrentCitizenCount();
int max       = cm.getMaxCitizens();
int potential = cm.getPotentialMaxCitizens();
cm.calculateMaxCitizens();

List<ICitizenData> citizens = cm.getCitizens();
for (ICitizenData cit : citizens) {
    System.out.printf("  [%d] %s  job=%s  saturation=%.1f%n",
        cit.getId(), cit.getName(),
        cit.getJob() != null ? cit.getJob().getJobRegistryEntry().getKey() : "none",
        cit.getSaturation());
}

cm.spawnOrCreateCitizen();
cm.spawnOrCreateCitizen(null, world, new BlockPos(103, 65, 203));

CompoundTag savedNBT = ...;
ICitizenData resurrected = cm.resurrectCivilianData(savedNBT,
    /*resetId=*/false, world, new BlockPos(100, 64, 200));

cm.checkCitizensForHappiness();
cm.updateCitizenMourn(resurrected, false);

ICitizenData cit     = cm.getCivilian(3);
ICitizenData jobless = cm.getJoblessCitizen();
ICitizenData random  = cm.getRandomCitizen();
```

---

## IRequestManager - Item / Resource Request System

Manages the centralized request-and-resolve pipeline for colony resources. Citizens declare needs, and resolvers fulfill them using `IToken<?>` handles.

```java
IRequestManager rm = colony.getRequestManager();
IRequester building = colony.getRequesterBuildingForPosition(new BlockPos(120, 64, 220));
IToken<?> token = rm.createAndAssignRequest(building,
        new Stack(new ItemStack(Items.IRON_INGOT, 32)));

// ── Inspect the request ───────────────────────────────────────────────────────
IRequest<?> req = rm.getRequestForToken(token);
if (req != null) {
    RequestState state = req.getState();   // QUEUED | ASSIGNED | IN_PROGRESS | RESOLVED
    System.out.println("State: " + state);
}

// ── Resolve state transitions ─────────────────────────────────────────────────
rm.updateRequestState(token, RequestState.RESOLVED);

// ── Overrule: manually provide the item (admin shortcut) ─────────────────────
rm.overruleRequest(token, new ItemStack(Items.IRON_INGOT, 32));

// ── Reassign after a resolver became unavailable ─────────────────────────────
Collection<IToken<?>> blacklist = List.of(token);
IToken<?> newResolverToken = rm.reassignRequest(token, blacklist);

// ── Notify after item availability changed ────────────────────────────────────
rm.onColonyUpdate(request -> request.getState() == RequestState.QUEUED);

// ── Persistence ───────────────────────────────────────────────────────────────
CompoundTag nbt = rm.serializeNBT();
rm.deserializeNBT(nbt);
```

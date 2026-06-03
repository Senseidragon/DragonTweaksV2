# Context7 — MineColonies: Buildings, Structure Manager, Raids (detailed)
# Query: raiders guards combat events NeoForge mod registration buildings huts colony events API hooks

Source: https://context7.com/ldtteam/minecolonies/llms.txt
Fetched: 2026-06-02

---

## IBuilding - Colony Building

Represents any player-built colony structure (worker hut, Town Hall, Warehouse, Barracks, etc.). Obtained from `colony.getServerBuildingManager().getBuildings()` or via `IColonyManager.getInstance().getBuilding(world, pos)`.

### Methods
- `getBuildingDisplayName()`: Returns the display name of the building.
- `getCustomName()`: Returns the custom name set for the building.
- `getBuildingLevel()`: Returns the current level of the building.
- `getMaxBuildingLevel()`: Returns the maximum possible level for the building.
- `isBuilt()`: Checks if the building has been fully constructed.
- `isPendingConstruction()`: Checks if the building is currently under construction.
- `setCustomBuildingName(String name)`: Sets a custom name for the building.
- `requestUpgrade(ServerPlayer player, BlockPos builderPos)`: Requests an upgrade for the building.
- `requestRepair(BlockPos repairPos)`: Requests a repair for the building.
- `requestRemoval(ServerPlayer player, BlockPos removalPos)`: Requests the removal of the building.
- `canAssignCitizens()`: Checks if citizens can be assigned to this building.
- `getAllAssignedCitizen()`: Returns a set of all citizens assigned to this building.
- `getMaxEquipmentLevel()`: Returns the maximum equipment level the building allows for its workers.
- `forceTransferStack(ItemStack stack, World world)`: Attempts to transfer an item stack into the building's inventory.
- `createRequest(ICitizenData citizen, Stack stack, boolean async)`: Creates a supply request for a citizen.
- `isInBuilding(BlockPos pos)`: Checks if a given position is within the building's bounding box.

```java
IBuilding building = colony.getServerBuildingManager().getBuilding(new BlockPos(120, 64, 220));

String displayName = building.getBuildingDisplayName();
String customName  = building.getCustomName();
int    level       = building.getBuildingLevel();
int    maxLevel    = building.getMaxBuildingLevel();
boolean built      = building.isBuilt();
boolean pending    = building.isPendingConstruction();

building.setCustomBuildingName("Deep Shaft Alpha");

building.requestUpgrade(player, new BlockPos(130, 64, 130));
building.requestRepair(new BlockPos(130, 64, 130));
building.requestRemoval(player, new BlockPos(130, 64, 130));

boolean canAssign = building.canAssignCitizens();
Set<ICitizenData> assigned = building.getAllAssignedCitizen();

int maxEquip = building.getMaxEquipmentLevel(); // 0=wood … 4=diamond/netherite

ItemStack leftover = building.forceTransferStack(new ItemStack(Items.OAK_LOG, 64), world);

ICitizenData miner = colony.getCitizen(5);
IToken<?> token = building.createRequest(miner,
        new Stack(new ItemStack(Items.IRON_PICKAXE)), /*async=*/false);

boolean inside = building.isInBuilding(new BlockPos(121, 65, 221));
```

---

## Manage Colony Buildings with IRegisteredStructureManager

Obtain the building manager via `colony.getServerBuildingManager()`. Use it to look up buildings, manage warehouses, set leisure sites, query guard proximity, check colony prestige, manage chunk loading, and validate new building placements.

```java
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.workerbuildings.IWareHouse;
import com.minecolonies.api.colony.managers.interfaces.IRegisteredStructureManager;

IRegisteredStructureManager bm = colony.getServerBuildingManager();

// ── Lookup ────────────────────────────────────────────────────────────────────
Map<BlockPos, IBuilding> all = bm.getBuildings();
IBuilding at = bm.getBuilding(new BlockPos(120, 64, 220));
ITownHall th = bm.getTownHall();                           // null before TH placed

// ── Warehouses ────────────────────────────────────────────────────────────────
List<IWareHouse> warehouses = bm.getWareHouses();
IWareHouse closest = bm.getClosestWarehouseInColony(new BlockPos(150, 64, 150));

// ── Leisure sites (citizens relax here) ──────────────────────────────────────
bm.addLeisureSite(new BlockPos(112, 64, 212));
List<BlockPos> leisure = bm.getLeisureSites();
BlockPos randomLeisure = bm.getRandomLeisureSite();
bm.removeLeisureSite(new BlockPos(112, 64, 212));

// ── Guard proximity query ─────────────────────────────────────────────────────
boolean guardNearby = bm.hasGuardBuildingNear(at);
bm.guardBuildingChangedAt(at, 3);

// ── Prestige (cosmetic score) ─────────────────────────────────────────────────
int prestige = bm.getColonyPrestige();

// ── Chunk retention ───────────────────────────────────────────────────────────
LevelChunk chunk = world.getChunkAt(new BlockPos(120, 64, 220));
boolean keep = bm.keepChunkColonyLoaded(chunk);

// ── Validate new building placement ──────────────────────────────────────────
boolean canPlace = bm.canPlaceAt(ModBlocks.blockHutMiner.get(),
        new BlockPos(140, 64, 140), player);
```

---

## IRaiderManager — Raid Events (API Doc)

Governs barbarian / Egyptian / Pirate raid scheduling, difficulty, and spawn mechanics. Retrieved via `colony.getRaiderManager()`.

### Key Methods
- `isRaided()`: Is the colony currently being raided?
- `willRaidTonight()`: Is a raid scheduled for tonight?
- `getNightsSinceLastRaid()`: Nights since the last raid.
- `getColonyRaidLevel()`: Scales with colony size.
- `getRaidDifficultyModifier()`: Default 1.0.
- `calculateRaiderAmount(int raidLevel)`: Number of raiders for the given level.
- `setCanHaveRaiderEvents(boolean)`: Enable/disable raids.
- `setRaidNextNight(RaidSettings)`: Schedule a raid.
- `raiderEvent(RaidSettings)`: Force a specific raid immediately. Returns `RaidSpawnResult`.
- `setSpiesEnabled(boolean)`: Toggle spy mechanics.
- `areSpiesEnabled()`: Are spies currently active?
- `calculateSpawnLocation()`: Get the raid spawn point.
- `getLastSpawnPoints()`: List of recent spawn positions.
- `getRandomBuilding()`: A random building targeted by raiders.
- `getLostCitizen()`: Citizens lost in the last raid.
- `setNightsSinceLastRaid(int)`: Reset the counter.

**RaidSpawnResult values:** `SUCCESS | TOO_SMALL | CANNOT_RAID | NO_SPAWN_POINT | ERROR`

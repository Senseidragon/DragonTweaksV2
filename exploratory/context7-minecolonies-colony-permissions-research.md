# Context7 — MineColonies: Colony, Permissions, Research, Raids
# Query: IColony colony management town hall permissions research tree configuration colony events

Source: https://context7.com/ldtteam/minecolonies/llms.txt
Fetched: 2026-06-02

---

## IColony - Core Colony Object

Represents a single living colony. Obtained from `IColonyManager`. Exposes identity, geometry, sub-managers, and lifecycle hooks. Server-side code works with `IColony`; client-side code receives `IColonyView`.

### Methods
- **getID()**: Returns the unique integer ID of the colony.
- **getName()**: Returns the name of the colony.
- **setName(String name)**: Sets the name of the colony.
- **getCenter()**: Returns the `BlockPos` of the Town Hall, representing the colony's center.
- **isDay()**: Returns `true` if it is currently daytime in the colony's world.
- **getOverallHappiness()**: Returns the overall happiness level of the colony (0.0 - 10.0).
- **getDay()**: Returns the current colony day counter.
- **getLastContactInHours()**: Returns the last contact time in hours, used for auto-delete checks.
- **isCoordInColony(World world, BlockPos pos)**: Checks if a given coordinate is within the colony's boundaries.
- **getDistanceSquared(BlockPos pos)**: Calculates the squared distance from the colony's center to a given position.
- **setColonyColor(ChatFormatting color)**: Sets the visual color tint for the colony.
- **setStructurePack(String packName)**: Sets the building schematic pack to be used for the colony.
- **setTextureStyle(String styleName)**: Sets the citizen skin pack for the colony.
- **setNameStyle(String styleName)**: Sets the citizen name generator style for the colony.
- **getState()**: Returns the current `ColonyState` of the colony (ACTIVE, INACTIVE, UNLOADED).
- **isActive()**: Returns `true` if the colony is currently active.
- **isColonyUnderAttack()**: Returns `true` if the colony is currently under attack.
- **addWayPoint(BlockPos pos, BlockState state)**: Adds a waypoint for citizen navigation.
- **getWayPoints()**: Returns a map of all waypoints in the colony.
- **write(CompoundTag tag)**: Serializes the colony's data into a `CompoundTag`.
- **read(CompoundTag tag)**: Deserializes the colony's data from a `CompoundTag`.

```java
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.ColonyState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

IColony colony = IColonyManager.getInstance().getColonyByWorld(1, world);
if (colony == null) return;

// ── Basic identity ───────────────────────────────────────────────────────────
int    id       = colony.getID();
String name     = colony.getName();
colony.setName("Rising Tide");

BlockPos center = colony.getCenter();
boolean isDay   = colony.isDay();
double  happy   = colony.getOverallHappiness();  // 0.0 – 10.0
int     day     = colony.getDay();
int     lastContact = colony.getLastContactInHours();

// ── Geometry ─────────────────────────────────────────────────────────────────
boolean inside = colony.isCoordInColony(world, new BlockPos(110, 64, 210));
long    distSq = colony.getDistanceSquared(new BlockPos(200, 64, 300));

// ── Visual style ─────────────────────────────────────────────────────────────
colony.setColonyColor(ChatFormatting.GOLD);
colony.setStructurePack("medieval_oak");
colony.setTextureStyle("medieval");
colony.setNameStyle("nordic");

// ── Lifecycle / state ────────────────────────────────────────────────────────
ColonyState state   = colony.getState();       // ACTIVE | INACTIVE | UNLOADED
boolean     active  = colony.isActive();
boolean     attacked = colony.isColonyUnderAttack();

// ── Waypoints (navigation helpers for citizens) ──────────────────────────────
colony.addWayPoint(new BlockPos(105, 64, 205), Blocks.COBBLESTONE.defaultBlockState());
Map<BlockPos, BlockState> waypoints = colony.getWayPoints();

// ── Persist / load ────────────────────────────────────────────────────────────
CompoundTag tag = colony.write(new CompoundTag());
colony.read(tag);
```

---

## Manage Player Permissions with IPermissions

Access permissions via `colony.getPermissions()`. Check player permissions for actions, get player ranks, add/remove players, alter permissions for ranks, and create custom ranks.

```java
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.permissions.IPermissions;
import com.minecolonies.api.colony.permissions.Rank;

IPermissions perms = colony.getPermissions();

// ── Check permissions ────────────────────────────────────────────────────────
boolean canBreak  = perms.hasPermission(player, Action.BREAK_HUTS);
boolean canAccess = perms.hasPermission(player, Action.ACCESS_HUTS);

// ── Rank of a player ─────────────────────────────────────────────────────────
Rank rank  = perms.getRank(player);
Rank owner = perms.getRankOwner();
UUID ownerUUID = perms.getOwner();
String ownerName = perms.getOwnerName();

// ── Add/promote a player ─────────────────────────────────────────────────────
boolean added = perms.addPlayer(player.getGameProfile(), perms.getRankFriend());
perms.setPlayerRank(player.getUUID(), perms.getRankOfficer(), world);

// ── Remove a player ───────────────────────────────────────────────────────────
perms.removePlayer(player.getUUID());

// ── Toggle an action for a rank ──────────────────────────────────────────────
Rank actorRank = perms.getRankOfficer();
perms.alterPermission(actorRank, perms.getRankFriend(), Action.PLACE_BLOCKS, true);
perms.setPermission(perms.getRankNeutral(), Action.OPEN_CONTAINER, false);

// ── Create a custom rank ─────────────────────────────────────────────────────
perms.addRank("Foreman");
Map<Integer, Rank> allRanks = perms.getRanks();

// ── Membership test ───────────────────────────────────────────────────────────
boolean isMember = perms.isColonyMember(player);
Set<ColonyPlayer> officers = perms.getPlayersByRank(perms.getRankOfficer());
```

---

## Manage Colony Research with IResearchManager

Access and manage the colony's research tree, effects, and persistence.

```java
import com.minecolonies.api.research.IResearchManager;
import com.minecolonies.api.research.ILocalResearchTree;
import com.minecolonies.api.research.IResearchEffectManager;
import net.minecraft.resources.ResourceLocation;

IResearchManager rm   = colony.getResearchManager();
ILocalResearchTree tree = rm.getResearchTree();
IResearchEffectManager effects = rm.getResearchEffects();

// ── Check an effect multiplier granted by research ────────────────────────────
ResourceLocation speedEffect = new ResourceLocation("minecolonies", "effects/workerspeed");
double speedMult = effects.getEffectStrength(speedEffect); // default 1.0 if no research

// ── Trigger auto-start research (called on colony tick) ───────────────────────
rm.checkAutoStartResearch();

// ── Map a block to its research effect ID ────────────────────────────────────
ResourceLocation effectId = rm.getResearchEffectIdFrom(ModBlocks.blockHutSmeltery.get());
// → "minecolonies:effects/smeltery"

// ── Dirty / sync tracking ────────────────────────────────────────────────────
if (rm.isDirty()) {
    rm.sendPackets(closeSubscribers, newSubscribers);
    rm.clearDirty();
}

// ── NBT persistence ───────────────────────────────────────────────────────────
CompoundTag nbt = new CompoundTag();
rm.writeToNBT(nbt);
rm.readFromNBT(nbt);
```

---

## Manage Raid Events with IRaiderManager

Governs barbarian, Egyptian, and Pirate raid scheduling, difficulty, and spawn mechanics. Retrieved via `colony.getRaiderManager()`.

```java
import com.minecolonies.api.colony.managers.interfaces.IRaiderManager;
import com.minecolonies.api.colony.managers.interfaces.IRaiderManager.RaidSettings;
import com.minecolonies.api.colony.managers.interfaces.IRaiderManager.RaidSpawnResult;

IRaiderManager raider = colony.getRaiderManager();

// ── Query current state ───────────────────────────────────────────────────────
boolean isRaided       = raider.isRaided();
boolean raidTonight    = raider.willRaidTonight();
int     nightsSinceLast = raider.getNightsSinceLastRaid();
int     raidLevel       = raider.getColonyRaidLevel();
double  difficulty      = raider.getRaidDifficultyModifier(); // default 1.0
int     raiderCount     = raider.calculateRaiderAmount(raidLevel);

// ── Enable / schedule a raid ──────────────────────────────────────────────────
raider.setCanHaveRaiderEvents(true);
raider.setRaidNextNight(RaidSettings.defaultRaidSettings());

// ── Force a specific raid immediately (admin / test) ─────────────────────────
RaidSettings settings = new RaidSettings(
        /*forcedSpawn=*/true,
        /*raidType=*/"minecolonies:norsemen_raid",
        /*allowShips=*/false,
        /*raiderAmount=*/20,
        /*location=*/null);

RaidSpawnResult result = raider.raiderEvent(settings);
// result: SUCCESS | TOO_SMALL | CANNOT_RAID | NO_SPAWN_POINT | ERROR

// ── Spy mechanics ─────────────────────────────────────────────────────────────
raider.setSpiesEnabled(true);
boolean spyActive = raider.areSpiesEnabled();

// ── Get spawn telemetry ───────────────────────────────────────────────────────
BlockPos spawnPos = raider.calculateSpawnLocation();
List<BlockPos> lastSpawns = raider.getLastSpawnPoints();
BlockPos raiderTarget = raider.getRandomBuilding();

// ── Record aftermath ──────────────────────────────────────────────────────────
int lostCitizens = raider.getLostCitizen();
raider.setNightsSinceLastRaid(0);
```

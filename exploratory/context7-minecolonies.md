# Context7 — MineColonies (/ldtteam/minecolonies)

Source: https://context7.com/ldtteam/minecolonies/llms.txt
Fetched: 2026-06-02

---

## Overview

MineColonies is a Minecraft Forge mod that enables players to establish and manage autonomous NPC colonies. Players can construct various buildings like Town Halls and worker huts, and the mod automatically spawns, assigns, and directs a diverse range of citizen workers. These workers possess job-specific AI, navigate the world, manage resources, maintain their well-being, participate in research, and defend against raiders.

**Summary:** MineColonies adds a simulated NPC colony layer to Minecraft, where players establish colonies using a Town Hall and upgrade worker huts to unlock professions. Each profession manages its own AI, resources, and inventory autonomously. The player's role is strategic, focusing on building choices, permissions, research, and responding to events like raids. A quest and reputation system provides narrative progression, and a research tree offers long-term power growth.

---

## Manage Colony Building Data

Access and modify properties of colony buildings, including display names, levels, upgrade/repair requests, citizen assignments, and inventory transfers. Requires a valid BlockPos for the building.

```java
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.ICitizenData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

IBuilding building = colony.getServerBuildingManager()
        .getBuilding(new BlockPos(120, 64, 220));

// ── Metadata ──────────────────────────────────────────────────────────────────
String displayName = building.getBuildingDisplayName();  // "Miner's Hut"
String customName  = building.getCustomName();
int    level       = building.getBuildingLevel();        // 0–5
int    maxLevel    = building.getMaxBuildingLevel();
boolean built      = building.isBuilt();
boolean pending    = building.isPendingConstruction();

// ── Custom name ───────────────────────────────────────────────────────────────
building.setCustomBuildingName("Deep Shaft Alpha");

// ── Request an upgrade or repair ─────────────────────────────────────────────
building.requestUpgrade(player, /* builder pos */ new BlockPos(130, 64, 130));
building.requestRepair(new BlockPos(130, 64, 130));
building.requestRemoval(player, new BlockPos(130, 64, 130));

// ── Assign citizens ───────────────────────────────────────────────────────────
boolean canAssign = building.canAssignCitizens(); // level > 0 && isBuilt
Set<ICitizenData> assigned = building.getAllAssignedCitizen();

// ── Equipment level the building allows its worker ────────────────────────────
int maxEquip = building.getMaxEquipmentLevel(); // 0=wood … 4=diamond/netherite

// ── Inventory management ──────────────────────────────────────────────────────
ItemStack leftover = building.forceTransferStack(
        new ItemStack(Items.OAK_LOG, 64), world);  // null if fully inserted

// ── Create a supply request for a citizen ────────────────────────────────────
ICitizenData miner = colony.getCitizen(5);
IToken<?> token = building.createRequest(miner,
        new Stack(new ItemStack(Items.IRON_PICKAXE)), /*async=*/false);

// ── Check if a position is inside this building's bounding box ───────────────
boolean inside = building.isInBuilding(new BlockPos(121, 65, 221));
```

---

## ICitizenManager - Citizen Lifecycle

Sub-manager of `IColony` responsible for spawning, tracking, and removing citizen NPCs. Access via `colony.getCitizenManager()`.

### Methods
- **getCurrentCitizenCount()**: Returns the number of living citizens currently in the colony.
- **getMaxCitizens()**: Returns the maximum number of citizens the colony can support (based on beds).
- **getPotentialMaxCitizens()**: Returns the potential maximum number of citizens, including beds from guard towers.
- **calculateMaxCitizens()**: Forces a recalculation of the maximum citizen count after a building change.
- **getCitizens()**: Returns a list of `ICitizenData` for all citizens in the colony.
- **spawnOrCreateCitizen()**: Spawns a new citizen or creates data for one if needed.
- **spawnOrCreateCitizen(Object data, World world, BlockPos pos)**: Spawns a citizen at a specific location, optionally with provided data.
- **resurrectCivilianData(CompoundTag savedNBT, boolean resetId, World world, BlockPos pos)**: Resurrects a dead citizen from saved NBT data.
- **checkCitizensForHappiness()**: Updates citizen happiness levels.
- **updateCitizenMourn(ICitizenData citizen, boolean mourn)**: Updates the mourning status for a specific citizen.
- **getCivilian(int id)**: Retrieves a specific citizen by their integer ID.
- **getJoblessCitizen()**: Returns the first available jobless citizen, or null if none exist.
- **getRandomCitizen()**: Returns a random citizen from the colony.

```java
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.managers.interfaces.ICitizenManager;

ICitizenManager cm = colony.getCitizenManager();

// ── Population census ────────────────────────────────────────────────────────
int current = cm.getCurrentCitizenCount();   // living citizens right now
int max     = cm.getMaxCitizens();           // beds available
int potential = cm.getPotentialMaxCitizens(); // including guard-tower beds
cm.calculateMaxCitizens();                   // force recalculation after building change

// ── Iterate citizens ──────────────────────────────────────────────────────────
List<ICitizenData> citizens = cm.getCitizens();
for (ICitizenData cit : citizens) {
    System.out.printf("  [%d] %s  job=%s  saturation=%.1f%n",
        cit.getId(), cit.getName(),
        cit.getJob() != null ? cit.getJob().getJobRegistryEntry().getKey() : "none",
        cit.getSaturation());
}

// ── Spawn a brand-new citizen (e.g. after building a new house) ──────────────
cm.spawnOrCreateCitizen();  // game will create data + entity automatically

// ── Spawn at a specific location ─────────────────────────────────────────────
BlockPos spawnAt = new BlockPos(103, 65, 203);
cm.spawnOrCreateCitizen(null, world, spawnAt);   // null → generate new CitizenData

// ── Resurrect a dead citizen from saved NBT (Undertaker mechanic) ────────────
CompoundTag savedNBT = ...; // previously serialised citizen tag
ICitizenData resurrected = cm.resurrectCivilianData(savedNBT,
    /*resetId=*/false, world, new BlockPos(100, 64, 200));

// ── Happiness & mourn propagation ────────────────────────────────────────────
cm.checkCitizensForHappiness();
cm.updateCitizenMourn(resurrected, /*mourn=*/false);

// ── Retrieve a single citizen ────────────────────────────────────────────────
ICitizenData cit = cm.getCivilian(3);   // by integer id
ICitizenData jobless = cm.getJoblessCitizen(); // first unemployed citizen or null
ICitizenData random  = cm.getRandomCitizen();
```

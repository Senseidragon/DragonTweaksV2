package com.minecolonies.core.entity.ai.workers;

import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.entity.ai.statemachine.AIEventTarget;
import com.minecolonies.api.entity.ai.statemachine.AITarget;
import com.minecolonies.api.entity.ai.statemachine.states.AIBlockingEventType;
import com.minecolonies.api.entity.ai.statemachine.states.IAIState;
import com.minecolonies.api.equipment.registry.EquipmentTypeEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all custom worker AIs in MineColonies.
 * Extend this when implementing a worker AI for a custom job.
 *
 * Type parameters:
 *   J — the paired job class (extends AbstractJob)
 *   B — the paired building class (extends AbstractBuilding)
 *
 * Source: com.minecolonies.core.entity.ai.workers.AbstractEntityAIBasic
 *         (version/1.21, tag v1.21.1-1.1.1320-SNAPSHOT)
 * Full source: ~1933 lines — this is a method-signature reference stub.
 */
public abstract class AbstractEntityAIBasic<J extends AbstractJob<?, J>, B extends AbstractBuilding>
    extends AbstractAISkeleton<J>
{
    // -----------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------

    /** Default delay between AI state transitions (ticks). */
    protected static final int STANDARD_DELAY = 5;
    /** Delay between request-system checks (60 ticks = 3 seconds). */
    protected static final int REQUEST_DELAY  = TICKS_20 * 3;
    /** Sentinel value returned when no tool slot is available. */
    protected static final int NO_TOOL        = -10;

    // -----------------------------------------------------------------------
    // Key protected fields
    // -----------------------------------------------------------------------

    /** The block the AI is currently targeting for work. */
    protected BlockPos currentWorkingLocation;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    protected AbstractEntityAIBasic(final J job) { super(job); }

    // -----------------------------------------------------------------------
    // State machine registration (call in subclass constructor)
    // -----------------------------------------------------------------------

    /**
     * Register AI state targets. Call super.registerTargets(...) in subclass constructor.
     * Example:
     * <pre>
     *   super.registerTargets(
     *       new AITarget(IDLE, START_WORKING, 1),
     *       new AITarget(START_WORKING, this::startWorking, TICKS_20),
     *       new AIEventTarget(AIBlockingEventType.AI_BLOCKING, this::shouldDump, this::dump, TICKS_20)
     *   );
     * </pre>
     */
    protected void registerTargets(final Object... targets) { /* super implementation */ }

    // -----------------------------------------------------------------------
    // Utility methods available to subclasses
    // -----------------------------------------------------------------------

    /**
     * Pathfind to a position. Returns true while the citizen is still walking.
     * Call from a state method; stay in current state until this returns false.
     */
    protected boolean walkToBlock(@NotNull final BlockPos pos) { return false; }

    /**
     * Request that the citizen dumps its inventory at the assigned building.
     * Transitions the AI to INVENTORY_FULL if needed.
     */
    protected IAIState dumpInventory() { return null; }

    /**
     * Prevent specific essential items from being dumped when dumpInventory() runs.
     */
    protected void holdEssentialItems() {}

    /**
     * Returns the typed building associated with this AI (type B).
     * Shortcut for job.getWorkBuilding() cast to B.
     */
    protected B getOwnBuilding() { return null; }

    /**
     * Check if the worker has a tool/weapon of the required type and level in inventory.
     * Requests the tool from the request system if missing.
     *
     * @param toolType  the required equipment type
     * @param minLevel  minimum tool level
     * @param maxLevel  maximum tool level
     * @return true if the tool is already present
     */
    protected boolean checkForToolOrWeapon(@NotNull final EquipmentTypeEntry toolType, final int minLevel, final int maxLevel) { return false; }

    /**
     * Async variant of checkForToolOrWeapon — places the request without blocking.
     */
    protected void checkForToolOrWeaponAsync(@NotNull final EquipmentTypeEntry toolType, final int minLevel, final int maxLevel) {}

    /**
     * Request a stack of items from the request system.
     *
     * @param stack    the item to request
     * @param count    desired quantity
     * @param minCount minimum acceptable quantity
     * @param async    if true, request is non-blocking
     * @return true if the request was already satisfied from inventory
     */
    protected boolean checkIfRequestForItemExistOrCreateAsynch(@NotNull final ItemStack stack, final int count, final int minCount, final boolean async) { return false; }

    /**
     * Returns the exception/error timer value. Used to throttle repeated failures.
     */
    public int getExceptionTimer() { return 0; }

    /**
     * Whether the citizen can currently go idle (wander).
     * Override to return true when the worker has no queued tasks.
     */
    public boolean canGoIdle() { return false; }
}

package com.minecolonies.core.colony.jobs;

import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.buildings.modules.IAssignsJob;
import com.minecolonies.api.colony.jobs.IJob;
import com.minecolonies.api.colony.jobs.registry.IJobRegistry;
import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.api.colony.requestsystem.token.IToken;
import com.minecolonies.api.entity.ai.ITickingStateAI;
import com.minecolonies.api.entity.ai.statemachine.states.AIWorkerState;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static com.minecolonies.api.util.constant.NbtTagConstants.TAG_JOB_TYPE;

/**
 * Abstract base class for all MineColonies citizen jobs.
 * Extend this when implementing a custom job. The only abstract method
 * subclasses must implement is generateAI().
 *
 * Source: com.minecolonies.core.colony.jobs.AbstractJob (version/1.21, tag v1.21.1-1.1.1320-SNAPSHOT)
 */
public abstract class AbstractJob<AI extends AbstractAISkeleton<J> & ITickingStateAI, J extends AbstractJob<AI, J>> implements IJob<AI>
{
    private static final String TAG_ASYNC_REQUESTS = "asyncRequests";
    private static final String TAG_ACTIONS_DONE   = "actionsDone";
    private static final String TAG_WORK_POS       = "workPos";

    private JobEntry entry;
    private int actionsDone = 0;
    private final ICitizenData citizen;
    private String nameTag = "";
    private final Set<IToken<?>> asyncRequests = new HashSet<>();
    private boolean searchedForFoodToday;

    /** Position of the assigned work building. */
    protected BlockPos workBuildingPos = null;
    /** The assigned work building instance. */
    protected IBuilding workBuilding = null;
    /** The work module this job is assigned to. */
    protected IAssignsJob workModule = null;

    public AbstractJob(final ICitizenData entity) { this.citizen = entity; }

    /** Must be implemented by subclasses. Returns a new AI instance for this job. */
    @Override
    public abstract AI generateAI();

    @Override public boolean pickupSuccess(@NotNull ItemStack pickedUpStack) { return true; }
    @Override public ResourceLocation getModel() { return ModModelTypes.CITIZEN_ID; }
    @Override public IColony getColony() { return citizen.getColony(); }
    @Override public void setRegistryEntry(final JobEntry jobEntry) { this.entry = jobEntry; }
    @Override public BlockPos getBuildingPos() { return workBuildingPos; }
    @Override public IBuilding getWorkBuilding() { return workBuilding; }
    @Override public IAssignsJob getWorkModule() { return workModule; }
    @Override final public JobEntry getJobRegistryEntry() { return this.entry; }
    @Override public Set<IToken<?>> getAsyncRequests() { return asyncRequests; }
    @Override public boolean hasCheckedForFoodToday() { return searchedForFoodToday; }
    @Override public void setCheckedForFood() { searchedForFoodToday = true; }
    @Override public String getNameTagDescription() { return this.nameTag; }
    @Override public final void setNameTag(final String nameTag) { this.nameTag = nameTag; }
    @Override public void triggerDeathAchievement(final DamageSource source, final AbstractEntityCitizen citizen) {}
    @Override public ICitizenData getCitizen() { return citizen; }
    @Override public void onWakeUp() { searchedForFoodToday = false; }
    @Override public int getActionsDone() { return actionsDone; }
    @Override public void incrementActionsDone() { actionsDone++; }
    @Override public void incrementActionsDone(final int n) { actionsDone += n; }
    @Override public void clearActionsDone() { actionsDone = 0; }
    @Override public boolean allowsAvoidance() { return true; }
    @Override public double getDiseaseModifier() { return 1; }
    @Override public boolean ignoresDamage(@NotNull final DamageSource d) { return false; }
    @Override public void processOfflineTime(final long time) {}

    @Override
    public boolean assignTo(final IAssignsJob module)
    {
        if (module == null || !module.getJobEntry().equals(getJobRegistryEntry())) { return false; }
        workBuilding = module.getBuilding();
        workBuildingPos = workBuilding.getID();
        workModule = module;
        citizen.setJob(this);
        return true;
    }

    @Override
    public void createAI()
    {
        final AI tempAI = generateAI();
        if (tempAI != null) { citizen.getEntity().get().getCitizenJobHandler().setWorkAI(tempAI); }
    }

    @Override
    public AI getWorkerAI()
    {
        if (citizen.getEntity().isPresent()) { return (AI) citizen.getEntity().get().getCitizenJobHandler().getWorkAI(); }
        return null;
    }

    @Override public boolean isIdling() { return getWorkerAI() != null && getWorkerAI().getState() == AIWorkerState.IDLE; }
    @Override public void resetAI() { if (getWorkerAI() != null) { getWorkerAI().resetAI(); } }
    @Override public boolean canAIBeInterrupted() { return getWorkerAI() == null || getWorkerAI().canBeInterrupted(); }

    @Override
    public void onRemoval()
    {
        citizen.setJob(null);
        if (getWorkerAI() != null) { getWorkerAI().onRemoval(); }
        workBuilding = null;
        workModule = null;
    }

    @Override
    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider)
    {
        final CompoundTag compound = new CompoundTag();
        compound.putString(TAG_JOB_TYPE, getJobRegistryEntry().getKey().toString());
        compound.putInt(TAG_ACTIONS_DONE, actionsDone);
        if (workBuildingPos != null) { /* BlockPosUtil.write(compound, TAG_WORK_POS, workBuildingPos); */ }
        return compound;
    }

    @Override
    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, final CompoundTag compound)
    {
        if (compound.contains(TAG_ACTIONS_DONE)) { actionsDone = compound.getInt(TAG_ACTIONS_DONE); }
    }

    @Override
    public void serializeToView(final RegistryFriendlyByteBuf buffer)
    {
        buffer.writeUtf(getJobRegistryEntry().getKey().toString());
        buffer.writeInt(getAsyncRequests().size());
        buffer.writeById(IJobRegistry.getInstance()::getIdOrThrow, getJobRegistryEntry());
    }

    @Override
    public final boolean equals(final Object o)
    {
        if (!(o instanceof final AbstractJob<?, ?> that)) { return false; }
        return entry.equals(that.entry);
    }

    @Override public final int hashCode() { return entry.hashCode(); }
}

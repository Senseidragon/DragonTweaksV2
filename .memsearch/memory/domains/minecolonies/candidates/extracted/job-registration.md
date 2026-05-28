**Title:** MineColonies custom job registration pattern
**Type:** fact
**Intent triggers:** job registration, JobEntry, DeferredHolder, IJobRegistry, ModJobs, custom job, register job, DeferredRegister, createJobEntry, setRegistryName, setJobProducer
**Source/evidence:** com.minecolonies.api.colony.jobs.registry.JobEntry (cloned source version/1.21), com.minecolonies.api.colony.jobs.ModJobs (cloned source version/1.21)
**Rule or fact:** Custom jobs are registered via NeoForge's DeferredRegister against MineColonies' IJobRegistry. JobEntry is built via JobEntry.Builder — constructor is private, builder is the only entry point.

JobEntry.Builder methods (all required unless noted):
- `setJobProducer(Function<ICitizenData, IJob<?>>)` — factory for the server-side job instance; **required**
- `setJobViewProducer(Supplier<BiFunction<IColonyView, ICitizenDataView, IJobView>>)` — factory for the client-side view; **required**
- `setRegistryName(ResourceLocation)` — registry key; **required**
- `createJobEntry()` — builds the entry (NOT `.build()`)

Translation key is auto-generated from the registry name as `"com.<namespace>.job.<path>"` — no setter exists.

`produceJob(ICitizenData)` calls the jobProducer and also calls `job.setRegistryEntry(this)` — subclasses do not need to call setRegistryEntry manually.

Registration pattern:
```java
private static final DeferredRegister<JobEntry> JOB_REGISTER =
    DeferredRegister.create(IJobRegistry.getInstance().getRegistryKey(), MODID);

public static final DeferredHolder<JobEntry, JobEntry> MY_JOB =
    JOB_REGISTER.register("my_job", () ->
        new JobEntry.Builder()
            .setJobProducer(MyJob::new)
            .setJobViewProducer(() -> MyJobView::new)
            .setRegistryName(ResourceLocation.fromNamespaceAndPath(MODID, "my_job"))
            .createJobEntry());
```

MineColonies' own jobs live in `ModJobs` as static `DeferredHolder<JobEntry, JobEntry>` fields — useful reference for the expected pattern.

**Version scope:** MineColonies 1.1.1299 / NeoForge 21.1.x / MC 1.21.1
**Confidence:** 0.95
**Status:** candidate
**Date:** 2026-05-28

---
title: MineColonies — custom job registration pattern (JobEntry.Builder)
domain: minecolonies
fact: Custom jobs are registered via NeoForge's DeferredRegister against MineColonies' IJobRegistry. JobEntry is built via JobEntry.Builder — the constructor is private, so the builder is the only entry point. All three builder setters are required: setJobProducer, setJobViewProducer, setRegistryName. Build with createJobEntry() (NOT .build()). Translation key is auto-generated as "com.<namespace>.job.<path>". produceJob(ICitizenData) calls jobProducer and also calls job.setRegistryEntry(this) automatically.
confidence: 0.95
usefulness: high
supersedes: job-registration.md
authority: authoritative
validator_version: memory-pipeline-v1
validator_stage: first
validator_hash: c162b8947eb9f904d84c92ae8bf5f0fba6079b20432a48074aa8d32fc7d49404
validated_at: 2026-05-31T23:43:31.955808+00:00
approval_route: auto
---

Custom jobs are registered via NeoForge's `DeferredRegister` against MineColonies' `IJobRegistry`. `JobEntry` is built via `JobEntry.Builder` — the constructor is private, so the builder is the only entry point. All three builder setters are required.

**Key API surfaces:**
- `JobEntry.Builder.setJobProducer(Function<ICitizenData, IJob<?>>)` — server-side job factory; **required**
- `JobEntry.Builder.setJobViewProducer(Supplier<BiFunction<IColonyView, ICitizenDataView, IJobView>>)` — client-side view factory; **required**
- `JobEntry.Builder.setRegistryName(ResourceLocation)` — registry key; **required**
- `JobEntry.Builder.createJobEntry()` — builds the entry (`createJobEntry()`, NOT `.build()`)
- Translation key is auto-generated as `"com.<namespace>.job.<path>"` — no setter exists
- `produceJob(ICitizenData)` calls the `jobProducer` and also calls `job.setRegistryEntry(this)` — subclasses do not need to call `setRegistryEntry` manually

**Registration pattern:**
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

**Useful for:** registering a new citizen job type so MineColonies can produce and serialize it, providing both server job and client view factories.

**Does not prove:** how to implement the job class itself (see `AbstractJob`); how to implement the AI class (see `AbstractEntityAIBasic`); how to assign a job to a citizen programmatically.

**Source:** [[docs/api/minecolonies/colony/jobs/registry/JobEntry.java]]

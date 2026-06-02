**Title:** DataComponentType -- data components API, registration, read, write, item defaults (1.21.1)
**Type:** fact
**Intent triggers:** data components, DataComponentType, ItemStack data, NBT replacement, persistent item data, component codec, DeferredRegister.DataComponents, stack.get, stack.set, stack.update, registerComponentType
**Source:** [[https://github.com/neoforged/documentation/blob/main/docs/items/datacomponents.md]] -- fetched via Context7 2026-06-01
**Rule or fact:** DataComponentType<T> is the 1.21.x replacement for NBT-based item stack data. Each component is a typed Java record backed by a Codec (disk persistence) and a StreamCodec (network sync), registered via DeferredRegister.DataComponents.

Registration:

  public static final DeferredRegister.DataComponents REGISTRAR =
      DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "dragontweaksv2");

  // Persistent + network-synced (most common)
  public static final Supplier<DataComponentType<ExampleRecord>> MY_COMPONENT =
      REGISTRAR.registerComponentType("my_data",
          builder -> builder.persistent(CODEC).networkSynchronized(STREAM_CODEC));

  // Transient -- not saved to disk
  public static final Supplier<DataComponentType<ExampleRecord>> TRANSIENT =
      REGISTRAR.registerComponentType("transient",
          builder -> builder.networkSynchronized(STREAM_CODEC));

  // Persistent only -- use StreamCodec.unit() as a no-op stream codec
  public static final Supplier<DataComponentType<ExampleRecord>> NO_NETWORK =
      REGISTRAR.registerComponentType("no_network",
          builder -> builder.persistent(CODEC).networkSynchronized(StreamCodec.unit(DEFAULT_VALUE)));

Codec definition (RecordCodecBuilder pattern):

  public static final Codec<ExampleRecord> CODEC = RecordCodecBuilder.create(instance ->
      instance.group(
          Codec.INT.fieldOf("value1").forGetter(ExampleRecord::value1),
          Codec.BOOL.fieldOf("value2").forGetter(ExampleRecord::value2)
      ).apply(instance, ExampleRecord::new)
  );
  public static final StreamCodec<ByteBuf, ExampleRecord> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.INT, ExampleRecord::value1,
      ByteBufCodecs.BOOL, ExampleRecord::value2,
      ExampleRecord::new
  );

Reading:

  @Nullable ExampleRecord data = stack.get(MY_COMPONENT.get()); // null if absent

Writing / updating:

  stack.set(MY_COMPONENT.get(), new ExampleRecord(42, true));

  // update: get -> modify -> set; safe when component may be absent
  stack.update(MY_COMPONENT.get(), ExampleRecord.DEFAULT,
      record -> record.withValue1(99));

  // BiFunction variant (supplies extra argument)
  stack.update(DataComponents.FIREWORK_EXPLOSION, FireworkExplosion.DEFAULT,
      new IntArrayList(new int[]{1, 2, 3}), FireworkExplosion::withFadeColors);

Default components on an Item at registration:

  new Item.Properties()
      .setId(ResourceKey.create(Registries.ITEM, registryName))
      .component(MY_COMPONENT.get(), new ExampleRecord(24, true))
      .delayedComponent(DataComponents.DAMAGE_RESISTANT,
          context -> new DamageResistant(context.getOrThrow(DamageTypeTags.IS_EXPLOSION)));

Key rules:
- REGISTRAR must be registered to the mod event bus like any other DeferredRegister.
- stack.update() calls set internally -- prefer it over manual get->modify->set.
- StreamCodec.unit(default) is the correct no-op when network sync is unwanted.
- For entity/block entity data use AttachmentType, not DataComponentType.
- Components are immutable by convention -- always produce a new record, never mutate in place.

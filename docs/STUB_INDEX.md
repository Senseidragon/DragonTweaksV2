# Stub Library Index

This file serves as a high-level directory for the read-only stub files located in the `docs/stubs/` directory.

Because of the large volume of files, these are not automatically loaded into context. Use this index to identify the correct file, then load only what is directly relevant to the current task.

## Usage Instructions for Claude Code

1. **Search:** Find the relevant category below for the API you need.
2. **Load:** Use the path shown to read the specific stub file(s) needed.
3. **Context discipline:** Only load stubs directly relevant to the current task. Do not bulk-load categories.

---

## net/neoforged — `docs/stubs/net/neoforged/`

### neoforge/attachment
- `AttachmentHolder.java` — Implementation class for objects that can hold data attachments.
- `AttachmentInternals.java` — Stub file
- `AttachmentSync.java` — Stub file
- `AttachmentSyncHandler.java` — Manages how data attachments are written (on the server) and read (on the client) from packets.
- `AttachmentType.java` — Represents a data attachment type: some data that can be added to any object implementing IAttachmentHolder.
- `IAttachmentCopyHandler.java` — Custom copy handler for data attachments, to improve efficiency compared to the default implementation.
- `IAttachmentHolder.java` — An object that can hold data attachments.
- `IAttachmentSerializer.java` — Serializer for data attachments.
- `LevelAttachmentsSavedData.java` — Stub file
- `package-info.java` — Stub file

### neoforge/capabilities
- `BaseCapability.java` — Base class to reuse code common between most/all capability implementations.
- `BlockCapability.java` — Gives flexible access to objects of type T located in the world.
- `BlockCapabilityCache.java` — A cache for block capabilities, to track capabilities at a specific position with a specific context.
- `Capabilities.java` — Capabilities provided by NeoForge itself, for modders to directly reference.
- `CapabilityHooks.java` — Stub file
- `CapabilityListenerHolder.java` — Holder for capability listeners associated to a level.
- `CapabilityRegistry.java` — Helper class to manage registering capabilities.
- `EntityCapability.java` — Gives flexible access to objects of type T from entities.
- `IBlockCapabilityProvider.java` — Stub file
- `ICapabilityInvalidationListener.java` — A listener for block capability invalidation.
- `ICapabilityProvider.java` — Stub file
- `ItemCapability.java` — Gives flexible access to objects of type T from item stacks.
- `RegisterCapabilitiesEvent.java` — Fired to register capability providers at an appropriate time.
- `package-info.java` — Stub file

### neoforge/client
- `BlockEntityRenderBoundsDebugRenderer.java` — Stub file
- `ChunkRenderTypeSet.java` — An immutable ordered set of chunk render types.
- `ClientCommandHandler.java` — Stub file
- `ClientCommandSourceStack.java` — Overrides for CommandSourceStack so that methods will run successfully client side.
- `ClientHooks.java` — Class for various client-side-only hooks.
- `ClientNeoForgeMod.java` — Stub file
- `ClientTooltipFlag.java` — A version of TooltipFlag that knows about Screen and can provide modifier key states.
- `ColorResolverManager.java` — Manager for custom ColorResolver instances.
- `CreativeModeTabSearchRegistry.java` — Stub file
- `DimensionSpecialEffectsManager.java` — Manager for DimensionSpecialEffects instances.
- `DimensionTransitionScreenManager.java` — Stub file
- `EntitySpectatorShaderManager.java` — Manager for entity spectator mode shaders.
- `ExtendedServerListData.java` — Stub file
- `FireworkShapeFactoryRegistry.java` — Keeps track of custom firework shape types.
- `GlStateBackup.java` — Backup of the OpenGL render state, for use in GUI rendering.
- `IArmPoseTransformer.java` — An ArmPose that can be defined by the user.
- `IItemDecorator.java` — An ItemDecorator that is used to render something on specific items.
- `ItemDecoratorHandler.java` — Stub file
- `NamedRenderTypeManager.java` — Manager for named RenderType render types.
- `NeoForgeRenderTypes.java` — Stub file
- `ParticleBoundsDebugRenderer.java` — Stub file
- `PresetEditorManager.java` — Stub file
- `RecipeBookManager.java` — Manager for RecipeBookType recipe book types and categories.
- `RenderTypeGroup.java` — A set of functionally equivalent shaders.
- `RenderTypeHelper.java` — Provides helper functions replacing those in ItemBlockRenderTypes.
- `StencilManager.java` — Stub file
- `TagConventionLogWarningClient.java` — Stub file

### neoforge/client/command
- `ClientConfigCommand.java` — Stub file
- `package-info.java` — Stub file

### neoforge/client/entity/animation
- `AnimationKeyframeTarget.java` — A function for transforming vectors into values that make sense to their keyframe's target.
- `AnimationTarget.java` — Wrapper for an AnimationChannel.Target and a way to transform a simple keyframe vector.

### neoforge/client/entity/animation/json
- `AnimationHolder.java` — Holds a single AnimationDefinition loaded from resource packs.
- `AnimationLoader.java` — A loader for entity animations written in JSON.
- `AnimationParser.java` — A parser for parsing JSON-based entity animation files.
- `AnimationTypeManager.java` — Manager for custom AnimationTargets and interpolations.
- `package-info.java` — Stub file

### neoforge/client/event
- `AddAttributeTooltipsEvent.java` — Fired after attribute tooltip lines have been added to an item stack's tooltip.
- `AddSectionGeometryEvent.java` — Can be used to add static geometry to chunk sections.
- `CalculateDetachedCameraDistanceEvent.java` — Fired for hooking the maximum distance from the player to the camera.
- `CalculatePlayerTurnEvent.java` — Fired in MouseHandler#turnPlayer() when retrieving the values.
- `ClientChatEvent.java` — Fired when the client is about to send a chat message to the server.
- `ClientChatReceivedEvent.java` — Fired when a chat message is received on the client.
- `ClientPauseChangeEvent.java` — Fired when game pause state is about to change.
- `ClientPlayerChangeGameTypeEvent.java` — Fired when the client player is notified of a change of GameType.
- `ClientPlayerNetworkEvent.java` — Fired for different client connectivity events.
- `ClientTickEvent.java` — Base class of the two client tick events.
- `ComputeFovModifierEvent.java` — Fired after the field of vision (FOV) modifier for the player is calculated.
- `ContainerScreenEvent.java` — Fired for hooking into AbstractContainerScreen events.
- `CustomizeGuiOverlayEvent.java` — Fired when an overlay is about to be rendered to the screen.
- `EntityRenderersEvent.java` — Fired for different events/actions relating to entity renderers.
- `GatherEffectScreenTooltipsEvent.java` — Called when an EffectRenderingInventoryScreen draws tooltip lines.
- `GatherSkippedAttributeTooltipsEvent.java` — Used to collect the IDs of attribute modifiers that should be skipped.
- `InputEvent.java` — Fired when an input is detected from the user's input devices.
- `ModelEvent.java` — Houses events related to models.
- `MovementInputUpdateEvent.java` — Fired after the player's movement inputs are updated.
- `RecipesUpdatedEvent.java` — Fired when the RecipeManager has received and synced the recipes.
- `RegisterClientCommandsEvent.java` — Fired to allow mods to register client commands.
- `RegisterClientReloadListenersEvent.java` — Fired to allow mods to register their reload listeners.
- `RegisterClientTooltipComponentFactoriesEvent.java` — Allows users to register custom ClientTooltipComponent factories.
- `RegisterColorHandlersEvent.java` — Fired for registering block and item color handlers.
- `RegisterDimensionSpecialEffectsEvent.java` — Allows users to register custom DimensionSpecialEffects.
- `RegisterDimensionTransitionScreenEvent.java` — Event for registering screen effects when transitioning across dimensions.
- `RegisterEntitySpectatorShadersEvent.java` — Allows users to register custom shaders for entity spectation.
- `RegisterGuiLayersEvent.java` — Allows users to register custom LayeredDraw.Layer layers.
- `RegisterItemDecorationsEvent.java` — Allows users to register custom IItemDecorator instances.
- `RegisterJsonAnimationTypesEvent.java` — Allows registering custom AnimationTargets and interpolations.
- `RegisterKeyMappingsEvent.java` — Allows users to register custom KeyMapping key mappings.
- `RegisterMaterialAtlasesEvent.java` — Fired for registering TextureAtlas texture atlases.
- `RegisterMenuScreensEvent.java` — Stub file
- `RegisterNamedRenderTypesEvent.java` — Allows users to register custom named RenderType render types.
- `RegisterParticleProvidersEvent.java` — Fired for registering particle providers.
- `RegisterPresetEditorsEvent.java` — Event for registering PresetEditor screen factories.
- `RegisterRecipeBookCategoriesEvent.java` — Allows users to register custom categories for the vanilla recipe book.
- `RegisterRenderBuffersEvent.java` — Fired to allow mods to register custom render buffers.
- `RegisterShadersEvent.java` — Fired to allow mods to register custom ShaderInstance shaders.
- `RegisterSpriteSourceTypesEvent.java` — Fired to allow mods to register their own SpriteSourceType.
- `RenderArmEvent.java` — Fired before the player's arm is rendered in first person.
- `RenderBlockScreenEffectEvent.java` — Fired before a block texture will be overlaid on the player's view.
- `RenderFrameEvent.java` — Base class of the two render frame events.
- `RenderGuiEvent.java` — Fired when the HUD is rendered to the screen.
- `RenderGuiLayerEvent.java` — Fired when a GUI layer is rendered to the screen.
- `RenderHandEvent.java` — Fired before a hand is rendered in the first person view.
- `RenderHighlightEvent.java` — Fired before a selection highlight is rendered.
- `RenderItemInFrameEvent.java` — Fired before an item stack is rendered in an item frame.
- `RenderLevelStageEvent.java` — Fires at various times during LevelRenderer.renderLevel.
- `RenderLivingEvent.java` — Fired when a LivingEntity is rendered.
- `RenderNameTagEvent.java` — Fired before an entity renderer renders the nameplate.
- `RenderPlayerEvent.java` — Fired when a player is being rendered.
- `RenderTooltipEvent.java` — Fired during tooltip rendering.
- `ScreenEvent.java` — Fired on different events/actions when a Screen is active.
- `ScreenshotEvent.java` — Fired when a screenshot is taken, but before it is written to disk.
- `SelectMusicEvent.java` — Fired when the MusicManager checks what situational music should play.
- `TextureAtlasStitchedEvent.java` — Fired after a texture atlas is stitched together.
- `ToastAddEvent.java` — Fired when the client queues a Toast message to be shown onscreen.
- `ViewportEvent.java` — Fired for hooking into the entity view rendering in GameRenderer.
- `package-info.java` — Events fired only on the client-side.

### neoforge/client/event/sound
- `PlaySoundEvent.java` — Fired when a sound is about to be played by the sound engine.
- `PlaySoundSourceEvent.java` — Fired when a non-streaming sound is being played.
- `PlayStreamingSourceEvent.java` — Fired when a streaming sound is being played.
- `SoundEngineLoadEvent.java` — Fired when the SoundEngine is constructed or reloaded.
- `SoundEvent.java` — Superclass for sound related events.
- `package-info.java` — Client-only events relating to sounds.

### neoforge/client/extensions
- `IAbstractWidgetExtension.java` — Extension interface for AbstractWidget.
- `IBakedModelExtension.java` — Extension interface for BakedModel.
- `IBlockEntityRendererExtension.java` — Stub file
- `IDimensionSpecialEffectsExtension.java` — Extension interface for DimensionSpecialEffects.
- `IFontExtension.java` — Extension interface for Font.
- `IGuiGraphicsExtension.java` — Extension interface for GuiGraphics.
- `IKeyMappingExtension.java` — Extension interface for KeyMapping.
- `IMenuProviderExtension.java` — Extension type for the MenuProvider interface.
- `IMinecraftExtension.java` — Extension interface for Minecraft.
- `IModelBakerExtension.java` — Stub file
- `IPoseStackExtension.java` — Extension interface for PoseStack.
- `IVertexConsumerExtension.java` — Extension interface for VertexConsumer.
- `ModelStateExtension.java` — Stub file

### neoforge/client/extensions/common
- `ClientExtensionsManager.java` — Stub file
- `IClientBlockExtensions.java` — Client-only extensions to Block.
- `IClientFluidTypeExtensions.java` — Client-only extensions to FluidType.
- `IClientItemExtensions.java` — Client-only extensions to Item.
- `IClientMobEffectExtensions.java` — Client-only extensions to MobEffect.
- `RegisterClientExtensionsEvent.java` — Allows registering client extensions for various game objects.
- `package-info.java` — Stub file

### neoforge/client/gui
- `ClientTooltipComponentManager.java` — Manager for ClientTooltipComponent factories.
- `ConfigurationScreen.java` — A generic configuration UI.
- `CreativeTabsScreenPage.java` — Stub file
- `GuiLayerManager.java` — Adaptation of LayeredDraw used for Gui rendering.
- `IConfigScreenFactory.java` — Register an instance to ModContainer#registerExtensionPoint.
- `LoadingErrorScreen.java` — Stub file
- `ModListScreen.java` — Stub file
- `ModMismatchDisconnectedScreen.java` — Stub file
- `ScreenUtils.java` — Provides several methods and constants used by the Config GUI classes.
- `ScrollableExperimentsScreen.java` — Stub file
- `VanillaGuiLayers.java` — Identifiers for the vanilla LayeredDraw.Layer in render order.

### neoforge/client/gui/map
- `IMapDecorationRenderer.java` — Interface for custom MapDecoration renderers.
- `MapDecorationRendererManager.java` — Stub file
- `RegisterMapDecorationRenderersEvent.java` — Allows users to register custom decoration renderers.
- `package-info.java` — Stub file

### neoforge/client/gui/widget
- `ExtendedButton.java` — A button that fixes several bugs in the vanilla GuiButton drawing code.
- `ExtendedSlider.java` — Slider widget implementation which allows inputting values in a certain range.
- `ModListWidget.java` — Stub file
- `ModsButton.java` — Custom button subclass to draw an indicator overlay.
- `ScrollPanel.java` — Abstract scroll panel class.
- `UnicodeGlyphButton.java` — A button that shows a string glyph at the beginning.
- `package-info.java` — Stub file

### neoforge/client/internal
- `SelfTestClient.java` — Stub file
- `package-info.java` — Stub file

### neoforge/client/loading
- `ClientModLoader.java` — Stub file
- `NeoForgeLoadingOverlay.java` — Implementation of the LoadingOverlay.
- `NoVizFallback.java` — Stub file
- `package-info.java` — Stub file

### neoforge/client/model
- `BakedModelWrapper.java` — Wrapper for BakedModel which delegates all operations to its parent.
- `CompositeModel.java` — A model composed of several named children.
- `DynamicFluidContainerModel.java` — A dynamic fluid container model, capable of re-texturing itself at runtime.
- `ElementsModel.java` — A model composed of vanilla block elements.
- `EmptyModel.java` — A completely empty model with no quads or texture dependencies.
- `ExtendedBlockModelDeserializer.java` — A version of BlockModel.Deserializer capable of deserializing extended models.
- `ExtraFaceData.java` — Holds extra data that may be injected into a face.
- `IDynamicBakedModel.java` — Convenience interface with default implementation for dynamic baked models.
- `IModelBuilder.java` — Base interface for any object that collects culled and unculled faces.
- `IQuadTransformer.java` — Transformer for BakedQuad baked quads.
- `ItemLayerModel.java` — Forge reimplementation of vanilla's ItemModelGenerator.
- `QuadTransformers.java` — A collection of IQuadTransformer implementations.
- `RegistryAwareItemModelShaper.java` — Wrapper around ItemModelShaper that cleans up the internal maps.
- `SeparateTransformsModel.java` — A model composed of multiple sub-models which are picked based on context.
- `SimpleModelState.java` — Simple implementation of ModelState.

### neoforge/client/model/data
- `ModelData.java` — A container for data to be passed to BakedModel instances.
- `ModelDataManager.java` — A manager for the lifecycle of all the ModelData instances.
- `ModelProperty.java` — A property to be used in ModelData.
- `MultipartModelData.java` — Stub file
- `package-info.java` — Stub file

### neoforge/client/model/generators
- `BlockModelBuilder.java` — Builder for block models.
- `BlockModelProvider.java` — Stub class to extend for block model data providers.
- `BlockStateProvider.java` — Data provider for blockstate files.
- `ConfiguredModel.java` — Represents a model with blockstate configurations.
- `CustomLoaderBuilder.java` — Stub file
- `IGeneratedBlockState.java` — Stub file
- `ItemModelBuilder.java` — Builder for item models.
- `ItemModelProvider.java` — Stub class to extend for item model data providers.
- `ModelBuilder.java` — General purpose model builder.
- `ModelFile.java` — Stub file
- `ModelProvider.java` — Stub file
- `MultiPartBlockStateBuilder.java` — Stub file
- `VariantBlockStateBuilder.java` — Builder for variant-type blockstates.

### neoforge/client/model/generators/loaders
- `CompositeModelBuilder.java` — Stub file
- `DynamicFluidContainerModelBuilder.java` — Stub file
- `ItemLayerModelBuilder.java` — Stub file
- `ObjModelBuilder.java` — Stub file
- `SeparateTransformsModelBuilder.java` — Stub file
- `package-info.java` — Stub file

### neoforge/client/model/geometry
- `BlockGeometryBakingContext.java` — A geometry baking context bound to a block model.
- `GeometryLoaderManager.java` — Manager for IGeometryLoader geometry loaders.
- `IGeometryBakingContext.java` — The context in which a geometry is being baked.
- `IGeometryLoader.java` — A loader for custom IUnbakedGeometry model geometries.
- `IUnbakedGeometry.java` — General interface for any model that can be baked.
- `SimpleUnbakedGeometry.java` — Base class for implementations of IUnbakedGeometry.
- `StandaloneGeometryBakingContext.java` — A geometry baking context not bound to block/item model.
- `UnbakedGeometryHelper.java` — Helper for dealing with unbaked models and geometries.
- `package-info.java` — Stub file

### neoforge/client/model/lighting
- `FlatQuadLighter.java` — Implementation of QuadLighter that lights flat quads.
- `LightPipelineAwareModelBlockRenderer.java` — Wrapper around ModelBlockRenderer to allow rendering blocks with the light pipeline.
- `QuadLighter.java` — Base class for all quad lighting providers.
- `SmoothQuadLighter.java` — Implementation of QuadLighter using ambient occlusion.
- `package-info.java` — Stub file

### neoforge/client/model/obj
- `ObjLoader.java` — A loader for ObjModel OBJ models.
- `ObjMaterialLibrary.java` — An OBJ material library (MTL), composed of named materials.
- `ObjModel.java` — A model loaded from an OBJ file.
- `ObjTokenizer.java` — A tokenizer for OBJ and MTL files.
- `package-info.java` — Stub file

### neoforge/client/model/pipeline
- `QuadBakingVertexConsumer.java` — Vertex consumer that outputs BakedQuad baked quads.
- `RemappingVertexPipeline.java` — Vertex pipeline element that remaps incoming data.
- `TransformingVertexPipeline.java` — Vertex pipeline element that applies a transformation.
- `VertexConsumerWrapper.java` — Wrapper for VertexConsumer which delegates all operations.
- `package-info.java` — Stub file

### neoforge/client/model/renderable
- `BakedModelRenderable.java` — Renderable wrapper for BakedModel.
- `CompositeRenderable.java` — A renderable object composed of a hierarchy of parts.
- `IRenderable.java` — A standard interface for things that can be rendered.
- `ITextureRenderTypeLookup.java` — A generic lookup for RenderType implementations.
- `package-info.java` — Stub file

### neoforge/client/resources
- `NeoForgeSplashHooks.java` — Stub file
- `package-info.java` — Stub file

### neoforge/client/settings
- `IKeyConflictContext.java` — Defines the context that a KeyMapping is used.
- `KeyConflictContext.java` — Stub file
- `KeyMappingLookup.java` — Stub file
- `KeyModifier.java` — Stub file
- `package-info.java` — Stub file

### neoforge/client/textures
- `FluidSpriteCache.java` — Helper class for safely accessing fluid textures.
- `NamespacedDirectoryLister.java` — Namespace-aware version of DirectoryLister.
- `SpriteContentsConstructor.java` — Stub file
- `UnitTextureAtlasSprite.java` — Stub file
- `package-info.java` — Stub file

### neoforge/client/util
- `DebuggingHelper.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common
- `BasicItemListing.java` — Stub file
- `BooleanAttribute.java` — Stub file
- `CommonHooks.java` — Stub file
- `CreativeModeTabRegistry.java` — Stub file
- `DataMapHooks.java` — Stub file
- `DeferredSpawnEggItem.java` — Stub file
- `EffectCure.java` — Stub file
- `EffectCures.java` — Stub file
- `FarmlandWaterManager.java` — Stub file
- `IMinecartCollisionHandler.java` — Stub file
- `IOUtilities.java` — Stub file
- `IShearable.java` — Stub file
- `ItemAbilities.java` — Stub file
- `ItemAbility.java` — Stub file
- `LenientUnboundedMapCodec.java` — Stub file
- `ModConfigSpec.java` — Stub file
- `MonsterRoomHooks.java` — Stub file
- `MutableDataComponentHolder.java` — Stub file
- `NeoForge.java` — Stub file
- `NeoForgeConfig.java` — Stub file
- `NeoForgeEventHandler.java` — Stub file
- `NeoForgeMod.java` — Stub file
- `PercentageAttribute.java` — Stub file
- `SimpleTier.java` — Stub file
- `SoundAction.java` — Stub file
- `SoundActions.java` — Stub file
- `SpecialPlantable.java` — Stub file
- `TagConventionLogWarning.java` — Stub file
- `Tags.java` — Stub file
- `TranslatableEnum.java` — Stub file
- `UsernameCache.java` — Stub file
- `VillagerTradingManager.java` — Stub file
- `WorldWorkerManager.java` — Stub file

### neoforge/common/advancements/critereon
- `ItemAbilityPredicate.java` — Stub file
- `PiglinCurrencyItemPredicate.java` — Stub file
- `PiglinNeutralArmorEntityPredicate.java` — Stub file
- `SnowBootsEntityPredicate.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/brewing
- `BrewingRecipe.java` — Stub file
- `BrewingRecipeRegistry.java` — Stub file
- `IBrewingRecipe.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/command
- `EntitySelectorManager.java` — Stub file
- `IEntitySelectorType.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/conditions
- `AndCondition.java` — Stub file
- `ConditionContext.java` — Stub file
- `ConditionalOps.java` — Stub file
- `FalseCondition.java` — Stub file
- `ICondition.java` — Stub file
- `IConditionBuilder.java` — Stub file
- `ItemExistsCondition.java` — Stub file
- `ModLoadedCondition.java` — Stub file
- `NotCondition.java` — Stub file
- `OrCondition.java` — Stub file
- `TagEmptyCondition.java` — Stub file
- `TrueCondition.java` — Stub file
- `WithConditions.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/crafting
- `BlockTagIngredient.java` — Stub file
- `CompoundIngredient.java` — Stub file
- `ConditionalRecipeOutput.java` — Stub file
- `CraftingHelper.java` — Stub file
- `DataComponentIngredient.java` — Stub file
- `DifferenceIngredient.java` — Stub file
- `ICustomIngredient.java` — Stub file
- `IRecipeContainer.java` — Stub file
- `IngredientType.java` — Stub file
- `IntersectionIngredient.java` — Stub file
- `SizedIngredient.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/damagesource
- `DamageContainer.java` — Stub file
- `IDeathMessageProvider.java` — Stub file
- `IReductionFunction.java` — Stub file
- `IScalingFunction.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/data
- `AdvancementProvider.java` — Stub file
- `BlockTagsProvider.java` — Stub file
- `DataMapProvider.java` — Stub file
- `DatapackBuiltinEntriesProvider.java` — Stub file
- `ExistingFileHelper.java` — Stub file
- `GeneratingOverlayMetadataSection.java` — Stub file
- `GlobalLootModifierProvider.java` — Stub file
- `JsonCodecProvider.java` — Stub file
- `LanguageProvider.java` — Stub file
- `ParticleDescriptionProvider.java` — Stub file
- `SoundDefinition.java` — Stub file
- `SoundDefinitionsProvider.java` — Stub file
- `SpriteSourceProvider.java` — Stub file

### neoforge/common/data/fixes
- `NeoForgeEntityLegacyAttributesFix.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/data/internal
- `NeoForgeAdvancementProvider.java` — Stub file
- `NeoForgeBiomeTagsProvider.java` — Stub file
- `NeoForgeBlockTagsProvider.java` — Stub file
- `NeoForgeDamageTypeTagsProvider.java` — Stub file
- `NeoForgeDataMapsProvider.java` — Stub file
- `NeoForgeEnchantmentTagsProvider.java` — Stub file
- `NeoForgeEntityTypeTagsProvider.java` — Stub file
- `NeoForgeFluidTagsProvider.java` — Stub file
- `NeoForgeItemTagsProvider.java` — Stub file
- `NeoForgeLanguageProvider.java` — Stub file
- `NeoForgeLootTableProvider.java` — Stub file
- `NeoForgeRecipeProvider.java` — Stub file
- `NeoForgeRegistryOrderReportProvider.java` — Stub file
- `NeoForgeSpriteSourceProvider.java` — Stub file
- `NeoForgeStructureTagsProvider.java` — Stub file
- `VanillaSoundDefinitionsProvider.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/enums
- `BubbleColumnDirection.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/extensions
- `IAbstractMinecartExtension.java` — Stub file
- `IAdvancementBuilderExtension.java` — Stub file
- `IAttributeExtension.java` — Stub file
- `IBaseRailBlockExtension.java` — Stub file
- `IBlockAndTintGetterExtension.java` — Stub file
- `IBlockEntityExtension.java` — Stub file
- `IBlockExtension.java` — Stub file
- `IBlockGetterExtension.java` — Stub file
- `IBlockStateExtension.java` — Stub file
- `IBoatExtension.java` — Stub file
- `IBucketPickupExtension.java` — Stub file
- `IClientCommonPacketListenerExtension.java` — Stub file
- `ICommandSourceStackExtension.java` — Stub file
- `ICommonPacketListener.java` — Stub file
- `IDataComponentHolderExtension.java` — Stub file
- `IDataComponentMapBuilderExtensions.java` — Stub file
- `IDispensibleContainerItemExtension.java` — Stub file
- `IEnchantmentExtension.java` — Stub file
- `IEntityExtension.java` — Stub file
- `IFluidExtension.java` — Stub file
- `IFluidStateExtension.java` — Stub file
- `IFriendlyByteBufExtension.java` — Stub file
- `IHolderExtension.java` — Stub file
- `IHolderLookupProviderExtension.java` — Stub file
- `IHolderSetExtension.java` — Stub file
- `IIntrinsicHolderTagAppenderExtension.java` — Stub file
- `IItemExtension.java` — Stub file
- `IItemPropertiesExtensions.java` — Stub file
- `IItemStackExtension.java` — Stub file
- `ILevelExtension.java` — Stub file
- `ILevelReaderExtension.java` — Stub file
- `ILivingEntityExtension.java` — Stub file
- `IMenuTypeExtension.java` — Stub file
- `IMobEffectExtension.java` — Stub file
- `IOwnedSpawner.java` — Stub file
- `IPackResourcesExtension.java` — Stub file
- `IPacketFlowExtension.java` — Stub file
- `IPlayerExtension.java` — Stub file
- `IPlayerListExtension.java` — Stub file
- `IRecipeOutputExtension.java` — Stub file
- `IServerChunkCacheExtension.java` — Stub file
- `IServerCommonPacketListenerExtension.java` — Stub file
- `IServerConfigurationPacketListenerExtension.java` — Stub file
- `IServerGamePacketListenerExtension.java` — Stub file
- `ITagAppenderExtension.java` — Stub file
- `ITagBuilderExtension.java` — Stub file
- `ITransformationExtension.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/loot
- `AddTableLootModifier.java` — Stub file
- `CanItemPerformAbility.java` — Stub file
- `IGlobalLootModifier.java` — Stub file
- `LootModifier.java` — Stub file
- `LootModifierManager.java` — Stub file
- `LootTableIdCondition.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/property
- `Properties.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/ticket
- `AABBTicket.java` — Stub file
- `ChunkTicketManager.java` — Stub file
- `ITicketGetter.java` — Stub file
- `ITicketManager.java` — Stub file
- `SimpleTicket.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/util
- `AttributeTooltipContext.java` — Stub file
- `AttributeUtil.java` — Stub file
- `BlockSnapshot.java` — Stub file
- `CenterChunkPosComparator.java` — Stub file
- `ConcatenatedListView.java` — Stub file
- `DataComponentUtil.java` — Stub file
- `DeferredSoundType.java` — Stub file
- `DummySavedData.java` — Stub file
- `FakePlayer.java` — Stub file
- `FakePlayerFactory.java` — Stub file
- `FriendlyByteBufUtil.java` — Stub file
- `HexDumper.java` — Stub file
- `INBTSerializable.java` — Stub file
- `InsertableLinkedOpenCustomHashSet.java` — Stub file
- `InsertingContents.java` — Stub file
- `ItemStackMap.java` — Stub file
- `JsonUtils.java` — Stub file
- `Lazy.java` — Stub file
- `LogMessageAdapter.java` — Stub file
- `LogicalSidedProvider.java` — Stub file
- `MutableHashedLinkedMap.java` — Stub file
- `NeoForgeExtraCodecs.java` — Stub file
- `RecipeMatcher.java` — Stub file
- `SelfTest.java` — Stub file
- `Size2i.java` — Stub file
- `SortedProperties.java` — Stub file
- `TablePrinter.java` — Stub file
- `TextTable.java` — Stub file
- `TransformationHelper.java` — Stub file
- `TriPredicate.java` — Stub file
- `TriState.java` — Stub file

### neoforge/common/util/flag
- `FeatureFlagLoader.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/util/strategy
- `BasicStrategy.java` — Stub file
- `IdentityStrategy.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/world
- `AuxiliaryLightManager.java` — Stub file
- `BiomeGenerationSettingsBuilder.java` — Stub file
- `BiomeModifier.java` — Stub file
- `BiomeModifiers.java` — Stub file
- `BiomeSpecialEffectsBuilder.java` — Stub file
- `ClimateSettingsBuilder.java` — Stub file
- `LevelChunkAuxiliaryLightManager.java` — Stub file
- `MobSpawnSettingsBuilder.java` — Stub file
- `ModifiableBiomeInfo.java` — Stub file
- `ModifiableStructureInfo.java` — Stub file
- `NoneBiomeModifier.java` — Stub file
- `NoneStructureModifier.java` — Stub file
- `PieceBeardifierModifier.java` — Stub file
- `StructureModifier.java` — Stub file
- `StructureModifiers.java` — Stub file
- `StructureSettingsBuilder.java` — Stub file

### neoforge/common/world/chunk
- `ForcedChunkManager.java` — Stub file
- `LoadingValidationCallback.java` — Stub file
- `RegisterTicketControllersEvent.java` — Stub file
- `TicketController.java` — Stub file
- `TicketHelper.java` — Stub file
- `TicketSet.java` — Stub file
- `package-info.java` — Stub file

### neoforge/common/world/poi
- `ExtendPoiTypesEvent.java` — Stub file
- `PoiStateSet.java` — Stub file
- `PoiTypeExtender.java` — Stub file
- `package-info.java` — Stub file

### neoforge/data/event
- `GatherDataEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/data/loading
- `DatagenModLoader.java` — Stub file
- `package-info.java` — Stub file

### neoforge/energy
- `ComponentEnergyStorage.java` — Stub file
- `EmptyEnergyStorage.java` — Stub file
- `EnergyStorage.java` — Stub file
- `IEnergyStorage.java` — Stub file
- `package-info.java` — Stub file

### neoforge/entity
- `IEntityWithComplexSpawn.java` — Stub file
- `PartEntity.java` — Stub file
- `XpOrbTargetingEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event
- `AddPackFindersEvent.java` — Stub file
- `AddReloadListenerEvent.java` — Stub file
- `AnvilUpdateEvent.java` — Stub file
- `BlockEntityTypeAddBlocksEvent.java` — Stub file
- `BuildCreativeModeTabContentsEvent.java` — Stub file
- `CommandEvent.java` — Stub file
- `DifficultyChangeEvent.java` — Stub file
- `EventHooks.java` — Stub file
- `GameShuttingDownEvent.java` — Stub file
- `GrindstoneEvent.java` — Stub file
- `ItemAttributeModifierEvent.java` — Stub file
- `ItemStackedOnOtherEvent.java` — Stub file
- `LootTableLoadEvent.java` — Stub file
- `ModMismatchEvent.java` — Stub file
- `ModifyDefaultComponentsEvent.java` — Stub file
- `OnDatapackSyncEvent.java` — Stub file
- `PlayLevelSoundEvent.java` — Stub file
- `RegisterCommandsEvent.java` — Stub file
- `RegisterGameTestsEvent.java` — Stub file
- `RegisterStructureConversionsEvent.java` — Stub file
- `ServerChatEvent.java` — Stub file
- `StatAwardEvent.java` — Stub file
- `TagsUpdatedEvent.java` — Stub file
- `VanillaGameEvent.java` — Stub file

### neoforge/event/brewing
- `PlayerBrewedPotionEvent.java` — Stub file
- `PotionBrewEvent.java` — Stub file
- `RegisterBrewingRecipesEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/enchanting
- `EnchantmentLevelSetEvent.java` — Stub file
- `GetEnchantmentLevelEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/entity
- `EntityAttributeCreationEvent.java` — Stub file
- `EntityAttributeModificationEvent.java` — Stub file
- `EntityEvent.java` — Stub file
- `EntityInvulnerabilityCheckEvent.java` — Stub file
- `EntityJoinLevelEvent.java` — Stub file
- `EntityLeaveLevelEvent.java` — Stub file
- `EntityMobGriefingEvent.java` — Stub file
- `EntityMountEvent.java` — Stub file
- `EntityStruckByLightningEvent.java` — Stub file
- `EntityTeleportEvent.java` — Stub file
- `EntityTravelToDimensionEvent.java` — Stub file
- `ProjectileImpactEvent.java` — Stub file
- `RegisterSpawnPlacementsEvent.java` — Stub file

### neoforge/event/entity/item
- `ItemEvent.java` — Stub file
- `ItemExpireEvent.java` — Stub file
- `ItemTossEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/entity/living
- `AnimalTameEvent.java` — Stub file
- `ArmorHurtEvent.java` — Stub file
- `BabyEntitySpawnEvent.java` — Stub file
- `EffectParticleModificationEvent.java` — Stub file
- `EnderManAngerEvent.java` — Stub file
- `FinalizeSpawnEvent.java` — Stub file
- `LivingBreatheEvent.java` — Stub file
- `LivingChangeTargetEvent.java` — Stub file
- `LivingConversionEvent.java` — Stub file
- `LivingDamageEvent.java` — Stub file
- `LivingDeathEvent.java` — Stub file
- `LivingDestroyBlockEvent.java` — Stub file
- `LivingDropsEvent.java` — Stub file
- `LivingDrownEvent.java` — Stub file
- `LivingEntityUseItemEvent.java` — Stub file
- `LivingEquipmentChangeEvent.java` — Stub file
- `LivingEvent.java` — Stub file
- `LivingExperienceDropEvent.java` — Stub file
- `LivingFallEvent.java` — Stub file
- `LivingGetProjectileEvent.java` — Stub file
- `LivingHealEvent.java` — Stub file
- `LivingIncomingDamageEvent.java` — Stub file
- `LivingKnockBackEvent.java` — Stub file
- `LivingShieldBlockEvent.java` — Stub file
- `LivingSwapItemsEvent.java` — Stub file
- `LivingUseTotemEvent.java` — Stub file
- `MobDespawnEvent.java` — Stub file
- `MobEffectEvent.java` — Stub file
- `MobSpawnEvent.java` — Stub file
- `MobSplitEvent.java` — Stub file
- `SpawnClusterSizeEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/entity/player
- `AdvancementEvent.java` — Stub file
- `AnvilRepairEvent.java` — Stub file
- `ArrowLooseEvent.java` — Stub file
- `ArrowNockEvent.java` — Stub file
- `AttackEntityEvent.java` — Stub file
- `BonemealEvent.java` — Stub file
- `CanContinueSleepingEvent.java` — Stub file
- `CanPlayerSleepEvent.java` — Stub file
- `ClientInformationUpdatedEvent.java` — Stub file
- `CriticalHitEvent.java` — Stub file
- `ItemEntityPickupEvent.java` — Stub file
- `ItemFishedEvent.java` — Stub file
- `ItemTooltipEvent.java` — Stub file
- `PermissionsChangedEvent.java` — Stub file
- `PlayerContainerEvent.java` — Stub file
- `PlayerDestroyItemEvent.java` — Stub file
- `PlayerEnchantItemEvent.java` — Stub file
- `PlayerEvent.java` — Stub file
- `PlayerFlyableFallEvent.java` — Stub file
- `PlayerHeartTypeEvent.java` — Stub file
- `PlayerInteractEvent.java` — Stub file
- `PlayerNegotiationEvent.java` — Stub file
- `PlayerRespawnPositionEvent.java` — Stub file
- `PlayerSetSpawnEvent.java` — Stub file
- `PlayerSpawnPhantomsEvent.java` — Stub file
- `PlayerWakeUpEvent.java` — Stub file
- `PlayerXpEvent.java` — Stub file
- `SweepAttackEvent.java` — Stub file
- `TradeWithVillagerEvent.java` — Stub file
- `UseItemOnBlockEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/furnace
- `FurnaceFuelBurnTimeEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/level
- `AlterGroundEvent.java` — Stub file
- `BlockDropsEvent.java` — Stub file
- `BlockEvent.java` — Stub file
- `BlockGrowFeatureEvent.java` — Stub file
- `ChunkDataEvent.java` — Stub file
- `ChunkEvent.java` — Stub file
- `ChunkTicketLevelUpdatedEvent.java` — Stub file
- `ChunkWatchEvent.java` — Stub file
- `ExplosionEvent.java` — Stub file
- `ExplosionKnockbackEvent.java` — Stub file
- `LevelEvent.java` — Stub file
- `ModifyCustomSpawnersEvent.java` — Stub file
- `NoteBlockEvent.java` — Stub file
- `PistonEvent.java` — Stub file
- `SleepFinishedTimeEvent.java` — Stub file

### neoforge/event/level/block
- `CreateFluidSourceEvent.java` — Stub file
- `CropGrowEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/server
- `ServerAboutToStartEvent.java` — Stub file
- `ServerLifecycleEvent.java` — Stub file
- `ServerStartedEvent.java` — Stub file
- `ServerStartingEvent.java` — Stub file
- `ServerStoppedEvent.java` — Stub file
- `ServerStoppingEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/tick
- `EntityTickEvent.java` — Stub file
- `LevelTickEvent.java` — Stub file
- `PlayerTickEvent.java` — Stub file
- `ServerTickEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/event/village
- `VillageSiegeEvent.java` — Stub file
- `VillagerTradesEvent.java` — Stub file
- `WandererTradesEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/fluids
- `BaseFlowingFluid.java` — Stub file
- `CauldronFluidContent.java` — Stub file
- `DispenseFluidContainer.java` — Stub file
- `FluidActionResult.java` — Stub file
- `FluidInteractionRegistry.java` — Stub file
- `FluidStack.java` — Stub file
- `FluidStackLinkedSet.java` — Stub file
- `FluidType.java` — Stub file
- `FluidUtil.java` — Stub file
- `IFluidTank.java` — Stub file
- `RegisterCauldronFluidContentEvent.java` — Stub file
- `SimpleFluidContent.java` — Stub file

### neoforge/fluids/capability
- `IFluidHandler.java` — Stub file
- `IFluidHandlerItem.java` — Stub file
- `package-info.java` — Stub file

### neoforge/fluids/capability/templates
- `EmptyFluidHandler.java` — Stub file
- `FluidHandlerItemStack.java` — Stub file
- `FluidHandlerItemStackSimple.java` — Stub file
- `FluidTank.java` — Stub file
- `VoidFluidHandler.java` — Stub file
- `package-info.java` — Stub file

### neoforge/fluids/capability/wrappers
- `BlockWrapper.java` — Stub file
- `BucketPickupHandlerWrapper.java` — Stub file
- `CauldronWrapper.java` — Stub file
- `FluidBucketWrapper.java` — Stub file
- `package-info.java` — Stub file

### neoforge/fluids/crafting
- `CompoundFluidIngredient.java` — Stub file
- `DataComponentFluidIngredient.java` — Stub file
- `DifferenceFluidIngredient.java` — Stub file
- `EmptyFluidIngredient.java` — Stub file
- `FluidIngredient.java` — Stub file
- `FluidIngredientType.java` — Stub file
- `IntersectionFluidIngredient.java` — Stub file
- `SingleFluidIngredient.java` — Stub file
- `SizedFluidIngredient.java` — Stub file
- `TagFluidIngredient.java` — Stub file
- `package-info.java` — Stub file

### neoforge/forge/snapshots
- `ForgeSnapshotsMod.java` — Stub file
- `ForgeSnapshotsModClient.java` — Stub file
- `package-info.java` — Stub file

### neoforge/gametest
- `BlockPosValueConverter.java` — Stub file
- `GameTestHolder.java` — Stub file
- `GameTestHooks.java` — Stub file
- `PrefixGameTestTemplate.java` — Stub file
- `package-info.java` — Stub file

### neoforge/internal
- `BrandingControl.java` — Stub file
- `CommonModLoader.java` — Stub file
- `NeoForgeBindings.java` — Stub file
- `RegistrationEvents.java` — Stub file
- `package-info.java` — Stub file

### neoforge/internal/versions/neoforge
- `NeoForgeVersion.java` — Stub file
- `package-info.java` — Stub file

### neoforge/internal/versions/neoform
- `package-info.java` — Stub file

### neoforge/items
- `ComponentItemHandler.java` — Stub file
- `IItemHandler.java` — Stub file
- `IItemHandlerModifiable.java` — Stub file
- `ItemHandlerCopySlot.java` — Stub file
- `ItemHandlerHelper.java` — Stub file
- `ItemStackHandler.java` — Stub file
- `SlotItemHandler.java` — Stub file
- `StackCopySlot.java` — Stub file
- `VanillaHopperItemHandler.java` — Stub file
- `VanillaInventoryCodeHooks.java` — Stub file
- `package-info.java` — Stub file

### neoforge/items/wrapper
- `CombinedInvWrapper.java` — Stub file
- `EmptyItemHandler.java` — Stub file
- `EntityArmorInvWrapper.java` — Stub file
- `EntityEquipmentInvWrapper.java` — Stub file
- `EntityHandsInvWrapper.java` — Stub file
- `ForwardingItemHandler.java` — Stub file
- `InvWrapper.java` — Stub file
- `PlayerArmorInvWrapper.java` — Stub file
- `PlayerInvWrapper.java` — Stub file
- `PlayerMainInvWrapper.java` — Stub file
- `PlayerOffhandInvWrapper.java` — Stub file
- `RangedWrapper.java` — Stub file
- `RecipeWrapper.java` — Stub file
- `SidedInvWrapper.java` — Stub file
- `package-info.java` — Stub file

### neoforge/junit
- `JUnitMain.java` — Stub file
- `package-info.java` — Stub file

### neoforge/logging
- `CrashReportExtender.java` — Stub file
- `PacketDump.java` — Stub file
- `ThreadInfoUtil.java` — Stub file
- `package-info.java` — Stub file

### neoforge/mixins
- `BlockEntityTypeAccessor.java` — Stub file
- `MappedRegistryAccessor.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network
- `ConfigSync.java` — Stub file
- `ConfigurationInitialization.java` — Stub file
- `DualStackUtils.java` — Stub file
- `IContainerFactory.java` — Stub file
- `NetworkInitialization.java` — Stub file
- `PacketDistributor.java` — Stub file

### neoforge/network/bundle
- `BundlePacketUtils.java` — Stub file
- `PacketAndPayloadAcceptor.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/codec
- `NeoForgeStreamCodecs.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/configuration
- `CheckExtensibleEnums.java` — Stub file
- `CheckFeatureFlags.java` — Stub file
- `CommonRegisterTask.java` — Stub file
- `CommonVersionTask.java` — Stub file
- `ICustomConfigurationTask.java` — Stub file
- `RegistryDataMapNegotiation.java` — Stub file
- `SyncConfig.java` — Stub file
- `SyncRegistries.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/connection
- `ConnectionType.java` — Stub file
- `ConnectionUtils.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/event
- `RegisterConfigurationTasksEvent.java` — Stub file
- `RegisterPayloadHandlersEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/filters
- `CommandTreeCleaner.java` — Stub file
- `DynamicChannelHandler.java` — Stub file
- `GenericPacketSplitter.java` — Stub file
- `NetworkFilters.java` — Stub file
- `VanillaConnectionNetworkFilter.java` — Stub file
- `VanillaPacketFilter.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/handlers
- `ClientPayloadHandler.java` — Stub file
- `ServerPayloadHandler.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/handling
- `ClientPayloadContext.java` — Stub file
- `DirectionalPayloadHandler.java` — Stub file
- `IPayloadContext.java` — Stub file
- `IPayloadHandler.java` — Stub file
- `MainThreadPayloadHandler.java` — Stub file
- `ServerPayloadContext.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/negotiation
- `NegotiableNetworkComponent.java` — Stub file
- `NegotiatedNetworkComponent.java` — Stub file
- `NegotiationResult.java` — Stub file
- `NetworkComponentNegotiator.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/payload
- `AdvancedAddEntityPayload.java` — Stub file
- `AdvancedContainerSetDataPayload.java` — Stub file
- `AdvancedOpenScreenPayload.java` — Stub file
- `AuxiliaryLightDataPayload.java` — Stub file
- `ClientboundCustomSetTimePayload.java` — Stub file
- `CommonRegisterPayload.java` — Stub file
- `CommonVersionPayload.java` — Stub file
- `ConfigFilePayload.java` — Stub file
- `DinnerboneProtocolUtils.java` — Stub file
- `ExtensibleEnumAcknowledgePayload.java` — Stub file
- `ExtensibleEnumDataPayload.java` — Stub file
- `FeatureFlagAcknowledgePayload.java` — Stub file
- `FeatureFlagDataPayload.java` — Stub file
- `FrozenRegistryPayload.java` — Stub file
- `FrozenRegistrySyncCompletedPayload.java` — Stub file
- `FrozenRegistrySyncStartPayload.java` — Stub file
- `KnownRegistryDataMapsPayload.java` — Stub file
- `KnownRegistryDataMapsReplyPayload.java` — Stub file
- `MinecraftRegisterPayload.java` — Stub file
- `MinecraftUnregisterPayload.java` — Stub file
- `ModdedNetworkComponent.java` — Stub file
- `ModdedNetworkPayload.java` — Stub file
- `ModdedNetworkQueryComponent.java` — Stub file
- `ModdedNetworkQueryPayload.java` — Stub file
- `ModdedNetworkSetupFailedPayload.java` — Stub file
- `RegistryDataMapSyncPayload.java` — Stub file
- `SplitPacketPayload.java` — Stub file
- `SyncAttachmentsPayload.java` — Stub file
- `package-info.java` — Stub file

### neoforge/network/registration
- `ChannelAttributes.java` — Stub file
- `HandlerThread.java` — Stub file
- `ModdedConfigurationPayloadRegistration.java` — Stub file
- `ModdedPlayPayloadRegistration.java` — Stub file
- `NetworkChannel.java` — Stub file
- `NetworkPayloadSetup.java` — Stub file
- `NetworkRegistry.java` — Stub file
- `PayloadRegistrar.java` — Stub file
- `PayloadRegistration.java` — Stub file
- `package-info.java` — Stub file

### neoforge/registries
- `BaseMappedRegistry.java` — Stub file
- `ClientRegistryManager.java` — Stub file
- `DataMapLoader.java` — Stub file
- `DataPackRegistriesHooks.java` — Stub file
- `DataPackRegistryEvent.java` — Stub file
- `DeferredBlock.java` — Stub file
- `DeferredHolder.java` — Stub file
- `DeferredItem.java` — Stub file
- `DeferredRegister.java` — Stub file
- `GameData.java` — Stub file
- `IRegistryExtension.java` — Stub file
- `IdMappingEvent.java` — Stub file
- `ModifyRegistriesEvent.java` — Stub file
- `NeoForgeRegistries.java` — Stub file
- `NeoForgeRegistriesSetup.java` — Stub file
- `NeoForgeRegistryCallbacks.java` — Stub file
- `NewRegistryEvent.java` — Stub file
- `RegisterEvent.java` — Stub file
- `RegistryBuilder.java` — Stub file
- `RegistryManager.java` — Stub file
- `RegistrySnapshot.java` — Stub file

### neoforge/registries/callback
- `AddCallback.java` — Stub file
- `BakeCallback.java` — Stub file
- `ClearCallback.java` — Stub file
- `RegistryCallback.java` — Stub file
- `package-info.java` — Stub file

### neoforge/registries/datamaps
- `AdvancedDataMapType.java` — Stub file
- `DataMapEntry.java` — Stub file
- `DataMapFile.java` — Stub file
- `DataMapType.java` — Stub file
- `DataMapValueMerger.java` — Stub file
- `DataMapValueRemover.java` — Stub file
- `DataMapsUpdatedEvent.java` — Stub file
- `IWithData.java` — Stub file
- `RegisterDataMapTypesEvent.java` — Stub file

### neoforge/registries/datamaps/builtin
- `BiomeVillagerType.java` — Stub file
- `Compostable.java` — Stub file
- `FurnaceFuel.java` — Stub file
- `MonsterRoomMob.java` — Stub file
- `NeoForgeDataMaps.java` — Stub file
- `Oxidizable.java` — Stub file
- `ParrotImitation.java` — Stub file
- `RaidHeroGift.java` — Stub file
- `Strippable.java` — Stub file
- `VibrationFrequency.java` — Stub file
- `Waxable.java` — Stub file
- `package-info.java` — Stub file

### neoforge/registries/holdersets
- `AndHolderSet.java` — Stub file
- `AnyHolderSet.java` — Stub file
- `CompositeHolderSet.java` — Stub file
- `HolderSetType.java` — Stub file
- `ICustomHolderSet.java` — Stub file
- `NotHolderSet.java` — Stub file
- `OrHolderSet.java` — Stub file
- `package-info.java` — Stub file

### neoforge/resource
- `ContextAwareReloadListener.java` — Stub file
- `EmptyPackResources.java` — Stub file
- `ResourcePackLoader.java` — Stub file
- `package-info.java` — Stub file

### neoforge/server
- `LanguageHook.java` — Stub file
- `ServerLifecycleHooks.java` — Stub file

### neoforge/server/command
- `CommandHelper.java` — Stub file
- `CommandUtils.java` — Stub file
- `ConfigCommand.java` — Stub file
- `DataComponentCommand.java` — Stub file
- `DimensionsCommand.java` — Stub file
- `DumpCommand.java` — Stub file
- `EntityCommand.java` — Stub file
- `EnumArgument.java` — Stub file
- `GenerateCommand.java` — Stub file
- `ModIdArgument.java` — Stub file
- `ModListCommand.java` — Stub file
- `NeoForgeCommand.java` — Stub file
- `TPSCommand.java` — Stub file
- `TagsCommand.java` — Stub file
- `TimeSpeedCommand.java` — Stub file
- `TrackCommand.java` — Stub file

### neoforge/server/command/generation
- `CoarseOnionIterator.java` — Stub file
- `GenerationBar.java` — Stub file
- `GenerationTask.java` — Stub file
- `OnionIterator.java` — Stub file
- `package-info.java` — Stub file

### neoforge/server/console
- `ConsoleCommandCompleter.java` — Stub file
- `TerminalHandler.java` — Stub file
- `package-info.java` — Stub file

### neoforge/server/loading
- `ServerModLoader.java` — Stub file
- `package-info.java` — Stub file

### neoforge/server/permission
- `PermissionAPI.java` — Stub file

### neoforge/server/permission/events
- `PermissionGatherEvent.java` — Stub file
- `package-info.java` — Stub file

### neoforge/server/permission/exceptions
- `UnregisteredPermissionException.java` — Stub file
- `package-info.java` — Stub file

### neoforge/server/permission/handler
- `DefaultPermissionHandler.java` — Stub file
- `IPermissionHandler.java` — Stub file
- `IPermissionHandlerFactory.java` — Stub file
- `package-info.java` — Stub file

### neoforge/server/permission/nodes
- `PermissionDynamicContext.java` — Stub file
- `PermissionDynamicContextKey.java` — Stub file
- `PermissionNode.java` — Stub file
- `PermissionType.java` — Stub file
- `PermissionTypes.java` — Stub file
- `package-info.java` — Stub file

### neoforge/server/timings
- `ObjectTimings.java` — Stub file
- `TimeTracker.java` — Stub file
- `package-info.java` — Stub file

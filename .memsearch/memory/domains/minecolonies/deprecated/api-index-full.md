---
status: deprecated
superseded_by: api-index-full.md
---

﻿**Title:** MineColonies — full API index (625 Java source files, all packages)
**Type:** fact
**Intent triggers:** MineColonies API index, colony, buildings, citizens, jobs, requestsystem, research, crafting, entity, quests, advancements, compatibility, configuration, permissions, workorders, modules, events, pathfinding, equipment, guardtype
**Source/evidence:** docs/MINECOLONIES_API_INDEX.md — generated from Desktop/minecolonies git repo (branch version/1.21, tag v1.21.1-1.1.1320-SNAPSHOT) via tools/rebuild-neoforge-api-index.js --no-extract
# MineColonies API Index

Source: `Desktop/minecolonies (branch version/1.21, tag v1.21.1-1.1.1320-SNAPSHOT)`
Generated from MineColonies 1.1.1320-SNAPSHOT source files.

Use this index to identify the specific source reference file to load from `docs/api/minecolonies`.

Indexed Java files: 625

## (root)

- `IMinecoloniesAPI.java` â€” Entry point for minecolonies APIs
- `MinecoloniesAPIProxy.java` â€” Class for minecolonies apiproxy

## advancements

- `advancements/AdvancementTriggers.java` â€” The collection of advancement triggers for minecolonies
- `advancements/AllTowersTrigger.java` â€” Triggered when all barracks towers have been fully upgraded on any one barracks
- `advancements/ArmyPopulationTrigger.java` â€” Triggered on allocation of new soldiers
- `advancements/BuildingAddRecipeTrigger.java` â€” Triggered whenever a new recipe has been set in any building
- `advancements/CitizenBuryTrigger.java` â€” A Trigger that is triggered when a citizen is buried in a graveyard
- `advancements/CitizenEatFoodTrigger.java` â€” Class for citizen eat food trigger
- `advancements/CitizenResurrectTrigger.java` â€” A Trigger that is triggered when the miner reaches a certain depth
- `advancements/ClickGuiButtonTrigger.java` â€” Class for click gui button trigger
- `advancements/ColonyPopulationTrigger.java` â€” Class for colony population trigger
- `advancements/CompleteBuildRequestTrigger.java` â€” Class for complete build request trigger
- `advancements/CreateBuildRequestTrigger.java` â€” A Trigger for any building request that gets made
- `advancements/DeepMineTrigger.java` â€” A Trigger that is triggered when the miner reaches a certain depth
- `advancements/MaxFieldsTrigger.java` â€” Is triggered when the maximum number of fields has been allocated to a single farmer
- `advancements/OpenGuiWindowTrigger.java` â€” Triggered when a blockui window is opened
- `advancements/PlaceStructureTrigger.java` â€” Triggers whenever the build tool is used to position a new structure
- `advancements/PlaceSupplyTrigger.java` â€” Triggered when a supply camp or supply ship has been placed
- `advancements/UndertakerTotemTrigger.java` â€” A Trigger that is triggered when the undertaker recieves a totem of undying

## blocks

- `blocks/AbstractBlockBarrel.java` â€” Class for abstract block barrel
- `blocks/AbstractBlockHut.java` â€” Base class for all Minecolonies Hut Blocks
- `blocks/AbstractBlockMinecolonies.java` â€” Class for abstract block minecolonies
- `blocks/AbstractBlockMinecoloniesContainer.java` â€” Class AbstractBlockMinecoloniesContainer
- `blocks/AbstractBlockMinecoloniesDirectional.java` â€” Class for abstract block minecolonies directional
- `blocks/AbstractBlockMinecoloniesFalling.java` â€” Class for abstract block minecolonies falling
- `blocks/AbstractBlockMinecoloniesGrave.java` â€” Class for abstract block minecolonies grave
- `blocks/AbstractBlockMinecoloniesHorizontal.java` â€” Class for abstract block minecolonies horizontal
- `blocks/AbstractBlockMinecoloniesNamedGrave.java` â€” Abstract class for minecolonies named graves
- `blocks/AbstractBlockMinecoloniesRack.java` â€” Class for abstract block minecolonies rack
- `blocks/AbstractColonyBlock.java` â€” Base class for all blocks that have a functionality within a colony
- `blocks/ModBlocks.java` â€” Class to create the modBlocks

## blocks/decorative

- `blocks/decorative/AbstractBlockGate.java` â€” Block as one big door
- `blocks/decorative/AbstractBlockMinecoloniesConstructionTape.java` â€” Class for abstract block minecolonies construction tape
- `blocks/decorative/AbstractColonyFlagBanner.java` â€” Represents the common functions of both the wall and floor colony flag banner blocks

## blocks/huts

- `blocks/huts/AbstractBlockMinecoloniesDefault.java` â€” Class for abstract block minecolonies default

## blocks/interfaces

- `blocks/interfaces/IBlockMinecolonies.java` â€” Interface for block minecolonies
- `blocks/interfaces/IBuildingBrowsableBlock.java` â€” Right-clicking this block in the air triggers the building browser window interface
- `blocks/interfaces/IRSComponentBlock.java` â€” Empty interface that indicates the Block is actually just a single block, not a building with an schematic
- `blocks/interfaces/ITickableBlockMinecolonies.java` â€” Interface for tickable block minecolonies

## blocks/types

- `blocks/types/BarrelType.java` â€” Enum describing barrel types
- `blocks/types/GraveType.java` â€” Defines the types of Grave that the AbstractBlockMinecoloniesGrave supports
- `blocks/types/RackType.java` â€” Defines the types of Racks that the AbstractBlockMinecoloniesRack supports

## client

- `client/ModKeyMappings.java` â€” Key mappings

## client/render/modeltype

- `client/render/modeltype/AmazonModel.java` â€” Amazon model
- `client/render/modeltype/CitizenModel.java` â€” Citizen model
- `client/render/modeltype/EgyptianModel.java` â€” Egyptian model
- `client/render/modeltype/IModelType.java` â€” Defines a model type and its textures
- `client/render/modeltype/ISimpleModelType.java` â€” Interface describing simple model types
- `client/render/modeltype/ModModelTypes.java` â€” Class for mod model types
- `client/render/modeltype/NorsemenModel.java` â€” Norsemen model
- `client/render/modeltype/SimpleModelType.java` â€” A class that implements the ISimpleModelType interface

## client/render/modeltype/registry

- `client/render/modeltype/registry/IModelTypeRegistry.java` â€” The registry interface for model types

## colony

- `colony/CitizenNameFile.java` â€” The citizen name file of a specific style of names
- `colony/ColonyProgressType.java` â€” Contains all possible progress events
- `colony/ColonyState.java` â€” THe states a colony can be in
- `colony/CompactColonyReference.java` â€” Compact colony for allies and feud data
- `colony/GraveData.java` â€” Container for all the grave data
- `colony/IAnimalData.java` â€” Data interface for animals managed by the Animal Manager
- `colony/ICitizen.java` â€” Higher level Citizen data (Englobes server and client side variants)
- `colony/ICitizenData.java` â€” Interface for citizen data
- `colony/ICitizenDataManager.java` â€” Manages access to citizen data types
- `colony/ICitizenDataView.java` â€” Interface for citizen data view
- `colony/ICivilianData.java` â€” Data for all civilians of a colony, can be citizen/trader/visitor etc
- `colony/IColony.java` â€” Interface of the Colony and ColonyView which will have to implement the following methods
- `colony/IColonyManager.java` â€” Interface for colony manager
- `colony/IColonyRelated.java` â€” Interface type for entities belonging to a colony
- `colony/IColonyView.java` â€” Interface for colony view
- `colony/IGraveData.java` â€” Data to store in a citizen grave
- `colony/IVisitorData.java` â€” Data for colony visitors, based on citizendata
- `colony/IVisitorViewData.java` â€” View data for visitors

## colony/buildingextensions

- `colony/buildingextensions/IBuildingExtension.java` â€” Interface for building extension instances

## colony/buildingextensions/modules

- `colony/buildingextensions/modules/IBuildingExtensionModule.java` â€” Default interface for all building extension modules

## colony/buildingextensions/plantation

- `colony/buildingextensions/plantation/IPlantationModule.java` â€” Interface for planter modules that determines how the AI should work specific fields

## colony/buildingextensions/registry

- `colony/buildingextensions/registry/BuildingExtensionRegistries.java` â€” Registry implementation for building extension instances

## colony/buildings

- `colony/buildings/HiringMode.java` â€” Different hiring mode of buildings
- `colony/buildings/IBuilding.java` â€” Interface for building
- `colony/buildings/IBuildingContainer.java` â€” Interface for building container
- `colony/buildings/IBuildingWorker.java` â€” Interface for building worker
- `colony/buildings/IBuildingWorkerModule.java` â€” Interface for building worker module
- `colony/buildings/IBuildingWorkerView.java` â€” Interface for building worker view
- `colony/buildings/ICommonBuilding.java` â€” Common building interface for both client & server
- `colony/buildings/IGuardBuilding.java` â€” Interface for guard building
- `colony/buildings/IMysticalSite.java` â€” Interface for mystical site
- `colony/buildings/IRSComponent.java` â€” Empty interface that indicates the Building is actually just a single block
- `colony/buildings/ISchematicProvider.java` â€” Interface for schematic provider
- `colony/buildings/ModBuildings.java` â€” Class for mod buildings

## colony/buildings/modules

- `colony/buildings/modules/AbstractBuildingModule.java` â€” Abstract class for all modules
- `colony/buildings/modules/AbstractBuildingModuleView.java` â€” Abstract class for all modules
- `colony/buildings/modules/IAltersBuildingFootprint.java` â€” Interface for buildings with an extended footprint
- `colony/buildings/modules/IAltersRequiredItems.java` â€” Module type to register specific blocks to a building (beds, workstations, etc)
- `colony/buildings/modules/IAssignmentModuleView.java` â€” Assignment module view interface
- `colony/buildings/modules/IAssignsCitizen.java` â€” Interface for all modules that need special assignment handling
- `colony/buildings/modules/IAssignsJob.java` â€” Interface for all modules that need special assignment handling
- `colony/buildings/modules/IBuildingEventsModule.java` â€” Module interface for all building based events
- `colony/buildings/modules/IBuildingModule.java` â€” Default interface for all building modules
- `colony/buildings/modules/IBuildingModuleView.java` â€” Default interface for all client side building modules
- `colony/buildings/modules/ICommonSettingsModule.java` â€” Common (sideless) Settings module interface
- `colony/buildings/modules/ICraftingBuildingModule.java` â€” This module represents the ability for a building to generate items via some form of crafting, whether vanilla,...
- `colony/buildings/modules/ICreatesResolversModule.java` â€” Interface for modules that creates resolvers
- `colony/buildings/modules/IDefinesCoreBuildingStatsModule.java` â€” Interface describing core building stats
- `colony/buildings/modules/IEntityListModule.java` â€” Module for ignore/acceptance lists of entities
- `colony/buildings/modules/IEntityListModuleView.java` â€” Client side version of the abstract class for all buildings which require a filterable list of allowed items
- `colony/buildings/modules/IHasRequiredItemsModule.java` â€” Module defining items to be left behind and not used otherwise
- `colony/buildings/modules/IItemListModule.java` â€” Module for ignore/acceptance lists of items
- `colony/buildings/modules/IItemListModuleView.java` â€” Client side version of the abstract class for all buildings which require a filterable list of allowed items
- `colony/buildings/modules/IMinimumStockModule.java` â€” Module for adding minimum stocks to buildings
- `colony/buildings/modules/IMinimumStockModuleView.java` â€” Client side version of the abstract class for all buildings which require a filterable list of allowed items
- `colony/buildings/modules/IModuleWithExternalBlocks.java` â€” Module type to register specific blocks to a building (beds, workstations, etc)
- `colony/buildings/modules/IPersistentModule.java` â€” Interface for all building modules that store additional data
- `colony/buildings/modules/ISettingsModule.java` â€” Settings module interface
- `colony/buildings/modules/ITickingModule.java` â€” For all modules that require colony ticks

## colony/buildings/modules/settings

- `colony/buildings/modules/settings/IBlockSettingFactory.java` â€” Interface for the boolean settings factory which is responsible for creating and maintaining bool setting objects
- `colony/buildings/modules/settings/IBoolSettingFactory.java` â€” Interface for the boolean settings factory which is responsible for creating and maintaining bool setting objects
- `colony/buildings/modules/settings/ICraftingSetting.java` â€” Crafting Setting
- `colony/buildings/modules/settings/IIntSettingFactory.java` â€” Interface for the integer settings factory which is responsible for creating and maintaining int setting objects
- `colony/buildings/modules/settings/IRecipeSettingFactory.java` â€” Interface for the recipe setting factory
- `colony/buildings/modules/settings/ISetting.java` â€” Generic ISetting that represents all possible setting objects (string, numbers, boolean, etc)
- `colony/buildings/modules/settings/ISettingKey.java` â€” Key type for settings
- `colony/buildings/modules/settings/ISettingsModuleView.java` â€” Client side part of the settings module
- `colony/buildings/modules/settings/IStringSetting.java` â€” String Setting
- `colony/buildings/modules/settings/IStringSettingFactory.java` â€” Interface for the enum settings factory which is responsible for creating and maintaining enum setting objects

## colony/buildings/modules/stat

- `colony/buildings/modules/stat/IStat.java` â€” A specific stat

## colony/buildings/registry

- `colony/buildings/registry/BuildingEntry.java` â€” Entry for the IBuilding registry
- `colony/buildings/registry/IBuildingDataManager.java` â€” Helper manager to analyse and process the registry for BuildingEntry
- `colony/buildings/registry/IBuildingRegistry.java` â€” Interface for building registration

## colony/buildings/views

- `colony/buildings/views/IBuildingView.java` â€” Interface for building view
- `colony/buildings/views/IModuleContainerView.java` â€” Interface for module container view

## colony/buildings/workerbuildings

- `colony/buildings/workerbuildings/IBuildingDeliveryman.java` â€” Marker interface for deliveryman buildings
- `colony/buildings/workerbuildings/ITownHall.java` â€” Interface for town hall
- `colony/buildings/workerbuildings/ITownHallView.java` â€” Interface for town hall view
- `colony/buildings/workerbuildings/IWareHouse.java` â€” Interface for ware house

## colony/claim

- `colony/claim/ChunkClaimData.java` â€” The implementation of the colonyTagCapability
- `colony/claim/IChunkClaimData.java` â€” Capability for the colony tag for chunks

## colony/colonyEvents

- `colony/colonyEvents/EventStatus.java` â€” Status enum for colony events
- `colony/colonyEvents/IColonyCampFireRaidEvent.java` â€” Raid event with campfires for delayed start
- `colony/colonyEvents/IColonyEntitySpawnEvent.java` â€” A colony event which spawns and uses entities
- `colony/colonyEvents/IColonyEvent.java` â€” Interface for all colony event types
- `colony/colonyEvents/IColonyRaidEvent.java` â€” Interface type for raid events
- `colony/colonyEvents/IColonySpawnEvent.java` â€” An colony event which spawns at a certain position
- `colony/colonyEvents/IColonyStructureSpawnEvent.java` â€” Used by events which do spawn a structure in the world

## colony/colonyEvents/descriptions

- `colony/colonyEvents/descriptions/IBuildingEventDescription.java` â€” Event description for building events
- `colony/colonyEvents/descriptions/ICitizenEventDescription.java` â€” Event description for citizen spawn/death events
- `colony/colonyEvents/descriptions/IColonyEventDescription.java` â€” Description for an event that happened in the colony

## colony/colonyEvents/registry

- `colony/colonyEvents/registry/ColonyEventDescriptionTypeRegistryEntry.java` â€” The colonies event registry entry class, used for registering any colony related events
- `colony/colonyEvents/registry/ColonyEventTypeRegistryEntry.java` â€” The colonies event registry entry class, used for registering any colony related events

## colony/connections

- `colony/connections/ColonyConnection.java` â€” Small storage class to hold colony connection data
- `colony/connections/ColonyConnectionNode.java` â€” Node in the path from one colony to another
- `colony/connections/ConnectionEvent.java` â€” Connected Event Data with:
- `colony/connections/ConnectionEventType.java` â€” Diplomacy Status between two colonies
- `colony/connections/DiplomacyStatus.java` â€” Diplomacy Status between two colonies
- `colony/connections/IColonyConnectionManager.java` â€” Connection manager interface
- `colony/connections/PendingConnectionNode.java` â€” Pending connected colony data while pathfinding is still trying to connect

## colony/guardtype

- `colony/guardtype/GuardType.java` â€” Guard type class

## colony/guardtype/registry

- `colony/guardtype/registry/IGuardTypeDataManager.java` â€” Interface describing guard  data manager types
- `colony/guardtype/registry/IGuardTypeRegistry.java` â€” Interface for guard type registration
- `colony/guardtype/registry/ModGuardTypes.java` â€” Class for mod guard types

## colony/interactionhandling

- `colony/interactionhandling/AbstractInteractionResponseHandler.java` â€” The abstract interaction response handler to be extended by the other ones
- `colony/interactionhandling/ChatPriority.java` â€” Different priority types of the interactions
- `colony/interactionhandling/IChatPriority.java` â€” Interface for chat priority
- `colony/interactionhandling/IInteractionResponseHandler.java` â€” Response handler for all kind of GUI interactions
- `colony/interactionhandling/InteractionValidatorRegistry.java` â€” Utility class to store all validator predicates for the chat handling
- `colony/interactionhandling/ModInteractionResponseHandlers.java` â€” List of mod interaction handlers

## colony/interactionhandling/registry

- `colony/interactionhandling/registry/IInteractionResponseHandlerDataManager.java` â€” The data manager of the interaction handler
- `colony/interactionhandling/registry/InteractionResponseHandlerEntry.java` â€” Entry for the IInteractionResponseHandler registry

## colony/jobs

- `colony/jobs/IJob.java` â€” Interface for job
- `colony/jobs/IJobView.java` â€” Interface for job view
- `colony/jobs/IJobWithColonyFlag.java` â€” Interface for workers that make use of the colony flag (e.g
- `colony/jobs/IJobWithExternalWorkStations.java` â€” Interface for workers that have an additional workstations besides their hut (e.g., the Quarrier) This allows to treat...
- `colony/jobs/ModJobs.java` â€” Class for mod jobs

## colony/jobs/registry

- `colony/jobs/registry/IJobDataManager.java` â€” Interface for job data manager
- `colony/jobs/registry/IJobRegistry.java` â€” Interface for job registration
- `colony/jobs/registry/JobEntry.java` â€” Entry for the IJob registry

## colony/managers/interfaces

- `colony/managers/interfaces/IAnimalDataView.java` â€” Interface for animal data view
- `colony/managers/interfaces/IAnimalManager.java` â€” The interface for managed animals, such as Cavalry horses
- `colony/managers/interfaces/ICitizenManager.java` â€” The interface of the citizen manager
- `colony/managers/interfaces/IColonyPackageManager.java` â€” Colony package manager, responsible to update views etc
- `colony/managers/interfaces/ICommonRegisteredStructureManager.java` â€” Interface for the managers for registered structures
- `colony/managers/interfaces/IEntityManager.java` â€” Manager interface for managing entities for a colony
- `colony/managers/interfaces/IEventDescriptionManager.java` â€” Interface for the event description manager, the event description manager deals the colony event log events
- `colony/managers/interfaces/IEventManager.java` â€” Interface for the event manager, the event manager deals with all colony related events, such as raid events
- `colony/managers/interfaces/IEventStructureManager.java` â€” Interface for the Event structure manager The manager takes care of structures spawned for events, takes a backup...
- `colony/managers/interfaces/IGraveManager.java` â€” Interface for grave managers
- `colony/managers/interfaces/IManagedAnimal.java` â€” Interface for managed animal
- `colony/managers/interfaces/IRaiderManager.java` â€” Interface implementing all methods required for all raider managers
- `colony/managers/interfaces/IRegisteredStructureManager.java` â€” Interface for the managers for registered structures
- `colony/managers/interfaces/IReproductionManager.java` â€” Reproduction manager for colony wide reproduction (100% family friendly code)
- `colony/managers/interfaces/IStatisticsManager.java` â€” Interface for the statistics manager
- `colony/managers/interfaces/ITravellingManager.java` â€” Manages the traveling system for a given colony
- `colony/managers/interfaces/IVisitorManager.java` â€” Visitor manager to manage visiting entities

## colony/managers/interfaces/views

- `colony/managers/interfaces/views/IRegisteredStructureManagerView.java` â€” Interface for registered structure manager view

## colony/modules

- `colony/modules/IBuildingModuleContainer.java` â€” Module container for buildings
- `colony/modules/IModuleContainer.java` â€” Default interface for objects that contain module instances

## colony/permissions

- `colony/permissions/Action.java` â€” Actions that can be performed in a colony
- `colony/permissions/ColonyPlayer.java` â€” Player within a colony
- `colony/permissions/Explosions.java` â€” Enum for explosions
- `colony/permissions/IPermissions.java` â€” Permission interface
- `colony/permissions/OldRank.java` â€” Ranks within a colony
- `colony/permissions/PermissionEvent.java` â€” Permission event class, used to store events happening in the colony
- `colony/permissions/Rank.java` â€” Class for rank

## colony/requestsystem

- `colony/requestsystem/StandardFactoryController.java` â€” Default implementation of a FactoryController Singleton

## colony/requestsystem/data

- `colony/requestsystem/data/IAssignmentDataStore.java` â€” A Key-Value-MultiMap Store that handles assignments from a Value to a Key
- `colony/requestsystem/data/IDataStore.java` â€” Core class that describes the datastores that are part of the RS
- `colony/requestsystem/data/IDataStoreManager.java` â€” Interface for data store manager
- `colony/requestsystem/data/IIdentitiesDataStore.java` â€” IDataStore definition for a KeyValue-Store
- `colony/requestsystem/data/IProviderResolverAssignmentDataStore.java` â€” Interface for provider resolver assignment data store
- `colony/requestsystem/data/IRequestableTypeRequestResolverAssignmentDataStore.java` â€” Interface describing requestable  request resolver assignment data store types
- `colony/requestsystem/data/IRequestIdentitiesDataStore.java` â€” The KV-Store for the requests and their identities
- `colony/requestsystem/data/IRequestResolverIdentitiesDataStore.java` â€” The KV-Store for the requests and their identities
- `colony/requestsystem/data/IRequestResolverRequestAssignmentDataStore.java` â€” Interface for request resolver request assignment data store
- `colony/requestsystem/data/IRequestSystemBuildingDataStore.java` â€” Interface for request system building data store
- `colony/requestsystem/data/IRequestSystemCrafterJobDataStore.java` â€” Interface defining the datastore for crafters
- `colony/requestsystem/data/IRequestSystemDeliveryManJobDataStore.java` â€” Specific datastore for couriers
- `colony/requestsystem/data/ITokenTokenAssignmentDataStore.java` â€” KV-Collection-Store for IToken to IToken

## colony/requestsystem/factory

- `colony/requestsystem/factory/FactoryVoidInput.java` â€” Class for used when a Factory does not require any input to produce an output
- `colony/requestsystem/factory/IFactory.java` â€” Interface used to describe factories
- `colony/requestsystem/factory/IFactoryController.java` â€” Interface used to describe classes that function as Factory controllers
- `colony/requestsystem/factory/ITypeOverrideHandler.java` â€” Interface for type override handling

## colony/requestsystem/factory/standard

- `colony/requestsystem/factory/standard/IntegerFactory.java` â€” Class for creating integer instances
- `colony/requestsystem/factory/standard/TypeTokenFactory.java` â€” Class for creating type token instances

## colony/requestsystem/location

- `colony/requestsystem/location/ILocatable.java` â€” Interface describing objects that are locatable
- `colony/requestsystem/location/ILocation.java` â€” Interface used to describe locations in the world
- `colony/requestsystem/location/ILocationFactory.java` â€” Marker interface used to specify a factory for locations

## colony/requestsystem/management

- `colony/requestsystem/management/IProviderHandler.java` â€” Interface for provider handling
- `colony/requestsystem/management/IRequestHandler.java` â€” Interface for request handling
- `colony/requestsystem/management/IResolverHandler.java` â€” Interface for resolver handling
- `colony/requestsystem/management/ITokenHandler.java` â€” Interface for token handling
- `colony/requestsystem/management/IUpdateHandler.java` â€” Interface for update handling

## colony/requestsystem/management/update

- `colony/requestsystem/management/update/UpdateType.java` â€” Update types (reset, or onLoad)

## colony/requestsystem/manager

- `colony/requestsystem/manager/AssigningStrategy.java` â€” Enum determining the assigning strategy
- `colony/requestsystem/manager/IRequestManager.java` â€” Interface used to describe classes that function as managers for requests inside a colony
- `colony/requestsystem/manager/RequestMappingHandler.java` â€” Class used to manage IRequestable to IRequest mappings

## colony/requestsystem/request

- `colony/requestsystem/request/IRequest.java` â€” Used to represent requests, of type R, made to the internal market of the colony
- `colony/requestsystem/request/IRequestFactory.java` â€” Marker interface used to specify a factory for requests
- `colony/requestsystem/request/RequestState.java` â€” Enum used to describe the state of a Request
- `colony/requestsystem/request/RequestUtils.java` â€” Util class for requests

## colony/requestsystem/requestable

- `colony/requestsystem/requestable/Burnable.java` â€” Burnable requestable
- `colony/requestsystem/requestable/Food.java` â€” Eatable requestable
- `colony/requestsystem/requestable/IConcreteDeliverable.java` â€” An IConcreteDeliverable is an Requestable that can be looked up fast in the warehouse, and delivered
- `colony/requestsystem/requestable/IDeliverable.java` â€” An IDeliverable is an Requestable that can be delivered
- `colony/requestsystem/requestable/INonExhaustiveDeliverable.java` â€” An INonExhaustiveDeliverable is an Requestable that can be looked up fast in the warehouse, and delivered
- `colony/requestsystem/requestable/IRequestable.java` â€” Marker interface for requestable objects
- `colony/requestsystem/requestable/IRetryable.java` â€” Marker interface for requests that should be retried when they initially failed a couple of seconds later
- `colony/requestsystem/requestable/IStackBasedTask.java` â€” Stack based requests interface for display purposes
- `colony/requestsystem/requestable/MinimumStack.java` â€” Minimum stack request type
- `colony/requestsystem/requestable/RequestTag.java` â€” Deliverable that can only be fulfilled by a stack whos item is contained in a given tag with...
- `colony/requestsystem/requestable/Stack.java` â€” Deliverable that can only be fulfilled by a single stack with a given minimal amount of items
- `colony/requestsystem/requestable/StackList.java` â€” Deliverable that can only be fulfilled by a single stack with a given minimal amount of items matching...
- `colony/requestsystem/requestable/Tool.java` â€” Class used to represent equipment inside the request system

## colony/requestsystem/requestable/crafting

- `colony/requestsystem/requestable/crafting/AbstractCrafting.java` â€” Abstract crafting request
- `colony/requestsystem/requestable/crafting/PrivateCrafting.java` â€” Class for private crafting
- `colony/requestsystem/requestable/crafting/PublicCrafting.java` â€” Class for public crafting

## colony/requestsystem/requestable/deliveryman

- `colony/requestsystem/requestable/deliveryman/AbstractDeliverymanRequestable.java` â€” Abstract class for all deliveryman-requests
- `colony/requestsystem/requestable/deliveryman/Delivery.java` â€” Class used to represent deliveries inside the request system
- `colony/requestsystem/requestable/deliveryman/IDeliverymanRequestable.java` â€” Marker interface for requestables handled by deliverymen
- `colony/requestsystem/requestable/deliveryman/Pickup.java` â€” Class used to represent pickups inside the request system

## colony/requestsystem/requester

- `colony/requestsystem/requester/IRequester.java` â€” Interface that describes an object that can be located in the Minecraft universe and can request objects inside...
- `colony/requestsystem/requester/IRequesterFactory.java` â€” Interface describing objects that can construct IRequester objects

## colony/requestsystem/resolver

- `colony/requestsystem/resolver/IQueuedRequestResolver.java` â€” Interface for queued request resolver
- `colony/requestsystem/resolver/IRequestResolver.java` â€” Used to resolve a request
- `colony/requestsystem/resolver/IRequestResolverFactory.java` â€” Interface describing an object that is capable of constructing a specific IRequestResolver
- `colony/requestsystem/resolver/IRequestResolverProvider.java` â€” Interface used to describe a class that provides resolvers

## colony/requestsystem/resolver/player

- `colony/requestsystem/resolver/player/IPlayerRequestResolver.java` â€” Interface for player request resolver

## colony/requestsystem/resolver/retrying

- `colony/requestsystem/resolver/retrying/IRetryingRequestResolver.java` â€” Interface for retrying request resolver

## colony/requestsystem/token

- `colony/requestsystem/token/AbstractTokenFactory.java` â€” An abstract implementation of the ITokenFactory interface that handles serialization etc
- `colony/requestsystem/token/InitializedTokenFactory.java` â€” IToken factory that produces an IToken from a random UUID
- `colony/requestsystem/token/IToken.java` â€” Interface used to represent a request outside of the request management system
- `colony/requestsystem/token/ITokenFactory.java` â€” Marker interface used to specify a factory for requesttokens
- `colony/requestsystem/token/RandomSeededTokenFactory.java` â€” Class for creating random seeded token instances
- `colony/requestsystem/token/StandardToken.java` â€” Internal implementation of the IToken interface
- `colony/requestsystem/token/StandardTokenFactory.java` â€” Factory for the standard request token, StandardToken

## colony/savedata

- `colony/savedata/IServerColonySaveData.java` â€” Capability for the colony tag for chunks
- `colony/savedata/ServerColonySaveData.java` â€” The implementation of the colonyTagCapability

## colony/workorders

- `colony/workorders/IBuilderWorkOrder.java` â€” Interface for builder work order
- `colony/workorders/IServerWorkOrder.java` â€” Interface for server work order
- `colony/workorders/IWorkManager.java` â€” Interface for work manager
- `colony/workorders/IWorkOrder.java` â€” Interface for work order
- `colony/workorders/IWorkOrderView.java` â€” Interface for work order view
- `colony/workorders/WorkOrderType.java` â€” Types of workorders

## compatibility

- `compatibility/Compatibility.java` â€” Class for to store the methods that call the methods to check for miscellaneous compatibility problems
- `compatibility/CompatibilityManager.java` â€” CompatibilityManager handling certain list and maps of itemStacks of certain types
- `compatibility/ICompatibilityManager.java` â€” Interface for all compatabilityManagers
- `compatibility/IFurnaceRecipes.java` â€” Interface for the new furnace recipes
- `compatibility/IJeiProxy.java` â€” An interface (and placeholder) to intercept callouts to JEI when it is not running

## compatibility/candb

- `compatibility/candb/AbstractChiselAndBitsProxy.java` â€” The fallback for when candb is not present!
- `compatibility/candb/ChiselAndBitsCheck.java` â€” Class for to store a check to see if a block is a chiselsandbits block

## compatibility/dynamictrees

- `compatibility/dynamictrees/DynamicTreeCompat.java` â€” Class for dynamic tree compat
- `compatibility/dynamictrees/DynamicTreeProxy.java` â€” The fallback for when dynamictrees is not present!

## compatibility/newstruct

- `compatibility/newstruct/BlueprintMapping.java` â€” The class that is responsible for the mapping of the old structurename/style to new style/structurename

## compatibility/resourcefulbees

- `compatibility/resourcefulbees/IBeehiveCompat.java` â€” Interface for beehive compat
- `compatibility/resourcefulbees/ResourcefulBeesCompat.java` â€” Class for resourceful bees compat

## compatibility/tinkers

- `compatibility/tinkers/SlimeTreeCheck.java` â€” Class for to store a check to see if a tree is a slime tree
- `compatibility/tinkers/SlimeTreeProxy.java` â€” The fallback for when tinkers is not present!
- `compatibility/tinkers/TinkersToolHelper.java` â€” Class to check if certain tinkers items serve as weapons for the guards
- `compatibility/tinkers/TinkersToolProxy.java` â€” Class to check if certain tinkers items serve as weapons for the guards

## configuration

- `configuration/ClientConfiguration.java` â€” Mod client configuration
- `configuration/CommonConfiguration.java` â€” Class for common configuration
- `configuration/ServerConfiguration.java` â€” Mod server configuration

## crafting

- `crafting/AbstractRecipeType.java` â€” Base class for RecipeStorage types
- `crafting/ClassicRecipe.java` â€” The Classic Recipe type
- `crafting/CompostRecipe.java` â€” A vanilla recipe class describing the operation of the compost barrel
- `crafting/CountedIngredient.java` â€” An ingredient that can be used in a vanilla recipe to require more than one item in a...
- `crafting/ExactMatchItemStorage.java` â€” Used to exact match stacks when storing them
- `crafting/GenericRecipe.java` â€” Standard implementation of IGenericRecipe
- `crafting/IGenericRecipe.java` â€” This is a generic recipe wrapper, used to abstract both potential IRecipes (with multiple alternative ingredient sets) and...
- `crafting/IImmutableItemStorageFactory.java` â€” Interface for the IItemStorageFactory which is responsible for creating and maintaining ItemStorage objects
- `crafting/IItemStorageFactory.java` â€” Interface for the IItemStorageFactory which is responsible for creating and maintaining ItemStorage objects
- `crafting/ImmutableItemStorage.java` â€” Immutable ItemStorage version
- `crafting/IRecipeManager.java` â€” The Interface describing the recipeManager which takes care of the recipes discovered by the colonies in this world
- `crafting/IRecipeStorage.java` â€” Interface which describes the RecipeStorage
- `crafting/IRecipeStorageFactory.java` â€” Interface for the IRecipeStorageFactory which is responsible for creating and maintaining RecipeStorage objects
- `crafting/ItemStorage.java` â€” Used to store an stack with various informations to compare items later on
- `crafting/ModCraftingTypes.java` â€” Class for mod crafting types
- `crafting/ModRecipeTypes.java` â€” Class for mod recipe types
- `crafting/MultiOutputRecipe.java` â€” The mult-output recipe type
- `crafting/RecipeCraftingType.java` â€” A CraftingType for the vanilla RecipeType
- `crafting/RecipeStorage.java` â€” Class used to represent a recipe in minecolonies
- `crafting/ZeroWasteRecipe.java` â€” A shapeless recipe that discards any remaining items

## crafting/registry

- `crafting/registry/CraftingType.java` â€” Class to represent the different types of crafting supported by MineColonies
- `crafting/registry/ModRecipeSerializer.java` â€” Holds ref to the mod recipe serializers and recipe types
- `crafting/registry/RecipeTypeEntry.java` â€” Entry for the AbstractRecipeType registry

## creativetab

- `creativetab/ModCreativeTabs.java` â€” Class used to handle the creativeTab of minecolonies

## enchants

- `enchants/ModEnchants.java` â€” All our mods renchants

## entity

- `entity/ModEntities.java` â€” Class for mod entities

## entity/ai

- `entity/ai/DesiredActivity.java` â€” Enum describing the citizens activity
- `entity/ai/IStateAI.java` â€” AI using our states
- `entity/ai/ITickingStateAI.java` â€” Interface for ticking AI's
- `entity/ai/JobStatus.java` â€” Enum for job status
- `entity/ai/Status.java` â€” Used for chat messages, sounds, and other need based interactions

## entity/ai/combat

- `entity/ai/combat/CombatAIStates.java` â€” Combat AI States

## entity/ai/combat/threat

- `entity/ai/combat/threat/IThreatTableEntity.java` â€” Entities implement this for the necessary hooks
- `entity/ai/combat/threat/ThreatTable.java` â€” Threat table class, basically a list of entities with an associated threat value
- `entity/ai/combat/threat/ThreatTableEntry.java` â€” Data entry in the threat table

## entity/ai/statemachine

- `entity/ai/statemachine/AIEventTarget.java` â€” Special AI Targets which are used for preState cecks and limits
- `entity/ai/statemachine/AIOneTimeEventTarget.java` â€” One time usage AITarget, unregisters itself after usage
- `entity/ai/statemachine/AITarget.java` â€” A simple target the AI tries to accomplish

## entity/ai/statemachine/basestatemachine

- `entity/ai/statemachine/basestatemachine/BasicEvent.java` â€” Basic event for statemachines, consists of a condition and a statesupplier to transition the statemachine into
- `entity/ai/statemachine/basestatemachine/BasicStateMachine.java` â€” Basic statemachine class, can be used for any Transition typed which extends the transition interface
- `entity/ai/statemachine/basestatemachine/BasicTransition.java` â€” Basic Transition class for statemachines
- `entity/ai/statemachine/basestatemachine/IStateMachine.java` â€” Statemachine interface, implement to add more statemachine types

## entity/ai/statemachine/states

- `entity/ai/statemachine/states/AIBlockingEventType.java` â€” Event types used in statemachine events
- `entity/ai/statemachine/states/AIWorkerState.java` â€” Basic state enclosing states all ai's use
- `entity/ai/statemachine/states/CitizenAIState.java` â€” AI States for citizen's state
- `entity/ai/statemachine/states/EntityState.java` â€” States of entity loading/activity
- `entity/ai/statemachine/states/IAIState.java` â€” Interface type for IAIState enums Implement this interface to add new statetypes
- `entity/ai/statemachine/states/IState.java` â€” The basic state type for statemachine states
- `entity/ai/statemachine/states/IStateEventType.java` â€” Interface type for all statemachine Event types

## entity/ai/statemachine/tickratestatemachine

- `entity/ai/statemachine/tickratestatemachine/IBooleanConditionSupplier.java` â€” Serializeable version of a boolean supplier for AI transitions, used for name generation
- `entity/ai/statemachine/tickratestatemachine/IStateSupplier.java` â€” Serializeable version of a supplier for AI states, used for name generation
- `entity/ai/statemachine/tickratestatemachine/ITickingTransition.java` â€” Interface for ticking transition
- `entity/ai/statemachine/tickratestatemachine/ITickRateStateMachine.java` â€” Interface for tick rate state machine
- `entity/ai/statemachine/tickratestatemachine/TickingEvent.java` â€” Event with a tickrate for a statemachine using a tickrate
- `entity/ai/statemachine/tickratestatemachine/TickingOneTimeEvent.java` â€” One time event that can be checked at the given tickrate, one time events are removed after causing...
- `entity/ai/statemachine/tickratestatemachine/TickingTransition.java` â€” Transition with tickrate logic, allows to define an intended tickrate at which this transition will be checked
- `entity/ai/statemachine/tickratestatemachine/TickRateConstants.java` â€” Constants for tickrate limited Transitions and statemachines
- `entity/ai/statemachine/tickratestatemachine/TickRateStateMachine.java` â€” Statemachine with an added tickrate limiting of transitions, allowing transitions to be checked at a lower rate

## entity/ai/statemachine/transitions

- `entity/ai/statemachine/transitions/IStateMachineEvent.java` â€” Event transition type for Statemachines
- `entity/ai/statemachine/transitions/IStateMachineOneTimeEvent.java` â€” Type for one time usage events
- `entity/ai/statemachine/transitions/IStateMachineTransition.java` â€” Transition type for Statemachines

## entity/ai/workers/util

- `entity/ai/workers/util/GuardGear.java` â€” Class to hold information about required item for the guard
- `entity/ai/workers/util/GuardGearBuilder.java` â€” Class for guard gear builder
- `entity/ai/workers/util/IBuilderUndestroyable.java` â€” Marker interface that is used to mark blocks that the Builder cannot break

## entity/citizen

- `entity/citizen/AbstractCivilianEntity.java` â€” Class for abstract civilian entity
- `entity/citizen/AbstractEntityCitizen.java` â€” The abstract citizen entity
- `entity/citizen/Skill.java` â€” All possible citizen skills with their complementaries and adversaries
- `entity/citizen/VisibleCitizenStatus.java` â€” Enum for citizen status icons, resource location and translation

## entity/citizen/citizenhandlers

- `entity/citizen/citizenhandlers/ICitizenColonyHandler.java` â€” Interface for citizen colony handling
- `entity/citizen/citizenhandlers/ICitizenDiseaseHandler.java` â€” Citizen disease handler interface
- `entity/citizen/citizenhandlers/ICitizenExperienceHandler.java` â€” Interface for citizen experience handling
- `entity/citizen/citizenhandlers/ICitizenFoodHandler.java` â€” Citizen food handler interface
- `entity/citizen/citizenhandlers/ICitizenHappinessHandler.java` â€” The citizen happiness handler interface
- `entity/citizen/citizenhandlers/ICitizenInventoryHandler.java` â€” Interface for citizen inventory handling
- `entity/citizen/citizenhandlers/ICitizenJobHandler.java` â€” Interface for citizen job handling
- `entity/citizen/citizenhandlers/ICitizenMournHandler.java` â€” The citizen happiness handler interface
- `entity/citizen/citizenhandlers/ICitizenSkillHandler.java` â€” The interface for the citizen skill handler
- `entity/citizen/citizenhandlers/ICitizenSleepHandler.java` â€” Interface for citizen sleep handling

## entity/citizen/happiness

- `entity/citizen/happiness/AbstractHappinessModifier.java` â€” Abstract happiness modifier implementation
- `entity/citizen/happiness/DynamicHappinessSupplier.java` â€” Dynamic Happiness supplier
- `entity/citizen/happiness/ExpirationBasedHappinessModifier.java` â€” The Expiration based happiness modifier
- `entity/citizen/happiness/HappinessRegistry.java` â€” Happiness forge registry to facilitate loading and saving to nbt
- `entity/citizen/happiness/IHappinessModifier.java` â€” Interface describing possible happiness factors
- `entity/citizen/happiness/IHappinessSupplierWrapper.java` â€” Wrapper to deal with happiness suppliers
- `entity/citizen/happiness/ITimeBasedHappinessModifier.java` â€” Interface describing possible happiness factors
- `entity/citizen/happiness/StaticHappinessModifier.java` â€” Static modifier that doesn't change
- `entity/citizen/happiness/StaticHappinessSupplier.java` â€” Static Happiness supplier
- `entity/citizen/happiness/TimeBasedHappinessModifier.java` â€” The time based happiness modifier

## entity/mobs

- `entity/mobs/AbstractEntityMinecoloniesMonster.java` â€” Abstract for all villain entities
- `entity/mobs/AbstractEntityMinecoloniesRaider.java` â€” Abstract for all raider entities
- `entity/mobs/IArcherMobEntity.java` â€” Marker interfaces for mobs that are archers
- `entity/mobs/IChiefMobEntity.java` â€” Interface for chief mob entity
- `entity/mobs/ICustomAttackSound.java` â€” Used in by RaiderRangedAI to denote that the project has a custom firing sound
- `entity/mobs/IMeleeMobEntity.java` â€” Interface for melee mob entity
- `entity/mobs/IRangedMobEntity.java` â€” Indicates the mob utilizes ranged weaponry
- `entity/mobs/ISpearmanMobEntity.java` â€” Marker interfaces for mobs that wield spears
- `entity/mobs/RaiderMobUtils.java` â€” Util class for raider mobs/spawning
- `entity/mobs/RaiderType.java` â€” All the raiders we got

## entity/mobs/amazons

- `entity/mobs/amazons/AbstractEntityAmazon.java` â€” Abstract for all amazon entities
- `entity/mobs/amazons/AbstractEntityAmazonRaider.java` â€” Abstract for all amazon entities
- `entity/mobs/amazons/IAmazonChief.java` â€” A tagging interface for Amazon Chief that is a IChiefMobEntity and an IArcherAmazon
- `entity/mobs/amazons/IAmazonEntity.java` â€” A tagging interface for Amazon Entities
- `entity/mobs/amazons/IAmazonSpearman.java` â€” A tagging interface for Amazon Spearman that is both an IAmazonEntity and an ISpearmanMobEntity
- `entity/mobs/amazons/IArcherAmazon.java` â€” A tagging interface for Amazon Archers that is both an IAmazonEntity and an IArcherMobEntity

## entity/mobs/barbarians

- `entity/mobs/barbarians/AbstractEntityBarbarian.java` â€” Abstract for all Barbarian entities
- `entity/mobs/barbarians/AbstractEntityBarbarianRaider.java` â€” Abstract for all Barbarian entities
- `entity/mobs/barbarians/IArcherBarbarianEntity.java` â€” Interface for archer barbarian entity
- `entity/mobs/barbarians/IBarbarianEntity.java` â€” Interface for barbarian entity
- `entity/mobs/barbarians/IChiefBarbarianEntity.java` â€” Interface for chief barbarian entity
- `entity/mobs/barbarians/IMeleeBarbarianEntity.java` â€” Interface for melee barbarian entity

## entity/mobs/drownedpirate

- `entity/mobs/drownedpirate/AbstractDrownedEntityPirate.java` â€” Abstract for all drowned pirate entities
- `entity/mobs/drownedpirate/AbstractDrownedEntityPirateRaider.java` â€” Abstract for all drowned pirate entities

## entity/mobs/egyptians

- `entity/mobs/egyptians/AbstractEntityEgyptian.java` â€” Abstract for all egyptian entities
- `entity/mobs/egyptians/AbstractEntityEgyptianRaider.java` â€” Abstract for all egyptian entities
- `entity/mobs/egyptians/IArcherMummyEntity.java` â€” Interface for archer mummy entity
- `entity/mobs/egyptians/IEgyptianEntity.java` â€” Interface for egyptian entity
- `entity/mobs/egyptians/IMeleeMummyEntity.java` â€” Melee mummy interface
- `entity/mobs/egyptians/IPharaoEntity.java` â€” Interface for pharao entity

## entity/mobs/pirates

- `entity/mobs/pirates/AbstractEntityPirate.java` â€” Abstract for all pirate entities
- `entity/mobs/pirates/AbstractEntityPirateRaider.java` â€” Abstract for all pirate entities
- `entity/mobs/pirates/IArcherPirateEntity.java` â€” Interface for archer pirate entity
- `entity/mobs/pirates/ICaptainPirateEntity.java` â€” Interface for captain pirate entity
- `entity/mobs/pirates/IMeleePirateEntity.java` â€” Interface for melee pirate entity
- `entity/mobs/pirates/IPirateEntity.java` â€” Interface for pirate entity

## entity/mobs/registry

- `entity/mobs/registry/IMobAIRegistry.java` â€” Interface for mob airegistry registration

## entity/mobs/vikings

- `entity/mobs/vikings/AbstractEntityNorsemen.java` â€” Abstract for all norsemen entities
- `entity/mobs/vikings/AbstractEntityNorsemenRaider.java` â€” Abstract for all norsemen entities
- `entity/mobs/vikings/IArcherNorsemenEntity.java` â€” Archer norsemen entity interface
- `entity/mobs/vikings/IMeleeNorsemenEntity.java` â€” Melee norsemen entity interface
- `entity/mobs/vikings/INorsemenChiefEntity.java` â€” Chief norsemen entity interface
- `entity/mobs/vikings/INorsemenEntity.java` â€” Interface for norsemen entity

## entity/other

- `entity/other/AbstractFastMinecoloniesEntity.java` â€” Special abstract minecolonies mob that overrides laggy vanilla behaviour
- `entity/other/MinecoloniesMinecart.java` â€” Special minecolonies minecart that doesn't collide

## entity/pathfinding

- `entity/pathfinding/IDynamicHeuristicNavigator.java` â€” Interface for navigators which keep an internal heuristic mod
- `entity/pathfinding/IMinecoloniesNavigator.java` â€” Describes the Navigator used by minecolonies entities
- `entity/pathfinding/IPathJob.java` â€” Interface for path jobs
- `entity/pathfinding/IStuckHandler.java` â€” Stuck handler for pathing, gets called to check/deal with stuck status
- `entity/pathfinding/IStuckHandlerEntity.java` â€” Implemented by entities the stuck handler takes care of, used to ask the AI if it possibly could...

## entity/pathfinding/proxy

- `entity/pathfinding/proxy/IWalkToProxy.java` â€” Interface which defines the walkToProxy

## entity/pathfinding/registry

- `entity/pathfinding/registry/IPathNavigateRegistry.java` â€” Interface for path navigate registration

## equipment

- `equipment/ModEquipmentTypes.java` â€” Class used for storing and registering any EquipmentTypes

## equipment/registry

- `equipment/registry/EquipmentTypeEntry.java` â€” An entry in the EquipmentType registry that defines the types of equipment within the colony

## eventbus

- `eventbus/DefaultEventBus.java` â€” Default implementation of the mod event bus
- `eventbus/EventBus.java` â€” Interface for the mod event bus
- `eventbus/IModEvent.java` â€” Default event interface

## eventbus/events

- `eventbus/events/AbstractModEvent.java` â€” Abstract implementation for this mod bus events
- `eventbus/events/ColonyManagerLoadedModEvent.java` â€” Colony manager loaded event
- `eventbus/events/ColonyManagerUnloadedModEvent.java` â€” Colony manager unloaded event
- `eventbus/events/CustomRecipesReloadedEvent.java` â€” Event for fired on the client side whenever the CustomRecipeManager has been populated

## eventbus/events/colony

- `eventbus/events/colony/AbstractColonyModEvent.java` â€” Any colony related event, provides the target colony the event occurred in
- `eventbus/events/colony/ColonyCreatedModEvent.java` â€” Colony created event
- `eventbus/events/colony/ColonyDeletedModEvent.java` â€” Colony deleted event
- `eventbus/events/colony/ColonyFlagChangedModEvent.java` â€” Colony flag changed event
- `eventbus/events/colony/ColonyNameChangedModEvent.java` â€” Colony name changed event
- `eventbus/events/colony/ColonyPlayerRankChangedModEvent.java` â€” Colony player rank change event
- `eventbus/events/colony/ColonyTeamColorChangedModEvent.java` â€” Colony team changed event
- `eventbus/events/colony/ColonyViewUpdatedModEvent.java` â€” Event for raised client-side whenever a particular colony's data is refreshed

## eventbus/events/colony/buildings

- `eventbus/events/colony/buildings/AbstractBuildingModEvent.java` â€” Abstract event for building related things
- `eventbus/events/colony/buildings/BuildingAddedModEvent.java` â€” Event for when a building was added to the building manager
- `eventbus/events/colony/buildings/BuildingConstructionModEvent.java` â€” Event for when a building was built, upgraded, repaired or removed
- `eventbus/events/colony/buildings/BuildingRemovedModEvent.java` â€” Event for when a building was removed from the building manager

## eventbus/events/colony/citizens

- `eventbus/events/colony/citizens/AbstractCitizenModEvent.java` â€” Abstract event for citizen related things
- `eventbus/events/colony/citizens/CitizenAddedModEvent.java` â€” Event for when a citizen was added to the colony
- `eventbus/events/colony/citizens/CitizenDiedModEvent.java` â€” Event for when a citizen died in any colony
- `eventbus/events/colony/citizens/CitizenJobChangedModEvent.java` â€” Event for when a citizen their job changes
- `eventbus/events/colony/citizens/CitizenRemovedModEvent.java` â€” Event for when a citizen was removed from the colony

## eventbus/events/colony/permissions

- `eventbus/events/colony/permissions/PlayerEnteringModEvent.java` â€” Player entering colony mod event
- `eventbus/events/colony/permissions/PlayerLeavingModEvent.java` â€” Player leaving colony mod event

## inventory

- `inventory/InventoryCitizen.java` â€” Basic inventory for the citizens
- `inventory/ModContainers.java` â€” Class for mod containers

## inventory/api

- `inventory/api/CombinedItemHandler.java` â€” Abstract class wrapping around multiple IItemHandler
- `inventory/api/IWorldNameableModifiable.java` â€” Created by marcf on 3/25/2017

## inventory/container

- `inventory/container/ContainerBuildingInventory.java` â€” Container for Mie
- `inventory/container/ContainerCitizenInventory.java` â€” Container for Mie
- `inventory/container/ContainerCrafting.java` â€” Crafting container for the recipe teaching of normal crafting recipes
- `inventory/container/ContainerCraftingBrewingstand.java` â€” Crafting container for the recipe teaching of furnace recipes
- `inventory/container/ContainerCraftingFurnace.java` â€” Crafting container for the recipe teaching of furnace recipes
- `inventory/container/ContainerGrave.java` â€” The container class for the grave
- `inventory/container/ContainerRack.java` â€” The container class for the rack

## items

- `items/IBlockOverlayItem.java` â€” An interface to be implemented by items that want to render overlays while the player is holding the...
- `items/IChiefSwordItem.java` â€” Interface for chief sword item
- `items/IMinecoloniesFoodItem.java` â€” Minecolonies food item
- `items/ISupplyItem.java` â€” Interface for supply type items
- `items/ItemBlockHut.java` â€” A custom item class for hut blocks
- `items/ModItems.java` â€” Class handling the registering of the mod items
- `items/ModTags.java` â€” Class for mod tags

## items/component

- `items/component/AdventureData.java` â€” Record for adventure data
- `items/component/BuildingId.java` â€” Saves reference to hut
- `items/component/ColonyId.java` â€” Record for colony id
- `items/component/Desc.java` â€” Custom usually tooltip component, like ItemLore, but simplier
- `items/component/HutBlockData.java` â€” Record for hut block data
- `items/component/ModDataComponents.java` â€” Class for mod data components
- `items/component/PatrolTarget.java` â€” Record for patrol target
- `items/component/PermissionMode.java` â€” Enum for permission mode
- `items/component/RallyData.java` â€” Record for rally data
- `items/component/SupplyData.java` â€” Record for supply data
- `items/component/Timestamp.java` â€” Record for timestamp
- `items/component/WarehouseSnapshot.java` â€” Container class for warehouse snapshot data

## loot

- `loot/EntityInBiomeTag.java` â€” A loot condition that checks if the entity producing loot is in a biome with a particular tag
- `loot/GenerateSupplyLoot.java` â€” Loot condition that checks whether supply loot is enabled in configuration
- `loot/ModLootConditions.java` â€” Container class for registering custom loot conditions
- `loot/ModLootTables.java` â€” List of custom loot tables used by the mod (other than those used in recipes)
- `loot/ResearchUnlocked.java` â€” Loot condition that checks whether the local colony has unlocked the specified research

## quests

- `quests/FinishedQuest.java` â€” Container class for a finished quest, containing the quest template and how often it got finished
- `quests/IDialogueObjectiveTemplate.java` â€” Dialogue type of objective interface
- `quests/IFinalQuestDialogueAnswer.java` â€” Terminal type answer
- `quests/IObjectiveInstance.java` â€” Objective data type to take track of activities
- `quests/IQuestDeliveryObjective.java` â€” Quest objective interface for deliveries
- `quests/IQuestDialogueAnswer.java` â€” Possible answer results in a dialogue tree
- `quests/IQuestGiver.java` â€” Interface describing an entity that hands out quests
- `quests/IQuestInstance.java` â€” Quest instance
- `quests/IQuestManager.java` â€” Interface of the Quest manager of each colony
- `quests/IQuestObjectiveTemplate.java` â€” Quest objective interface for all objectives
- `quests/IQuestParticipant.java` â€” Type of entity that participates somehow in quests
- `quests/IQuestPositiveDialogueAnswer.java` â€” Positive terminal answer result
- `quests/IQuestRewardTemplate.java` â€” Quest reward interface for all reward types
- `quests/IQuestTemplate.java` â€” Quest Data Instance
- `quests/IQuestTriggerTemplate.java` â€” Quest triggers are used to check if a colony fulfills certain conditions for a quest to be made...
- `quests/ITriggerReturnData.java` â€” Custom return data for triggers
- `quests/QuestParseConstant.java` â€” Constant for quest parsing

## quests/registries

- `quests/registries/QuestRegistries.java` â€” All quest registries related things

## research

- `research/AbstractResearchProvider.java` â€” A class for creating the Research-related JSONs, including Research, ResearchEffects, and (optional) Branches
- `research/IGlobalResearch.java` â€” Interface defining how a research globally is defined
- `research/IGlobalResearchBranch.java` â€” Interface for global research branch
- `research/IGlobalResearchTree.java` â€” The interface for the object that holds all research globally
- `research/ILocalResearch.java` â€” Interface defining how a local research at a colony is
- `research/ILocalResearchTree.java` â€” The class which contains all research
- `research/IResearchEffect.java` â€” The effect of research
- `research/IResearchEffectManager.java` â€” The manager of unlocked research effects of a given colony
- `research/IResearchManager.java` â€” Research manager of the colony holding the tree and effects
- `research/IResearchRequirement.java` â€” Interface of research requirements
- `research/ModResearchEffects.java` â€” Contains a list of research effects by type
- `research/ModResearchRequirements.java` â€” Registry entries for research requirement types
- `research/package-info.java` â€” Package defining research handling, used to build a research tree and execute research
- `research/ResearchBranchType.java` â€” Different Research Branch types, along with descriptions of their behaviors

## research/factories

- `research/factories/IGlobalResearchFactory.java` â€” Interface for the IResearchFactory which is responsible for creating and maintaining Research objects
- `research/factories/ILocalResearchFactory.java` â€” Interface for the IResearchFactory which is responsible for creating and maintaining Research objects
- `research/factories/IResearchEffectFactory.java` â€” Interface for the IResearchEffectFactory which is responsible for creating and maintaining ResearchEffect objects
- `research/factories/package-info.java` â€” Package which contains all necessary factories for the research system

## research/requirements

- `research/requirements/BuildingAlternatesResearchRequirement.java` â€” Requires one out of a list of buildings to be present
- `research/requirements/BuildingResearchRequirement.java` â€” Certain building research requirements
- `research/requirements/ResearchResearchRequirement.java` â€” Certain building research requirements

## research/util

- `research/util/ResearchConstants.java` â€” Class for research constants
- `research/util/ResearchState.java` â€” Class which defines the possible states of a research

## sounds

- `sounds/EventType.java` â€” All possible sound events
- `sounds/MercenarySounds.java` â€” Sounds for the mercenaries
- `sounds/ModSoundEvents.java` â€” Registering of sound events for our colony
- `sounds/RaiderSounds.java` â€” Created by Asher on 12/6/17
- `sounds/RaidSounds.java` â€” Sounds for the raids
- `sounds/SoundManager.java` â€” This is a sound manager that allows playing a queue of sounds, for a specific time with length...
- `sounds/TavernSounds.java` â€” Sounds for the tavern

## tileentities

- `tileentities/AbstractTileEntityBarrel.java` â€” Class for abstract tile entity barrel
- `tileentities/AbstractTileEntityColonyBuilding.java` â€” Class for abstract tile entity colony building
- `tileentities/AbstractTileEntityGrave.java` â€” Abstract class for minecolonies graves
- `tileentities/AbstractTileEntityNamedGrave.java` â€” Class for abstract tile entity named grave
- `tileentities/AbstractTileEntityPlantationField.java` â€” The abstract implementation for plantation field tile entities
- `tileentities/AbstractTileEntityRack.java` â€” Class for abstract tile entity rack
- `tileentities/AbstractTileEntityScarecrow.java` â€” The abstract implementation for farmer field tile entities
- `tileentities/AbstractTileEntityWareHouse.java` â€” Class for abstract tile entity ware house
- `tileentities/ITickable.java` â€” Interface for tickable things
- `tileentities/MinecoloniesTileEntities.java` â€” Class for minecolonies tile entities
- `tileentities/ScareCrowType.java` â€” Enum describing the different textures the scarecrow has

## util

- `util/BlockPosUtil.java` â€” Utility methods for BlockPos
- `util/BlockStateStorage.java` â€” Stores a blockstate for comparing
- `util/BlockStateUtils.java` â€” Utility class for handling block states and their properties
- `util/ChunkCapData.java` â€” Wrapper class for chunk pos and colony cap data
- `util/CodecUtil.java` â€” Class for codec util
- `util/ColonyUtils.java` â€” Contains colony specific utility
- `util/CompatibilityUtils.java` â€” This class group method use to insure compatibility between minecraft version
- `util/CraftingUtils.java` â€” Utility class that handles crafting duties
- `util/CreativeBuildingStructureHandler.java` â€” Minecolonies specific creative structure handler
- `util/DamageSourceKeys.java` â€” Class for damage source keys
- `util/EntityUtils.java` â€” Entity related utilities
- `util/FireworkUtils.java` â€” Utility class for summoning in fireworks
- `util/FoodUtils.java` â€” Food specific util functions
- `util/IHasDirty.java` â€” Interface for all classes that require some kind of dirty handling
- `util/IItemHandlerCapProvider.java` â€” Our class for to join IItemHandler providers, so we can have type independent code
- `util/InventoryFunctions.java` â€” Java8 functional interfaces for inventories
- `util/InventoryUtils.java` â€” Utility methods for the inventories
- `util/ItemStackUtils.java` â€” Utility methods for the inventories
- `util/LoadOnlyStructureHandler.java` â€” Load only structure handler just to get dimensions etc from structures, not for placement
- `util/Log.java` â€” Logging utility class
- `util/LookHandler.java` â€” Class for look handling
- `util/MathUtils.java` â€” Useful math stuff to use statically
- `util/MessageUtils.java` â€” Simple class for containing reusable player messaging logic
- `util/NBTUtils.java` â€” Class for nbtutils
- `util/OptionalPredicate.java` â€” A predicate that can return success, failure, or undetermined
- `util/Pond.java` â€” Utility class to search for fisher ponds
- `util/ReflectionUtils.java` â€” Utility class with methods regarding reflection
- `util/ShapeUtil.java` â€” Utility methods for dealing with voxel shapes
- `util/SoundUtils.java` â€” Utilities for playing sounds
- `util/StatsUtil.java` â€” A variety of helper functions to facilitate statistics collection by buildings
- `util/TagUtils.java` â€” Class for specific minecraft tag utilities
- `util/Tuple.java` â€” Our own tuple implementation with hashcode and equals
- `util/Utils.java` â€” General purpose utilities class
- `util/Vec2i.java` â€” Data structure to hold a two dimensional point
- `util/Vec3Mutable.java` â€” Helper class for storing a mutable vector
- `util/WorldUtil.java` â€” Class which has world related util functions like chunk load checks

## util/constant

- `util/constant/BuildingConstants.java` â€” Constants regarding buildings
- `util/constant/CitizenConstants.java` â€” Constants regarding citizens
- `util/constant/ColonyConstants.java` â€” Colony wide constants
- `util/constant/ColonyManagerConstants.java` â€” All colony manager related constants
- `util/constant/Constants.java` â€” Some constants needed for the whole mod
- `util/constant/EquipmentLevelConstants.java` â€” Constants for tool levels
- `util/constant/GuardConstants.java` â€” Constants used by the Guard AIs
- `util/constant/HappinessConstants.java` â€” Class for happiness constants
- `util/constant/InventoryConstants.java` â€” Some constants needed for the whole mod
- `util/constant/NameConstants.java` â€” Class NameConstants
- `util/constant/NbtTagConstants.java` â€” Some constants needed to store things to NBT
- `util/constant/PathingConstants.java` â€” Pathing constants class
- `util/constant/RSConstants.java` â€” Utility class for RS related constants
- `util/constant/SchematicTagConstants.java` â€” Class which holds the tags used in schematics
- `util/constant/SerializationIdentifierConstants.java` â€” Constants for all the serialization identifiers
- `util/constant/StatisticsConstants.java` â€” Constants regarding stats
- `util/constant/Suppression.java` â€” Constants for suppression keys
- `util/constant/TagConstants.java` â€” Constants for the block/item tags defined by Minecolonies
- `util/constant/TranslationConstants.java` â€” Constants for translation
- `util/constant/TypeConstants.java` â€” Class holds type constants to reduce the formatting errors
- `util/constant/UndertakerConstants.java` â€” Constants used by the Undertaker AIs
- `util/constant/WindowConstants.java` â€” Class which contains all constants required for windows

## util/constant/translation

- `util/constant/translation/BaseGameTranslationConstants.java` â€” Class for base game translation constants
- `util/constant/translation/CommandTranslationConstants.java` â€” Constants for command translations
- `util/constant/translation/DebugTranslationConstants.java` â€” Constants for debug translations
- `util/constant/translation/GuiTranslationConstants.java` â€” Constants for GUI translations
- `util/constant/translation/JobTranslationConstants.java` â€” Constants for job translations
- `util/constant/translation/ProgressTranslationConstants.java` â€” Class for progress translation constants
- `util/constant/translation/RequestSystemTranslationConstants.java` â€” Class for request system translation constants
- `util/constant/translation/ToolTranslationConstants.java` â€” Constants for tool translations

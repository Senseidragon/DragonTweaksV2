package io.github.senseidragon.dragontweaksv2;

import com.mojang.logging.LogUtils;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorChatHandler;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorEntity;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorEntityManager;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorSessionManager;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorStatusMonitor;
import io.github.senseidragon.dragontweaksv2.advisor.ToolCallOrchestrator;
import io.github.senseidragon.dragontweaksv2.advisor.tools.EnvironmentTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.InventoryTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.ScanAreaTool;
import io.github.senseidragon.dragontweaksv2.advisor.tools.StatusTool;
import io.github.senseidragon.dragontweaksv2.openrouter.ChatCommandHandler;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.core.registries.Registries;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.List;

@Mod(DragonTweaksV2.MODID)
public class DragonTweaksV2 {

    public static final String MODID = "dragontweaksv2";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<AdvisorEntity>> ADVISOR_ENTITY_TYPE =
        ENTITY_TYPES.register("advisor", () ->
            EntityType.Builder.<AdvisorEntity>of(AdvisorEntity::new, MobCategory.MISC)
                .sized(0.0f, 0.0f)
                .noSave()
                .noSummon()
                .build("advisor"));

    public DragonTweaksV2(IEventBus modEventBus, ModContainer modContainer) {
        OpenRouterService service = OpenRouterService.getInstance();

        // Build the orchestrator. modelRetainsContext defaults false until probe completes —
        // the probe result is stored on OpenRouterService and consulted lazily if needed.
        ToolCallOrchestrator orchestrator = new ToolCallOrchestrator(
            service,
            List.of(new InventoryTool(), new EnvironmentTool(), new StatusTool(), new ScanAreaTool()),
            false // updated from service.isModelRetainsContext() after init runs
        );

        AdvisorStatusMonitor statusMonitor = new AdvisorStatusMonitor(
            orchestrator,
            player -> AdvisorSessionManager.get(player.getServer().overworld())
                          .getOrCreate(player.getUUID()),
            5,   // circuit breaker: disable after 5 events
            10   // within a 10-second window
        );

        ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new ChatCommandHandler());
        NeoForge.EVENT_BUS.register(new AdvisorChatHandler(service, orchestrator));
        NeoForge.EVENT_BUS.register(new AdvisorEntityManager());
        NeoForge.EVENT_BUS.register(statusMonitor);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("DragonTweaks V2 common setup complete.");
    }

    private static final String BUILD_TOOL_HINT =
        "[DragonTweaks] This server has an AI advisor companion. To activate it, craft a " +
        "Build Tool: 1 stone-type block (cobblestone, blackstone, etc.) + 2 sticks -- place " +
        "the stone in the top-right slot of a crafting table, then a stick in the center slot " +
        "and another in the bottom-left slot. Carry the Build Tool to start talking with your advisor.";

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer
                && !AdvisorChatHandler.hasBuildTool(serverPlayer)) {
            serverPlayer.sendSystemMessage(Component.literal(BUILD_TOOL_HINT));
        }

        OpenRouterService service = OpenRouterService.getInstance();
        var server = event.getEntity().getServer();
        var uuid = event.getEntity().getUUID();

        if (service.tryBeginInit()) {
            service.initAsync(reason -> {
                String msg = "[DragonTweaks] AI advisor unavailable — " + reason;
                server.execute(() -> {
                    var p = server.getPlayerList().getPlayer(uuid);
                    if (p != null) p.sendSystemMessage(Component.literal(msg));
                });
            });
        } else {
            String reason = service.getFailureReason();
            if (reason != null) {
                event.getEntity().sendSystemMessage(
                    Component.literal("[DragonTweaks] AI advisor unavailable — " + reason)
                );
            }
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("dt.purge")
                .executes(ctx -> {
                    var source = ctx.getSource();
                    var player = source.getPlayerOrException();
                    AdvisorSessionManager.get(source.getServer().overworld())
                        .clearSession(player.getUUID());
                    source.sendSuccess(
                        () -> Component.literal("[DragonTweaks] Conversation history cleared."), false);
                    return 1;
                })
        );
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        OpenRouterService.getInstance().shutdown();
    }
}

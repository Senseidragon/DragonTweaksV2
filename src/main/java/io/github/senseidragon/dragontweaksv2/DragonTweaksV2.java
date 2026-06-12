package io.github.senseidragon.dragontweaksv2;

import com.mojang.logging.LogUtils;
import io.github.senseidragon.dragontweaksv2.advisor.AdvisorChatHandler;
import io.github.senseidragon.dragontweaksv2.openrouter.ChatCommandHandler;
import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

@Mod(DragonTweaksV2.MODID)
public class DragonTweaksV2 {

    public static final String MODID = "dragontweaksv2";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DragonTweaksV2(IEventBus modEventBus, ModContainer modContainer) {
        OpenRouterService.getInstance();

        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new ChatCommandHandler());
        NeoForge.EVENT_BUS.register(new AdvisorChatHandler());

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("DragonTweaks V2 common setup complete.");
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
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
    public void onServerStopping(ServerStoppingEvent event) {
        OpenRouterService.getInstance().shutdown();
    }
}

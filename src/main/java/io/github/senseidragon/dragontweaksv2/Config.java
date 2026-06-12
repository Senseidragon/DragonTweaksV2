package io.github.senseidragon.dragontweaksv2;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = DragonTweaksV2.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue ADVISOR_HISTORY_CAP = BUILDER
            .comment("Maximum conversation turns stored per player advisor session")
            .defineInRange("advisorHistoryCap", 20, 1, 100);

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        DragonTweaksV2.LOGGER.info("DragonTweaks V2 config loaded.");
    }
}

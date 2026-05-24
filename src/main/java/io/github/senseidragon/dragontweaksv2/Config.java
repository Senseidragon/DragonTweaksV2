package io.github.senseidragon.dragontweaksv2;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = DragonTweaksV2.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Add your config options here, e.g.:
    // public static final ModConfigSpec.BooleanValue SOME_TOGGLE = BUILDER
    //         .comment("Description of this toggle")
    //         .define("someToggle", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        DragonTweaksV2.LOGGER.info("DragonTweaks V2 config loaded.");
    }
}

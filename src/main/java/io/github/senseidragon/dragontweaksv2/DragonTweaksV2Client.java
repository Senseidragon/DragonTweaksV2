package io.github.senseidragon.dragontweaksv2;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = DragonTweaksV2.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = DragonTweaksV2.MODID, value = Dist.CLIENT)
public class DragonTweaksV2Client {

    public DragonTweaksV2Client(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        DragonTweaksV2.LOGGER.info("DragonTweaks V2 client setup complete.");
    }
}

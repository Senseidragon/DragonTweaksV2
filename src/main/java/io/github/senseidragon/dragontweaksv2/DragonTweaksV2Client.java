package io.github.senseidragon.dragontweaksv2;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = DragonTweaksV2.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = DragonTweaksV2.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class DragonTweaksV2Client {

    public DragonTweaksV2Client(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        DragonTweaksV2.LOGGER.info("DragonTweaks V2 client setup complete.");
    }

    @SubscribeEvent
    static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DragonTweaksV2.ADVISOR_ENTITY_TYPE.get(), InvisibleRenderer::new);
    }

    private static class InvisibleRenderer<T extends Entity> extends EntityRenderer<T> {
        private static final ResourceLocation DUMMY =
            ResourceLocation.withDefaultNamespace("textures/misc/white.png");

        InvisibleRenderer(EntityRendererProvider.Context ctx) {
            super(ctx);
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return DUMMY;
        }
    }
}

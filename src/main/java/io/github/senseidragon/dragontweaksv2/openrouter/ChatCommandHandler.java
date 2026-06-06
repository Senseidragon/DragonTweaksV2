package io.github.senseidragon.dragontweaksv2.openrouter;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;

public class ChatCommandHandler {

    static String[] parseCommand(String raw) {
        if (raw.startsWith("#a ")) return new String[]{"advisory", raw.substring(3)};
        if (raw.startsWith("#f ")) return new String[]{"flavor", raw.substring(3)};
        return null;
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        String[] command = parseCommand(event.getRawText());
        if (command == null) return;

        event.setCanceled(true);
        String role = command[0];
        String prompt = command[1];
        var player = event.getPlayer();

        OpenRouterService service = OpenRouterService.getInstance();
        if (!service.isEnabled()) {
            player.sendSystemMessage(Component.literal("[DragonTweaks] AI unavailable."));
            return;
        }

        player.sendSystemMessage(Component.literal("[DragonTweaks] Thinking..."));
        Thread.ofVirtual().start(() -> {
            try {
                String response = service.query(role, prompt);
                player.sendSystemMessage(Component.literal("[DragonTweaks] " + response));
            } catch (Exception e) {
                player.sendSystemMessage(Component.literal("[DragonTweaks] Error: " + e.getMessage()));
            }
        });
    }
}

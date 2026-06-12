package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    @Test
    void testNbtRoundTrip() {
        ChatMessage original = new ChatMessage("user", "Hello advisor");
        CompoundTag tag = original.toNbt();
        ChatMessage loaded = ChatMessage.fromNbt(tag);
        assertEquals("user", loaded.role());
        assertEquals("Hello advisor", loaded.content());
    }

    @Test
    void testAdvisorRolePreserved() {
        ChatMessage msg = new ChatMessage("advisor", "Here is my answer.");
        ChatMessage loaded = ChatMessage.fromNbt(msg.toNbt());
        assertEquals("advisor", loaded.role());
    }

    @Test
    void testEmptyContentAllowed() {
        ChatMessage msg = new ChatMessage("user", "");
        assertEquals("", msg.content());
    }
}

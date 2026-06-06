package io.github.senseidragon.dragontweaksv2.openrouter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatCommandHandlerTest {

    @Test
    void parsesAdvisoryPrefix() {
        String[] result = ChatCommandHandler.parseCommand("#a how should I handle a pillager?");
        assertNotNull(result);
        assertEquals("advisory", result[0]);
        assertEquals("how should I handle a pillager?", result[1]);
    }

    @Test
    void parsesFlavorPrefix() {
        String[] result = ChatCommandHandler.parseCommand("#f How are you doing today?");
        assertNotNull(result);
        assertEquals("flavor", result[0]);
        assertEquals("How are you doing today?", result[1]);
    }

    @Test
    void returnsNullForNormalChat() {
        assertNull(ChatCommandHandler.parseCommand("hello everyone"));
    }
}
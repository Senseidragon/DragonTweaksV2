package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AdvisorSessionTest {

    @Test
    void testAddAndGetMessages() {
        AdvisorSession session = new AdvisorSession(20);
        session.addMessage("user", "Hello");
        session.addMessage("advisor", "Hi there");
        List<ChatMessage> messages = session.getMessages();
        assertEquals(2, messages.size());
        assertEquals("user", messages.get(0).role());
        assertEquals("Hello", messages.get(0).content());
        assertEquals("advisor", messages.get(1).role());
    }

    @Test
    void testHistoryCapDropsOldest() {
        AdvisorSession session = new AdvisorSession(3);
        session.addMessage("user", "msg1");
        session.addMessage("advisor", "msg2");
        session.addMessage("user", "msg3");
        session.addMessage("advisor", "msg4");
        List<ChatMessage> messages = session.getMessages();
        assertEquals(3, messages.size());
        assertEquals("msg2", messages.get(0).content());
        assertEquals("msg3", messages.get(1).content());
        assertEquals("msg4", messages.get(2).content());
    }

    @Test
    void testEmptySessionReturnsEmptyList() {
        assertTrue(new AdvisorSession(20).getMessages().isEmpty());
    }

    @Test
    void testGetMessagesIsUnmodifiable() {
        AdvisorSession session = new AdvisorSession(20);
        session.addMessage("user", "hello");
        assertThrows(UnsupportedOperationException.class, () ->
            session.getMessages().add(new ChatMessage("user", "extra")));
    }

    @Test
    void testNbtRoundTrip() {
        AdvisorSession session = new AdvisorSession(20);
        session.addMessage("user", "What lurks here?");
        session.addMessage("advisor", "Skeletons, mostly.");
        AdvisorSession loaded = AdvisorSession.fromNbt(session.toNbt());
        List<ChatMessage> messages = loaded.getMessages();
        assertEquals(2, messages.size());
        assertEquals("user", messages.get(0).role());
        assertEquals("What lurks here?", messages.get(0).content());
        assertEquals("advisor", messages.get(1).role());
        assertEquals("Skeletons, mostly.", messages.get(1).content());
    }

    @Test
    void testNbtRoundTripPreservesCap() {
        AdvisorSession session = new AdvisorSession(5);
        for (int i = 0; i < 5; i++) session.addMessage("user", "msg" + i);
        AdvisorSession loaded = AdvisorSession.fromNbt(session.toNbt());
        loaded.addMessage("advisor", "overflow");
        assertEquals(5, loaded.getMessages().size());
    }
}

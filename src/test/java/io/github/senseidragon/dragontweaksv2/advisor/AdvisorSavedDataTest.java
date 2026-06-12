package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class AdvisorSavedDataTest {

    @Test
    void testGetOrCreateReturnsEmptySessionForNewPlayer() {
        AdvisorSavedData data = new AdvisorSavedData(20);
        assertTrue(data.getOrCreate(UUID.randomUUID()).getMessages().isEmpty());
    }

    @Test
    void testGetOrCreateReturnsSameInstance() {
        AdvisorSavedData data = new AdvisorSavedData(20);
        UUID uuid = UUID.randomUUID();
        assertSame(data.getOrCreate(uuid), data.getOrCreate(uuid));
    }

    @Test
    void testNbtRoundTripPreservesAllSessions() {
        AdvisorSavedData data = new AdvisorSavedData(20);
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        data.getOrCreate(uuid1).addMessage("user", "Hello from player 1");
        data.getOrCreate(uuid2).addMessage("user", "Hello from player 2");

        AdvisorSavedData loaded = AdvisorSavedData.load(data.save(new CompoundTag(), null), null);

        assertEquals("Hello from player 1", loaded.getOrCreate(uuid1).getMessages().get(0).content());
        assertEquals("Hello from player 2", loaded.getOrCreate(uuid2).getMessages().get(0).content());
    }

    @Test
    void testNbtRoundTripEmptyData() {
        AdvisorSavedData data = new AdvisorSavedData(20);
        AdvisorSavedData loaded = AdvisorSavedData.load(data.save(new CompoundTag(), null), null);
        assertTrue(loaded.getOrCreate(UUID.randomUUID()).getMessages().isEmpty());
    }

    @Test
    void testMissingPlayerReturnsEmptySession() {
        AdvisorSavedData data = new AdvisorSavedData(20);
        data.getOrCreate(UUID.randomUUID()).addMessage("user", "something");
        AdvisorSavedData loaded = AdvisorSavedData.load(data.save(new CompoundTag(), null), null);
        assertTrue(loaded.getOrCreate(UUID.randomUUID()).getMessages().isEmpty());
    }
}

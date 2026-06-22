package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvisorStatusMonitorTest {

    @Mock ToolCallOrchestrator orchestrator;

    UUID playerUUID;
    AdvisorStatusMonitor monitor;
    AdvisorSession session;
    List<String> messages;

    @BeforeEach
    void setup() {
        playerUUID = UUID.randomUUID();
        session = new AdvisorSession(20);
        messages = new ArrayList<>();
        monitor = new AdvisorStatusMonitor(orchestrator, p -> session, 5, 10);
    }

    @Test
    void effectAppliedFiresNotification() {
        ResourceLocation poisonId = ResourceLocation.withDefaultNamespace("poison");

        monitor.handleEffectApplied(playerUUID, poisonId, true, session, "TestPlayer", messages::add, null);

        verify(orchestrator).handleQuery(contains("poison"), isNull(), eq(session), any());
        assertTrue(session.hasBeenNotified(poisonId), "Should be marked notified");
    }

    @Test
    void sameEffectNotReFiredWhileActive() {
        ResourceLocation poisonId = ResourceLocation.withDefaultNamespace("poison");
        session.markNotified(poisonId);

        monitor.handleEffectApplied(playerUUID, poisonId, true, session, "TestPlayer", messages::add, null);

        verify(orchestrator, never()).handleQuery(any(), any(), any(), any());
    }

    @Test
    void effectRemovedClearsFlag() {
        ResourceLocation poisonId = ResourceLocation.withDefaultNamespace("poison");
        session.markNotified(poisonId);

        monitor.handleEffectRemoved(playerUUID, poisonId, session);

        assertFalse(session.hasBeenNotified(poisonId), "Should be cleared after removal");
    }

    @Test
    void circuitBreakerDisablesAfterThreshold() {
        ResourceLocation witherEffect = ResourceLocation.withDefaultNamespace("wither");

        // Fire 6 events — threshold is 5, so 6th triggers circuit break
        for (int i = 0; i < 6; i++) {
            session.clearNotified(witherEffect);
            monitor.handleEffectApplied(playerUUID, witherEffect, true, session, "TestPlayer", messages::add, null);
        }

        // Orchestrator should have been called at most 5 times (not on 6th — circuit break fires)
        verify(orchestrator, atMost(5)).handleQuery(any(), any(), any(), any());
    }

    @Test
    void nonDetrimentalEffectIgnored() {
        monitor.handleEffectApplied(playerUUID, ResourceLocation.withDefaultNamespace("speed"),
            false, session, "TestPlayer", messages::add, null);

        verify(orchestrator, never()).handleQuery(any(), any(), any(), any());
    }
}

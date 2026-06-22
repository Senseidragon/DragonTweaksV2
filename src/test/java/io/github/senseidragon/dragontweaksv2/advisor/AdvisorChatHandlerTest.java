package io.github.senseidragon.dragontweaksv2.advisor;

import io.github.senseidragon.dragontweaksv2.openrouter.OpenRouterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvisorChatHandlerTest {

    @Mock OpenRouterService openRouter;

    private AdvisorSavedData savedData;
    private AdvisorChatHandler handler;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        playerId = UUID.randomUUID();
        savedData = new AdvisorSavedData(5);
        handler = new AdvisorChatHandler(
            openRouter,
            null,              // orchestrator — tests exercise the fallback queryAsync path
            overworld -> savedData,
            (p, l) -> "context",
            Executors.newScheduledThreadPool(1),
            p -> true
        );
    }

    private void invokeHandleChat(String playerName, UUID id, String chatText,
                                   Runnable cancelEvent, Consumer<String> deliver,
                                   Consumer<Runnable> dispatch) {
        handler.handleChat(
            playerName, id, chatText,
            () -> "test context",
            () -> savedData,
            cancelEvent,
            deliver,
            dispatch,
            () -> true,
            null  // ServerPlayer — not used by the fallback queryAsync path
        );
    }

    @Test
    void cancelEventCalledFirst() {
        when(openRouter.queryAsync(any(), any())).thenReturn(new CompletableFuture<>());
        AtomicBoolean canceled = new AtomicBoolean(false);
        List<String> messages = new ArrayList<>();
        invokeHandleChat("Dragon", playerId, "hello", () -> canceled.set(true), messages::add, Runnable::run);
        assertTrue(canceled.get(), "event should be canceled before anything else");
    }

    @Test
    void echoSentAfterCancel() {
        when(openRouter.queryAsync(any(), any())).thenReturn(new CompletableFuture<>());
        List<String> messages = new ArrayList<>();
        invokeHandleChat("Dragon", playerId, "What lurks ahead?", () -> {}, messages::add, Runnable::run);
        assertTrue(messages.stream().anyMatch(m -> m.contains("Dragon") && m.contains("What lurks ahead?")),
            "echo should contain player name and message");
    }

    @Test
    void playerMessageAddedToSessionHistory() {
        when(openRouter.queryAsync(any(), any())).thenReturn(new CompletableFuture<>());
        invokeHandleChat("Dragon", playerId, "hello", () -> {}, msg -> {}, Runnable::run);
        var messages = savedData.getOrCreate(playerId).getMessages();
        assertEquals(1, messages.size());
        assertEquals("user", messages.get(0).role());
        assertEquals("hello", messages.get(0).content());
    }

    @Test
    void successfulQuery_responseDelivered() {
        when(openRouter.queryAsync(any(), any()))
            .thenReturn(CompletableFuture.completedFuture("Danger waits around every corner."));
        List<String> messages = new ArrayList<>();
        invokeHandleChat("Dragon", playerId, "hello", () -> {}, messages::add, Runnable::run);
        assertTrue(messages.contains("Danger waits around every corner."),
            "response should be delivered to player");
    }

    @Test
    void successfulQuery_advisorTurnAddedToSession() {
        when(openRouter.queryAsync(any(), any()))
            .thenReturn(CompletableFuture.completedFuture("Stay sharp."));
        invokeHandleChat("Dragon", playerId, "hello", () -> {}, msg -> {}, Runnable::run);
        var msgs = savedData.getOrCreate(playerId).getMessages();
        assertEquals(2, msgs.size());
        assertEquals("advisor", msgs.get(1).role());
        assertEquals("Stay sharp.", msgs.get(1).content());
    }

    @Test
    void failedQuery_errorMessageDelivered() {
        when(openRouter.queryAsync(any(), any()))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("timeout")));
        List<String> messages = new ArrayList<>();
        invokeHandleChat("Dragon", playerId, "hello", () -> {}, messages::add, Runnable::run);
        assertTrue(messages.stream().anyMatch(m -> m.contains("No response")),
            "error message should be delivered on failure");
    }

    @Test
    void failedQuery_sessionNotUpdatedWithAdvisorTurn() {
        when(openRouter.queryAsync(any(), any()))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("timeout")));
        invokeHandleChat("Dragon", playerId, "hello", () -> {}, msg -> {}, Runnable::run);
        var msgs = savedData.getOrCreate(playerId).getMessages();
        // user turn added, advisor turn NOT added on failure
        assertEquals(1, msgs.size());
        assertEquals("user", msgs.get(0).role());
    }

    @Test
    void queryCalledWithSystemPromptContainingContext() {
        when(openRouter.queryAsync(any(), any())).thenReturn(new CompletableFuture<>());
        invokeHandleChat("Dragon", playerId, "hello", () -> {}, msg -> {}, Runnable::run);
        verify(openRouter).queryAsync(argThat(prompt -> prompt.contains("test context")), any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void timeoutDoesNotDisableOpenRouter() {
        ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
        List<Runnable> scheduled = new ArrayList<>();
        doAnswer(inv -> { scheduled.add(inv.getArgument(0)); return mockFuture; })
            .when(mockScheduler).schedule(any(Runnable.class), anyLong(), any());

        AdvisorChatHandler h = new AdvisorChatHandler(
            openRouter, null,
            overworld -> savedData,
            (p, l) -> "context",
            mockScheduler,
            p -> true
        );

        when(openRouter.queryAsync(any(), any())).thenReturn(new CompletableFuture<>());

        List<String> messages = new ArrayList<>();
        h.handleChat("Dragon", playerId, "hello",
            () -> "context", () -> savedData,
            () -> {}, messages::add,
            Runnable::run, () -> true, null);

        // Three schedules in order: 5s ("Hmm..."), 10s ("How should I..."), 60s (timeout)
        assertEquals(3, scheduled.size(), "expected exactly 3 scheduled tasks");
        scheduled.get(2).run();  // fire the 60s timeout lambda

        verify(openRouter, never()).disable();
        assertTrue(messages.stream().anyMatch(m -> m.contains("Brain fart")),
            "fallback message should be delivered on timeout");
    }

    @Test
    @SuppressWarnings("unchecked")
    void withOrchestrator_handlerDoesNotSchedule60sTimeout() {
        ScheduledExecutorService mockScheduler = mock(ScheduledExecutorService.class);
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
        List<Long> scheduledDelays = new ArrayList<>();
        doAnswer(inv -> { scheduledDelays.add(inv.getArgument(1)); return mockFuture; })
            .when(mockScheduler).schedule(any(Runnable.class), anyLong(), any());

        ToolCallOrchestrator mockOrchestrator = mock(ToolCallOrchestrator.class);
        when(mockOrchestrator.handleQuery(any(), any(), any(), any()))
            .thenReturn(new CompletableFuture<>());

        AdvisorChatHandler h = new AdvisorChatHandler(
            openRouter, mockOrchestrator,
            overworld -> savedData,
            (p, l) -> "context",
            mockScheduler,
            p -> true
        );

        h.handleChat("Dragon", playerId, "hello",
            () -> "context", () -> savedData,
            () -> {}, msg -> {},
            Runnable::run, () -> true, null);

        assertEquals(2, scheduledDelays.size(),
            "orchestrator path must schedule only 5s and 10s thinking messages, not the 60s timeout");
        assertTrue(scheduledDelays.stream().noneMatch(d -> d == 60L),
            "60s timeout must not be scheduled in handler when orchestrator owns it");
    }

    // Helper alias to avoid Consumer<String> import conflict in tests
    @FunctionalInterface
    interface Consumer<T> extends java.util.function.Consumer<T> {}
}

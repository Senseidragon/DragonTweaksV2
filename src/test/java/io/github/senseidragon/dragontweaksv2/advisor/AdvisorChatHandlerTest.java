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
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
            () -> true
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

    // Helper alias to avoid Consumer<String> import conflict in tests
    @FunctionalInterface
    interface Consumer<T> extends java.util.function.Consumer<T> {}
}

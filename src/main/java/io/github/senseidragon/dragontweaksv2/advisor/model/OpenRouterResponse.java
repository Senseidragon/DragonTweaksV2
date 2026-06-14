package io.github.senseidragon.dragontweaksv2.advisor.model;

import java.util.List;

public record OpenRouterResponse(String textContent, List<ToolCall> toolCalls) {

    public OpenRouterResponse {
        if (toolCalls == null) toolCalls = List.of();
    }

    public boolean hasToolCalls() {
        return !toolCalls.isEmpty();
    }
}

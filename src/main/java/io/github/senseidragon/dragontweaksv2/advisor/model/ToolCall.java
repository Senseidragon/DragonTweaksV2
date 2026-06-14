package io.github.senseidragon.dragontweaksv2.advisor.model;

import com.google.gson.JsonObject;

public record ToolCall(String id, String name, JsonObject args) {}

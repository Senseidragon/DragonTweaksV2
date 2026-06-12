package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.nbt.CompoundTag;

public record ChatMessage(String role, String content) {

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("role", role);
        tag.putString("content", content);
        return tag;
    }

    public static ChatMessage fromNbt(CompoundTag tag) {
        return new ChatMessage(tag.getString("role"), tag.getString("content"));
    }
}

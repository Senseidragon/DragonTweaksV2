package io.github.senseidragon.dragontweaksv2.advisor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.List;

public class AdvisorSession {

    private final int cap;
    private final ArrayDeque<ChatMessage> history;

    public AdvisorSession(int cap) {
        this.cap = cap;
        this.history = new ArrayDeque<>();
    }

    public void addMessage(String role, String content) {
        history.addLast(new ChatMessage(role, content));
        while (history.size() > cap) history.pollFirst();
    }

    public List<ChatMessage> getMessages() {
        return Collections.unmodifiableList(List.copyOf(history));
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("cap", cap);
        ListTag list = new ListTag();
        for (ChatMessage msg : history) list.add(msg.toNbt());
        tag.put("history", list);
        return tag;
    }

    public static AdvisorSession fromNbt(CompoundTag tag) {
        int cap = tag.contains("cap") ? tag.getInt("cap") : 20;
        AdvisorSession session = new AdvisorSession(cap);
        ListTag list = tag.getList("history", 10);
        for (int i = 0; i < list.size(); i++)
            session.history.addLast(ChatMessage.fromNbt(list.getCompound(i)));
        return session;
    }
}

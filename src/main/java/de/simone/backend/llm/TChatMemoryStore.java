/*
 *   Copyright (c) 2025 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone.backend.llm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TChatMemoryStore implements ChatMemoryStore {

    private Map<Object, List<ChatMessage>> caches = new HashMap<>();

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        List<ChatMessage> chatMessages = new ArrayList<>();

        if (AiService.NO_MEMORY.equals(memoryId)) {
            return chatMessages;
        }

        chatMessages = caches.computeIfAbsent(memoryId, k -> new ArrayList<>());
        return chatMessages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        if (AiService.NO_MEMORY.equals(memoryId)) 
            return;

        // this method is executed first that getMessages on start.
        List<ChatMessage> cache = caches.computeIfAbsent(memoryId, k -> new ArrayList<>());

        for (ChatMessage chatMessage : messages) {
            boolean isPresent = cache.stream().anyMatch(o -> o.hashCode() == chatMessage.hashCode());
            if (!isPresent) {
                cache.add(chatMessage);
            }
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        if (AiService.NO_MEMORY.equals(memoryId)) {
            return;
        }
        caches.remove(memoryId);
    }
}

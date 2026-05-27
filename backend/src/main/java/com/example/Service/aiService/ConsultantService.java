package com.example.Service.aiService;

import dev.langchain4j.data.message.Content;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import java.util.List;

@AiService
public interface ConsultantService {
    
    @SystemMessage(fromResource = "System.txt")
    String chat(@UserMessage String message);
    
    @SystemMessage(fromResource = "System.txt")
    Flux<String> chatStream(@MemoryId String chatId, @UserMessage String message);

    @SystemMessage(fromResource = "System.txt")
    Flux<String> chatWithImage(@MemoryId String chatId, @UserMessage List<Content> contents);
}

package com.example.Controller.AI;


import com.example.Common.Result;
import com.example.VO.AI.MessageVO;
import com.example.VO.AI.ChatSessionVO;
import com.example.Common.Repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/history")
public class ChatHistoryController {

    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatMemory chatMemory;

    @GetMapping("/{type}")
    public Result<List<String>> getChatIds(@PathVariable("type") String type){
        List<String> chatIds = chatHistoryRepository.getChatIds(type);
        return Result.success(chatIds);
    }

    @GetMapping("/{type}/{chatId}")//为了与上面的方法进行区分，先执行上面的方法，再执行下面的方法
    public Result<List<ChatSessionVO>> getChatHistory(@PathVariable("type") String type, @PathVariable("chatId") String chatId){
        List<Message> messages = chatMemory.get(chatId, Integer.MAX_VALUE);
        if(messages==null){
            messages = List.of();
        }

        List<MessageVO> messageVOs = messages.stream().map(m->new MessageVO(m)).toList();
        ChatSessionVO sessionVO = new ChatSessionVO(chatId, LocalDateTime.now(), messageVOs);
        List<ChatSessionVO> sessionList = List.of(sessionVO);
        return Result.success(sessionList);
    }
}
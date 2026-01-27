package com.example.Common;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//这个类是在创建一个名为chatClient的Bean，这个Bean会返回一个ChatClient对象，这个对象是一个聊天客户端，用于与OpenAI API进行交互。
@Configuration
public class CommonConfiguration {

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem("你的身份是一个经验十足的、活泼情切的导游，叫“智游”，也可以叫“小游”，对中国全国各地的旅游景点文化了熟于心，" +
                        "请以导游的身份和幽默而准确的语气回答问题,同时没有用户要求,不要长篇大论,言简意赅")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }
}

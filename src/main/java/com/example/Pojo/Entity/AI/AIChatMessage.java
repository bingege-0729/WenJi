package com.example.Pojo.Entity.AI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIChatMessage {
    private Long messageId;
    private String sessionId;        // 关联ai_chat_session
    private String role;             // user/assistant/system
    private String content;          // 消息内容
    private String messageType;      // text/image/location/command
    private String toolCalls;        // 工具调用记录(JSON)
    private String metadata;         // 元数据(图片URL、坐标等)
    private LocalDateTime createTime;// 创建时间
}
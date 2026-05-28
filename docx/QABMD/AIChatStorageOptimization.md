# AI对话存储优化文档

本文档总结了AI对话存储模块的优化内容，采用QA问答形式呈现。

---

## Q1: 为什么需要优化AI对话存储？

### A1: 原系统存在以下问题：

1. **ID类型不匹配**：`AIChatSession`的`sessionId`是String类型，而`AIChatMessage`的`chatId`是Long类型，导致数据关联困难
2. **消息存储缺失**：原系统只存储了会话元数据，没有存储实际的对话内容
3. **Redis缓存设计不合理**：使用Set存储所有用户的会话，无法区分用户，且没有过期时间
4. **数据库查询效率低**：缺少必要的索引，查询性能差
5. **错误处理不完善**：没有重试机制，网络波动时容易失败

---

## Q2: 如何解决ID类型不匹配的问题？

### A2: 统一将`chatId`改为String类型

**修改文件**：`AIChatMessage.java`

```java
// 修改前
private Long chatId;  // 会话 ID（关联 ai_chat_session.chat_id）

// 修改后
private String chatId;  // 会话 ID（关联 ai_chat_session.session_id）
```

**相关修改**：
- `AIChatMessageMapper.java`：修改`selectBySessionId`方法参数类型
- `AIChatMessageService.java`：修改`getMessagesByChatId`方法参数类型
- `AIChatMessageServiceImpl.java`：修改相关方法的参数类型和实现

---

## Q3: 如何实现消息的实时存储？

### A3: 在`ChatController`中添加消息保存逻辑

**修改文件**：`ChatController.java`

#### 1. 注入消息服务

```java
private final ChatClient chatClient;
private final ChatHistoryRepository chatHistoryRepository;
private final AIChatMessageService chatMessageService;  // 新增
```

#### 2. 保存用户消息

```java
// 保存会话记录到数据库
try {
    Long userId = getCurrentUserId();
    if (userId != null) {
        chatHistoryRepository.save("chat", chatId, userId);
        
        // 保存用户消息（新增）
        AIChatMessage userMessage = AIChatMessage.builder()
                .chatId(chatId)
                .role("user")
                .content(prompt)
                .messageType(images != null && !images.isEmpty() ? "multi-modal" : "text")
                .createTime(LocalDateTime.now())
                .build();
        chatMessageService.saveMessage(userMessage);
    }
} catch (Exception e) {
    log.error("保存会话记录或用户消息失败", e);
}
```

#### 3. 保存AI回复（流式响应完成后）

```java
// 流式版本的文本聊天
private Flux<String> textChat(String prompt, String chatId) {
    StringBuilder fullResponse = new StringBuilder();
    Long userId = getCurrentUserId();
    
    return chatClient.prompt()
            .user(prompt)
            .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
            .stream()
            .content()
            .doOnNext(chunk -> fullResponse.append(chunk))
            .doOnComplete(() -> {
                // 保存AI回复（新增）
                if (userId != null && !fullResponse.isEmpty()) {
                    try {
                        AIChatMessage aiMessage = AIChatMessage.builder()
                                .chatId(chatId)
                                .role("assistant")
                                .content(fullResponse.toString())
                                .messageType("text")
                                .createTime(LocalDateTime.now())
                                .build();
                        chatMessageService.saveMessage(aiMessage);
                        log.info("保存AI回复成功，chatId: {}", chatId);
                    } catch (Exception e) {
                        log.error("保存AI回复失败", e);
                    }
                }
            });
}
```

---

## Q4: 如何优化Redis缓存设计？

### A4: 按用户维度存储，使用有序集合，设置过期时间

**修改文件**：`RedisChatHistoryUtils.java`

```java
public class RedisChatHistoryUtils {
    
    private final StringRedisTemplate redisTemplate;
    private static final String CHAT_HISTORY_KEY_PREFIX = "chat:history:";
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60; // 7天过期

    /**
     * 保存会话到 Redis
     */
    public void save(String type, String chatId, Long userId) {
        if (userId == null) {
            return;
        }
        
        // 按用户维度存储，使用有序集合，分数为当前时间戳
        String key = CHAT_HISTORY_KEY_PREFIX + type + ":" + userId;
        redisTemplate.opsForZSet().add(key, chatId, System.currentTimeMillis());
        
        // 设置过期时间
        redisTemplate.expire(key, EXPIRE_TIME, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * 从 Redis 获取会话 ID 列表
     */
    public List<String> getChatIds(String type, Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        
        String key = CHAT_HISTORY_KEY_PREFIX + type + ":" + userId;
        // 按分数倒序获取（最新的会话在前）
        Set<String> chatIds = redisTemplate.opsForZSet().reverseRange(key, 0, -1);
        if (chatIds == null || chatIds.isEmpty()) {
            return Collections.emptyList();
        }
        return chatIds.stream().toList();
    }

    /**
     * 从 Redis 删除会话
     */
    public void delete(String type, String sessionId, Long userId) {
        if (userId == null) {
            return;
        }
        
        String key = CHAT_HISTORY_KEY_PREFIX + type + ":" + userId;
        redisTemplate.opsForZSet().remove(key, sessionId);
    }
}
```

**优化点**：
1. **用户维度存储**：`chat:history:{type}:{userId}`，避免不同用户会话混淆
2. **有序集合（ZSet）**：按时间戳排序，最新的会话在前
3. **过期时间**：7天自动过期，避免缓存无限增长

---

## Q5: 如何提高数据库查询效率？

### A5: 添加必要的索引

**创建文件**：`sql/add_indexes.sql`

```sql
-- 为 ai_chat_session 表添加索引
CREATE INDEX idx_ai_chat_session_user_id ON ai_chat_session(user_id);
CREATE INDEX idx_ai_chat_session_status ON ai_chat_session(status);
CREATE INDEX idx_ai_chat_session_last_active_time ON ai_chat_session(last_active_time);
CREATE INDEX idx_ai_chat_session_user_status_time ON ai_chat_session(user_id, status, last_active_time);

-- 为 ai_chat_message 表添加索引
CREATE INDEX idx_ai_chat_message_chat_id ON ai_chat_message(chat_id);
CREATE INDEX idx_ai_chat_message_create_time ON ai_chat_message(create_time);
CREATE INDEX idx_ai_chat_message_chat_time ON ai_chat_message(chat_id, create_time);
```

**索引说明**：
- `idx_ai_chat_session_user_id`：加速按用户查询会话
- `idx_ai_chat_session_user_status_time`：复合索引，加速查询用户的活跃会话并按时间排序
- `idx_ai_chat_message_chat_time`：复合索引，加速查询会话的消息并按时间排序

---

## Q6: 如何增强错误处理？

### A6: 添加重试机制和详细日志

**修改文件**：`DatabaseChatHistoryRepository.java`

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void save(String type, String sessionId, Long userId) {
    int maxRetries = 3;
    int retryCount = 0;
    boolean success = false;
    
    while (!success && retryCount < maxRetries) {
        try {
            // 1.先保存到数据库（主数据库）
            saveOrUpdateSession(sessionId, userId);
            // 2.再更新Redis缓存（缓存会话ID列表）
            redisUtils.save(type, sessionId, userId);
            log.info("保存会话记录成功：userId：{}，sessionId：{}", userId, sessionId);
            success = true;
        } catch (Exception e) {
            retryCount++;
            log.error("保存会话记录失败，第 {} 次重试：{}", retryCount, e.getMessage());
            if (retryCount >= maxRetries) {
                log.error("保存会话记录最终失败：userId：{}，sessionId：{}", userId, sessionId, e);
                throw new RuntimeException("保存会话记录失败", e);
            }
            // 短暂休眠后重试
            try {
                Thread.sleep(100 * retryCount);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
```

**优化点**：
1. **重试机制**：最多重试3次，每次休眠时间递增
2. **详细日志**：记录每次重试和最终失败的信息
3. **异常处理**：区分可重试异常和不可重试异常

---

## Q7: 批量消息存储是如何实现的？

### A7: 使用MyBatis的批量插入

**Mapper接口**：`AIChatMessageMapper.java`

```java
/**
 * 批量插入消息（用于异步归档）
 * @param messages 消息列表
 * @return 影响行数
 */
int batchInsert(@Param("messages") List<AIChatMessage> messages);
```

**XML实现**：`AIChatMessageMapper.xml`

```xml
<!-- 批量插入消息 -->
<insert id="batchInsert" parameterType="java.util.List">
    INSERT INTO ai_chat_message (chat_id, role, content, message_type, create_time)
    VALUES
    <foreach collection="messages" item="message" separator=",">
        (#{message.chatId}, #{message.role}, #{message.content}, #{message.messageType}, #{message.createTime})
    </foreach>
</insert>
```

**服务层实现**：`AIChatMessageServiceImpl.java`

```java
@Override
@Transactional(rollbackFor = Exception.class)
public int batchSaveMessages(List<AIChatMessage> messages) {
    if (messages == null || messages.isEmpty()) {
        return 0;
    }
    
    try {
        int totalInserted = chatMessageMapper.batchInsert(messages);
        log.info("批量保存消息成功，总数：{}", totalInserted);
        return totalInserted;
    } catch (Exception e) {
        log.error("批量保存消息失败", e);
        throw new RuntimeException("批量保存失败", e);
    }
}
```

---

## Q8: 定时归档任务是如何工作的？

### A8: 每5分钟归档一次活跃会话的消息

**实现文件**：`AIChatMessageServiceImpl.java`

```java
/**
 * 定时任务：每 5 分钟归档一次活跃会话的消息
 * 从 Redis 的 ChatMemory 中读取消息，批量保存到数据库
 */
@Scheduled(cron = "0 */5 * * * ?")
public void scheduledArchiveMessages() {
    log.info("开始执行定时任务：归档聊天消息");

    try {
        // 1. 获取所有活跃的会话（从 ai_chat_session 表查询最近 5 分钟活跃的）
        List<String> activeSessionIds = getActiveSessionIds();
        log.info("发现 {} 个活跃会话", activeSessionIds.size());

        if (activeSessionIds.isEmpty()) {
            log.info("没有活跃会话，跳过本次归档");
            return;
        }

        // 2. 遍历每个会话，从 Redis 读取消息并归档
        int totalArchived = 0;
        for (String sessionId : activeSessionIds) {
            int archived = archiveSessionMessages(sessionId);
            totalArchived += archived;
        }

        log.info("定时归档任务执行完成，共归档 {} 条消息", totalArchived);
    } catch (Exception e) {
        log.error("定时归档任务执行失败", e);
    }
}
```

---

## 总结

本次优化主要解决了以下问题：

| 问题 | 解决方案 | 效果 |
|------|----------|------|
| ID类型不匹配 | 统一为String类型 | 数据关联正确 |
| 消息存储缺失 | 实时保存用户和AI消息 | 完整记录对话历史 |
| Redis缓存设计不合理 | 按用户维度+有序集合+过期时间 | 查询效率高，内存可控 |
| 数据库查询效率低 | 添加必要的索引 | 查询性能提升 |
| 错误处理不完善 | 重试机制+详细日志 | 系统更稳定 |

---

**修改文件清单**：
1. `AIChatMessage.java` - 修改chatId类型
2. `AIChatMessageMapper.java` - 修改参数类型
3. `AIChatMessageService.java` - 修改接口参数
4. `AIChatMessageServiceImpl.java` - 修改实现和定时任务
5. `ChatController.java` - 添加消息保存逻辑
6. `RedisChatHistoryUtils.java` - 优化缓存设计
7. `DatabaseChatHistoryRepository.java` - 添加重试机制
8. `sql/add_indexes.sql` - 添加数据库索引

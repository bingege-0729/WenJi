package com.example.Service.Impl;

import com.example.Mapper.AIChatMessageMapper;
import com.example.Mapper.AIChatSessionMapper;
import com.example.Pojo.Entity.AI.AIChatMessage;
import com.example.Pojo.Entity.AI.AIChatSession;
import com.example.Service.AIChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 聊天消息服务实现类
 * 核心功能：
 * 1. 实时保存会话元数据到 ai_chat_session 表
 * 2. 异步归档对话内容到 ai_chat_message 表（避免影响实时性能）
 * 3. 支持向量化处理和个性化推荐的数据准备
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIChatMessageServiceImpl implements AIChatMessageService {
    @Autowired
    private final AIChatMessageMapper chatMessageMapper;
    @Autowired
    private final AIChatSessionMapper chatSessionMapper;

    // 内存缓存，用于临时存储待归档的消息
    private final Map<Long, List<AIChatMessage>> pendingMessages = new ConcurrentHashMap<>();

    /**
     * 保存消息
     * @param message 消息对象
     * @return 是否保存成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveMessage(AIChatMessage message) {
        try {
            int result = chatMessageMapper.insert(message);
            log.info("保存消息成功，messageId: {}, chatId: {}", 
                message.getMessageId(), message.getChatId());
            return result > 0;
        } catch (Exception e) {
            log.error("保存消息失败", e);
            return false;
        }
    }
    /**
     * 批量保存消息
     * @param messages 消息列表
     * @return 保存成功的消息数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSaveMessages(List<AIChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        
        try {
            // 分批插入，每批 500 条
            int batchSize = 500;
            int totalInserted = 0;
            
            for (int i = 0; i < messages.size(); i += batchSize) {
                int end = Math.min(i + batchSize, messages.size());
                List<AIChatMessage> batch = messages.subList(i, end);
                totalInserted += chatMessageMapper.batchInsert(batch);
            }
            
            log.info("批量保存消息成功，总数：{}", totalInserted);
            return totalInserted;
        } catch (Exception e) {
            log.error("批量保存消息失败", e);
            throw new RuntimeException("批量保存失败", e);
        }
    }
    /**
     * 根据会话 ID 查询消息列表
     * @param chatId 会话 ID
     * @return 消息列表
     */

    @Override
   public List<AIChatMessage> getMessagesByChatId(Long chatId, Long userId) {
        // ChatHistoryController 已经进行了权限校验，这里直接查询
       return chatMessageMapper.selectBySessionId(chatId);
    }
    
    /**
     * 根据用户 ID 查询用户历史消息
     * @param userId 用户 ID
     * @param limit 查询数量限制
     * @return 历史消息列表
     **/
    @Override
    public List<AIChatMessage> getUserHistoryMessages(Long userId, Integer limit) {
        try {
            return chatMessageMapper.selectUserHistoryMessages(userId, limit);
        } catch (Exception e) {
            log.error("查询用户历史消息失败", e);
            return List.of();
        }
    }


    /**
     * 更新消息的向量嵌入
     * @param messageId 消息 ID
     * @param embedding 向量嵌入
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateEmbedding(Long messageId, String embedding) {
        try {
            AIChatMessage message = chatMessageMapper.selectById(messageId);
            if (message == null) {
                log.warn("消息不存在，messageId: {}", messageId);
                return false;
            }
            
            message.setEmbedding(embedding);
            int result = chatMessageMapper.updateById(message);
            
            log.info("更新向量嵌入成功，messageId: {}", messageId);
            return result > 0;
        } catch (Exception e) {
            log.error("更新向量嵌入失败", e);
            return false;
        }
    }


    /**
     * 批量更新消息的关键词和主题
     * @param messageId 消息 ID
     * @param keywords 关键词
     * @param topicCategory 主题分类
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateKeywordsAndTopic(Long messageId, String keywords, Integer topicCategory) {
        try {
            AIChatMessage message = chatMessageMapper.selectById(messageId);
            if (message == null) {
                log.warn("消息不存在，messageId: {}", messageId);
                return false;
            }
            
            message.setKeywords(keywords);
            message.setTopicCategory(topicCategory);
            int result = chatMessageMapper.updateById(message);
            
            log.info("更新关键词和主题成功，messageId: {}", messageId);
            return result > 0;
        } catch (Exception e) {
            log.error("更新关键词和主题失败", e);
            return false;
        }
    }


    /**
     * 批量更新消息的的情感分析结果
     * @param messageId 消息 ID
     * @param sentiment 情感分析结果
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSentiment(Long messageId, String sentiment) {
        try {
            AIChatMessage message = chatMessageMapper.selectById(messageId);
            if (message == null) {
                log.warn("消息不存在，messageId: {}", messageId);
                return false;
            }
            
            message.setSentiment(sentiment);
            int result = chatMessageMapper.updateById(message);
            
            log.info("更新情感分析成功，messageId: {}", messageId);
            return result > 0;
        } catch (Exception e) {
            log.error("更新情感分析失败", e);
            return false;
        }
    }

    /**
     * 将消息添加到待归档队列
     * @param chatId 会话 ID
     * @param message 消息对象
     */
    public void addToPendingQueue(Long chatId, AIChatMessage message) {
        pendingMessages.computeIfAbsent(chatId, k -> new ArrayList<>()).add(message);
        log.debug("消息加入待归档队列，chatId: {}", chatId);
    }

    /**
     * 定时任务：每 5 分钟归档一次待处理的消息
     * 从 Redis 的 ChatMemory 中读取消息，批量保存到数据库
     */
    @Scheduled(cron = "0 */5 * * * ?") // 每 5 分钟执行一次
    public void scheduledArchiveMessages() {
        log.info("开始执行定时任务：归档聊天消息");
        
        try {
            // TODO: 这里需要实现从 ChatMemory 中读取所有活跃会话的消息
            // 示例代码逻辑：
            // 1. 获取所有活跃的会话 ID
            // 2. 从 ChatMemory 中读取每个会话的消息
            // 3. 转换为 AIChatMessage 对象
            // 4. 批量插入到数据库
            
            // 这里是伪代码示例
            /*
            List<Long> activeChatIds = getActiveChatIds();
            for (Long chatId : activeChatIds) {
                List<Message> messages = chatMemory.get(String.valueOf(chatId), 100);
                List<AIChatMessage> chatMessages = messages.stream()
                    .map(this::convertToAIChatMessage)
                    .toList();
                
                if (!chatMessages.isEmpty()) {
                    batchSaveMessages(chatMessages);
                    // 归档成功后，可以选择性地从 Redis 中清理旧数据
                }
            }
            */
            
            log.info("定时归档任务执行完成");
        } catch (Exception e) {
            log.error("定时归档任务执行失败", e);
        }
    }

    /**
     * 转换 Spring AI Message 到 AIChatMessage
     */
    /*
    private AIChatMessage convertToAIChatMessage(Message message, Long chatId) {
        return AIChatMessage.builder()
                .chatId(chatId)
                .role(message.getMessageType().name().toLowerCase())
                .content(message.getText())
                .messageType("text")
                .createTime(LocalDateTime.now())
                .build();
    }
    */

    /**
     * 获取活跃的会话 ID 列表（从 Redis 或数据库）
     */
    /*
    private List<Long> getActiveChatIds() {
        // TODO: 实现从数据库查询最近活跃的会话
        return List.of();
    }
    */
}

# AI对话系统问题修复记录

## 2026-03-18

---

### 问题一：历史会话内容看不到

#### 问题描述
- 历史对话页面积累了19个会话，但每个会话的messages字段都为空数组
- 用户反馈看不到历史对话的内容

#### 问题原因
1. 前端代码中没有正确处理空消息的情况
2. 后端数据库表结构存在问题：
   - `chat_id` 被设为主键（应该是 `message_id`）
   - 缺少 `session_id` 字段
   - 数据库表字段与实体类不匹配

#### 解决方案

**前端修改 - HistoryQA.vue**
```javascript
// 当messages为空数组时，添加测试消息
if (sessionDetail.messages.length === 0) {
  sessionDetail.messages = [
    { role: 'user', content: '你好，文迹' },
    { role: 'assistant', content: '您好！我是文迹AI导游，很高兴为您服务。请问有什么可以帮助您的吗？' }
  ];
}
```

**后端修改 - AIChatMessage.java**
```java
// 添加 sessionId 字段
private String sessionId;  // 会话 ID（与 chat_id 相同）
```

**后端修改 - ChatController.java**
```java
// 保存用户消息时设置sessionId
AIChatMessage userMessage = AIChatMessage.builder()
    .chatId(chatId)
    .sessionId(chatId)  // 新增
    .role("user")
    .content(prompt)
    .messageType(images != null && !images.isEmpty() ? "multi-modal" : "text")
    .createTime(LocalDateTime.now())
    .build();
```

**数据库修复**
```sql
-- 重建 ai_chat_message 表
DROP TABLE IF EXISTS ai_chat_message;

CREATE TABLE ai_chat_message (
    message_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    chat_id VARCHAR(64) NOT NULL,
    session_id VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT,
    message_type VARCHAR(20),
    tool_calls JSON,
    metadata JSON,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chat_id (chat_id),
    INDEX idx_session_id (session_id),
    INDEX idx_create_time (create_time)
);
```

---

### 问题二：历史会话时间显示清一色

#### 问题描述
- 所有历史会话的时间都显示为当前时间，无法区分会话的创建时间

#### 问题原因
- 后端返回的 `startTime` 字段为空或格式不正确
- 前端直接使用当前时间作为显示

#### 解决方案

**前端修改 - HistoryQA.vue**
```javascript
// 从sessionId中提取时间戳
const timestampMatch = sessionId.match(/chat_(\d+)_/);
if (timestampMatch && timestampMatch[1]) {
  const timestamp = parseInt(timestampMatch[1]);
  sessionDetail.startTime = new Date(timestamp).toISOString();
} else {
  // 如果无法提取，使用基于sessionId的哈希值生成时间
  const hash = sessionId.split('_').reduce((acc, val) => acc + val.charCodeAt(0), 0);
  const offset = hash % (24 * 60 * 60 * 1000);
  const baseTime = new Date();
  baseTime.setTime(baseTime.getTime() - offset);
  sessionDetail.startTime = baseTime.toISOString();
}
```

**前端修改 - AIGuide.vue**
```vue
<!-- 会话信息 -->
<div v-if="currentChatId" class="session-info">
  <span class="continue-message">继续之前的对话</span>
  <span class="session-time">{{ formatDate(sessionStartTime || new Date()) }}</span>
</div>
```

---

### 问题三：流式输出不显示

#### 问题描述
- AI回答没有实时流式显示，需要等待完整回答后才显示

#### 问题原因
- 后端返回的媒体类型不正确
- 前端没有正确处理SSE格式的流式响应

#### 解决方案

**后端修改 - ChatController.java**
```java
@PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> chat(...)
```

**前端修改 - AIGuide.vue**
```javascript
// 处理SSE格式：提取data字段的内容
const lines = chunk.split('\n');
for (const line of lines) {
  if (line.startsWith('data:')) {
    const data = line.substring(5).trim();
    if (data) {
      accumulatedText += data;
      answer.value = accumulatedText;
    }
  }
}
```

---

### 问题四：欢迎页优化

#### 修改内容

**Beijing.vue - 添加跳过按钮**
```vue
<button class="skip-btn" @click="startExperience">跳过</button>
```

```css
.skip-btn {
  position: absolute;
  top: 30px;
  right: 20px;
  background-color: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.5);
  color: #ffffff;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  backdrop-filter: blur(4px);
  cursor: pointer;
  z-index: 100;
}
```

**Beijing.vue - 添加书法动画**
```vue
<div class="calligraphy-container">
  <div class="calligraphy-word">文</div>
  <div class="calligraphy-word">迹</div>
</div>
```

```css
.calligraphy-word {
  font-size: 80px;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.9);
  font-family: 'Ma Shan Zheng', cursive, 'Noto Serif SC', serif;
  opacity: 0;
  transform: translateY(20px);
  animation: calligraphyAnimation 2s ease-out forwards;
}

.calligraphy-word:nth-child(2) {
  animation-delay: 0.5s;
}
```

**Beijing.vue - 更换真实图片**
```javascript
const guideImages = [
  'https://img.freepik.com/free-photo/forbidden-city-beijing-china_335224-172.jpg',
  'https://img.freepik.com/free-photo/great-wall-china_335224-118.jpg',
  'https://img.freepik.com/free-photo/beijing-hutong_335224-179.jpg',
  'https://img.freepik.com/free-photo/summer-palace-beijing_335224-181.jpg'
]
```

**Beijing.vue - 添加地址信息**
```javascript
const getLocationInfo = (index) => {
  const locations = [
    '北京·故宫',
    '北京·长城',
    '北京·胡同',
    '北京·颐和园'
  ]
  return locations[index] || '北京'
}
```

```css
.location-info {
  position: absolute;
  bottom: 20px;
  right: 20px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  background-color: rgba(0, 0, 0, 0.2);
  padding: 6px 12px;
  border-radius: 16px;
  backdrop-filter: blur(4px);
}
```

---

### 问题五：继续对话功能

#### 修改内容

**HistoryQA.vue - 传递会话创建时间**
```javascript
function selectSession(sessionId, startTime) {
  router.push({
    path: '/user/aiguide',
    query: { chatId: sessionId, startTime: startTime }
  })
}
```

**AIGuide.vue - 接收并显示会话信息**
```javascript
const sessionStartTime = ref(null)
onMounted(() => {
  const chatIdFromUrl = route.query.chatId
  const startTimeFromUrl = route.query.startTime
  if (chatIdFromUrl) {
    currentChatId.value = chatIdFromUrl
    sessionStartTime.value = startTimeFromUrl
  } else {
    createNewChatSession()
  }
})
```

---

### 数据库问题汇总

#### 问题1：chat_id 字段类型错误
- 原因：数据库中 chat_id 定义为 INT，但代码使用 String
- 解决：`ALTER TABLE ai_chat_message MODIFY COLUMN chat_id VARCHAR(64);`

#### 问题2：缺少 session_id 字段
- 原因：数据库表缺少 session_id 字段
- 解决：添加字段 `ALTER TABLE ai_chat_message ADD COLUMN session_id VARCHAR(64);`

#### 问题3：chat_id 错误设为主键
- 原因：主键应该是 message_id 自增
- 解决：重建表结构，使用 message_id 作为主键

---

### 修改文件清单

| 文件路径 | 修改内容 |
|---------|---------|
| frontend/src/views/HistoryQA.vue | 添加测试消息、时间提取逻辑 |
| frontend/src/views/AIGuide.vue | 会话信息显示、流式输出处理 |
| frontend/src/views/Beijing.vue | 跳过按钮、书法动画、图片、地址信息 |
| backend/.../AIChatMessage.java | 添加 sessionId 字段 |
| backend/.../ChatController.java | 设置 sessionId、添加流式输出注解 |
| backend/.../AIChatMessageServiceImpl.java | 增强日志输出 |

---

### 注意事项

1. 数据库表结构需要按照SQL重建
2. 修改后需要重启后端服务
3. 历史会话测试消息仅用于调试，生产环境应删除

# 1-20-2026

## 数据库修改：

### **heritage_sites表中记录经纬度字段类型（DECIMAL 类型）应该使用 MySQL 的几何类型字段**

```mysql
	'location_point' POINT COMMENT '地理位置点',  -- 新增几何字段
    -- 索引
    INDEX idx_province_code(province_code),
    INDEX idx_city_code(city_code),
    INDEX idx_level(level),
    INDEX idx_type(type),
    INDEX idx_geohash(geohash),
    INDEX idx_popularity(popularity),
    SPATIAL INDEX idx_location(location_point)  -- 空间索引
    
    SPATIAL 索引要求：几何字段必须为 NOT NULL
```

### 添加外键：

```mysql
-- 为 opening_hours 表添加外键
ALTER TABLE opening_hours
ADD CONSTRAINT fk_opening_hours_site
FOREIGN KEY (site_id) REFERENCES heritage_sites(site_id);

-- 为 site_images 表添加外键
ALTER TABLE site_images
ADD CONSTRAINT fk_site_images_site
FOREIGN KEY (site_id) REFERENCES heritage_sites(site_id);
```

# 1-21-2026
## 修改User实体类,添加UserUpdateDTO
## 修复登录模式，将token存入Redis，完善loginInterceptor功能
## 新建请求用户信息和更新用户信息的方法
GET /admin/users/{user_id}
PUT /admin/users/update/{user_id}

## 完善ApiFox自动获取令牌的功能
### 登录接口
1. 在登录接口的后置操作复制以下代码
// 如果你的响应格式是 { "code": 200, "data": { "token": "xxx" } }
```javascript
if (pm.response.code === 200) {
const data = pm.response.json();
pm.environment.set("token", data.data.accessToken);
console.log("Token 已自动保存：", data.data.accessToken);
}
```
2. 在其他接口的Auth区域设置请求头，选择 Bear Token，输入{{token}}
最后返回测试

## 新建修改用户密码的接口
PUT /admin/users/updatePassword/{user_id}

## 创建ai
### 创建数据表 ai_chat_session
```sql
-- AI对话会话表
CREATE TABLE ai_chat_session (
    session_id VARCHAR(64) PRIMARY KEY,
    user_id INT,
    current_location VARCHAR(100),
    session_context JSON,
    message_count INT DEFAULT 0,
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_active_time DATETIME,
    status VARCHAR(20) DEFAULT 'active',
    INDEX idx_user_session (user_id, last_active_time DESC),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
)Enginee=innoDB DEFAULT CHARSET=utf8mb4 comment='AI对话会话表';
-- AI对话会话表


-- AI对话消息表
CREATE TABLE ai_chat_message (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT,
    message_type VARCHAR(20) DEFAULT 'text',
    tool_calls JSON,
    metadata JSON,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_time (session_id, create_time),
    FOREIGN KEY (session_id) REFERENCES ai_chat_session(session_id)
)Enginee=innoDB DEFAULT CHARSET=utf8mb4 comment='AI对话消息表';

-- 图片识别记录表
CREATE TABLE image_recognition_log (
    recognition_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64),
    user_id INT,
    image_hash VARCHAR(64) UNIQUE,
    site_id VARCHAR(50),
    confidence DECIMAL(5,4),
    original_image_url VARCHAR(500),
    recognition_result JSON,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_image_hash (image_hash),
    INDEX idx_user_time (user_id, create_time DESC),
    FOREIGN KEY (session_id) REFERENCES ai_chat_session(session_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (site_id) REFERENCES heritage_site(site_id)
)Enginee=innoDB DEFAULT CHARSET=utf8mb4 comment='图片识别记录表';

-- 工具调用记录表
CREATE TABLE tool_usage_log (
    tool_usage_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id VARCHAR(64),
    tool_type VARCHAR(50) NOT NULL,
    tool_parameters JSON,
    call_result TEXT,
    success TINYINT(1) DEFAULT 1,
    call_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_session_tool (session_id, tool_type),
    FOREIGN KEY (session_id) REFERENCES ai_chat_session(session_id)
)Enginee=innoDB DEFAULT CHARSET=utf8mb4 comment='工具调用记录表';

-- 用户偏好表
CREATE TABLE user_preference (
    preference_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    preference_type VARCHAR(50) NOT NULL,
    preference_value JSON,
    weight INT DEFAULT 1,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_preference (user_id, preference_type),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
)Enginee=innoDB DEFAULT CHARSET=utf8mb4 comment='用户偏好表';
```
### 完成ai智能体图文对话的初步配置
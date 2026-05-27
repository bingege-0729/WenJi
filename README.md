<div align="center">

# 文迹 (WenJi) - 文化遗产探索App

</div>

<div align="center" font-size=">

![Version](https://img.shields.io/badge/version-1.0.0--beta-blue)
![Vue](https://img.shields.io/badge/Vue-3.5-brightgreen)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)
![License](https://img.shields.io/badge/license-MIT-green)

**基于地理位置的传统文化探索应用**

[功能特性](#功能特性) • [快速开始](#快速开始) • [配置说明](#配置说明) • [项目结构](#项目结构) • [技术架构](#技术架构)

</div>

---

## 项目简介

文迹App是一款融合**AR增强现实**、**AI智能讲解**、**地理定位**技术的文化遗产探索应用。用户可通过手机探索周边文化地标，体验AR文物展示，与AI书生进行智能对话，在行走中感受传统文化的魅力。

### 核心亮点

- 🗺️ **文化地图点亮** - 到访文化地标即可点亮地图，形成个人文化足迹
- 📱 **WebAR展示** - 无需下载App，浏览器即可体验AR文物展示
- 🤖 **AI智能讲解** - 大模型驱动的个性化文化讲解与诗词创作
- 🎮 **游戏化激励** - 经验值、徽章、等级体系，提升用户粘性

---

## 功能特性

| 模块 | 功能 | 描述 |
|------|------|------|
| 🗺️ 地图探索 | 周边景点 | 基于GPS定位展示周边文化遗产 |
| | 地图点亮 | 进入景点范围自动点亮，记录文化足迹 |
| 📱 AR体验 | 文物识别 | 扫描文物图片触发AR展示 |
| | 3D模型 | Three.js渲染文物3D模型 |
| 🤖 AI书生 | 智能对话 | 大模型驱动的文化问答 |
| | 诗词创作 | 根据地理位置生成个性化诗词 |
| 👤 用户系统 | 登录注册 | 手机号验证码登录 |
| | 个人中心 | 经验值、等级、徽章管理 |
| 📝 游记系统 | 发布游记 | 记录文化探索心得 |
| | 经验奖励 | 发布游记获得经验值 |

---

## 快速开始

### 环境要求

| 软件 | 版本要求 |
|------|---------|
| Docker | 20.0+ |
| Docker Compose | 2.0+ |
| （可选）Node.js | 20.0+ |
| （可选）Java | 17+ |

### 方式一：Docker一键部署（推荐）

#### 1. 启动服务

```bash
# 进入项目目录
cd WenJi

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

#### 2. 访问应用

| 服务 | 地址 |
|------|------|
| 前端应用 | http://localhost |
| 后端API | http://localhost:8080 |
| API文档 | http://localhost:8080/doc.html |

#### 3. 默认测试账号

```
手机号：13800138000
验证码：123456
```

### 方式二：手动部署

#### 后端启动

```bash
# 进入后端目录
cd backend

# 打包（跳过测试）
mvn clean package -DskipTests

# 启动（需先启动MySQL和Redis）
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=docker
```

#### 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build
```

---

## 服务管理

### 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 仅启动特定服务
docker-compose up -d mysql redis
docker-compose up -d backend
docker-compose up -d frontend
```

### 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（清除所有数据）
docker-compose down -v

# 仅停止特定服务
docker-compose stop backend
```

### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart backend
```

### 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 实时查看特定服务日志
docker-compose logs -f backend

# 查看最近100行日志
docker-compose logs --tail=100 backend
```

---

## 配置说明

### 环境变量配置

创建 `.env` 文件（可选）：

```env
# MySQL配置
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=wenji

# Redis配置
REDIS_PASSWORD=123321

# OpenAI API配置（AI功能必需）
OPENAI_API_KEY=your-api-key-here
OPENAI_BASE_URL=https://api.openai.com

# 服务端口
BACKEND_PORT=8080
FRONTEND_PORT=80
```

### 数据库配置

数据库配置文件：`mysql.cnf`

```ini
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
default-time-zone='+08:00'

[client]
default-character-set=utf8mb4
```

### 后端配置

配置文件：`backend/src/main/resources/application-docker.yml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/wenji
    username: root
    password: 123456
  
  redis:
    host: redis
    port: 6379
    password: 123321

  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: ${OPENAI_BASE_URL:https://api.openai.com}
```

### 前端配置

高德地图API配置：`frontend/src/api/index.js`

```javascript
// 高德地图API Key（需替换为自己的Key）
window._AMapSecurityConfig = {
  securityJsCode: 'your-security-code',
}
```

---

## 项目结构

```
WenJi/
├── backend/                          # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── Controller/       # 控制器层
│   │   │   │   ├── Service/          # 服务层
│   │   │   │   ├── Repository/       # 存储层
│   │   │   │   ├── Mapper/           # MyBatis映射
│   │   │   │   ├── Pojo/             # 实体类
│   │   │   │   ├── DTO/              # 数据传输对象
│   │   │   │   ├── VO/               # 视图对象
│   │   │   │   ├── Config/           # 配置类
│   │   │   │   ├── Interceptor/      # 拦截器
│   │   │   │   └── Common/           # 公共组件
│   │   │   └── resources/
│   │   │       ├── db/               # 数据库脚本
│   │   │       ├── application.yml   # 主配置
│   │   │       └── application-docker.yml
│   │   └── test/                     # 测试代码
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                         # 前端项目
│   ├── src/
│   │   ├── api/                      # API请求
│   │   ├── components/               # 组件库
│   │   │   ├── atoms/                # 基础组件
│   │   │   ├── molecules/            # 组合组件
│   │   │   └── organisms/            # 业务组件
│   │   ├── stores/                   # 状态管理
│   │   ├── views/                    # 页面视图
│   │   ├── data/                     # 静态数据
│   │   └── router/                   # 路由配置
│   ├── public/
│   │   ├── images/                   # 静态图片
│   │   ├── models/                   # 3D模型
│   │   └── targets/                  # AR目标库
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── docker-compose.yml                # Docker编排配置
├── mysql.cnf                         # MySQL配置
│
├── 项目介绍.md                        # 项目背景介绍
├── 项目与技术性分析报告.md             # 技术分析报告
├── 设计及创新性分析报告.md             # 创新性分析报告
└── 文迹测试文档.docx                  # 测试报告
```

---

## 技术架构

### 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3.5 + Vite 7 |
| 状态管理 | Pinia 3 |
| UI组件 | Element Plus + Tailwind CSS |
| 地图服务 | 高德地图 API + Leaflet |
| AR引擎 | MindAR.js + Three.js |
| 后端框架 | Spring Boot 3.3.5 |
| ORM框架 | MyBatis-Plus 3.5.7 |
| AI集成 | Spring AI 1.0.0-M6 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 |
| 认证 | JWT |
| 容器化 | Docker + Docker Compose |

### 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      用户层 (Client)                         │
│              H5 / PWA / Capacitor 打包                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      网关层 (Nginx)                          │
│              反向代理 / 静态资源 / 负载均衡                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     应用层 (Spring Boot)                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │
│  │Controller│  │ Service  │  │Repository│  │   AI     │    │
│  │   层     │→ │    层    │→ │    层    │  │ Advisor │    │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘    │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │  MySQL   │   │  Redis   │   │ OpenAI   │
        │   8.0    │   │    7     │   │   API    │
        └──────────┘   └──────────┘   └──────────┘
```

---

## 测试报告

项目已完成完整的测试覆盖：

| 测试类型 | 覆盖范围 | 结果 |
|---------|---------|------|
| 单元测试 | 用户登录、游记幂等性 | 通过 |
| 功能测试 | AR识别、AI对话流式响应 | 通过 |
| 压力测试 | 100并发，吞吐量28.5 req/sec | 通过 |
| 异常率 | 0% | 通过 |

详细测试报告见：`文迹测试文档.docx`

---

## 常见问题

### Q1: 启动后无法访问？

检查服务状态：
```bash
docker-compose ps
```

确保所有服务状态为 `Up`。

### Q2: 数据库连接失败？

等待MySQL完全启动（约30秒），查看日志：
```bash
docker-compose logs mysql
```

### Q3: AI对话无响应？

检查OpenAI API配置：
```bash
docker-compose exec backend env | grep OPENAI
```

### Q4: 如何重置数据库？

```bash
docker-compose down -v
docker-compose up -d
```

---

## 版本信息

- **版本号**: v1.0.0-beta
- **发布日期**: 2026年3月
- **开发团队**: 文迹开发组

---

## 许可证

本项目仅供学习和研究使用。

---

<div align="center">

**文迹 - 让传统文化触手可及**

</div>

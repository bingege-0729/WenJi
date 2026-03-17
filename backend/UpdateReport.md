# 用户个人主页功能优化及系统维护报告 (2026-03-16)

本次修改全面升级了用户个人主页功能，引入了积分激励与徽章成就系统，并解决了项目启动时的数据库连接及端口冲突问题。

---

## 1. 数据库架构升级 (Schema Upgrade)

为了支撑社交化和激励功能，对 `wenji` 数据库进行了如下扩展：

- **`user` 表**：新增 `points` (积分) 字段。
- **`travel_blog` (旅游心得表)**：支持用户上传图文感想，实现“打卡”功能。
- **`badge` & `user_badge` (徽章系统)**：定义成就体系，记录用户获得的荣誉。
- **`point_record` (积分流水)**：记录每一笔积分的来源（博客、徽章）与去向。
- **`mall_item` (积分商城)**：预置基础奖品数据。

---

## 2. 后端核心功能实现

### **文件上传与头像管理**
- **[CommonController.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Controller/CommonController.java)**：新增 `/common/upload` 接口，支持图片上传。
- **[FileServiceImpl.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Service/Impl/FileServiceImpl.java)**：实现本地文件存储逻辑，自动生成 UUID 文件名，防止命名冲突。
- **静态资源映射**：在 [WebConfig.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Config/WebConfig.java) 中配置了 `/uploads/**` 路径，确保上传的头像可被前端直接访问。

### **旅游博客与积分激励**
- **[TravelBlogController.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Controller/TravelBlogController.java)**：提供发布博客和查询个人足迹的接口。
- **[PointServiceImpl.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Service/Impl/PointServiceImpl.java)**：封装了积分变动逻辑，确保用户发布博客或获得徽章时，积分和流水记录同步更新。

### **徽章成就系统**
- **[BadgeServiceImpl.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Service/Impl/BadgeServiceImpl.java)**：实现了自动检查逻辑。
    - **博客类型**：发布首篇博客点亮“初试啼声”。
    - **足迹类型**：累计打卡 5 个不同景点点亮“足迹遍布”。

---

## 3. 前端交互优化

### **个人资料编辑**
- **[UserInfo.vue](file:///D:/develop/Projectes/WenJi/frontend/src/views/UserInfo.vue)**：
    - 优化头像修改逻辑：支持调用手机图库上传真实照片。
    - 增加上传进度反馈，保存时自动先上传文件再更新用户信息。

### **个人主页展示**
- **[ProfileView.vue](file:///D:/develop/Projectes/WenJi/frontend/src/views/ProfileView.vue)**：
    - **资产展示**：新增积分 (Points) 显示。
    - **成就展示**：动态拉取并展示用户已获得的徽章图标。
    - **足迹流**：以卡片形式展示用户的历史打卡博客（图片 + 感想）。

---

## 4. 系统稳定性修复 (Infrastructure Fixes)

- **数据库连接优化**：
    - 在 [application.yml](file:///D:/develop/Projectes/WenJi/backend/src/main/resources/application.yml) 中完善了 JDBC URL 参数（时区、SSL、编码等），解决了连接超时和握手失败问题。
    - 修复了 [application-test.yml](file:///D:/develop/Projectes/WenJi/backend/src/main/resources/application-test.yml) 的 YAML 格式错误。
- **依赖冲突处理**：从 [pom.xml](file:///D:/develop/Projectes/WenJi/backend/pom.xml) 中移除了与 MyBatis-Plus 冲突的 `spring-boot-starter-data-jdbc`。
- **端口管理**：手动终止了占用 8080 端口的僵尸进程，确保应用可顺利启动。

---

## 5. 待办与扩展建议

- [ ] **商城兑换逻辑**：后端已具备表结构，需进一步实现扣积分、减库存的 Service 逻辑。
- [ ] **云存储切换**：当前为本地存储，生产环境建议在 `FileServiceImpl` 中集成阿里云 OSS。
- [ ] **图片压缩**：前端或后端可增加图片压缩处理，节省存储空间和带宽。

# 代码修改记录及区别文档 (2026-03-15)

此文档记录了针对项目中的 Lint 警告、MyBatis-Plus 配置警告以及端口冲突问题的修复内容。

---

## 1. 通用工具与 DTO 类 (Common & DTO)

### [PageResult.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Common/PageResult.java)
- **修改前**: 使用了原始类型 `List`，导致泛型警告。
- **修改后**: 引入泛型 `T`，改为 `PageResult<T>`，并将 `records` 定义为 `List<T>`。
- **区别**: 提高了类型安全性，消除了 IDE 的 Raw Type 警告。

### [JwtUtils.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Common/Utils/JwtUtils.java)
- **修改前**: 包含未使用的 `import java.util.HashMap;`。
- **修改后**: 移除了该导入。
- **区别**: 代码更整洁。

### [UserUpdateDTO.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/DTO/UserUpdateDTO.java)
- **修改前**: 包含未使用的 `import java.time.LocalDateTime;`。
- **修改后**: 移除了该导入。
- **区别**: 消除 Unused Import 警告。

---

## 2. 实体类 (POJO / Entity) - 修复 MyBatis-Plus 警告

### [AIChatMessage.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Pojo/Entity/AI/AIChatMessage.java)
- **修改前**: 缺少 `@TableId` 注解，MyBatis-Plus 无法识别主键，启动时报 `Not found @TableId annotation`。
- **修改后**: 为 `messageId` 字段添加了 `@TableId(type = IdType.AUTO)`。同时清理了未使用的导入。
- **区别**: 解决了 MyBatis-Plus 无法使用 `ById` 系列方法的问题。

### [OpeningHours.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Pojo/OpeningHours.java)
- **修改前**: 缺少 `@TableId` 注解。
- **修改后**: 为 `hourId` 字段添加了 `@TableId(type = IdType.AUTO)`。
- **区别**: 解决了启动时的警告提示。

### [Region.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Pojo/Region.java)
- **修改前**: 缺少 `@TableId` 注解。
- **修改后**: 为 `regionCode` 字段添加了 `@TableId`。
- **区别**: 解决了启动时的警告提示。

### [SiteImage.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Pojo/SiteImage.java)
- **修改前**: 缺少 `@TableId` 注解。
- **修改后**: 为 `imageId` 字段添加了 `@TableId(type = IdType.AUTO)`。
- **区别**: 解决了启动时的警告提示。

### [User.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Pojo/User.java)
- **修改前**: 包含未使用的 `import javax.validation.constraints.Email;`。
- **修改后**: 移除了该导入。

---

## 3. Mapper 与持久层 (Mapper & XML)

### [UserMapper.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Mapper/UserMapper.java)
- **修改前**: 定义了名为 `updateById` 的方法。由于该接口继承了 `BaseMapper<User>`，与 MyBatis-Plus 自带的 `updateById` 冲突，导致启动警告。
- **修改后**: 将方法重命名为 `updateUserInfo`。并清理了未使用的导入。
- **区别**: 消除了方法注入冲突的警告，明确了自定义更新方法的用途。

### [UserMapper.xml](file:///D:/develop/Projectes/WenJi/backend/src/main/resources/com/example/Mapper/UserMapper.xml)
- **修改前**: `<update id="updateById">`。
- **修改后**: `<update id="updateUserInfo">`。
- **区别**: 与接口中的重命名保持一致。

### [RegionMapper.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Mapper/RegionMapper.java)
- **修改前**: 包含未使用的 `import org.apache.ibatis.annotations.Select;`。
- **修改后**: 移除了该导入。

---

## 4. 业务层与拦截器 (Service & Interceptor)

### [UserServiceImpl.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Service/Impl/UserServiceImpl.java)
- **修改前**: 调用 `userMapper.updateById(userId, userUpdateDTO)`。
- **修改后**: 改为调用 `userMapper.updateUserInfo(userId, userUpdateDTO)`。
- **区别**: 适配了 Mapper 层的方法重命名。

### [RefreshTokenInterceptor.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Interceptor/RefreshTokenInterceptor.java)
- **修改前**: `Claims claims = JwtUtils.parseToken(token);`，变量 `claims` 未使用。
- **修改后**: 直接调用 `JwtUtils.parseToken(token);`，不再接收返回值。
- **区别**: 消除 Unused Variable 警告。

### [AIChatMessageServiceImpl.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Service/Impl/AIChatMessageServiceImpl.java)
- **修改前**: 包含未使用的私有方法 `convertToAIChatMessage` 和 `getActiveChatIds`。
- **修改后**: 将这两个方法暂时注释掉。
- **区别**: 消除 Unused Method 警告，同时保留代码逻辑供未来参考。

---

## 5. 配置与系统问题 (Config & System)

### [LevelConfig.java](file:///D:/develop/Projectes/WenJi/backend/src/main/java/com/example/Config/LevelConfig.java)
- **修改前**: 包含未使用的 `import java.util.HashMap;` 和 `import java.util.Map;`。
- **修改后**: 移除了这些导入。

### **端口占用 (Port 8080 Conflict)**
- **问题**: 启动时报 8080 端口已被占用。
- **解决**: 查找并强制终止了 PID 为 12364 的进程。
- **结果**: 现在项目可以正常启动。

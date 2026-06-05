# 拥抱妈妈·爱在平安 - 后端服务

## 项目简介

本项目是"拥抱妈妈·爱在平安"微信小程序的后端服务，基于 **Spring Boot 2.7** + **MyBatis** + **MySQL 8.0** 构建。

**核心理念**：AI向善 - 技术隐形于爱，伦理嵌入代码

## 技术栈

- **Java**：17
- **Spring Boot**：2.7.18
- **MyBatis**：2.3.2
- **MySQL**：8.0+
- **Lombok**：1.18.30
- **Hutool**：5.8.25（工具类库）
- **FastJson2**：2.0.48（JSON处理）
- **Maven**：3.6+

## 项目结构

```
back/
├── src/main/java/com/hugmom/back/
│   ├── BackApplication.java         # 启动类
│   ├── common/                      # 通用组件
│   │   ├── Result.java             # 统一返回结果
│   │   ├── ResultCode.java         # 响应状态码枚举
│   │   ├── BusinessException.java  # 业务异常类
│   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   ├── config/                      # 配置类
│   │   ├── CorsConfig.java         # 跨域配置
│   │   └── WebMvcConfig.java       # Web MVC配置
│   ├── controller/                  # 控制器层
│   │   ├── AuthController.java     # 认证接口
│   │   ├── UserController.java     # 用户接口
│   │   ├── FamilyController.java   # 家庭成员接口
│   │   ├── HugController.java      # 拥抱接口
│   │   ├── SafetyController.java   # 报平安接口
│   │   └── MemorialController.java # 纪念空间接口
│   ├── service/                     # 服务层接口
│   │   └── impl/                   # 服务层实现
│   ├── mapper/                      # MyBatis Mapper
│   └── entity/                      # 实体类
│       ├── User.java
│       ├── FamilyMember.java
│       ├── HugRecord.java
│       ├── SafetyMessage.java
│       └── MemorialMessage.java
├── src/main/resources/
│   ├── mapper/                      # MyBatis XML
│   └── application.yml              # 配置文件
├── database/
│   └── schema.sql                   # 数据库建表脚本
├── pom.xml                          # Maven配置
└── README.md                        # 项目说明
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- IDE（推荐 IntelliJ IDEA）

### 2. 数据库初始化

执行数据库建表脚本：

```bash
mysql -u root -p < database/schema.sql
```

或者直接在 MySQL 客户端中执行 `database/schema.sql` 文件。

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hug_mom?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your-password  # 修改为你的数据库密码
```

### 4. 启动项目

**方式一：使用 Maven 命令**

```bash
mvn clean install
mvn spring-boot:run
```

**方式二：使用 IDEA**

1. 导入项目：`File` → `Open` → 选择 `back` 目录
2. 等待 Maven 依赖下载完成
3. 运行 `BackApplication.java` 主类

### 5. 验证启动

启动成功后，访问：

```
http://localhost:8080
```

看到以下输出表示启动成功：

```
========================================
  拥抱妈妈·爱在平安 后端服务启动成功！
  AI向善 · 技术隐形于爱
  访问地址: http://localhost:8080
========================================
```

## API 接口说明

### 统一响应格式

所有接口均返回以下格式：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { /* 具体数据 */ },
  "success": true
}
```

### 核心接口列表

| 模块 | 接口路径 | 说明 |
|------|---------|------|
| 认证 | `POST /api/auth/login` | 微信登录 |
| 用户 | `GET /api/user/info` | 获取用户信息 |
| 用户 | `PUT /api/user/settings` | 更新用户设置 |
| 家庭成员 | `GET /api/family/members` | 获取家庭成员列表 |
| 家庭成员 | `GET /api/family/member/{memberId}` | 获取成员详情 |
| 家庭成员 | `POST /api/family/member` | 添加成员 |
| 家庭成员 | `PUT /api/family/member/{memberId}` | 更新成员 |
| 拥抱 | `POST /api/hug/send` | 发送拥抱 |
| 拥抱 | `GET /api/hug/history/{memberId}` | 拥抱历史 |
| 拥抱 | `GET /api/hug/stats/{memberId}` | 拥抱统计 |
| 报平安 | `POST /api/safety/send` | 发送平安消息 |
| 报平安 | `GET /api/safety/history/{memberId}` | 平安消息历史 |
| 报平安 | `DELETE /api/safety/scheduled/{messageId}` | 取消定时消息 |
| 纪念空间 | `POST /api/memorial/message` | 创建纪念留言 |
| 纪念空间 | `GET /api/memorial/messages/{memberId}` | 获取纪念留言 |
| 纪念空间 | `DELETE /api/memorial/message/{messageId}` | 删除纪念留言 |
| 纪念空间 | `GET /api/memorial/photos/{memberId}` | 获取纪念相册 |

详细的 API 文档请参考前端项目的 `API.md` 文件。

## 伦理守护机制

本项目在代码层面嵌入了严格的伦理验证逻辑：

### 1. 拥抱功能伦理阻断

```java
// HugServiceImpl.java
if ("deceased".equals(member.getStatus())) {
    throw new BusinessException(ResultCode.ETHICS_DECEASED_NO_HUG);
}
```

**规则**：已故亲人（`status=deceased`）不允许使用拥抱功能，必须跳转到纪念空间。

### 2. 报平安功能伦理阻断

```java
// SafetyServiceImpl.java
if ("deceased".equals(member.getStatus())) {
    throw new BusinessException(ResultCode.ETHICS_DECEASED_NO_SAFETY);
}
```

**规则**：已故亲人不允许使用报平安功能。

### 3. 纪念空间功能伦理阻断

```java
// MemorialServiceImpl.java
if ("alive".equals(member.getStatus())) {
    throw new BusinessException(ResultCode.ETHICS_ALIVE_NO_MEMORIAL);
}
```

**规则**：健在亲人（`status=alive`）不允许使用纪念空间功能。

## 核心业务逻辑说明

### 当前阶段实现状态

本阶段为**基础架构搭建阶段**，核心业务逻辑已保留接口和方法结构，但部分高级功能使用 `TODO` 标注，待后续实现：

#### ✅ 已实现

- 完整的项目结构和分层架构
- 统一返回结果和异常处理
- 数据库表结构设计和初始化
- 所有 API 接口的基础CRUD操作
- **伦理守护逻辑**（已完整实现）
- MyBatis XML 映射配置

#### 📝 待实现（标记 TODO）

1. **微信登录集成**
   ```java
   // UserServiceImpl.java
   // TODO: 调用微信API，使用code换取openid
   ```

2. **JWT Token 生成与验证**
   ```java
   // AuthController.java
   // TODO: 生成真实的JWT Token
   ```

3. **情感分析算法**
   ```java
   // HugServiceImpl.java
   // TODO: 情感分析 - 根据拥抱时长和频率分析情感
   ```

4. **定时任务调度**
   ```java
   // SafetyServiceImpl.java
   // TODO: 如果是定时消息，需要放入定时任务队列
   ```

5. **文件存储服务**
   ```java
   // MemorialServiceImpl.java
   // TODO: 从存储服务获取该成员的所有照片Hash
   ```

## 测试数据

数据库初始化脚本已包含测试数据：

- **测试用户**：`user_test`
- **测试成员（健在）**：`member_001` - 王芳（妈妈）
- **测试成员（已故）**：`member_002` - 李明（爷爷）

可直接使用这些数据进行接口测试。

## 前后端联调

### 1. 修改前端 API 地址

编辑前端项目 `front/utils/request.ts`：

```typescript
const BASE_URL = {
  dev: 'http://localhost:8080',  // 指向后端服务
  prod: 'https://api.hugmom.com'
}
```

### 2. 测试接口

使用 Postman 或前端小程序直接调用接口：

**示例：获取家庭成员列表**

```bash
curl http://localhost:8080/api/family/members
```

预期返回：

```json
{
  "code": 200,
  "msg": "获取成功",
  "data": [
    {
      "memberId": "member_001",
      "name": "王芳",
      "nickname": "妈妈",
      "status": "alive",
      ...
    }
  ],
  "success": true
}
```

## 常见问题

### 1. 启动报错：`Table 'hug_mom.hm_user' doesn't exist`

**原因**：数据库未初始化

**解决**：执行 `database/schema.sql` 建表脚本

### 2. 连接数据库失败

**检查**：
- MySQL 服务是否启动
- `application.yml` 中的数据库配置是否正确
- 数据库用户是否有权限

### 3. Maven 依赖下载慢

**解决**：配置国内镜像源（阿里云）

编辑 `~/.m2/settings.xml`：

```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

### 4. Lombok 注解不生效

**解决**：
1. 安装 Lombok 插件（IDEA）
2. 启用注解处理器：`Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors` → 勾选 `Enable annotation processing`

## 开发建议

1. **数据库索引优化**：根据实际查询场景添加索引
2. **日志完善**：在关键业务节点添加日志记录
3. **单元测试**：补充 Service 层和 Mapper 层的单元测试
4. **接口文档**：使用 Swagger 或 Knife4j 生成在线 API 文档
5. **Redis 缓存**：对高频查询接口（如家庭成员列表）增加缓存
6. **Token 验证**：实现完整的 JWT 认证和拦截器

## License

MIT

---

**核心价值观**：技术隐形于爱，伦理嵌入代码

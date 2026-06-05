# 拥抱妈妈 · 爱在平安

> AI 向善 —— 用科技缩短亲情距离

"拥抱妈妈"是一个以"AI 向善"为核心理念的微信小程序公益项目，旨在通过科技手段帮助家庭成员传递情感、维系亲情连接。项目涵盖虚拟拥抱、平安报信、追思空间、爱的回响等功能模块，构建中国首个"有温度的家庭情感连接平台"。

---

## 功能概览

| 功能 | 说明 |
|------|------|
| **虚拟拥抱** | 长按屏幕触发拥抱互动，配合振动反馈与心形粒子动画，为家人传递温暖 |
| **平安报信** | 发送"我平安"消息给家人，支持暖词高亮、快捷短语、智能定时发送 |
| **追思空间** | 为已故亲人保留的静谧纪念空间，可留言与查看旧照，无动画、无音乐 |
| **爱的回响** | 双向情感闭环 —— 妈妈可语音/文字/表情回复，AI 辅助语音转文字与情感识别 |
| **年度爱的报告** | 自动生成年度拥抱、报信、距离等数据统计，支持分享 |
| **公益体系** | 每次拥抱贡献 0.001 公益值，解锁成就徽章，平台公益故事墙展示 |
| **家庭成员管理** | 添加、编辑、删除家庭成员，在/已故状态驱动全应用伦理路由 |

### 伦理设计

- 已故亲人不可接收拥抱或平安报信，自动引导至追思空间
- 追思模式禁用所有娱乐功能（无粒子动画、无 AR、无音乐）
- AI 功能需用户授权，照片仅存储哈希值保护隐私
- 所有错误提示使用温暖非技术性语言

---

## 技术栈

| 层级 | 技术 |
|------|------|
| **前端** | 微信小程序原生框架 + TypeScript、Vant Weapp、Three.js（心形粒子动画） |
| **后端** | Java 17、Spring Boot 2.7.18、MyBatis、MySQL 8.0+ |
| **认证** | JWT（jjwt 0.12.5） |
| **工具** | Hutool 5.8.25、FastJSON2、Lombok |
| **HTTP 客户端** | Spring WebFlux（调用微信 API） |
| **数据库连接池** | HikariCP |

---

## 项目结构

```
├── back/                       # 后端 - Spring Boot 服务
│   ├── pom.xml                 # Maven 配置
│   ├── database/
│   │   └── schema.sql          # 数据库建表与测试数据
│   └── src/main/java/com/hugmom/back/
│       ├── BackApplication.java
│       ├── config/             # JWT 拦截器、CORS、Web 配置
│       ├── controller/         # 8 个 REST Controller
│       ├── service/            # 业务逻辑层
│       ├── mapper/             # MyBatis Mapper
│       ├── entity/             # 数据实体
│       └── common/             # 统一响应、异常处理、JWT 工具
│
├── front/                      # 前端 - 微信小程序
│   ├── app.ts / app.json       # 小程序入口与配置
│   ├── pages/                  # 9 个页面
│   │   ├── index/              # 首页（家庭成员列表 + 功能入口）
│   │   ├── login/              # 微信授权登录
│   │   ├── member-add/         # 添加家庭成员
│   │   ├── hug/                # 虚拟拥抱
│   │   ├── memorial/           # 追思空间
│   │   ├── safety/             # 平安报信
│   │   ├── echo/               # 爱的回响主页
│   │   ├── echo-report/        # 年度报告详情
│   │   └── echo-welfare/       # 公益详情
│   ├── components/             # 组件（HeartParticles 等）
│   ├── utils/                  # 工具模块（HTTP 请求、交互、API）
│   └── types/                  # TypeScript 类型定义
│
└── embrace-mom-project/        # 项目文档
    ├── 项目书/                  # 功能书、技术栈文档
    ├── 爱的回响-前后端接口对照表.md
    ├── 前后端联调说明.md
    ├── 小程序接口介绍.md
    └── 拥抱妈妈开发手册(前端).md
```

---

## 快速开始

### 环境要求

- **JDK** 17+
- **Maven** 3.6+
- **MySQL** 8.0+
- **Node.js** 16+
- **微信开发者工具**（用于运行小程序前端）

### 后端启动

1. 创建数据库并导入表结构：

```bash
mysql -u root -p < back/database/schema.sql
```

2. 修改数据库配置（如需要）：

编辑 `back/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hug_mom
    username: root
    password: 123456
```

3. 编译并启动：

```bash
cd back
mvn clean compile
mvn spring-boot:run
```

服务启动后访问 `http://localhost:8080`。

### 前端启动

1. 安装依赖并编译：

```bash
cd front
npm install
npm run build
```

2. 使用**微信开发者工具**打开 `front/` 目录即可预览运行。

> 微信登录默认关闭（开发模式），可在 `application.yml` 中设置 `wechat.mini.enable: true` 开启。

---

## API 概览

所有接口以 `/api` 为前缀，使用 JWT Bearer Token 认证。

| 模块 | 路径前缀 | 主要接口 |
|------|---------|---------|
| 认证 | `/api/auth` | 微信登录、Token 校验 |
| 用户 | `/api/user` | 获取用户信息、更新设置 |
| 家庭成员 | `/api/family` | 成员 CRUD |
| 虚拟拥抱 | `/api/hug` | 发送拥抱、历史记录、统计 |
| 平安报信 | `/api/safety` | 发送消息、历史、定时取消 |
| 追思空间 | `/api/memorial` | 留言、照片 |
| 爱的回响 | `/api/echo` | 妈妈回复、年度报告、公益统计 |
| 文件上传 | `/api/upload` | 照片上传（返回文件哈希） |

---

## 数据库

数据库 `hug_mom` 包含 11 张表：

| 表名 | 说明 |
|------|------|
| `hm_user` | 用户表 |
| `hm_family_member` | 家庭成员 |
| `hm_hug_record` | 拥抱记录 |
| `hm_safety_message` | 平安报信消息 |
| `hm_memorial_message` | 追思留言 |
| `hm_mother_reply` | 妈妈回复 |
| `hm_annual_report` | 年度爱的报告 |
| `hm_welfare_contribution` | 公益贡献记录 |
| `hm_welfare_achievement` | 成就解锁记录 |
| `hm_welfare_story` | 公益故事 |
| `hm_welfare_platform_stats` | 平台公益统计 |

---

## 相关文档

- [前后端接口对照表](embrace-mom-project/爱的回响-前后端接口对照表.md)
- [前后端联调说明](embrace-mom-project/前后端联调说明.md)
- [小程序接口介绍](embrace-mom-project/小程序接口介绍.md)
- [拥抱妈妈开发手册（前端）](embrace-mom-project/拥抱妈妈开发手册(前端).md)
- [项目文件清单](embrace-mom-project/项目文件清单.md)

---

## License

MIT

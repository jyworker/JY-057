# 项目结构说明

## 完整目录树

```
front/
├── README.md                           # 项目说明文档
├── API.md                              # 后端接口文档
├── DEPLOY.md                           # 部署指南
├── CHANGELOG.md                        # 更新日志
├── PROJECT_STRUCTURE.md                # 项目结构说明（本文档）
├── .gitignore                          # Git忽略规则
├── package.json                        # 项目依赖配置
├── tsconfig.json                       # TypeScript配置
├── project.config.json                 # 小程序项目配置
├── sitemap.json                        # 小程序sitemap配置
├── app.json                            # 小程序全局配置
├── app.ts                              # 应用入口文件
├── app.wxss                            # 全局样式文件
│
├── types/                              # TypeScript类型定义
│   └── index.d.ts                      # 全局类型定义
│
├── utils/                              # 工具函数模块
│   ├── request.ts                      # 网络请求封装
│   ├── interactions.ts                 # 情感化交互工具
│   └── api.ts                          # API接口定义
│
├── components/                         # 自定义组件
│   └── HeartParticles/                 # 情感粒子组件
│       ├── index.ts                    # 组件逻辑
│       ├── index.wxml                  # 组件模板
│       ├── index.wxss                  # 组件样式
│       └── index.json                  # 组件配置
│
├── pages/                              # 页面目录
│   ├── login/                          # 登录页
│   │   ├── index.ts
│   │   ├── index.wxml
│   │   ├── index.wxss
│   │   └── index.json
│   │
│   ├── index/                          # 首页（家庭成员列表）
│   │   ├── index.ts
│   │   ├── index.wxml
│   │   ├── index.wxss
│   │   └── index.json
│   │
│   ├── hug/                            # 虚拟拥抱页
│   │   ├── index.ts                    # 核心交互逻辑
│   │   ├── index.wxml
│   │   ├── index.wxss
│   │   └── index.json
│   │
│   ├── memorial/                       # 纪念空间页
│   │   ├── index.ts                    # 伦理守护逻辑
│   │   ├── index.wxml
│   │   ├── index.wxss
│   │   └── index.json
│   │
│   └── safety/                         # 智能报平安页
│       ├── index.ts                    # 智能提醒逻辑
│       ├── index.wxml
│       ├── index.wxss
│       └── index.json
│
└── assets/                             # 静态资源
    ├── README.md                       # 资源说明
    ├── logo.png                        # 应用Logo
    ├── empty.png                       # 空状态图片
    ├── avatars/                        # 家庭成员头像
    │   └── {photoHash}.jpg
    └── photos/                         # 纪念相册照片
        └── {photoHash}.jpg
```

---

## 核心模块详解

### 1. 应用入口 (app.ts)
**职责：**
- 全局数据管理
- 登录状态检查
- 小程序版本更新管理

**关键代码：**
```typescript
globalData: {
  userInfo: any
  token: string
  settings: UserSettings
}
```

---

### 2. 类型定义 (types/index.d.ts)
**职责：**
- 定义所有数据模型
- 确保类型安全

**关键类型：**
- `FamilyMember`：家庭成员（含 `status: 'alive' | 'deceased'`）
- `ApiResponse<T>`：统一响应格式
- `HugRecord`、`SafetyMessage`、`MemorialMessage`

---

### 3. 网络请求模块 (utils/request.ts)
**职责：**
- 统一处理HTTP请求
- 自动携带Token
- 401跳转登录
- 温和化错误提示

**核心功能：**
- `request()`：通用请求方法
- `get()` / `post()` / `put()` / `del()`：便捷方法
- `getFriendlyErrorMessage()`：错误信息温和化

---

### 4. 情感化交互工具 (utils/interactions.ts)
**职责：**
- 提供拥抱震动反馈
- 暖心词高亮处理
- 智能时间判断

**核心函数：**
- `triggerHugVibration()`：拥抱震动（轻→重→轻）
- `highlightWarmWords()`：暖心词高亮
- `isLateNight()`：判断是否夜深
- `getGreeting()`：时间问候语

---

### 5. API接口定义 (utils/api.ts)
**职责：**
- 集中管理所有后端接口调用

**模块分类：**
- 用户相关：`login()`, `getUserInfo()`
- 家庭成员：`getFamilyMembers()`, `getFamilyMember()`
- 拥抱相关：`sendHug()`, `getHugHistory()`
- 报平安：`sendSafetyMessage()`, `getSafetyHistory()`
- 纪念空间：`createMemorialMessage()`, `getMemorialPhotos()`

---

### 6. 情感粒子组件 (components/HeartParticles)
**职责：**
- 使用Three.js渲染爱心粒子
- 提供拥抱加速效果

**关键方法：**
- `initThreeJS()`：初始化3D场景
- `createParticles()`：创建粒子系统
- `accelerate()`：加速粒子下落
- `dispose()`：清理资源（防止内存泄漏）

**性能优化：**
- 使用正交相机（省资源）
- 关闭抗锯齿
- 粒子数量控制在25个
- 严格的资源释放机制

---

### 7. 页面模块

#### 7.1 登录页 (pages/login)
- 微信授权登录
- Token存储
- 跳转首页

#### 7.2 首页 (pages/index)
- 展示家庭成员列表
- 根据 `status` 判断跳转目标：
  - `alive` → 虚拟拥抱页
  - `deceased` → 纪念空间页
- 支持下拉刷新

#### 7.3 虚拟拥抱页 (pages/hug)
**核心交互：**
- 长按屏幕触发拥抱
- 震动反馈 + 粒子加速
- 松开发送拥抱记录

**伦理阻断（关键）：**
```typescript
if (member.status === 'deceased') {
  // 强制跳转到纪念空间
  wx.redirectTo({
    url: `/pages/memorial/index?memberId=${memberId}`
  })
}
```

#### 7.4 纪念空间页 (pages/memorial)
**设计原则：**
- 静默守护：无粒子、无AR、无音乐
- 仅保留"查看老照片"和"留言"功能
- 禁用娱乐化功能

**伦理验证：**
```typescript
if (member.status !== 'deceased') {
  // 非已故成员不可进入
  wx.navigateBack()
}
```

#### 7.5 智能报平安页 (pages/safety)
**核心功能：**
- 文字输入
- 实时高亮暖心词
- 快捷短语
- 定时发送

**智能提醒：**
```typescript
if (isLateNight()) {
  // 夜深时自动建议定时发送
  wx.showToast({ title: '建议明早8点定时发送' })
}
```

---

## 数据流图

```
用户操作
   ↓
页面逻辑 (pages/*.ts)
   ↓
API调用 (utils/api.ts)
   ↓
网络请求 (utils/request.ts)
   ↓
后端服务器
   ↓
响应数据
   ↓
页面更新
```

---

## 伦理守护机制

### 核心判断逻辑

```typescript
if (member.status === 'alive') {
  // 健在亲人
  // ✅ 可以：拥抱、报平安、语音视频
} else if (member.status === 'deceased') {
  // 已故亲人
  // ✅ 可以：纪念空间、留言、查看照片
  // ❌ 禁止：拥抱、报平安、娱乐功能
}
```

### 关键检查点

1. **首页点击成员时**：根据 `status` 跳转不同页面
2. **拥抱页加载时**：验证 `status`，已故则跳转
3. **报平安页加载时**：验证 `status`，已故则返回
4. **纪念空间加载时**：验证 `status`，健在则返回

---

## 技术亮点

### 1. TypeScript类型安全
所有数据模型定义在 `types/index.d.ts`，确保类型安全。

### 2. 统一错误处理
网络请求自动处理401、错误提示温和化。

### 3. 情感化设计
- 拥抱震动：轻→重→轻节奏
- 暖心词高亮：温暖色调
- 即时反馈：不等待网络响应

### 4. 性能优化
- Three.js资源严格管理
- 图片懒加载
- 组件按需注册

### 5. 伦理守护
- 代码层面嵌入伦理验证
- 关键位置添加注释提示

---

## 扩展建议

### 未来可添加的功能
1. 语音转文字（报平安）
2. AR拥抱（仅健在亲人）
3. 拥抱统计图表
4. 家庭日历
5. 情感日记

### 性能优化方向
1. 使用分包加载
2. 接入CDN加速
3. 添加请求缓存
4. 优化图片加载

---

## 维护建议

### 代码规范
- 保持TypeScript严格模式
- 所有涉及伦理的逻辑添加注释
- 错误提示保持温和化

### 版本管理
- 遵循语义化版本号
- 每次更新记录在 `CHANGELOG.md`

### 测试要点
- 伦理阻断逻辑测试（核心）
- 网络异常处理测试
- 低端机型性能测试

---

**文档版本：** v1.0.0  
**更新时间：** 2026-02-01

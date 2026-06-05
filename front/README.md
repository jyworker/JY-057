# 拥抱妈妈·爱在平安 - 微信小程序前端项目

## 项目简介

"拥抱妈妈·爱在平安"是一款基于"AI向善"理念的情感科技小程序，旨在通过技术手段增强家庭成员之间的情感连接。项目核心特点是**伦理优先**，针对已故亲人提供静默守护的纪念空间，确保技术的人文关怀。

### 核心理念
- **AI向善**：技术隐形于爱，伦理嵌入代码
- **情感优先**：每一个交互都经过精心设计，传递温暖
- **伦理守护**：严格区分健在/已故亲人，提供差异化的情感体验

## 技术栈

- **框架**：微信小程序原生开发 + TypeScript
- **UI组件库**：Vant WeUI
- **3D引擎**：Three.js (three-platformize)
- **网络请求**：wx.request 封装
- **交互反馈**：wx.vibrate 触感反馈

## 项目结构

```
front/
├── app.json                    # 小程序配置
├── app.ts                      # 应用入口
├── app.wxss                    # 全局样式
├── project.config.json         # 项目配置
├── tsconfig.json               # TypeScript配置
├── package.json                # 依赖管理
├── types/
│   └── index.d.ts             # 类型定义
├── utils/
│   ├── request.ts             # 网络请求封装
│   ├── interactions.ts        # 情感化交互工具
│   └── api.ts                 # API接口定义
├── components/
│   └── HeartParticles/        # 情感粒子组件
│       ├── index.ts
│       ├── index.wxml
│       ├── index.wxss
│       └── index.json
└── pages/
    ├── login/                 # 登录页
    ├── index/                 # 首页（家庭成员列表）
    ├── hug/                   # 虚拟拥抱页
    ├── memorial/              # 纪念空间页
    └── safety/                # 智能报平安页
```

## 核心功能模块

### 1. 虚拟拥抱 (pages/hug)
- 长按屏幕触发拥抱交互
- "轻→重→轻"节奏震动反馈
- Three.js情感粒子动画
- **伦理阻断**：已故亲人自动跳转到纪念空间

### 2. 纪念空间 (pages/memorial)
- **静默原则**：无粒子、无AR、无音乐
- 仅保留"查看老照片"和"留言"功能
- 底部守护文案："思念无需喧嚣，爱在静默中长存"
- 禁用娱乐化功能（如视频生成）

### 3. 智能报平安 (pages/safety)
- 文字输入，实时高亮暖心词汇
- 快捷短语功能
- **智能提醒**：夜深时（22:00后）自动建议定时发送

### 4. 情感粒子组件 (components/HeartParticles)
- Three.js实现的爱心粒子系统
- 正交相机优化性能
- 提供`accelerate()`方法供拥抱交互调用
- 严格的资源释放机制，防止内存泄漏

### 5. 情感化交互工具 (utils/interactions.ts)
- `triggerHugVibration()`：拥抱震动反馈
- `highlightWarmWords()`：暖心词高亮处理
- `isLateNight()`：智能时间判断

## 安装与运行

### 1. 安装依赖

```bash
npm install
```

### 2. 编译TypeScript

```bash
npm run compile
```

### 3. 配置小程序AppID

编辑 `project.config.json`，修改 `appid` 字段为你的小程序AppID。

### 4. 导入微信开发者工具

1. 打开微信开发者工具
2. 导入项目，选择 `front` 文件夹
3. 编译运行

## 环境配置

### 修改API地址

编辑 `utils/request.ts` 中的 `BASE_URL` 配置：

```typescript
const ENV = 'dev' // 'dev' | 'prod'

const BASE_URL = {
  dev: 'http://localhost:8080',      // 开发环境
  prod: 'https://api.hugmom.com'     // 生产环境
}
```

## 核心设计原则

### 1. 伦理优先
- 所有涉及已故亲人的逻辑都包含伦理验证
- 关键位置添加了 `// 伦理阻断` 注释
- 示例代码：
  ```typescript
  if (member.status === 'deceased') {
    // 伦理阻断：强制跳转到纪念空间
    wx.redirectTo({
      url: `/pages/memorial/index?memberId=${memberId}`
    })
  }
  ```

### 2. 温和化交互
- 所有错误提示都经过"温和化"处理
- 例如：`网络开小差了，请稍后再试` 而非 `Network Error`

### 3. 即时情感反馈
- 拥抱操作不等待网络返回，立即显示"拥抱已送达"
- 优先保证情感体验的流畅性

### 4. 性能优化
- Three.js使用正交相机，关闭抗锯齿
- 粒子数量控制在25个，平衡视觉效果和性能
- 严格的资源释放机制

## 数据模型

详见 `types/index.d.ts`，核心类型包括：

- `FamilyMember`：家庭成员实体（含 `status: 'alive' | 'deceased'`）
- `HugRecord`：拥抱记录
- `SafetyMessage`：报平安消息
- `MemorialMessage`：纪念留言

## 后端对接

后端接口文档详见 `API.md`。

所有API遵循统一响应格式：
```typescript
{
  code: number,        // 200为成功, 401为未授权
  msg: string,
  data: T,
  success: boolean
}
```

## 注意事项

### 1. 资源文件
项目中引用的图片资源路径（如 `/assets/avatars/`）需要根据实际情况调整，或替换为网络图片。

### 2. Vant WeUI
首次运行前需要构建Vant组件：
```bash
npm run dev:weapp
```

### 3. Three.js适配
使用的是 `three-platformize` 库，已针对小程序环境优化。

### 4. 伦理逻辑测试
务必测试以下场景：
- 已故亲人进入拥抱页面 → 应强制跳转到纪念空间
- 已故亲人进入报平安页面 → 应提示不可用
- 纪念空间中的视频生成功能 → 应提示不可用

## 开发团队

本项目基于"AI向善"理念开发，致力于用技术传递温暖。

## License

MIT

---

**项目核心价值观**：技术隐形于爱，伦理嵌入代码。

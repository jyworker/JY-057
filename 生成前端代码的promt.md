你好，请作为一位拥有 10 年经验的微信小程序全栈架构师，根据以下详细的项目规格说明，为“拥抱妈妈·爱在平安”小程序生成完整的 `front` 前端项目代码。事后请生成相应的接口文档，给后端开发使用。

# 1. 项目背景与核心理念

* **项目名称**：拥抱妈妈·爱在平安
* **核心理念**：“AI向善” —— 技术隐形于爱，伦理嵌入代码。
* **关键约束**：
* **伦理优先**：针对已故亲人（Memorial Mode），严禁使用娱乐化功能，保持静默守护。
* **轻量化**：Three.js 必须优化内存，确保在低端机型流畅运行。
* **后端对接**：适配 Java SpringBoot 标准响应结构。



# 2. 技术栈 (Tech Stack)

* **框架**：微信小程序原生 (Native) + TypeScript
* **UI 组件库**：Vant WeUI
* **3D 引擎**：Three.js (小程序适配版 `three-platformize`)
* **工具**：`wx.request` 封装, `wx.vibrate` 触感反馈

# 3. 数据模型定义 (必须严格遵守)

请在 `types/index.d.ts` 中定义以下接口：

```typescript
// 通用响应结构
export interface ApiResponse<T> {
  code: number; // 200为成功, 401为未授权
  msg: string;
  data: T;
  success: boolean;
}

// 家庭成员实体
export interface FamilyMember {
  memberId: string;
  name: string;
  nickname: string;
  // 核心状态：决定是进入“虚拟拥抱”还是“纪念空间”
  status: 'alive' | 'deceased'; 
  photoHash: string; // 仅存储Hash，不存原始URL
  lastHugTime?: string;
}

// 用户设置
export interface UserSettings {
  emotionHighlight: boolean; // 是否开启暖心词高亮
  vibration: boolean;        // 是否开启震动
}

```

# 4. 待生成的核心模块与逻辑要求

请生成以下 5 个核心文件的代码，需包含详细注释说明设计意图。

## 模块 A: 网络与工具层 (utils)

**1. `utils/request.ts` (网络请求封装)**

* **BaseURL**: 支持 dev/prod 切换。
* **Header**: 自动携带 `Authorization: Bearer <token>`。
* **响应拦截**:
* 若 `code === 401`：清除缓存，跳转至 `/pages/login/index`。
* 若 `success === false`：统一使用 `wx.showToast` 提示错误。
* **伦理细节**：错误提示语需温和，例如网络错误提示为“网络开小差了，请稍后再试”，而非冷冰冰的报错。



**2. `utils/interactions.ts` (情感化交互工具)**

* **函数 `triggerHugVibration()**`:
* 实现文档规定的“轻→重→轻”节奏：
* 逻辑：先震动 15ms (轻触)，延迟 100ms；再震动 400ms (紧拥)，延迟 100ms；最后震动 15ms (轻放)。(注：若安卓/iOS震动API差异，请做兼容处理，优先保证节奏感)。


* **函数 `highlightWarmWords(text: string): string**`:
* **词库**：`['开心', '平安', '加油', '想你', '温暖', '幸福', '吃饱了', '考好了', '回家', '妈妈']`。
* **逻辑**：使用正则将匹配词替换为 `<span class="warm-highlight">${word}</span>`，以便在 `rich-text` 组件中渲染高亮样式（暖黄色）。



## 模块 B: 情感粒子组件 (components)

**3. `components/HeartParticles/index.ts` & `.wxml**`

* **场景**：作为虚拟拥抱页面的背景。
* **逻辑**：
* 初始化 Three.js `Scene` 和 `OrthographicCamera` (正交相机更省资源)。
* 创建 20-30 个爱心形状或暖色光点的 `Points`。
* **动画 Loop**：粒子缓慢下落，并叠加 `Math.sin` 实现左右轻微摇摆。
* **性能要求**：必须实现 `detached` 生命周期，调用 `renderer.dispose()` 和 `geometry.dispose()` 防止内存泄漏。
* **交互接口**：暴露 `accelerate()` 方法，当用户长按时，粒子下落速度加快，颜色趋向金黄色。



## 模块 C: 业务页面逻辑 (pages)

**4. `pages/hug/index.ts` (虚拟拥抱页 - 核心交互)**

* **功能**：长按屏幕进行虚拟拥抱。
* **核心逻辑**：
* `onLoad`：获取当前选中的 `FamilyMember`。
* **伦理阻断 (关键)**：检查 `member.status === 'deceased'`。如果是，**强制跳转**到纪念空间页 (`/pages/memorial/index`)，或弹窗提示并返回，绝对不允许进入拥抱流程。
* **长按 (Long Press)**：
1. 调用 `interactions.triggerHugVibration()`。
2. 调用粒子组件的 `accelerate()`。
3. 记录开始时间。


* **松开 (Touch End)**：
1. 调用 API `POST /api/hug/send`。
2. 不等待网络返回，直接反馈：“拥抱已送达妈妈！”（确保情感反馈的即时性）。





**5. `pages/memorial/index.ts` (纪念空间页 - 伦理守护)**

* **功能**：针对已故亲人的静默缅怀。
* **UI/逻辑要求**：
* **静默原则**：不加载任何 Three.js 粒子，不启用 AR 相机，不播放背景音乐。
* **底部文案**：页面底部必须渲染静态文本：“思念无需喧嚣，爱在静默中长存”。
* **功能限制**：仅保留“查看老照片”和“留言”功能。若用户试图点击“视频生成”等功能，弹出模态框提示：“该功能在纪念模式下不可用”。



**6. `pages/safety/index.ts` (智能报平安)**

* **功能**：语音转文字输入，智能推荐发送时间。
* **逻辑**：
* 输入框输入文字后，实时调用 `highlightWarmWords` 处理显示。
* **智能提醒**：`onLoad` 获取当前时间。若时间晚于 22:00，Toast 提示：“夜深了，建议勾选‘明早8点定时发送’，让妈妈睡个好觉”。



# 5. 代码输出要求

请直接输出上述文件的代码块，无需过多的Markdown解释，代码中请保留必要的中文注释，特别是涉及“AI向善”伦理逻辑的地方。
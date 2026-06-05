# 部署指南

## 部署前准备

### 1. 环境检查

确保已安装以下工具：
- Node.js >= 14.x
- npm >= 6.x
- 微信开发者工具

### 2. 代码检查

```bash
# 安装依赖
npm install

# 编译TypeScript
npm run compile

# 检查编译结果
```

---

## 开发环境部署

### 1. 配置开发环境API

编辑 `utils/request.ts`：

```typescript
const ENV = 'dev'  // 使用开发环境
```

### 2. 配置小程序AppID

编辑 `project.config.json`：

```json
{
  "appid": "你的测试AppID"
}
```

### 3. 导入微信开发者工具

1. 打开微信开发者工具
2. 选择"导入项目"
3. 项目目录选择 `front` 文件夹
4. 点击"导入"

### 4. 运行调试

- 点击"编译"按钮
- 在模拟器或真机上预览
- 调试控制台查看日志

---

## 生产环境部署

### 1. 切换到生产环境

编辑 `utils/request.ts`：

```typescript
const ENV = 'prod'  // 切换到生产环境

const BASE_URL = {
  dev: 'http://localhost:8080',
  prod: 'https://api.hugmom.com'  // 你的正式API地址
}
```

### 2. 配置正式AppID

编辑 `project.config.json`：

```json
{
  "appid": "正式小程序AppID"
}
```

### 3. 代码优化

- 确保所有调试代码已移除
- 检查 `console.log` 语句
- 压缩图片资源

### 4. 编译上传

```bash
# 编译TypeScript
npm run compile

# 检查编译结果
# 确保没有TypeScript错误
```

在微信开发者工具中：
1. 点击"上传"按钮
2. 填写版本号（如 `1.0.0`）
3. 填写项目备注
4. 点击"上传"

### 5. 提交审核

1. 登录[微信公众平台](https://mp.weixin.qq.com)
2. 进入"版本管理"
3. 选择刚上传的版本
4. 点击"提交审核"
5. 填写审核信息：
   - 服务类目：社交-问候/祝福
   - 标签：情感、家庭、AI
   - 功能描述：详细描述小程序功能和使用场景

---

## 配置检查清单

### 必须修改的配置

- [ ] `project.config.json` 中的 `appid`
- [ ] `utils/request.ts` 中的 `BASE_URL.prod`
- [ ] `utils/request.ts` 中的 `ENV` 变量

### 可选配置

- [ ] 全局样式 `app.wxss`
- [ ] 导航栏标题 `app.json`
- [ ] 资源文件路径

---

## 性能优化建议

### 1. 图片优化

```bash
# 使用TinyPNG等工具压缩图片
# 建议图片大小：
# - Logo: 200KB以内
# - 头像: 100KB以内
# - 其他图片: 500KB以内
```

### 2. 代码优化

- 使用分包加载（如有需要）
- 开启懒加载 `"lazyCodeLoading": "requiredComponents"`
- 减少全局组件注册

### 3. 请求优化

- 合并相同接口的请求
- 使用缓存机制
- 设置合理的请求超时时间

---

## 监控与日志

### 1. 错误监控

建议接入小程序错误监控平台：
- 微信小程序官方监控
- Sentry
- 阿里云ARMS

### 2. 日志上报

在 `app.ts` 中添加错误上报：

```typescript
App({
  onError(error: string) {
    // 上报错误到监控平台
    console.error('应用错误:', error)
  }
})
```

---

## 版本管理

### 版本号规范

采用语义化版本号：`主版本号.次版本号.修订号`

- **主版本号**：重大功能变更或不兼容更新
- **次版本号**：新增功能，向下兼容
- **修订号**：Bug修复

示例：
- `1.0.0`：首次发布
- `1.1.0`：新增报平安功能
- `1.1.1`：修复拥抱震动Bug

### 版本记录

在 `CHANGELOG.md` 中记录每个版本的更新内容。

---

## 常见问题

### Q1: 编译报错 "Cannot find module 'three-platformize'"

```bash
# 重新安装依赖
rm -rf node_modules
npm install
```

### Q2: 真机预览白屏

- 检查API地址是否可访问
- 检查Token是否正确配置
- 查看真机调试控制台错误日志

### Q3: 震动功能不生效

- 检查用户是否开启震动权限
- 检查手机是否开启静音/勿扰模式
- iOS和Android震动API有差异，已在代码中做兼容处理

---

## 回滚方案

如果线上版本出现问题：

1. 登录微信公众平台
2. 进入"版本管理"
3. 选择之前的稳定版本
4. 点击"回退"

---

## 技术支持

遇到部署问题，请检查：
1. [微信小程序官方文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)
2. 项目 `README.md`
3. 项目 `API.md`

---

**最后检查：**
- [ ] 所有功能测试通过
- [ ] 伦理守护逻辑测试通过
- [ ] 真机测试通过
- [ ] 已配置正式API地址
- [ ] 已配置正式AppID
- [ ] 版本号已更新

**祝部署顺利！**

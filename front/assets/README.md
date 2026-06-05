# Assets 资源文件夹

## 说明

此文件夹用于存放小程序的静态资源文件。

## 目录结构

```
assets/
├── logo.png                # 应用Logo
├── empty.png               # 空状态图片
├── avatars/                # 家庭成员头像
│   ├── a1b2c3d4.jpg
│   └── e5f6g7h8.jpg
└── photos/                 # 纪念相册照片
    ├── photo_hash_001.jpg
    └── photo_hash_002.jpg
```

## 注意事项

1. **头像文件命名**：使用photoHash值作为文件名，例如 `a1b2c3d4.jpg`
2. **照片文件命名**：同样使用Hash值命名
3. **图片格式**：推荐使用JPG或PNG格式
4. **图片大小**：建议单张图片不超过500KB，确保小程序加载速度

## 替代方案

如果不想使用本地资源，可以：

1. 使用CDN服务存储图片
2. 修改代码中的图片路径为网络地址
3. 例如：`https://cdn.hugmom.com/avatars/${photoHash}.jpg`

## 占位图片

开发阶段可以使用占位图片服务，例如：
- `https://via.placeholder.com/100x100`
- `https://picsum.photos/100/100`

## 当前状态

- **empty.png / logo.png**：若未放置文件，首页空状态与登录页 Logo 会使用内联占位图（1×1 透明），不会报 500。
- 建议将正式用的 `empty.png`（空状态图）、`logo.png`（应用 Logo）放入本目录后，在 `pages/index/index.wxml` 和 `pages/login/index.wxml` 中把 `src` 改回 `/assets/empty.png`、`/assets/logo.png`。

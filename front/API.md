# 拥抱妈妈·爱在平安 - 后端接口文档

## 基础说明

### 请求基础URL
- 开发环境：`http://localhost:8080`
- 生产环境：`https://api.hugmom.com`

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

**响应码说明：**
- `200`：请求成功
- `401`：未授权（Token失效或未登录）
- `400`：请求参数错误
- `500`：服务器内部错误

### 请求头

所有需要授权的接口必须携带以下Header：

```
Authorization: Bearer {token}
Content-Type: application/json
```

---

## 1. 用户认证模块

### 1.1 微信登录

**接口：** `POST /api/auth/login`

**描述：** 通过微信登录code换取用户Token

**请求参数：**
```json
{
  "code": "string"  // 微信登录code
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "userId": "user_123456",
      "nickName": "张三",
      "avatarUrl": "https://...",
      "phoneNumber": "138****8888"
    }
  },
  "success": true
}
```

---

### 1.2 获取用户信息

**接口：** `GET /api/user/info`

**描述：** 获取当前登录用户的详细信息

**请求Header：** 需要Token

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": {
    "userId": "user_123456",
    "nickName": "张三",
    "avatarUrl": "https://...",
    "phoneNumber": "138****8888"
  },
  "success": true
}
```

---

### 1.3 更新用户设置

**接口：** `PUT /api/user/settings`

**描述：** 更新用户的个性化设置

**请求Header：** 需要Token

**请求参数：**
```json
{
  "emotionHighlight": true,  // 是否开启暖心词高亮
  "vibration": true          // 是否开启震动反馈
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "设置更新成功",
  "data": {
    "emotionHighlight": true,
    "vibration": true
  },
  "success": true
}
```

---

## 2. 家庭成员模块

### 2.1 获取家庭成员列表

**接口：** `GET /api/family/members`

**描述：** 获取当前用户的所有家庭成员

**请求Header：** 需要Token

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": [
    {
      "memberId": "member_001",
      "name": "王芳",
      "nickname": "妈妈",
      "status": "alive",          // 'alive' | 'deceased'
      "photoHash": "a1b2c3d4",
      "lastHugTime": "2026-01-31T10:30:00Z",
      "relationship": "母亲",
      "birthday": "1965-03-15"
    },
    {
      "memberId": "member_002",
      "name": "李明",
      "nickname": "爷爷",
      "status": "deceased",
      "photoHash": "e5f6g7h8",
      "lastHugTime": null,
      "relationship": "祖父",
      "birthday": "1940-07-20"
    }
  ],
  "success": true
}
```

**关键字段说明：**
- `status`：**核心字段**，决定前端展示逻辑
  - `alive`：健在亲人，可进入虚拟拥抱页面
  - `deceased`：已故亲人，必须进入纪念空间页面
- `photoHash`：照片的Hash值，前端仅存储Hash，不存储原始URL

---

### 2.2 获取单个家庭成员详情

**接口：** `GET /api/family/member/{memberId}`

**描述：** 获取指定家庭成员的详细信息

**请求Header：** 需要Token

**路径参数：**
- `memberId`：成员ID

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": {
    "memberId": "member_001",
    "name": "王芳",
    "nickname": "妈妈",
    "status": "alive",
    "photoHash": "a1b2c3d4",
    "lastHugTime": "2026-01-31T10:30:00Z",
    "relationship": "母亲",
    "birthday": "1965-03-15"
  },
  "success": true
}
```

---

### 2.3 添加家庭成员

**接口：** `POST /api/family/member`

**描述：** 添加新的家庭成员

**请求Header：** 需要Token

**请求参数：**
```json
{
  "name": "王芳",
  "nickname": "妈妈",
  "status": "alive",
  "photoHash": "a1b2c3d4",
  "relationship": "母亲",
  "birthday": "1965-03-15"
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "添加成功",
  "data": {
    "memberId": "member_003",
    "name": "王芳",
    "nickname": "妈妈",
    "status": "alive",
    "photoHash": "a1b2c3d4",
    "lastHugTime": null,
    "relationship": "母亲",
    "birthday": "1965-03-15"
  },
  "success": true
}
```

---

### 2.4 更新家庭成员信息

**接口：** `PUT /api/family/member/{memberId}`

**描述：** 更新指定家庭成员的信息

**请求Header：** 需要Token

**路径参数：**
- `memberId`：成员ID

**请求参数：**
```json
{
  "name": "王芳",
  "nickname": "妈妈",
  "status": "alive",
  "photoHash": "a1b2c3d4",
  "relationship": "母亲",
  "birthday": "1965-03-15"
}
```

**响应数据：** 同添加接口

---

## 3. 虚拟拥抱模块

### 3.1 发送拥抱

**接口：** `POST /api/hug/send`

**描述：** 记录一次虚拟拥抱

**请求Header：** 需要Token

**请求参数：**
```json
{
  "memberId": "member_001",
  "duration": 3500,                      // 拥抱时长（毫秒）
  "timestamp": "2026-02-01T14:30:00Z"
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "拥抱记录成功",
  "data": {
    "hugId": "hug_20260201_001",
    "memberId": "member_001",
    "duration": 3500,
    "timestamp": "2026-02-01T14:30:00Z",
    "emotion": "温暖"
  },
  "success": true
}
```

**前端处理建议：**
- 此接口应异步调用，不阻塞用户体验
- 前端应立即显示"拥抱已送达"提示，无需等待响应

---

### 3.2 获取拥抱历史记录

**接口：** `GET /api/hug/history/{memberId}`

**描述：** 获取指定成员的拥抱历史

**请求Header：** 需要Token

**路径参数：**
- `memberId`：成员ID

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": [
    {
      "hugId": "hug_20260201_001",
      "memberId": "member_001",
      "duration": 3500,
      "timestamp": "2026-02-01T14:30:00Z",
      "emotion": "温暖"
    },
    {
      "hugId": "hug_20260131_002",
      "memberId": "member_001",
      "duration": 2800,
      "timestamp": "2026-01-31T10:30:00Z",
      "emotion": "思念"
    }
  ],
  "success": true
}
```

---

### 3.3 获取拥抱统计数据

**接口：** `GET /api/hug/stats/{memberId}`

**描述：** 获取指定成员的拥抱统计信息

**请求Header：** 需要Token

**路径参数：**
- `memberId`：成员ID

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": {
    "totalCount": 156,                    // 总拥抱次数
    "totalDuration": 518400,              // 总拥抱时长（毫秒）
    "lastHugTime": "2026-02-01T14:30:00Z" // 最后一次拥抱时间
  },
  "success": true
}
```

---

## 4. 报平安模块

### 4.1 发送平安消息

**接口：** `POST /api/safety/send`

**描述：** 发送或定时发送平安消息

**请求Header：** 需要Token

**请求参数：**
```json
{
  "memberId": "member_001",
  "content": "妈妈我吃饱了，今天工作顺利，你放心吧",
  "isScheduled": false,                   // 是否定时发送
  "scheduledTime": null                   // 定时发送时间（ISO格式）
}
```

**定时发送示例：**
```json
{
  "memberId": "member_001",
  "content": "妈妈早上好，今天也要开心哦",
  "isScheduled": true,
  "scheduledTime": "2026-02-02T08:00:00Z"
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "消息发送成功",
  "data": {
    "messageId": "msg_20260201_001",
    "content": "妈妈我吃饱了，今天工作顺利，你放心吧",
    "sendTime": "2026-02-01T14:30:00Z",
    "isScheduled": false,
    "scheduledTime": null,
    "status": "sent"                      // 'pending' | 'sent' | 'failed'
  },
  "success": true
}
```

---

### 4.2 获取平安消息历史

**接口：** `GET /api/safety/history/{memberId}`

**描述：** 获取指定成员的平安消息历史

**请求Header：** 需要Token

**路径参数：**
- `memberId`：成员ID

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": [
    {
      "messageId": "msg_20260201_001",
      "content": "妈妈我吃饱了，今天工作顺利",
      "sendTime": "2026-02-01T14:30:00Z",
      "isScheduled": false,
      "scheduledTime": null,
      "status": "sent"
    }
  ],
  "success": true
}
```

---

### 4.3 取消定时消息

**接口：** `DELETE /api/safety/scheduled/{messageId}`

**描述：** 取消尚未发送的定时消息

**请求Header：** 需要Token

**路径参数：**
- `messageId`：消息ID

**响应数据：**
```json
{
  "code": 200,
  "msg": "定时消息已取消",
  "data": null,
  "success": true
}
```

---

## 5. 纪念空间模块

### 5.1 创建纪念留言

**接口：** `POST /api/memorial/message`

**描述：** 为已故亲人创建纪念留言

**请求Header：** 需要Token

**请求参数：**
```json
{
  "memberId": "member_002",
  "content": "爷爷，今天是您的生日，我们都很想您",
  "photos": ["photo_hash_1", "photo_hash_2"]  // 可选：附加照片Hash数组
}
```

**响应数据：**
```json
{
  "code": 200,
  "msg": "留言创建成功",
  "data": {
    "messageId": "memorial_20260201_001",
    "memberId": "member_002",
    "content": "爷爷，今天是您的生日，我们都很想您",
    "createTime": "2026-02-01T14:30:00Z",
    "photos": ["photo_hash_1", "photo_hash_2"]
  },
  "success": true
}
```

**伦理约束：**
- 此接口仅适用于 `status === 'deceased'` 的成员
- 建议后端验证成员状态，若为 `alive` 则返回错误

---

### 5.2 获取纪念留言列表

**接口：** `GET /api/memorial/messages/{memberId}`

**描述：** 获取指定已故成员的所有纪念留言

**请求Header：** 需要Token

**路径参数：**
- `memberId`：成员ID

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": [
    {
      "messageId": "memorial_20260201_001",
      "memberId": "member_002",
      "content": "爷爷，今天是您的生日，我们都很想您",
      "createTime": "2026-02-01T14:30:00Z",
      "photos": ["photo_hash_1", "photo_hash_2"]
    }
  ],
  "success": true
}
```

---

### 5.3 删除纪念留言

**接口：** `DELETE /api/memorial/message/{messageId}`

**描述：** 删除指定的纪念留言

**请求Header：** 需要Token

**路径参数：**
- `messageId`：留言ID

**响应数据：**
```json
{
  "code": 200,
  "msg": "留言删除成功",
  "data": null,
  "success": true
}
```

---

### 5.4 获取纪念相册（老照片）

**接口：** `GET /api/memorial/photos/{memberId}`

**描述：** 获取指定已故成员的照片Hash列表

**请求Header：** 需要Token

**路径参数：**
- `memberId`：成员ID

**响应数据：**
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": [
    "photo_hash_001",
    "photo_hash_002",
    "photo_hash_003"
  ],
  "success": true
}
```

**前端处理：**
- 前端根据Hash构建图片URL：`/assets/photos/{hash}.jpg`
- 或通过单独的图片服务获取：`https://cdn.hugmom.com/photos/{hash}.jpg`

---

## 6. 文件上传模块

### 6.1 上传文件（照片）

**接口：** `POST /api/upload`

**描述：** 上传照片文件，返回Hash值

**请求Header：** 需要Token

**请求格式：** `multipart/form-data`

**请求参数：**
- `file`：文件流

**响应数据：**
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": {
    "fileHash": "a1b2c3d4e5f6"
  },
  "success": true
}
```

**前端调用示例：**
```typescript
wx.uploadFile({
  url: 'https://api.hugmom.com/api/upload',
  filePath: tempFilePath,
  name: 'file',
  header: {
    'Authorization': 'Bearer ' + token
  },
  success: (res) => {
    const data = JSON.parse(res.data)
    console.log('文件Hash:', data.data.fileHash)
  }
})
```

---

## 错误处理

### 常见错误响应

**401 未授权：**
```json
{
  "code": 401,
  "msg": "Token失效，请重新登录",
  "data": null,
  "success": false
}
```

**400 参数错误：**
```json
{
  "code": 400,
  "msg": "参数错误：memberId不能为空",
  "data": null,
  "success": false
}
```

**500 服务器错误：**
```json
{
  "code": 500,
  "msg": "服务器内部错误",
  "data": null,
  "success": false
}
```

---

## 伦理约束建议

### 后端验证规则

1. **拥抱功能验证：**
   - 检查 `member.status === 'alive'`
   - 若为 `deceased`，返回错误码 `403`，提示"该成员已进入纪念模式"

2. **报平安功能验证：**
   - 同上，仅允许健在亲人接收报平安消息

3. **纪念空间验证：**
   - 仅允许 `status === 'deceased'` 的成员使用纪念功能
   - 若为 `alive`，返回错误码 `403`，提示"该功能仅适用于纪念模式"

---

## 附录：时间格式说明

所有时间字段均使用 **ISO 8601** 格式：
```
2026-02-01T14:30:00Z
```

前端可使用以下方法转换：
```typescript
const isoString = new Date().toISOString()
const date = new Date(isoString)
```

---

## 联系方式

如有接口问题，请联系后端开发团队。

**文档版本：** v1.0.0  
**更新时间：** 2026-02-01

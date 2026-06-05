# 拥抱妈妈后端 - 用户提示词清单

---

## 0-1 代码生成（基于 PRD 或自然语言描述的新项目/完整模块构建）

**1.** 基于拥抱妈妈后端现有实体结构，新增"亲情提醒"模块，包含提醒表设计、MyBatis Mapper XML、Service业务层和REST Controller的完整实现，支持按成员设置周期性提醒并关联家庭成员表。

**2.** 为拥抱妈妈后端追思空间模块开发照片管理子功能，基于hm_memorial_message表现有photos字段，实现照片Hash上传、按成员聚合查询和JSON解析返回的完整CRUD接口及Mapper层SQL。

**3.** 基于Spring Boot搭建公益故事墙完整后端，包含hm_welfare_story表的CRUD Mapper、分页查询接口、故事发布审核控制器和内容匿名化处理逻辑，支持图文混排与地理位置脱敏展示功能。

**4.** 为拥抱妈妈后端新增平台级公益数据聚合服务，基于hm_welfare_contribution表实现每日定时任务汇总拥抱总数、公益价值、受助家庭数和活跃贡献者，并暴露REST接口供前端公益首页调用。

**5.** 为平安报信模块构建完整的定时消息调度系统，基于Spring Scheduler周期扫描hm_safety_message中status为pending的记录，结合微信模板消息API实现自动触发与推送通知，含超时重试机制。

**6.** 为拥抱功能接入AI情感分析引擎，根据用户拥抱频率、持续时长和时间段通过加权算法生成多维情感标签，替换HugServiceImpl中emotion字段硬编码为"温暖"的临时方案，含算法实现与单元测试。

**7.** 基于拥抱妈妈现有年度报告模块，新增"时光轴回忆"功能，从hm_hug_record和hm_safety_message表按时间线聚合交互事件，生成带月份粒度的JSON时间轴数据结构并暴露REST查询接口。

---

## Feature 迭代（在已有功能基础上的平滑扩展）

**8.** 优化EchoServiceImpl中recordHugContribution方法在高并发场景下的数据一致性问题，将基于ON DUPLICATE KEY UPDATE的累加方案改造为Redis原子计数器加定期同步策略，确保拥抱计数和贡献值精确。

**9.** 为FamilyController的家庭成员列表接口增加分页查询和按status、relationship、birthday维度的筛选功能，需改造FamilyMemberMapper的SQL支持动态条件拼接和LIMIT OFFSET分页参数传递。

**10.** 对妈妈回复模块进行批量已读标记改造，在现有markReplyAsRead单条接口基础上新增batch-read端点，同时引入WebSocket实时通知机制，使子女端在收到妈妈新回复时获得即时推送提醒。

**11.** 扩展拥抱妈妈后端用户数据导出功能，支持按年份范围将拥抱记录、平安消息和年度报告数据导出为Excel格式，涉及Apache POI文件生成、大数据量流式写入和异步下载链接生成的完整实现。

**12.** 在现有SafetyServiceImpl基础上扩展智能调度建议功能，当用户深夜发送平安消息时自动推荐定时发送选项，需新增时间段判断工具方法、推荐策略配置表和前端适配的REST响应结构。

**13.** 为现有年度报告generateAnnualReport方法增加"年度对比"维度，查询上一年度hm_annual_report记录计算拥抱增长率、暖日变化趋势和公益贡献增量，在返回结构中附加同比数据字段。

**14.** 在现有公益成就体系基础上新增"连续打卡"类成就，需扩展checkAndUnlockAchievements方法检测连续N天拥抱记录，涉及hm_hug_record表的连续日期窗口SQL查询和成就解锁幂等性保障。

---

## Bug 修复（逻辑修复、报错排查、安全漏洞修补）

**15.** 修复HugController中getHugHistory和getHugStats接口仅凭memberId查询而无userId归属校验的IDOR越权漏洞，需在Mapper层SQL添加user_id与member_id联合查询条件确保用户只能访问自己数据。

**16.** 修复EchoServiceImpl中calculateHugStats和calculateSafetyMessageCount方法返回硬编码假数据的问题，替换为基于hm_hug_record和hm_safety_message表的真实聚合SQL，按年统计拥抱次数和活跃天数。

**17.** 修复WechatServiceImpl.code2Session中appid和secret参数直接拼接到URL字符串导致的注入风险，改用Spring WebFlux的UriComponentsBuilder进行参数编码，并对openid返回值增加长度与格式校验。

**18.** 修复TokenInterceptor调用validateToken仅捕获异常返回布尔值导致的算法混淆和Token伪造漏洞，需在JwtUtil中指定签名算法为HS256、添加token黑名单机制并在响应头输出token剩余有效期。

**19.** 排查MemorialController.deleteMemorialMessage接口是否存在水平越权漏洞，当前DELETE语句缺少userId条件，需在Service层增加归属校验或在SQL中添加userId联合条件防止跨用户误删纪念留言。

**20.** 修复SafetyServiceImpl.sendSafetyMessage将定时消息标记为pending后无调度器执行实际发送的问题，需引入Spring Scheduler实现周期扫描任务，处理到期消息的状态流转和微信推送通知逻辑。

**21.** 修复MemorialServiceImpl.getMemorialPhotos方法返回硬编码模拟数据的问题，改造为从hm_memorial_message表按memberId聚合提取photos字段并解析JSON返回去重后的照片hash列表的完整查询逻辑。

---

## 代码理解（解释逻辑、寻找潜在风险、回答技术细节）

**22.** 梳理拥抱妈妈后端伦理守护机制的完整实现链路，分析HugServiceImpl、SafetyServiceImpl和MemorialServiceImpl中status字段如何驱动功能路由，以及ResultCode中三个伦理状态码的触发条件与前端适配逻辑。

**23.** 评估后端安全机制的完整覆盖情况，涵盖TokenInterceptor鉴权链路、CorsConfig跨域策略、JWT密钥管理、数据权限校验和异常信息泄露维度，列出生產部署前必须修复的关键安全缺陷并给出分级修复方案。

---

## 代码重构（性能优化、可读性提升、解耦）

**24.** 重构EchoServiceImpl，当前该类超过500行混合妈妈回复、年度报告、公益贡献和故事墙四个业务域，需按单一职责拆分为独立Service并通过Facade层统一对外接口，抽取公共数据聚合方法。

**25.** 分析HugServiceImpl与EchoService的强耦合问题，通过引入Spring ApplicationEvent将sendHug中的recordHugContribution调用改为异步事件发布，配置@EventListener和事务事件绑定消除循环依赖风险。

---

## 工程化（环境配置、依赖管理、CI/CD 相关）

**26.** 编写docker-compose.yml编排后端Spring Boot服务、MySQL 8.0和Redis三个容器，包含多环境变量注入、healthcheck依赖配置、数据库初始化脚本挂载和生产与开发profile分离的完整方案。

---

## 代码测试（单测、集成测试等）

**27.** 为EchoServiceImpl.checkAndUnlockAchievements方法编写参数化单元测试，覆盖拥抱次数在零次、十次、五十次和一百次阈值边界的行为，验证重复解锁幂等性和成就数据写入校验，使用Mockito隔离Mapper依赖。

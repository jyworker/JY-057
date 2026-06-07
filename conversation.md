# 对话记录 - 数据聚合服务设计与实现

## 用户需求
> 项目缺少数据统计模块。请设计数据聚合服务：日/周/月维度的拥抱次数趋势、情感分布热力图、公益贡献排行榜、用户活跃留存曲线。使用定时任务预聚合 + Redis 缓存，配合 ECharts 数据结构返回，支持管理员导出 CSV 报表。

## 实现方案

### 一、技术架构
- **后端**: Spring Boot 2.7.18 + Java 17
- **ORM**: MyBatis
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **定时任务**: Spring Scheduling + Quartz
- **序列化**: FastJson2 / Jackson

### 二、数据库设计（新增4张表）

#### 1. hm_hug_stats - 拥抱统计表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| stat_id | VARCHAR(64) | 统计唯一标识 |
| stat_period | VARCHAR(16) | 统计周期（day/week/month） |
| stat_type | VARCHAR(16) | 统计类型（platform/user/member） |
| stat_date | DATE | 统计日期 |
| year | INT | 年份 |
| month | INT | 月份 |
| week | INT | 周数 |
| hug_count | INT | 拥抱次数 |
| user_count | INT | 参与用户数 |
| member_count | INT | 参与成员数 |
| total_duration | BIGINT | 总拥抱时长（毫秒） |
| avg_duration | DOUBLE | 平均拥抱时长（毫秒） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 2. hm_emotion_stats - 情感分布统计表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| stat_id | VARCHAR(64) | 统计唯一标识 |
| stat_period | VARCHAR(16) | 统计周期 |
| stat_date | DATE | 统计日期 |
| year/ month / week | INT | 时间维度 |
| emotion | VARCHAR(32) | 情感标签 |
| count | INT | 出现次数 |
| ratio | DOUBLE | 占比 |

#### 3. hm_welfare_ranking - 公益贡献排行榜
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| stat_id | VARCHAR(64) | 统计唯一标识 |
| stat_period | VARCHAR(16) | 统计周期（day/week/month/all） |
| stat_date | DATE | 统计日期 |
| user_id | VARCHAR(64) | 用户ID |
| nick_name | VARCHAR(64) | 用户昵称 |
| avatar_url | VARCHAR(500) | 头像URL |
| hug_count | INT | 拥抱次数 |
| total_contribution | DECIMAL(15,4) | 总贡献值 |
| ranking | INT | 排名 |

#### 4. hm_user_retention_stats - 用户活跃留存统计表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| stat_id | VARCHAR(64) | 统计唯一标识 |
| stat_date | DATE | 统计日期（新增用户日期） |
| day1 / day3 / day7 / day14 / day30 | INT | 对应日期留存数 |
| new_user_count | INT | 当日新增用户数 |
| active_user_count | INT | 当日活跃用户数 |

### 三、核心代码文件清单

#### 实体类 (Entity)
- `back/src/main/java/com/hugmom/back/entity/HugStats.java`
- `back/src/main/java/com/hugmom/back/entity/EmotionStats.java`
- `back/src/main/java/com/hugmom/back/entity/WelfareRanking.java`
- `back/src/main/java/com/hugmom/back/entity/UserRetentionStats.java`

#### Mapper 层
- `back/src/main/java/com/hugmom/back/mapper/HugStatsMapper.java` + XML
- `back/src/main/java/com/hugmom/back/mapper/EmotionStatsMapper.java` + XML
- `back/src/main/java/com/hugmom/back/mapper/WelfareRankingMapper.java` + XML
- `back/src/main/java/com/hugmom/back/mapper/UserRetentionStatsMapper.java` + XML

#### 服务层
- `back/src/main/java/com/hugmom/back/service/StatsService.java` (接口)
- `back/src/main/java/com/hugmom/back/service/impl/StatsServiceImpl.java` (实现)

#### 控制器
- `back/src/main/java/com/hugmom/back/controller/StatsController.java`

#### 定时任务
- `back/src/main/java/com/hugmom/back/task/StatsScheduledTask.java`

#### 配置类
- `back/src/main/java/com/hugmom/back/config/RedisConfig.java`
- `back/src/main/java/com/hugmom/back/config/SchedulingConfig.java`

### 四、定时任务调度

| 任务 | Cron 表达式 | 执行时间 | 说明 |
|------|-------------|----------|------|
| dailyStatsTask | `0 10 0 * * ?` | 每天 00:10 | 预聚合昨日日统计 |
| weeklyStatsTask | `0 30 0 ? * MON` | 每周一 00:30 | 预聚合上周周统计 |
| monthlyStatsTask | `0 0 1 1 * ?` | 每月1日 01:00 | 预聚合上月月统计 |
| retentionStatsTask | `0 0 3 * * ?` | 每天 03:00 | 更新用户留存数据 |

### 五、API 接口文档

所有接口前缀: `/api/admin/stats`

#### 1. 拥抱次数趋势
```
GET /hug/trend
参数:
  - period: day/week/month (默认: day)
  - startDate: yyyy-MM-dd
  - endDate: yyyy-MM-dd
返回: ECharts 折线图格式
  {
    xAxis: [日期数组],
    hugCount: [拥抱次数数组],
    userCount: [用户数数组],
    avgDuration: [平均时长(秒)数组]
  }
```

#### 2. 情感分布热力图
```
GET /emotion/distribution
参数:
  - period: day/week/month (默认: day)
  - startDate: yyyy-MM-dd
  - endDate: yyyy-MM-dd
返回: ECharts 热力图格式
  {
    emotions: [情感标签数组],
    totalCounts: [总次数数组],
    xAxis: [日期数组],
    heatmapData: [{x, y, value}]
  }
```

#### 3. 公益贡献排行榜
```
GET /welfare/ranking
参数:
  - period: day/week/month/all (默认: month)
  - date: yyyy-MM-dd
  - limit: 数量 (默认: 20)
返回: ECharts 柱状图格式
  {
    userNames: [用户名数组],
    contributions: [贡献值数组],
    hugCounts: [拥抱次数数组],
    detailList: [{ranking, nickName, avatarUrl, ...}]
  }
```

#### 4. 用户活跃留存曲线
```
GET /user/retention
参数:
  - startDate: yyyy-MM-dd
  - endDate: yyyy-MM-dd
返回: ECharts 双轴图格式
  {
    xAxis: [日期数组],
    newUsers: [新增用户数],
    activeUsers: [活跃用户数],
    day1Rates: [次日留存率%],
    day7Rates: [7日留存率%],
    day30Rates: [30日留存率%]
  }
```

#### 5. CSV 报表导出
```
GET /export/{type}
参数:
  - type: hug/emotion/ranking/retention
  - startDate: yyyy-MM-dd
  - endDate: yyyy-MM-dd
返回: CSV 文件下载
```

#### 6. 手动触发聚合
```
POST /aggregate/daily?date=yyyy-MM-dd
POST /aggregate/weekly?date=yyyy-MM-dd
POST /aggregate/monthly?date=yyyy-MM-dd
```

### 六、Redis 缓存策略

- **缓存前缀**: `hugmom:stats:`
- **过期时间**: 12小时
- **缓存 Key 格式**:
  - 拥抱趋势: `hugmom:stats:hug:{period}:{startDate}:{endDate}`
  - 情感分布: `hugmom:stats:emotion:{period}:{startDate}:{endDate}`
  - 公益排行: `hugmom:stats:ranking:{period}:{date}:{limit}`
  - 用户留存: `hugmom:stats:retention:{startDate}:{endDate}`
- **缓存更新**: 每次定时任务执行后自动清除所有统计缓存

### 七、配置变更

#### 1. pom.xml 新增依赖
```xml
<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- 定时任务 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

#### 2. application.yml 新增配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
```

### 八、部署说明

1. **数据库初始化**: 执行 `back/database/schema.sql` 新增4张统计表
2. **Redis**: 确保 Redis 服务运行在 `localhost:6379`
3. **启动应用**: 定时任务会自动开启，首次可通过手动触发接口进行历史数据聚合
4. **验证**: 调用 `GET /api/admin/stats/hug/trend` 接口验证返回数据格式

### 九、项目编译验证

✅ 项目编译通过，无错误。

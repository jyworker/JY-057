-- ========================================
-- 拥抱妈妈·爱在平安 - 数据库建表脚本
-- MySQL 8.0+
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `hug_mom` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `hug_mom`;

-- ========================================
-- 1. 用户表 (hm_user)
-- ========================================
DROP TABLE IF EXISTS `hm_user`;
CREATE TABLE `hm_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户唯一标识（业务主键）',
  `openid` VARCHAR(128) NOT NULL COMMENT '微信OpenID',
  `unionid` VARCHAR(128) DEFAULT NULL COMMENT '微信UnionID',
  `nick_name` VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `phone_number` VARCHAR(32) DEFAULT NULL COMMENT '手机号（脱敏）',
  `emotion_highlight` TINYINT(1) DEFAULT 1 COMMENT '是否开启暖心词高亮（1:是 0:否）',
  `vibration` TINYINT(1) DEFAULT 1 COMMENT '是否开启震动反馈（1:是 0:否）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ========================================
-- 2. 家庭成员表 (hm_family_member)
-- ========================================
DROP TABLE IF EXISTS `hm_family_member`;
CREATE TABLE `hm_family_member` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` VARCHAR(64) NOT NULL COMMENT '成员唯一标识（业务主键）',
  `user_id` VARCHAR(64) NOT NULL COMMENT '所属用户ID',
  `name` VARCHAR(32) NOT NULL COMMENT '真实姓名',
  `nickname` VARCHAR(32) DEFAULT NULL COMMENT '昵称（如"妈妈"、"爷爷"）',
  `status` VARCHAR(16) NOT NULL COMMENT '成员状态（alive:健在 deceased:已故）',
  `photo_hash` VARCHAR(128) DEFAULT NULL COMMENT '照片Hash值（隐私保护）',
  `last_hug_time` DATETIME DEFAULT NULL COMMENT '最后拥抱时间',
  `relationship` VARCHAR(32) DEFAULT NULL COMMENT '关系（如"母亲"、"父亲"）',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_id` (`member_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭成员表（核心表）';

-- ========================================
-- 3. 拥抱记录表 (hm_hug_record)
-- ========================================
DROP TABLE IF EXISTS `hm_hug_record`;
CREATE TABLE `hm_hug_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `hug_id` VARCHAR(64) NOT NULL COMMENT '拥抱记录唯一标识（业务主键）',
  `user_id` VARCHAR(64) NOT NULL COMMENT '所属用户ID',
  `member_id` VARCHAR(64) NOT NULL COMMENT '成员ID',
  `duration` INT(11) NOT NULL COMMENT '拥抱时长（毫秒）',
  `timestamp` DATETIME NOT NULL COMMENT '拥抱时间戳',
  `emotion` VARCHAR(32) DEFAULT NULL COMMENT '情感标签（温暖、思念等）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hug_id` (`hug_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='拥抱记录表';

-- ========================================
-- 4. 报平安消息表 (hm_safety_message)
-- ========================================
DROP TABLE IF EXISTS `hm_safety_message`;
CREATE TABLE `hm_safety_message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_id` VARCHAR(64) NOT NULL COMMENT '消息唯一标识（业务主键）',
  `user_id` VARCHAR(64) NOT NULL COMMENT '所属用户ID',
  `member_id` VARCHAR(64) NOT NULL COMMENT '成员ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `send_time` DATETIME NOT NULL COMMENT '发送时间',
  `is_scheduled` TINYINT(1) DEFAULT 0 COMMENT '是否定时发送（1:是 0:否）',
  `scheduled_time` DATETIME DEFAULT NULL COMMENT '定时发送时间',
  `status` VARCHAR(16) NOT NULL COMMENT '消息状态（pending:待发送 sent:已发送 failed:发送失败）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_id` (`message_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_status` (`status`),
  KEY `idx_send_time` (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报平安消息表';

-- ========================================
-- 5. 纪念留言表 (hm_memorial_message)
-- ========================================
DROP TABLE IF EXISTS `hm_memorial_message`;
CREATE TABLE `hm_memorial_message` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `message_id` VARCHAR(64) NOT NULL COMMENT '留言唯一标识（业务主键）',
  `user_id` VARCHAR(64) NOT NULL COMMENT '所属用户ID',
  `member_id` VARCHAR(64) NOT NULL COMMENT '成员ID（仅限status=deceased）',
  `content` TEXT NOT NULL COMMENT '留言内容',
  `photos` TEXT DEFAULT NULL COMMENT '附加照片Hash数组（JSON格式）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_message_id` (`message_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='纪念留言表（伦理守护表）';

-- ========================================
-- 初始化测试数据（可选）
-- ========================================

-- 插入测试用户
INSERT INTO `hm_user` (`user_id`, `openid`, `nick_name`, `avatar_url`, `emotion_highlight`, `vibration`) 
VALUES ('user_test', 'mock_openid_test', '测试用户', 'https://thirdwx.qlogo.cn/default.png', 1, 1);

-- 插入测试家庭成员（健在）
INSERT INTO `hm_family_member` (`member_id`, `user_id`, `name`, `nickname`, `status`, `photo_hash`, `relationship`, `birthday`) 
VALUES ('member_001', 'user_test', '王芳', '妈妈', 'alive', 'photo_hash_mom', '母亲', '1965-03-15');

-- 插入测试家庭成员（已故）
INSERT INTO `hm_family_member` (`member_id`, `user_id`, `name`, `nickname`, `status`, `photo_hash`, `relationship`, `birthday`) 
VALUES ('member_002', 'user_test', '李明', '爷爷', 'deceased', 'photo_hash_grandpa', '祖父', '1940-07-20');

-- 插入测试拥抱记录
INSERT INTO `hm_hug_record` (`hug_id`, `user_id`, `member_id`, `duration`, `timestamp`, `emotion`) 
VALUES ('hug_20260201_001', 'user_test', 'member_001', 3500, '2026-02-01 14:30:00', '温暖');

-- 插入测试报平安消息
INSERT INTO `hm_safety_message` (`message_id`, `user_id`, `member_id`, `content`, `send_time`, `is_scheduled`, `status`) 
VALUES ('msg_20260201_001', 'user_test', 'member_001', '妈妈我吃饱了，今天工作顺利，你放心吧', '2026-02-01 14:30:00', 0, 'sent');

-- 插入测试纪念留言
INSERT INTO `hm_memorial_message` (`message_id`, `user_id`, `member_id`, `content`, `photos`) 
VALUES ('memorial_20260201_001', 'user_test', 'member_002', '爷爷，今天是您的生日，我们都很想您', '["photo_hash_1","photo_hash_2"]');

-- ========================================
-- 6. 妈妈回复表 (hm_mother_reply) - 爱的回响功能
-- ========================================
DROP TABLE IF EXISTS `hm_mother_reply`;
CREATE TABLE `hm_mother_reply` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reply_id` VARCHAR(64) NOT NULL COMMENT '回复唯一标识（业务主键）',
  `user_id` VARCHAR(64) NOT NULL COMMENT '接收回复的用户ID',
  `member_id` VARCHAR(64) NOT NULL COMMENT '发送回复的成员ID（妈妈）',
  `original_message_id` VARCHAR(64) NOT NULL COMMENT '原始消息ID（拥抱或平安消息）',
  `original_message_type` VARCHAR(16) NOT NULL COMMENT '原始消息类型（hug:拥抱 safety:平安消息）',
  `reply_type` VARCHAR(16) NOT NULL COMMENT '回复类型（voice:语音 text:文字 emoji:表情）',
  `content` TEXT COMMENT '回复内容',
  `voice_url` VARCHAR(500) DEFAULT NULL COMMENT '语音文件URL（如果是语音回复）',
  `ai_transcript` TEXT DEFAULT NULL COMMENT 'AI转文字结果（语音消息时生成）',
  `emotion` VARCHAR(16) DEFAULT NULL COMMENT '情感标签（warm:温暖 caring:关心 proud:骄傲）',
  `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读（1:已读 0:未读）',
  `receive_time` DATETIME NOT NULL COMMENT '接收时间',
  `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_reply_id` (`reply_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_original_message_id` (`original_message_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_receive_time` (`receive_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='妈妈回复表 - 双向温暖闭环';

-- ========================================
-- 7. 年度爱的报告表 (hm_annual_report) - 爱的回响功能
-- ========================================
DROP TABLE IF EXISTS `hm_annual_report`;
CREATE TABLE `hm_annual_report` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_id` VARCHAR(64) NOT NULL COMMENT '报告唯一标识（业务主键）',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
  `year` INT(11) NOT NULL COMMENT '报告年份',
  `total_hugs` INT(11) DEFAULT 0 COMMENT '总拥抱次数',
  `total_safety_messages` INT(11) DEFAULT 0 COMMENT '总平安消息次数',
  `total_distance` DECIMAL(10,2) DEFAULT 0.00 COMMENT '跨越的总里程（公里）',
  `warm_days` INT(11) DEFAULT 0 COMMENT '温暖的日夜数',
  `most_active_month` VARCHAR(16) DEFAULT NULL COMMENT '最活跃月份',
  `favorite_message` TEXT DEFAULT NULL COMMENT '最常发送的话语',
  `member_stats` JSON DEFAULT NULL COMMENT '成员统计数据（JSON格式）',
  `milestones` JSON DEFAULT NULL COMMENT '里程碑记录（JSON格式）',
  `public_welfare_contribution` DECIMAL(10,4) DEFAULT 0.0000 COMMENT '公益贡献值',
  `helped_families_count` INT(11) DEFAULT 0 COMMENT '帮助的孤寡妈妈家庭数',
  `generate_time` DATETIME NOT NULL COMMENT '生成时间',
  `share_poster_url` VARCHAR(500) DEFAULT NULL COMMENT '分享海报URL',
  `share_code` VARCHAR(32) DEFAULT NULL COMMENT '分享码',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_id` (`report_id`),
  UNIQUE KEY `uk_user_year` (`user_id`, `year`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_year` (`year`),
  KEY `idx_generate_time` (`generate_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='年度爱的报告表';

-- ========================================
-- 8. 公益贡献记录表 (hm_welfare_contribution) - 爱的回响功能
-- ========================================
DROP TABLE IF EXISTS `hm_welfare_contribution`;
CREATE TABLE `hm_welfare_contribution` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `contribution_id` VARCHAR(64) NOT NULL COMMENT '贡献记录唯一标识',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
  `date` DATE NOT NULL COMMENT '贡献日期',
  `hug_count` INT(11) DEFAULT 0 COMMENT '当日拥抱次数',
  `contribution_value` DECIMAL(10,4) DEFAULT 0.0000 COMMENT '贡献值（每次拥抱0.001）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contribution_id` (`contribution_id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `date`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户公益贡献记录表';

-- ========================================
-- 9. 公益成就表 (hm_welfare_achievement) - 爱的回响功能
-- ========================================
DROP TABLE IF EXISTS `hm_welfare_achievement`;
CREATE TABLE `hm_welfare_achievement` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `achievement_id` VARCHAR(64) NOT NULL COMMENT '成就唯一标识',
  `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
  `name` VARCHAR(64) NOT NULL COMMENT '成就名称',
  `description` TEXT DEFAULT NULL COMMENT '成就描述',
  `icon` VARCHAR(500) DEFAULT NULL COMMENT '成就图标URL',
  `unlocked_at` DATETIME NOT NULL COMMENT '解锁时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_achievement` (`user_id`, `achievement_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_unlocked_at` (`unlocked_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户公益成就表';

-- ========================================
-- 10. 公益故事表 (hm_welfare_story) - 爱的回响功能
-- ========================================
DROP TABLE IF EXISTS `hm_welfare_story`;
CREATE TABLE `hm_welfare_story` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `story_id` VARCHAR(64) NOT NULL COMMENT '故事唯一标识',
  `title` VARCHAR(128) NOT NULL COMMENT '故事标题',
  `content` TEXT NOT NULL COMMENT '故事内容（匿名化处理）',
  `location` VARCHAR(64) DEFAULT NULL COMMENT '地区（脱敏）',
  `help_date` DATE NOT NULL COMMENT '帮助日期',
  `image_urls` JSON DEFAULT NULL COMMENT '图片URL数组（JSON格式）',
  `is_published` TINYINT(1) DEFAULT 1 COMMENT '是否发布（1:已发布 0:未发布）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_story_id` (`story_id`),
  KEY `idx_help_date` (`help_date`),
  KEY `idx_is_published` (`is_published`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公益故事表（匿名化）';

-- ========================================
-- 11. 平台公益统计表 (hm_welfare_platform_stats) - 爱的回响功能
-- ========================================
DROP TABLE IF EXISTS `hm_welfare_platform_stats`;
CREATE TABLE `hm_welfare_platform_stats` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `total_platform_hugs` BIGINT(20) DEFAULT 0 COMMENT '平台累计拥抱次数',
  `total_welfare_value` DECIMAL(15,2) DEFAULT 0.00 COMMENT '累计公益价值（元）',
  `helped_families_count` INT(11) DEFAULT 0 COMMENT '已帮助的孤寡妈妈家庭数',
  `current_month_hugs` INT(11) DEFAULT 0 COMMENT '本月拥抱次数',
  `realtime_contributors` INT(11) DEFAULT 0 COMMENT '当前活跃贡献者数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date` (`stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='平台公益统计表';

-- ========================================
-- 初始化"爱的回响"测试数据
-- ========================================

-- 插入测试妈妈回复
INSERT INTO `hm_mother_reply` (`reply_id`, `user_id`, `member_id`, `original_message_id`, `original_message_type`, `reply_type`, `content`, `emotion`, `is_read`, `receive_time`) 
VALUES ('reply_20260201_001', 'user_test', 'member_001', 'hug_20260201_001', 'hug', 'text', '宝贝，妈妈也想你了，注意保暖！', 'warm', 0, '2026-02-01 15:00:00');

-- 插入平台公益统计（初始化数据）
INSERT INTO `hm_welfare_platform_stats` (`stat_date`, `total_platform_hugs`, `total_welfare_value`, `helped_families_count`, `current_month_hugs`, `realtime_contributors`) 
VALUES ('2026-02-04', 10000, 100.00, 10, 500, 20);

-- 插入测试公益故事
INSERT INTO `hm_welfare_story` (`story_id`, `title`, `content`, `location`, `help_date`, `is_published`) 
VALUES ('story_001', '春节的温暖', '在武汉的一位独居老人收到了来自平台的温暖关怀...', '湖北省', '2026-01-25', 1);

-- ========================================
-- 执行完毕
-- ========================================

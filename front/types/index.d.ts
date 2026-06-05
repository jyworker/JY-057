/**
 * 拥抱妈妈·爱在平安 - 类型定义文件
 * 严格遵守数据模型定义，确保类型安全
 */

// ========== 通用响应结构 ==========
export interface ApiResponse<T> {
  code: number;        // 200为成功, 401为未授权
  msg: string;         // 响应消息
  data: T;             // 响应数据
  success: boolean;    // 是否成功
}

// ========== 家庭成员实体 ==========
export interface FamilyMember {
  memberId: string;              // 成员唯一标识
  name: string;                  // 真实姓名
  nickname: string;              // 昵称（如"妈妈"、"爷爷"）
  status: 'alive' | 'deceased';  // 核心状态：决定进入"虚拟拥抱"还是"纪念空间"
  photoHash: string;             // 照片Hash值（仅存Hash，不存原始URL，保护隐私）
  lastHugTime?: string;          // 最后一次拥抱时间（ISO格式）
  relationship?: string;         // 关系（如"母亲"、"父亲"）
  birthday?: string;             // 生日
}

// ========== 用户设置 ==========
export interface UserSettings {
  emotionHighlight: boolean;     // 是否开启暖心词高亮
  vibration: boolean;            // 是否开启震动反馈
}

// ========== 用户信息 ==========
export interface UserInfo {
  userId: string;
  nickName: string;
  avatarUrl: string;
  phoneNumber?: string;
}

// ========== 拥抱记录 ==========
export interface HugRecord {
  hugId: string;
  memberId: string;
  duration: number;              // 拥抱时长（毫秒）
  timestamp: string;             // ISO格式时间戳
  emotion?: string;              // 情感标签（可选）
}

// ========== 报平安消息 ==========
export interface SafetyMessage {
  messageId: string;
  content: string;               // 消息内容
  sendTime: string;              // 发送时间
  isScheduled: boolean;          // 是否定时发送
  scheduledTime?: string;        // 定时发送时间
  status: 'pending' | 'sent' | 'failed';  // 消息状态
}

// ========== 纪念留言 ==========
export interface MemorialMessage {
  messageId: string;
  memberId: string;
  content: string;
  createTime: string;
  photos?: string[];             // 附加照片Hash数组
}

// ========== API请求参数类型 ==========

// 登录请求
export interface LoginRequest {
  code: string;                  // 微信登录code
}

// 发送拥抱请求
export interface SendHugRequest {
  memberId: string;
  duration: number;              // 拥抱时长（毫秒）
  timestamp: string;
}

// 发送平安消息请求
export interface SendSafetyRequest {
  memberId: string;
  content: string;
  isScheduled: boolean;
  scheduledTime?: string;
}

// 创建纪念留言请求
export interface CreateMemorialRequest {
  memberId: string;
  content: string;
  photos?: string[];
}

// ========== 爱的回响 · 双向温暖闭环相关类型 ==========

// 妈妈回复类型
export interface MotherReply {
  replyId: string;
  originalMessageId: string;        // 原始消息ID（拥抱或平安消息）
  replyType: 'voice' | 'text' | 'emoji';  // 回复类型
  content?: string;                  // 回复内容
  textContent?: string;              // 文字内容
  emojiContent?: string;             // 表情内容
  voiceUrl?: string;                 // 语音URL
  aiTranscript?: string;             // AI转文字结果（语音消息时有）
  emotion?: 'warm' | 'caring' | 'proud';  // 情感标签
  receiveTime: string;               // 接收时间
  isRead: boolean;                   // 是否已读
  motherName?: string;               // 妈妈名称
}

// 发送妈妈回复请求
export interface SendMotherReplyRequest {
  originalMessageId: string;
  replyType: 'voice' | 'text' | 'emoji';
  voiceUrl?: string;                 // 语音文件URL（语音消息时必填）
  textContent?: string;              // 文字内容（文字消息时必填）
}

// 年度爱的报告
export interface AnnualLoveReport {
  reportId: string;
  year: number;
  userId: string;
  generateTime: string;
  summary: {
    totalHugs: number;               // 发送拥抱次数
    totalSafetyMessages: number;     // 平安消息次数
    totalDistance: number;           // 跨越的总里程（公里）
    warmDays: number;                // 温暖的日夜数
    mostActiveMonth: string;         // 最活跃月份
    favoriteMessage: string;         // 最常发送的话语
  };
  memberStats: Array<{
    memberId: string;
    memberName: string;
    hugCount: number;
    messageCount: number;
    firstInteraction: string;
    lastInteraction: string;
  }>;
  milestones: Array<{
    date: string;
    type: 'first_hug' | 'milestone_100' | 'continuous_30days';
    description: string;
  }>;
  publicWelfare: {
    totalContribution: number;       // 累计公益贡献值
    helpedFamilies: number;          // 帮助的孤寡妈妈家庭数
  };
}

// 年度报告摘要（列表展示用）
export interface AnnualReportSummary {
  reportId: string;
  year: number;
  generateTime: string;
  summary: {
    totalHugs: number;
    totalSafetyMessages: number;
  };
}

// 公益联动统计数据
export interface PublicWelfareStats {
  totalPlatformHugs: number;         // 平台总拥抱次数
  totalWelfareValue: number;         // 累计公益价值（元）
  helpedFamiliesCount: number;       // 已帮助的孤寡妈妈家庭数
  currentMonthHugs: number;          // 本月拥抱次数
  realtimeContributors: number;      // 当前活跃贡献者数
  milestones: Array<{
    value: number;                   // 里程碑值
    description: string;             // 描述
    reachedAt?: string;              // 达成时间
  }>;
}

// 个人公益贡献
export interface MyWelfareContribution {
  userId?: string;
  totalValue?: number;                // 公益价值（元）
  totalHugs?: number;                 // 总拥抱次数
  ranking?: number;                   // 贡献排名
  contributionHistory?: Array<{
    date: string;
    hugCount: number;
    contributionValue: number;
  }>;
  achievements?: Array<{
    achievementId: string;
    name: string;                     // 如："爱心传递者"、"温暖守护者"
    description: string;
    unlockedAt: string;
    icon: string;
  }>;
}

// 公益故事
export interface WelfareStory {
  storyId: string;
  title?: string;
  content: string;                   // 匿名化的故事内容
  location: string;                  // 地区（脱敏）
  date: string;                      // 日期
  author: string;                    // 署名（匿名化）
  helpDate?: string;
  imageUrls?: string[];
}

// 公益故事列表响应
export interface WelfareStoriesResponse {
  total: number;
  page: number;
  pageSize: number;
  hasMore: boolean;
  stories: WelfareStory[];
}

// ========== 全局应用类型扩展 ==========
declare global {
  interface IAppOption {
    globalData: {
      userInfo: UserInfo | null;
      token: string;
      settings: UserSettings;
    }
  }

  // 小程序全局函数（在小程序环境中可用）
  // 动画帧函数
  const requestAnimationFrame: (callback: () => void) => number;
  const cancelAnimationFrame: (id: number) => void;
}

export {}

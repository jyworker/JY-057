/**
 * API接口定义
 * 集中管理所有后端接口调用
 */

import { get, post, put, del } from './request'
import {
  FamilyMember,
  HugRecord,
  SafetyMessage,
  MemorialMessage,
  LoginRequest,
  SendHugRequest,
  SendSafetyRequest,
  CreateMemorialRequest,
  UserInfo,
  UserSettings,
  MotherReply,
  SendMotherReplyRequest,
  AnnualLoveReport,
  AnnualReportSummary,
  PublicWelfareStats,
  MyWelfareContribution,
  WelfareStoriesResponse
} from '../types/index'

// ========== 用户相关 ==========

/**
 * 微信登录
 */
export function login(data: LoginRequest) {
  return post<{ token: string; userInfo: UserInfo }>('/api/auth/login', data)
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return get<UserInfo>('/api/user/info')
}

/**
 * 更新用户设置
 */
export function updateUserSettings(settings: UserSettings) {
  return put<UserSettings>('/api/user/settings', settings)
}

// ========== 家庭成员相关 ==========

/**
 * 获取家庭成员列表
 */
export function getFamilyMembers() {
  return get<FamilyMember[]>('/api/family/members', {}, true)
}

/**
 * 获取单个家庭成员详情
 */
export function getFamilyMember(memberId: string) {
  return get<FamilyMember>(`/api/family/member/${memberId}`)
}

/**
 * 添加家庭成员
 */
export function addFamilyMember(member: Partial<FamilyMember>) {
  return post<FamilyMember>('/api/family/member', member)
}

/**
 * 更新家庭成员信息
 */
export function updateFamilyMember(memberId: string, member: Partial<FamilyMember>) {
  return put<FamilyMember>(`/api/family/member/${memberId}`, member)
}

/**
 * 删除家庭成员
 */
export function deleteFamilyMember(memberId: string) {
  return del<void>(`/api/family/member/${memberId}`)
}

// ========== 拥抱相关 ==========

/**
 * 发送拥抱
 */
export function sendHug(data: SendHugRequest) {
  return post<HugRecord>('/api/hug/send', data)
}

/**
 * 获取拥抱历史记录
 */
export function getHugHistory(memberId: string) {
  return get<HugRecord[]>(`/api/hug/history/${memberId}`)
}

/**
 * 获取拥抱统计数据
 */
export function getHugStats(memberId: string) {
  return get<{
    totalCount: number
    totalDuration: number
    lastHugTime: string
  }>(`/api/hug/stats/${memberId}`)
}

// ========== 报平安相关 ==========

/**
 * 发送平安消息
 */
export function sendSafetyMessage(data: SendSafetyRequest) {
  return post<SafetyMessage>('/api/safety/send', data)
}

/**
 * 获取平安消息历史
 */
export function getSafetyHistory(memberId: string) {
  return get<SafetyMessage[]>(`/api/safety/history/${memberId}`)
}

/**
 * 取消定时消息
 */
export function cancelScheduledMessage(messageId: string) {
  return del<void>(`/api/safety/scheduled/${messageId}`)
}

// ========== 纪念空间相关 ==========

/**
 * 创建纪念留言
 */
export function createMemorialMessage(data: CreateMemorialRequest) {
  return post<MemorialMessage>('/api/memorial/message', data)
}

/**
 * 获取纪念留言列表
 */
export function getMemorialMessages(memberId: string) {
  return get<MemorialMessage[]>(`/api/memorial/messages/${memberId}`)
}

/**
 * 删除纪念留言
 */
export function deleteMemorialMessage(messageId: string) {
  return del<void>(`/api/memorial/message/${messageId}`)
}

/**
 * 获取纪念相册（老照片）
 */
export function getMemorialPhotos(memberId: string) {
  return get<string[]>(`/api/memorial/photos/${memberId}`)
}

// ========== 爱的回响 · 双向温暖闭环 ==========

/**
 * 妈妈端回复 - 一键回发"妈妈也想你了"
 * @param data 回复数据
 */
export function sendMotherReply(data: SendMotherReplyRequest) {
  return post<{
    replyId: string
    aiTranscript?: string // AI转文字结果
    sendTime: string
    emotion: 'warm' | 'caring' | 'proud' // 情感标签
  }>('/api/echo/mother-reply', data)
}

/**
 * 获取收到的妈妈回复列表
 * @param memberId 家庭成员ID
 */
export function getMotherReplies(memberId: string) {
  return get<MotherReply[]>(`/api/echo/replies/${memberId}`)
}

/**
 * 标记妈妈回复为已读
 * @param replyId 回复ID
 */
export function markReplyAsRead(replyId: string) {
  return put<void>(`/api/echo/reply/${replyId}/read`, {})
}

/**
 * 生成年度爱的报告
 * @param year 年份，默认当前年份
 */
export function generateAnnualLoveReport(year?: number) {
  return post<AnnualLoveReport>('/api/echo/annual-report', { year })
}

/**
 * 获取历史年度报告列表
 */
export function getAnnualReports() {
  return get<AnnualReportSummary[]>('/api/echo/annual-reports')
}

/**
 * 获取指定年度报告详情
 * @param reportId 报告ID
 */
export function getAnnualReportDetail(reportId: string) {
  return get<AnnualLoveReport>(`/api/echo/annual-report/${reportId}`)
}

/**
 * 分享年度报告（生成分享海报）
 * @param reportId 报告ID
 */
export function shareAnnualReport(reportId: string) {
  return post<{
    posterUrl: string // 海报图片URL
    shareCode: string // 分享码
    expiresIn: number // 有效期（秒）
  }>(`/api/echo/annual-report/${reportId}/share`, {})
}

/**
 * 获取公益联动实时数据
 * 展示平台整体的公益贡献可视化数据
 */
export function getPublicWelfareStats() {
  return get<PublicWelfareStats>('/api/echo/public-welfare/stats')
}

/**
 * 获取个人公益贡献记录
 */
export function getMyWelfareContribution() {
  return get<MyWelfareContribution>('/api/echo/public-welfare/my-contribution')
}

/**
 * 获取公益故事墙
 * 展示被帮助的家庭故事（匿名化处理）
 */
export function getWelfareStories(page: number = 1, pageSize: number = 10) {
  return get<WelfareStoriesResponse>('/api/echo/public-welfare/stories', { page, pageSize })
}

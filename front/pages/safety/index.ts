/**
 * 智能报平安页面
 * 核心职责：
 * 1. 支持文字输入报平安消息
 * 2. 实时高亮暖心词汇
 * 3. 智能提醒：夜深时建议定时发送
 */

import { getFamilyMember, sendSafetyMessage } from '../../utils/api'
import { FamilyMember, SendSafetyRequest } from '../../types/index'
import { highlightWarmWords, isLateNight } from '../../utils/interactions'

Page({
  data: {
    member: null as FamilyMember | null,
    messageContent: '',           // 原始消息内容
    highlightedContent: '',       // 高亮处理后的内容
    isScheduled: false,           // 是否定时发送
    scheduledTime: '08:00',       // 定时发送时间
    showTimePicker: false,        // 是否显示时间选择器
    sending: false                // 是否正在发送
  },

  onLoad(options: { memberId: string }) {
    const { memberId } = options
    if (!memberId) {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      })
      setTimeout(() => wx.navigateBack(), 1500)
      return
    }

    this.loadMemberInfo(memberId)
    this.checkTimeAndRemind()
  },

  /**
   * 加载家庭成员信息
   */
  async loadMemberInfo(memberId: string) {
    try {
      const member = await getFamilyMember(memberId)

      // 验证成员状态（仅健在亲人可发送报平安）
      if (member.status === 'deceased') {
        wx.showModal({
          title: '温馨提示',
          content: '纪念模式下不支持发送报平安消息',
          showCancel: false,
          success: () => {
            wx.navigateBack()
          }
        })
        return
      }

      this.setData({ member })

    } catch (error) {
      console.error('加载成员信息失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  /**
   * 智能提醒：检查当前时间，夜深时提示定时发送
   */
  checkTimeAndRemind() {
    if (isLateNight()) {
      wx.showToast({
        title: '夜深了，建议勾选"明早8点定时发送"，让妈妈睡个好觉',
        icon: 'none',
        duration: 3500
      })

      // 自动勾选定时发送
      this.setData({
        isScheduled: true,
        scheduledTime: '08:00'
      })
    }
  },

  /**
   * 消息内容输入
   */
  handleInput(e: any) {
    const content = e.detail.value
    const highlighted = highlightWarmWords(content)

    this.setData({
      messageContent: content,
      highlightedContent: highlighted
    })
  },

  /**
   * 快捷短语选择
   */
  handleQuickPhrase(e: any) {
    const { phrase } = e.currentTarget.dataset
    const content = this.data.messageContent + phrase

    this.setData({
      messageContent: content,
      highlightedContent: highlightWarmWords(content)
    })
  },

  /**
   * 切换定时发送
   */
  handleScheduleToggle(e: any) {
    this.setData({
      isScheduled: e.detail.value
    })
  },

  /**
   * 选择定时时间
   */
  handleTimeChange(e: any) {
    this.setData({
      scheduledTime: e.detail.value
    })
  },

  /**
   * 语音输入（调用微信语音识别）
   */
  handleVoiceInput() {
    wx.showModal({
      title: '语音输入',
      content: '即将支持语音转文字功能',
      showCancel: false
    })
  },

  /**
   * 发送报平安消息
   */
  async handleSend() {
    const { messageContent, member, isScheduled, scheduledTime } = this.data

    // 验证消息内容
    if (!messageContent.trim()) {
      wx.showToast({
        title: '请输入消息内容',
        icon: 'none'
      })
      return
    }

    if (!member) return

    try {
      this.setData({ sending: true })

      const request: SendSafetyRequest = {
        memberId: member.memberId,
        content: messageContent.trim(),
        isScheduled,
        scheduledTime: isScheduled ? this.getScheduledDateTime(scheduledTime) : undefined
      }

      await sendSafetyMessage(request)

      // 发送成功提示
      const successMsg = isScheduled 
        ? `已设置定时发送，将在${scheduledTime}送达${member.nickname}`
        : `消息已送达${member.nickname}！`

      wx.showToast({
        title: successMsg,
        icon: 'success',
        duration: 2000
      })

      // 延迟后返回
      setTimeout(() => {
        wx.navigateBack()
      }, 2000)

    } catch (error) {
      console.error('发送消息失败:', error)
    } finally {
      this.setData({ sending: false })
    }
  },

  /**
   * 获取定时发送的完整日期时间
   */
  getScheduledDateTime(time: string): string {
    const now = new Date()
    const [hour, minute] = time.split(':')
    
    const scheduled = new Date()
    scheduled.setHours(parseInt(hour), parseInt(minute), 0, 0)

    // 如果设定时间早于当前时间，则设置为明天
    if (scheduled.getTime() <= now.getTime()) {
      scheduled.setDate(scheduled.getDate() + 1)
    }

    return scheduled.toISOString()
  }
})

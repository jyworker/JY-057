/**
 * 虚拟拥抱页 - 核心交互页面
 * 核心职责：
 * 1. 长按屏幕触发虚拟拥抱
 * 2. 伦理阻断：已故亲人强制跳转到纪念空间
 * 3. 配合粒子组件提供情感反馈
 */

import { getFamilyMember, sendHug } from '../../utils/api'
import { FamilyMember, SendHugRequest } from '../../types/index'
import { triggerHugVibration } from '../../utils/interactions'

Page({
  data: {
    member: null as FamilyMember | null,
    isHugging: false,           // 是否正在拥抱
    hugStartTime: 0,            // 拥抱开始时间
    hugDuration: 0,             // 拥抱时长（毫秒）
    showFeedback: false,        // 是否显示反馈提示
    canvasWidth: 375,
    canvasHeight: 667
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

    // 获取屏幕尺寸
    const { windowWidth, windowHeight } = wx.getSystemInfoSync()
    this.setData({
      canvasWidth: windowWidth,
      canvasHeight: windowHeight
    })

    this.loadMemberInfo(memberId)
  },

  /**
   * 加载家庭成员信息
   */
  async loadMemberInfo(memberId: string) {
    try {
      const member = await getFamilyMember(memberId)

      // 【伦理阻断 - 关键】检查成员状态
      if (member.status === 'deceased') {
        console.warn('伦理守护：已故亲人不可进入拥抱页面')
        
        wx.showModal({
          title: '温馨提示',
          content: '已为您跳转到纪念空间，在那里可以静静地缅怀',
          showCancel: false,
          success: () => {
            // 强制跳转到纪念空间
            wx.redirectTo({
              url: `/pages/memorial/index?memberId=${memberId}`
            })
          }
        })
        return
      }

      // 健在亲人：正常加载
      this.setData({ member })

    } catch (error) {
      console.error('加载成员信息失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
      setTimeout(() => wx.navigateBack(), 1500)
    }
  },

  /**
   * 长按开始拥抱
   */
  handleLongPress() {
    if (this.data.isHugging) return

    console.log('开始虚拟拥抱')

    // 触发震动反馈
    triggerHugVibration()

    // 记录开始时间
    const hugStartTime = Date.now()

    this.setData({
      isHugging: true,
      hugStartTime,
      showFeedback: false
    })

    // 通知粒子组件加速
    const particleComponent = this.selectComponent('#heart-particles')
    if (particleComponent) {
      particleComponent.accelerate()
    }
  },

  /**
   * 松开结束拥抱
   */
  handleTouchEnd() {
    if (!this.data.isHugging) return

    console.log('结束虚拟拥抱')

    // 计算拥抱时长
    const hugDuration = Date.now() - this.data.hugStartTime

    this.setData({
      isHugging: false,
      hugDuration
    })

    // 粒子恢复正常速度
    const particleComponent = this.selectComponent('#heart-particles')
    if (particleComponent) {
      particleComponent.decelerate()
    }

    // 发送拥抱记录（异步，不阻塞反馈）
    this.sendHugRecord(hugDuration)

    // 立即显示反馈（确保情感反馈的即时性）
    this.showHugFeedback()
  },

  /**
   * 发送拥抱记录到后端
   */
  async sendHugRecord(duration: number) {
    if (!this.data.member) return

    try {
      const request: SendHugRequest = {
        memberId: this.data.member.memberId,
        duration,
        timestamp: new Date().toISOString()
      }

      // 异步发送，不等待结果
      await sendHug(request)
      console.log('拥抱记录已发送')

    } catch (error) {
      console.error('发送拥抱记录失败:', error)
      // 不显示错误提示，避免打断情感体验
    }
  },

  /**
   * 显示拥抱反馈
   */
  showHugFeedback() {
    this.setData({ showFeedback: true })

    // 2秒后自动隐藏
    setTimeout(() => {
      this.setData({ showFeedback: false })
    }, 2000)
  },

  /**
   * 查看拥抱历史
   */
  handleViewHistory() {
    if (!this.data.member) return

    wx.navigateTo({
      url: `/pages/hug-history/index?memberId=${this.data.member.memberId}`
    })
  }
})

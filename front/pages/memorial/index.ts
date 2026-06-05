/**
 * 纪念空间页 - 伦理守护核心页面
 * 核心职责：
 * 1. 为已故亲人提供静默的缅怀空间
 * 2. 严格遵守静默原则：无粒子、无AR、无音乐
 * 3. 仅保留"查看老照片"和"留言"功能
 */

import { getFamilyMember, getMemorialMessages, getMemorialPhotos, createMemorialMessage } from '../../utils/api'
import { FamilyMember, MemorialMessage } from '../../types/index'
import { scrollToTop } from '../../utils/interactions'

Page({
  data: {
    member: null as FamilyMember | null,
    messages: [] as MemorialMessage[],
    photos: [] as string[],
    showMessageInput: false,
    messageContent: ''
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

    this.loadMemorialData(memberId)
  },

  /**
   * 加载纪念空间数据
   */
  async loadMemorialData(memberId: string) {
    try {
      // 并行加载成员信息、留言、照片
      const [member, messages, photos] = await Promise.all([
        getFamilyMember(memberId),
        getMemorialMessages(memberId),
        getMemorialPhotos(memberId)
      ])

      // 伦理验证：确保成员状态为deceased
      if (member.status !== 'deceased') {
        console.warn('伦理守护：该成员非已故状态，不应进入纪念空间')
        wx.showModal({
          title: '温馨提示',
          content: '该功能仅适用于纪念模式',
          showCancel: false,
          success: () => {
            wx.navigateBack()
          }
        })
        return
      }

      this.setData({
        member,
        messages,
        photos
      })

    } catch (error) {
      console.error('加载纪念空间数据失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  /**
   * 查看老照片
   */
  handleViewPhotos() {
    if (!this.data.photos || this.data.photos.length === 0) {
      wx.showToast({
        title: '暂无照片',
        icon: 'none'
      })
      return
    }

    // 预览照片
    wx.previewImage({
      urls: this.data.photos.map(hash => `/assets/photos/${hash}.jpg`),
      current: this.data.photos[0]
    })
  },

  /**
   * 显示留言输入框
   */
  showMessageInput() {
    this.setData({
      showMessageInput: true,
      messageContent: ''
    })
  },

  /**
   * 隐藏留言输入框
   */
  hideMessageInput() {
    this.setData({
      showMessageInput: false,
      messageContent: ''
    })
  },

  /**
   * 留言内容变化
   */
  handleMessageInput(e: any) {
    this.setData({
      messageContent: e.detail.value
    })
  },

  /**
   * 提交留言
   */
  async handleSubmitMessage() {
    const { messageContent, member } = this.data

    if (!messageContent.trim()) {
      wx.showToast({
        title: '请输入留言内容',
        icon: 'none'
      })
      return
    }

    if (!member) return

    try {
      wx.showLoading({ title: '提交中...' })

      await createMemorialMessage({
        memberId: member.memberId,
        content: messageContent.trim()
      })

      wx.hideLoading()
      wx.showToast({
        title: '留言已保存',
        icon: 'success'
      })

      // 重新加载留言列表
      const messages = await getMemorialMessages(member.memberId)
      this.setData({
        messages,
        showMessageInput: false,
        messageContent: ''
      })

      // 滚动到顶部查看新留言
      scrollToTop()

    } catch (error) {
      wx.hideLoading()
      console.error('提交留言失败:', error)
    }
  },

  /**
   * 阻止功能使用（视频生成等）
   */
  handleBlockedFeature() {
    wx.showModal({
      title: '温馨提示',
      content: '该功能在纪念模式下不可用',
      showCancel: false
    })
  },

  /**
   * 返回首页
   */
  handleBackHome() {
    wx.navigateBack()
  }
})

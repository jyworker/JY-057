/**
 * 首页 - 家庭成员列表
 * 核心职责：展示家庭成员，支持进入拥抱页或纪念空间
 */

import { getFamilyMembers, deleteFamilyMember } from '../../utils/api'
import { FamilyMember } from '../../types/index'
import { tapFeedback, getGreeting } from '../../utils/interactions'

Page({
  data: {
    members: [] as FamilyMember[],
    loading: true,
    greeting: ''
  },

  onLoad() {
    this.setData({
      greeting: getGreeting()
    })
    this.loadFamilyMembers()
  },

  onShow() {
    // 每次显示时刷新列表（可能有成员状态更新）
    this.loadFamilyMembers()
  },

  /**
   * 加载家庭成员列表
   */
  async loadFamilyMembers() {
    try {
      this.setData({ loading: true })
      
      const members = await getFamilyMembers()
      
      this.setData({ 
        members,
        loading: false 
      })

    } catch (error) {
      console.error('加载家庭成员失败:', error)
      this.setData({ loading: false })
    }
  },

  /**
   * 点击核心功能卡片 - 显示成员选择
   */
  handleFeatureTap(e: any) {
    tapFeedback()
    const { feature } = e.currentTarget.dataset
    
    // 检查是否有成员
    if (this.data.members.length === 0) {
      wx.showModal({
        title: '温馨提示',
        content: '请先添加家庭成员哦',
        showCancel: false
      })
      return
    }

    // 显示成员选择弹窗
    this.showMemberSelector(feature)
  },

  /**
   * 显示成员选择器
   */
  showMemberSelector(feature: string) {
    const items = this.data.members.map(m => m.nickname || m.name)
    
    wx.showActionSheet({
      itemList: items,
      success: (res) => {
        const selectedMember = this.data.members[res.tapIndex]
        this.navigateToFeature(feature, selectedMember)
      }
    })
  },

  /**
   * 跳转到对应功能页面
   */
  navigateToFeature(feature: string, member: any) {
    const featurePages: Record<string, string> = {
      'hug': '/pages/hug/index',
      'safety': '/pages/safety/index',
      'memorial': '/pages/memorial/index'
    }

    const url = featurePages[feature]
    if (!url) return

    // 伦理守护：已故成员只能访问纪念空间
    if (member.status === 'deceased' && feature !== 'memorial') {
      wx.showModal({
        title: '温馨提示',
        content: '已故亲人只能访问纪念空间',
        showCancel: false,
        success: () => {
          wx.navigateTo({
            url: `/pages/memorial/index?memberId=${member.memberId}`
          })
        }
      })
      return
    }

    wx.navigateTo({
      url: `${url}?memberId=${member.memberId}`
    })
  },

  /**
   * 点击删除按钮
   */
  handleDeleteTap(e: any) {
    tapFeedback()
    const { member } = e.currentTarget.dataset
    if (!member) return

    wx.showModal({
      title: '确认删除',
      content: `确定要删除家庭成员"${member.name}"吗？`,
      confirmText: '删除',
      confirmColor: '#ff6a88',
      success: (res) => {
        if (res.confirm) {
          this.deleteMember(member.memberId)
        }
      }
    })
  },

  /**
   * 删除家庭成员
   */
  async deleteMember(memberId: string) {
    try {
      wx.showLoading({ title: '删除中...' })
      
      // 调用后端删除接口
      await deleteFamilyMember(memberId)
      
      // 从本地数据中删除
      const members = this.data.members.filter(m => m.memberId !== memberId)
      this.setData({ members })
      
      wx.hideLoading()
      wx.showToast({
        title: '删除成功',
        icon: 'success'
      })
    } catch (error) {
      console.error('删除成员失败:', error)
      wx.hideLoading()
      wx.showToast({
        title: '删除失败',
        icon: 'none'
      })
    }
  },

  /**
   * 点击「添加成员」：跳转到添加成员页
   */
  handleAddMember() {
    tapFeedback()
    wx.navigateTo({
      url: '/pages/member-add/index'
    })
  },

  /**
   * 通用页面跳转（仅用于爱的回响等独立页面）
   */
  navigateTo(e: any) {
    tapFeedback()
    const { url } = e.currentTarget.dataset
    if (!url) return
    
    wx.navigateTo({
      url
    })
  },

  /**
   * 下拉刷新
   */
  async onPullDownRefresh() {
    await this.loadFamilyMembers()
    wx.stopPullDownRefresh()
  }
})

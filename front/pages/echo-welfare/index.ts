/**
 * 公益联动详情页
 * 展示平台公益数据、个人贡献、公益故事
 */

import { 
  getPublicWelfareStats, 
  getMyWelfareContribution,
  getWelfareStories
} from '../../utils/api'
import { 
  PublicWelfareStats,
  MyWelfareContribution,
  WelfareStory
} from '../../types/index'
import { tapFeedback } from '../../utils/interactions'

// 小程序Toast组件
const Toast = (wx as any).Toast || {
  loading: (options: any) => wx.showLoading({ title: options.message }),
  success: (msg: string) => wx.showToast({ title: msg, icon: 'success' }),
  fail: (msg: string) => wx.showToast({ title: msg, icon: 'none' }),
  clear: () => wx.hideLoading()
}

Page({
  data: {
    loading: true,
    platformStats: {} as PublicWelfareStats,
    myContribution: {} as MyWelfareContribution,
    
    // 公益故事
    stories: [] as WelfareStory[],
    loadingStories: false,
    loadingMore: false,
    currentPage: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad() {
    this.loadAllData()
  },

  /**
   * 加载所有数据
   */
  async loadAllData() {
    this.setData({ loading: true })

    try {
      await Promise.all([
        this.loadPlatformStats(),
        this.loadMyContribution(),
        this.loadStories(1)
      ])
    } catch (error) {
      console.error('加载数据失败:', error)
      Toast.fail('加载失败，请重试')
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 加载平台统计数据
   */
  async loadPlatformStats() {
    try {
      const stats = await getPublicWelfareStats()
      
      // 格式化金额（转换为 number）
      if (stats.totalWelfareValue) {
        stats.totalWelfareValue = Number(Number(stats.totalWelfareValue).toFixed(2))
      }

      // 格式化里程碑时间
      if (stats.milestones) {
        stats.milestones = stats.milestones.map(m => {
          if (m.reachedAt) {
            const date = new Date(m.reachedAt)
            m.reachedAt = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
          }
          return m
        })
      }

      this.setData({ platformStats: stats })
    } catch (error) {
      console.error('加载平台统计失败:', error)
      throw error
    }
  },

  /**
   * 加载我的贡献
   */
  async loadMyContribution() {
    try {
      const contribution = await getMyWelfareContribution()
      
      // 格式化金额（转换为 number）
      if (contribution.totalValue) {
        contribution.totalValue = Number(Number(contribution.totalValue).toFixed(2))
      }

      this.setData({ myContribution: contribution })
    } catch (error) {
      console.error('加载我的贡献失败:', error)
      throw error
    }
  },

  /**
   * 加载公益故事
   */
  async loadStories(page: number) {
    try {
      this.setData({ 
        loadingStories: page === 1,
        loadingMore: page > 1,
        currentPage: page
      })

      const response = await getWelfareStories(page, this.data.pageSize)
      
      // 格式化故事时间
      const formattedStories = response.stories.map(story => {
        if (story.date) {
          const date = new Date(story.date)
          story.date = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
        }
        return story
      })

      this.setData({
        stories: page === 1 ? formattedStories : [...this.data.stories, ...formattedStories],
        hasMore: response.hasMore,
        loadingStories: false,
        loadingMore: false
      })
    } catch (error) {
      console.error('加载故事失败:', error)
      this.setData({ 
        loadingStories: false,
        loadingMore: false
      })
      throw error
    }
  },

  /**
   * 加载更多故事
   */
  async loadMoreStories() {
    tapFeedback()

    if (this.data.loadingMore || !this.data.hasMore) return

    const nextPage = this.data.currentPage + 1
    await this.loadStories(nextPage)
  },

  /**
   * 下拉刷新
   */
  async onPullDownRefresh() {
    await this.loadAllData()
    wx.stopPullDownRefresh()
  },

  /**
   * 分享到微信
   */
  onShareAppMessage() {
    return {
      title: '拥抱妈妈·公益联动 - 让爱在平安中生生不息',
      path: '/pages/echo-welfare/index',
      imageUrl: '/assets/share-welfare.jpg'
    }
  }
})

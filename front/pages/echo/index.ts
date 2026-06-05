/**
 * 爱的回响 - 双向温暖闭环
 * 包含年度报告、妈妈回复、公益联动三大模块
 */

import { 
  getMotherReplies, 
  markReplyAsRead,
  generateAnnualLoveReport,
  getAnnualReports,
  getAnnualReportDetail,
  getPublicWelfareStats,
  getMyWelfareContribution
} from '../../utils/api'
import { 
  MotherReply, 
  AnnualLoveReport,
  AnnualReportSummary,
  PublicWelfareStats,
  MyWelfareContribution
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
    // 年度报告
    latestReport: null as AnnualLoveReport | null,
    reportHistory: [] as AnnualReportSummary[],
    hasNewReport: false,
    generatingReport: false,

    // 妈妈回复
    motherReplies: [] as MotherReply[],
    unreadCount: 0,
    loadingReplies: false,

    // 公益联动
    welfareStats: {} as PublicWelfareStats,
    myContribution: {} as MyWelfareContribution,
    loadingWelfare: false
  },

  onLoad() {
    this.loadAllData()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadMotherReplies()
  },

  /**
   * 加载所有数据
   */
  async loadAllData() {
    await Promise.all([
      this.loadReportHistory(),
      this.loadMotherReplies(),
      this.loadWelfareData()
    ])
  },

  /**
   * 加载年度报告历史
   */
  async loadReportHistory() {
    try {
      const reports = await getAnnualReports()
      
      // 获取最新报告的详情
      let latestReport = null
      if (reports.length > 0) {
        latestReport = await getAnnualReportDetail(reports[0].reportId)
      }

      // 检查是否是今年新生成的
      const currentYear = new Date().getFullYear()
      const hasNewReport = reports.length > 0 && reports[0].year === currentYear

      this.setData({
        reportHistory: reports,
        latestReport,
        hasNewReport
      })
    } catch (error) {
      console.error('加载报告历史失败:', error)
    }
  },

  /**
   * 加载妈妈回复列表
   */
  async loadMotherReplies() {
    try {
      this.setData({ loadingReplies: true })

      // 获取所有家庭成员的回复（这里简化处理，实际可能需要遍历所有成员）
      // 暂时使用空字符串，后端应该返回当前用户的所有回复
      const replies = await getMotherReplies('')
      
      // 计算未读数量
      const unreadCount = replies.filter(r => !r.isRead).length

      this.setData({
        motherReplies: replies,
        unreadCount,
        loadingReplies: false
      })
    } catch (error) {
      console.error('加载妈妈回复失败:', error)
      this.setData({ loadingReplies: false })
    }
  },

  /**
   * 加载公益数据
   */
  async loadWelfareData() {
    try {
      this.setData({ loadingWelfare: true })

      const [stats, contribution] = await Promise.all([
        getPublicWelfareStats(),
        getMyWelfareContribution()
      ])

      this.setData({
        welfareStats: stats,
        myContribution: contribution,
        loadingWelfare: false
      })
    } catch (error) {
      console.error('加载公益数据失败:', error)
      this.setData({ loadingWelfare: false })
    }
  },

  /**
   * 生成/查看年度报告
   */
  async handleGenerateReport() {
    tapFeedback()

    const { latestReport, generatingReport } = this.data
    
    if (generatingReport) return

    // 如果已有报告，直接查看
    if (latestReport) {
      wx.navigateTo({
        url: `/pages/echo-report/index?reportId=${latestReport.reportId}`
      })
      return
    }

    // 生成新报告
    try {
      this.setData({ generatingReport: true })
      
      Toast.loading({
        message: '正在生成报告...',
        forbidClick: true,
        duration: 0
      })

      const currentYear = new Date().getFullYear()
      const report = await generateAnnualLoveReport(currentYear)

      Toast.clear()
      Toast.success('生成成功！')

      // 跳转到报告详情页
      setTimeout(() => {
        wx.navigateTo({
          url: `/pages/echo-report/index?reportId=${report.reportId}`
        })
      }, 500)

    } catch (error: any) {
      console.error('生成报告失败:', error)
      Toast.fail(error.message || '生成失败，请重试')
    } finally {
      this.setData({ generatingReport: false })
    }
  },

  /**
   * 查看历史报告
   */
  handleViewReport(e: any) {
    tapFeedback()
    const { reportId } = e.currentTarget.dataset
    
    wx.navigateTo({
      url: `/pages/echo-report/index?reportId=${reportId}`
    })
  },

  /**
   * 点击妈妈回复
   */
  async handleReplyTap(e: any) {
    tapFeedback()
    
    const { reply } = e.currentTarget.dataset
    if (!reply) return

    // 标记为已读
    if (!reply.isRead) {
      try {
        await markReplyAsRead(reply.replyId)
        
        // 更新本地状态
        const replies = this.data.motherReplies.map(r => 
          r.replyId === reply.replyId ? { ...r, isRead: true } : r
        )
        const unreadCount = replies.filter(r => !r.isRead).length

        this.setData({
          motherReplies: replies,
          unreadCount
        })
      } catch (error) {
        console.error('标记已读失败:', error)
      }
    }

    // 显示回复详情（可以弹窗或跳转页面）
    if (reply.replyType === 'voice' && reply.voiceUrl) {
      // 播放语音
      const innerAudioContext = wx.createInnerAudioContext()
      innerAudioContext.src = reply.voiceUrl
      innerAudioContext.play()
    } else {
      // 显示文字/表情
      wx.showModal({
        title: `${reply.motherName}的回复`,
        content: reply.textContent || reply.emojiContent || '❤️',
        showCancel: false
      })
    }
  },

  /**
   * 跳转到公益详情页
   */
  navigateToWelfare() {
    tapFeedback()
    wx.navigateTo({
      url: '/pages/echo-welfare/index'
    })
  },

  /**
   * 下拉刷新
   */
  async onPullDownRefresh() {
    await this.loadAllData()
    wx.stopPullDownRefresh()
  }
})

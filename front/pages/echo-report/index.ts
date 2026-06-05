/**
 * 年度报告详情页
 * 展示用户的年度爱的数据报告
 */

import { getAnnualReportDetail, shareAnnualReport } from '../../utils/api'
import { AnnualLoveReport } from '../../types/index'
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
    reportId: '',
    report: null as AnnualLoveReport | null,
    welfareContribution: '0.000', // 公益贡献值（格式化后）
    loading: true,
    sharing: false
  },

  onLoad(options: any) {
    const { reportId } = options
    if (!reportId) {
      wx.showModal({
        title: '提示',
        content: '报告不存在',
        showCancel: false,
        success: () => {
          wx.navigateBack()
        }
      })
      return
    }

    this.setData({ reportId })
    this.loadReportDetail()
  },

  /**
   * 加载报告详情
   */
  async loadReportDetail() {
    try {
      this.setData({ loading: true })

      const report = await getAnnualReportDetail(this.data.reportId)

      // 格式化数据
      if (report.generateTime) {
        const date = new Date(report.generateTime)
        report.generateTime = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
      }

      // 计算公益贡献值
      const welfareContribution = ((report.summary?.totalHugs || 0) * 0.001).toFixed(3)

      this.setData({
        report,
        welfareContribution,
        loading: false
      })
    } catch (error: any) {
      console.error('加载报告失败:', error)
      this.setData({ loading: false })
      
      wx.showModal({
        title: '加载失败',
        content: error.message || '无法加载报告数据',
        showCancel: false,
        success: () => {
          wx.navigateBack()
        }
      })
    }
  },

  /**
   * 分享报告
   */
  async handleShare() {
    tapFeedback()

    const { report, sharing } = this.data
    if (!report || sharing) return

    try {
      this.setData({ sharing: true })

      Toast.loading({
        message: '生成分享海报...',
        forbidClick: true,
        duration: 0
      })

      const shareData = await shareAnnualReport(report.reportId)

      Toast.clear()

      // 预览海报
      wx.previewImage({
        urls: [shareData.posterUrl],
        current: shareData.posterUrl
      })

      // 提示保存分享
      setTimeout(() => {
        wx.showModal({
          title: '分享提示',
          content: '长按图片可保存到相册，分享给好友哦！',
          showCancel: false
        })
      }, 1000)

    } catch (error: any) {
      console.error('分享失败:', error)
      Toast.fail(error.message || '分享失败，请重试')
    } finally {
      this.setData({ sharing: false })
    }
  },

  /**
   * 分享到微信
   */
  onShareAppMessage() {
    const { report } = this.data
    if (!report) return {}

    return {
      title: `我的${report.year}年度爱的报告`,
      path: `/pages/echo-report/index?reportId=${report.reportId}`,
      imageUrl: '/assets/share-cover.jpg'
    }
  },

  /**
   * 分享到朋友圈
   */
  onShareTimeline() {
    const { report } = this.data
    if (!report) return {}

    return {
      title: `拥抱妈妈·${report.year}年度爱的报告`,
      query: `reportId=${report.reportId}`,
      imageUrl: '/assets/share-cover.jpg'
    }
  }
})

/**
 * 登录页面
 * 核心职责：微信授权登录，获取Token
 */

import { login } from '../../utils/api'

Page({
  data: {
    loading: false
  },

  onLoad() {
    // 检查是否已登录
    const token = wx.getStorageSync('token')
    if (token) {
      this.navigateToHome()
    }
  },

  /**
   * 微信授权登录
   */
  async handleLogin() {
    try {
      this.setData({ loading: true })

      // 1. 获取微信登录code
      const { code } = await wx.login()
      if (!code) {
        throw new Error('获取微信登录code失败')
      }

      // 2. 调用后端登录接口
      const result = await login({ code })

      // 3. 保存Token和用户信息
      wx.setStorageSync('token', result.token)
      wx.setStorageSync('userInfo', result.userInfo)

      const app = getApp<IAppOption>()
      app.globalData.token = result.token
      app.globalData.userInfo = result.userInfo

      // 4. 提示登录成功
      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 1500
      })

      // 5. 延迟跳转到首页
      setTimeout(() => {
        this.navigateToHome()
      }, 1500)

    } catch (error) {
      console.error('登录失败:', error)
      wx.showToast({
        title: '登录失败，请重试',
        icon: 'none',
        duration: 2000
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  /**
   * 跳转到首页
   */
  navigateToHome() {
    wx.reLaunch({
      url: '/pages/index/index'
    })
  }
})

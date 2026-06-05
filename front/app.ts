// 拥抱妈妈·爱在平安 - 应用入口
// 核心理念：AI向善 - 技术隐形于爱，伦理嵌入代码

import { UserSettings } from './types/index'

interface IAppOption {
  globalData: {
    userInfo: any
    token: string
    settings: UserSettings
  }
  onLaunch: () => void
  onShow: () => void
  onHide: () => void
  checkUpdate: () => void
}

App<IAppOption>({
  globalData: {
    userInfo: null,
    token: '',
    settings: {
      emotionHighlight: true, // 默认开启暖心词高亮
      vibration: true         // 默认开启震动反馈
    }
  },

  onLaunch() {
    console.log('拥抱妈妈小程序启动')
    
    // 检查登录状态
    const token = wx.getStorageSync('token')
    if (token) {
      this.globalData.token = token
    }

    // 加载用户设置
    const settings = wx.getStorageSync('userSettings')
    if (settings) {
      this.globalData.settings = settings
    }

    // 检查小程序版本更新
    this.checkUpdate()
  },

  onShow() {
    console.log('小程序显示')
  },

  onHide() {
    console.log('小程序隐藏')
  },

  /**
   * 检查小程序更新
   * 确保用户使用最新版本的伦理守护逻辑
   */
  checkUpdate() {
    if (wx.canIUse('getUpdateManager')) {
      const updateManager = wx.getUpdateManager()
      
      updateManager.onCheckForUpdate((res) => {
        console.log('检查更新:', res.hasUpdate)
      })

      updateManager.onUpdateReady(() => {
        wx.showModal({
          title: '更新提示',
          content: '新版本已准备好，是否重启应用？',
          success: (res) => {
            if (res.confirm) {
              updateManager.applyUpdate()
            }
          }
        })
      })

      updateManager.onUpdateFailed(() => {
        console.error('新版本下载失败')
      })
    }
  }
})

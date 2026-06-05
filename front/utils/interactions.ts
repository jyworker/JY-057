/**
 * 情感化交互工具
 * 核心职责：
 * 1. 提供"轻→重→轻"节奏的拥抱震动反馈
 * 2. 提供暖心词高亮处理
 * 伦理设计：通过细腻的触觉和视觉反馈，增强情感连接
 */

/**
 * 触发拥抱震动反馈
 * 震动节奏："轻触 → 紧拥 → 轻放"
 * 设计理念：模拟真实拥抱的力度变化，传递温暖
 */
export function triggerHugVibration(): void {
  // 检查用户是否开启震动设置
  const app = getApp<IAppOption>()
  if (!app.globalData.settings.vibration) {
    console.log('用户已关闭震动反馈')
    return
  }

  // 兼容性检查
  if (!wx.canIUse('vibrateShort') && !wx.canIUse('vibrateLong')) {
    console.warn('当前设备不支持震动API')
    return
  }

  /**
   * 震动序列：
   * 1. 轻触（15ms）- 模拟初次接触
   * 2. 延迟（100ms）
   * 3. 紧拥（400ms）- 模拟用力拥抱
   * 4. 延迟（100ms）
   * 5. 轻放（15ms）- 模拟松开的瞬间
   */

  // 第一段：轻触
  wx.vibrateShort({
    type: 'light', // 轻微震动
    success: () => {
      console.log('拥抱反馈 - 轻触')
    },
    fail: (err) => {
      console.warn('震动失败:', err)
    }
  })

  // 延迟后第二段：紧拥
  setTimeout(() => {
    // 安卓和iOS的长震动实现差异处理
    if (wx.getSystemInfoSync().platform === 'android') {
      // 安卓：使用vibrateLong
      wx.vibrateLong({
        success: () => {
          console.log('拥抱反馈 - 紧拥（安卓）')
        }
      })
    } else {
      // iOS：使用vibrateShort的heavy类型，重复调用模拟长震动
      wx.vibrateShort({
        type: 'heavy',
        success: () => {
          console.log('拥抱反馈 - 紧拥（iOS）')
          // iOS需要再次调用以延长震动时间
          setTimeout(() => {
            wx.vibrateShort({ type: 'heavy' })
          }, 200)
        }
      })
    }
  }, 100)

  // 延迟后第三段：轻放
  setTimeout(() => {
    wx.vibrateShort({
      type: 'light',
      success: () => {
        console.log('拥抱反馈 - 轻放')
      }
    })
  }, 600) // 100ms + 400ms + 100ms
}

/**
 * 暖心词高亮处理
 * 将文本中的暖心词汇替换为带高亮样式的HTML标签
 * @param text 原始文本
 * @returns 处理后的HTML字符串（用于rich-text组件）
 */
export function highlightWarmWords(text: string): string {
  if (!text) return ''

  // 检查用户是否开启暖心词高亮
  const app = getApp<IAppOption>()
  if (!app.globalData.settings.emotionHighlight) {
    return text // 未开启，返回原文
  }

  // 暖心词库
  // 设计理念：涵盖日常关心、祝福、思念等情感词汇
  const warmWords = [
    '开心', '快乐', '幸福', '平安', '健康', '顺利',
    '加油', '想你', '爱你', '温暖', '关心', '牵挂',
    '吃饱了', '睡好了', '考好了', '回家', '到家',
    '妈妈', '爸爸', '宝贝', '亲爱的',
    '好好的', '放心', '保重', '小心',
    '美好', '开开心心', '平平安安'
  ]

  // 构建正则表达式（全局匹配）
  const pattern = new RegExp(`(${warmWords.join('|')})`, 'g')

  // 替换为带样式的span标签
  const highlighted = text.replace(pattern, '<span class="warm-highlight">$1</span>')

  return highlighted
}

/**
 * 平滑滚动到页面顶部
 * 用于内容较长的页面（如纪念空间）
 */
export function scrollToTop(duration: number = 300): void {
  wx.pageScrollTo({
    scrollTop: 0,
    duration
  })
}

/**
 * 触觉反馈 - 轻微点击反馈
 * 用于按钮点击等交互
 */
export function tapFeedback(): void {
  const app = getApp<IAppOption>()
  if (!app.globalData.settings.vibration) return

  if (wx.canIUse('vibrateShort')) {
    wx.vibrateShort({ type: 'light' })
  }
}

/**
 * 获取当前时间的温馨问候语
 * 根据时间段返回不同的问候
 */
export function getGreeting(): string {
  const hour = new Date().getHours()

  if (hour < 6) {
    return '夜深了，注意休息'
  } else if (hour < 9) {
    return '早上好'
  } else if (hour < 12) {
    return '上午好'
  } else if (hour < 14) {
    return '中午好'
  } else if (hour < 18) {
    return '下午好'
  } else if (hour < 22) {
    return '晚上好'
  } else {
    return '夜深了，早点休息'
  }
}

/**
 * 检查是否为深夜（用于智能报平安提醒）
 * @returns 是否为深夜（22:00-06:00）
 */
export function isLateNight(): boolean {
  const hour = new Date().getHours()
  return hour >= 22 || hour < 6
}

/**
 * 格式化时间为友好显示
 * @param isoString ISO格式时间字符串
 * @returns 友好的时间显示（如"刚刚"、"5分钟前"）
 */
export function formatFriendlyTime(isoString: string): string {
  const now = new Date().getTime()
  const time = new Date(isoString).getTime()
  const diff = now - time

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < 2 * day) {
    return '昨天'
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`
  } else {
    // 超过7天，显示具体日期
    const date = new Date(isoString)
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}

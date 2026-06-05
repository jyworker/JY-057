/**
 * 网络请求封装
 * 核心职责：
 * 1. 统一处理请求头（Token）
 * 2. 统一处理响应拦截（401跳转、错误提示）
 * 3. 伦理细节：错误提示语温和化
 */

import { ApiResponse } from '../types/index'

// 环境配置
const ENV = 'dev' // 'dev' | 'prod'

const BASE_URL = {
  dev: 'http://localhost:8080',
  prod: 'https://api.hugmom.com'
}

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: any
  showLoading?: boolean
  loadingText?: string
}

/**
 * 通用请求方法
 */
export function request<T>(options: RequestOptions): Promise<T> {
  const {
    url,
    method = 'GET',
    data,
    header = {},
    showLoading = false,
    loadingText = '加载中...'
  } = options

  // 显示加载提示
  if (showLoading) {
    wx.showLoading({
      title: loadingText,
      mask: true
    })
  }

  // 获取Token
  const token = wx.getStorageSync('token') || ''

  return new Promise((resolve, reject) => {
    wx.request({
      url: `${BASE_URL[ENV]}${url}`,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...header
      },
      success: (res) => {
        if (showLoading) {
          wx.hideLoading()
        }

        const response = res.data as ApiResponse<T>

        // 响应拦截 - 统一处理业务逻辑
        if (response.code === 401) {
          // 未授权：清除缓存，跳转登录页
          console.warn('Token失效，跳转登录页')
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          
          // 温和的提示语
          wx.showToast({
            title: '请先登录哦',
            icon: 'none',
            duration: 2000
          })

          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/index'
            })
          }, 2000)

          reject(new Error('未授权'))
          return
        }

        if (!response.success) {
          // 业务失败：温和化错误提示
          const friendlyMsg = getFriendlyErrorMessage(response.msg)
          wx.showToast({
            title: friendlyMsg,
            icon: 'none',
            duration: 2500
          })
          reject(new Error(response.msg))
          return
        }

        // 成功返回数据
        resolve(response.data)
      },
      fail: (err) => {
        if (showLoading) {
          wx.hideLoading()
        }

        console.error('网络请求失败:', err)

        // 伦理细节：温和的网络错误提示
        wx.showToast({
          title: '网络开小差了，请稍后再试',
          icon: 'none',
          duration: 2500
        })

        reject(err)
      }
    })
  })
}

/**
 * GET请求
 */
export function get<T>(url: string, data?: any, showLoading = false): Promise<T> {
  return request<T>({
    url,
    method: 'GET',
    data,
    showLoading
  })
}

/**
 * POST请求
 */
export function post<T>(url: string, data?: any, showLoading = false): Promise<T> {
  return request<T>({
    url,
    method: 'POST',
    data,
    showLoading
  })
}

/**
 * PUT请求
 */
export function put<T>(url: string, data?: any, showLoading = false): Promise<T> {
  return request<T>({
    url,
    method: 'PUT',
    data,
    showLoading
  })
}

/**
 * DELETE请求
 */
export function del<T>(url: string, data?: any, showLoading = false): Promise<T> {
  return request<T>({
    url,
    method: 'DELETE',
    data,
    showLoading
  })
}

/**
 * 伦理细节：将技术性错误信息转换为温和的用户友好提示
 * @param errorMsg 原始错误消息
 * @returns 友好的错误提示
 */
function getFriendlyErrorMessage(errorMsg: string): string {
  const errorMap: Record<string, string> = {
    'Network Error': '网络开小差了，请稍后再试',
    'Timeout': '请求超时了，请检查网络后重试',
    'Server Error': '服务器累了，休息一下再试吧',
    '404': '找不到这个页面了',
    '500': '服务器遇到了问题，我们正在修复',
    'Token过期': '登录信息过期了，请重新登录',
    '参数错误': '输入信息有误，请检查后重试'
  }

  // 查找匹配的友好提示
  for (const [key, value] of Object.entries(errorMap)) {
    if (errorMsg.includes(key)) {
      return value
    }
  }

  // 如果没有匹配，返回原始消息（但确保不会太技术化）
  return errorMsg.length > 20 ? '操作失败，请稍后再试' : errorMsg
}

/**
 * 上传文件（用于照片上传）
 */
export function uploadFile(filePath: string, formData?: any): Promise<string> {
  const token = wx.getStorageSync('token') || ''

  return new Promise((resolve, reject) => {
    wx.uploadFile({
      url: `${BASE_URL[ENV]}/api/upload`,
      filePath,
      name: 'file',
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      formData,
      success: (res) => {
        const response = JSON.parse(res.data) as ApiResponse<{ fileHash: string }>
        if (response.success) {
          resolve(response.data.fileHash)
        } else {
          wx.showToast({
            title: '上传失败，请重试',
            icon: 'none'
          })
          reject(new Error(response.msg))
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '上传失败，请检查网络',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

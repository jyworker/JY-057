/**
 * 添加家庭成员页面
 */

import { addFamilyMember } from '../../utils/api'
import { tapFeedback } from '../../utils/interactions'
import { FamilyMember } from '../../types/index'

interface AddForm {
  name: string
  nickname: string
  relationship: string
  status: 'alive' | 'deceased'
}

Page({
  data: {
    form: {
      name: '',
      nickname: '',
      relationship: '',
      status: 'alive' as 'alive' | 'deceased'
    },
    submitting: false
  },

  onFieldChange(e: WechatMiniprogram.CustomEvent) {
    const field = (e.currentTarget.dataset.field) as keyof AddForm
    const value = (e.detail && (e.detail as { value?: string }).value !== undefined
      ? (e.detail as { value: string }).value
      : String(e.detail || ''))
    this.setData({
      [`form.${field}`]: value
    })
  },

  onStatusTap(e: WechatMiniprogram.TouchEvent) {
    tapFeedback()
    const status = e.currentTarget.dataset.status as 'alive' | 'deceased'
    this.setData({
      'form.status': status
    })
  },

  async handleSubmit() {
    tapFeedback()
    const { form } = this.data as { form: AddForm }

    if (!form.name || !form.name.trim()) {
      wx.showToast({
        title: '请输入姓名',
        icon: 'none'
      })
      return
    }

    this.setData({ submitting: true })

    try {
      const payload: Partial<FamilyMember> = {
        name: form.name.trim(),
        nickname: form.nickname.trim() || form.name.trim(),
        relationship: form.relationship.trim() || undefined,
        status: form.status,
        photoHash: '' // 暂无头像，后端可接受空
      }

      await addFamilyMember(payload)

      wx.showToast({
        title: '添加成功',
        icon: 'success'
      })

      setTimeout(() => {
        wx.navigateBack()
      }, 800)
    } catch (err) {
      console.error('添加成员失败:', err)
      this.setData({ submitting: false })
    }
  }
})

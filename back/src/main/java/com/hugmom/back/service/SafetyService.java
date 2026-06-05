package com.hugmom.back.service;

import com.hugmom.back.entity.SafetyMessage;

import java.util.List;

/**
 * 报平安服务接口
 * 
 * @author HugMom Team
 */
public interface SafetyService {

    /**
     * 发送平安消息
     */
    SafetyMessage sendSafetyMessage(String userId, String memberId, String content, 
                                   Boolean isScheduled, String scheduledTime);

    /**
     * 获取平安消息历史
     */
    List<SafetyMessage> getSafetyHistory(String memberId);

    /**
     * 取消定时消息
     */
    void cancelScheduledMessage(String messageId);
}

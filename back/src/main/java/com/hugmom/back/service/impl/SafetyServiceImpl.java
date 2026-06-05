package com.hugmom.back.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.hugmom.back.common.BusinessException;
import com.hugmom.back.common.ResultCode;
import com.hugmom.back.entity.FamilyMember;
import com.hugmom.back.entity.SafetyMessage;
import com.hugmom.back.mapper.FamilyMemberMapper;
import com.hugmom.back.mapper.SafetyMessageMapper;
import com.hugmom.back.service.SafetyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 报平安服务实现类
 * 
 * @author HugMom Team
 */
@Slf4j
@Service
public class SafetyServiceImpl implements SafetyService {

    @Autowired
    private SafetyMessageMapper safetyMessageMapper;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Override
    public SafetyMessage sendSafetyMessage(String userId, String memberId, String content, 
                                          Boolean isScheduled, String scheduledTime) {
        // 伦理守护：验证成员状态
        FamilyMember member = familyMemberMapper.selectByMemberId(memberId);
        if (member == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "家庭成员不存在");
        }
        
        // 伦理阻断：已故亲人不可报平安
        if ("deceased".equals(member.getStatus())) {
            throw new BusinessException(ResultCode.ETHICS_DECEASED_NO_SAFETY);
        }
        
        SafetyMessage message = new SafetyMessage();
        message.setMessageId("msg_" + DateUtil.format(DateUtil.date(), "yyyyMMdd") + "_" + IdUtil.randomUUID().substring(0, 8));
        message.setUserId(userId);
        message.setMemberId(memberId);
        message.setContent(content);
        message.setIsScheduled(isScheduled != null && isScheduled);
        
        if (message.getIsScheduled() && scheduledTime != null) {
            // 定时消息
            try {
                LocalDateTime dt = LocalDateTime.parse(scheduledTime, DateTimeFormatter.ISO_DATE_TIME);
                message.setScheduledTime(dt);
                message.setStatus("pending");
            } catch (Exception e) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "定时时间格式错误");
            }
            message.setSendTime(message.getScheduledTime());
        } else {
            // 立即发送
            message.setSendTime(LocalDateTime.now());
            message.setStatus("sent");
        }
        
        safetyMessageMapper.insert(message);
        
        // TODO: 如果是定时消息，需要放入定时任务队列
        
        log.info("报平安消息{}成功: messageId={}, memberId={}", 
                 message.getIsScheduled() ? "定时发送" : "发送", message.getMessageId(), memberId);
        return message;
    }

    @Override
    public List<SafetyMessage> getSafetyHistory(String memberId) {
        return safetyMessageMapper.selectByMemberId(memberId);
    }

    @Override
    public void cancelScheduledMessage(String messageId) {
        SafetyMessage message = safetyMessageMapper.selectByMessageId(messageId);
        if (message == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "消息不存在");
        }
        
        if (!"pending".equals(message.getStatus())) {
            throw new BusinessException("该消息已发送，无法取消");
        }
        
        safetyMessageMapper.deleteByMessageId(messageId);
        log.info("取消定时消息成功: messageId={}", messageId);
    }
}

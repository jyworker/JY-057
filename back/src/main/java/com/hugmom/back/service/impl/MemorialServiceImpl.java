package com.hugmom.back.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.hugmom.back.common.BusinessException;
import com.hugmom.back.common.ResultCode;
import com.hugmom.back.entity.FamilyMember;
import com.hugmom.back.entity.MemorialMessage;
import com.hugmom.back.mapper.FamilyMemberMapper;
import com.hugmom.back.mapper.MemorialMessageMapper;
import com.hugmom.back.service.MemorialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 纪念空间服务实现类
 * 
 * @author HugMom Team
 */
@Slf4j
@Service
public class MemorialServiceImpl implements MemorialService {

    @Autowired
    private MemorialMessageMapper memorialMessageMapper;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Override
    public MemorialMessage createMemorialMessage(String userId, String memberId, String content, List<String> photos) {
        // 伦理守护：验证成员状态
        FamilyMember member = familyMemberMapper.selectByMemberId(memberId);
        if (member == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "家庭成员不存在");
        }
        
        // 伦理阻断：健在亲人不可使用纪念功能
        if ("alive".equals(member.getStatus())) {
            throw new BusinessException(ResultCode.ETHICS_ALIVE_NO_MEMORIAL);
        }
        
        MemorialMessage message = new MemorialMessage();
        message.setMessageId("memorial_" + DateUtil.format(DateUtil.date(), "yyyyMMdd") + "_" + IdUtil.randomUUID().substring(0, 8));
        message.setUserId(userId);
        message.setMemberId(memberId);
        message.setContent(content);
        
        // 将照片列表序列化为JSON
        if (photos != null && !photos.isEmpty()) {
            message.setPhotos(JSON.toJSONString(photos));
        }
        
        memorialMessageMapper.insert(message);
        
        log.info("创建纪念留言成功: messageId={}, memberId={}", message.getMessageId(), memberId);
        return message;
    }

    @Override
    public List<MemorialMessage> getMemorialMessages(String memberId) {
        return memorialMessageMapper.selectByMemberId(memberId);
    }

    @Override
    public void deleteMemorialMessage(String messageId) {
        int rows = memorialMessageMapper.deleteByMessageId(messageId);
        if (rows == 0) {
            throw new BusinessException("删除纪念留言失败");
        }
        log.info("删除纪念留言成功: messageId={}", messageId);
    }

    @Override
    public List<String> getMemorialPhotos(String memberId) {
        // TODO: 从存储服务获取该成员的所有照片Hash
        // 这里返回模拟数据
        List<String> photos = new ArrayList<>();
        photos.add("photo_hash_001");
        photos.add("photo_hash_002");
        photos.add("photo_hash_003");
        return photos;
    }
}

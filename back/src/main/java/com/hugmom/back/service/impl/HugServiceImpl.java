package com.hugmom.back.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.hugmom.back.common.BusinessException;
import com.hugmom.back.common.ResultCode;
import com.hugmom.back.entity.FamilyMember;
import com.hugmom.back.entity.HugRecord;
import com.hugmom.back.mapper.FamilyMemberMapper;
import com.hugmom.back.mapper.HugRecordMapper;
import com.hugmom.back.service.EchoService;
import com.hugmom.back.service.HugService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 拥抱服务实现类
 * 
 * @author HugMom Team
 */
@Slf4j
@Service
public class HugServiceImpl implements HugService {

    @Autowired
    private HugRecordMapper hugRecordMapper;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private EchoService echoService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HugRecord sendHug(String userId, String memberId, Integer duration, String timestamp) {
        // 伦理守护：验证成员状态
        FamilyMember member = familyMemberMapper.selectByMemberId(memberId);
        if (member == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "家庭成员不存在");
        }
        
        // 伦理阻断：已故亲人不可拥抱
        if ("deceased".equals(member.getStatus())) {
            throw new BusinessException(ResultCode.ETHICS_DECEASED_NO_HUG);
        }
        
        // 创建拥抱记录
        HugRecord record = new HugRecord();
        record.setHugId("hug_" + DateUtil.format(DateUtil.date(), "yyyyMMdd") + "_" + IdUtil.randomUUID().substring(0, 8));
        record.setUserId(userId);
        record.setMemberId(memberId);
        record.setDuration(duration);
        
        // 解析时间戳
        try {
            LocalDateTime dt = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
            record.setTimestamp(dt);
        } catch (Exception e) {
            record.setTimestamp(LocalDateTime.now());
        }
        
        // TODO: 情感分析 - 根据拥抱时长和频率分析情感
        record.setEmotion("温暖");
        
        hugRecordMapper.insert(record);
        
        // 更新成员的最后拥抱时间
        familyMemberMapper.updateLastHugTime(memberId);
        
        // 爱的回响：记录公益贡献
        try {
            echoService.recordHugContribution(userId);
            log.info("公益贡献记录成功: userId={}", userId);
        } catch (Exception e) {
            log.error("公益贡献记录失败", e);
            // 不影响主流程，继续执行
        }
        
        log.info("拥抱记录成功: hugId={}, memberId={}, duration={}", record.getHugId(), memberId, duration);
        return record;
    }

    @Override
    public List<HugRecord> getHugHistory(String memberId) {
        return hugRecordMapper.selectByMemberId(memberId);
    }

    @Override
    public Map<String, Object> getHugStats(String memberId) {
        return hugRecordMapper.selectStatsByMemberId(memberId);
    }
}

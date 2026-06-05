package com.hugmom.back.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.hugmom.back.common.BusinessException;
import com.hugmom.back.common.ResultCode;
import com.hugmom.back.entity.FamilyMember;
import com.hugmom.back.mapper.FamilyMemberMapper;
import com.hugmom.back.service.FamilyMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 家庭成员服务实现类
 * 
 * @author HugMom Team
 */
@Slf4j
@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Override
    public List<FamilyMember> getFamilyMembers(String userId) {
        return familyMemberMapper.selectByUserId(userId);
    }

    @Override
    public FamilyMember getFamilyMember(String memberId) {
        FamilyMember member = familyMemberMapper.selectByMemberId(memberId);
        if (member == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "家庭成员不存在");
        }
        return member;
    }

    @Override
    public FamilyMember addFamilyMember(String userId, FamilyMember member) {
        // 参数校验
        if (StrUtil.isBlank(member.getName())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "姓名不能为空");
        }
        if (StrUtil.isBlank(member.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "成员状态不能为空");
        }
        
        // 生成成员ID
        member.setMemberId("member_" + IdUtil.simpleUUID());
        member.setUserId(userId);
        
        int rows = familyMemberMapper.insert(member);
        if (rows == 0) {
            throw new BusinessException("添加家庭成员失败");
        }
        
        log.info("添加家庭成员成功: memberId={}, name={}", member.getMemberId(), member.getName());
        return member;
    }

    @Override
    public FamilyMember updateFamilyMember(String memberId, FamilyMember member) {
        FamilyMember existMember = getFamilyMember(memberId);
        
        member.setMemberId(memberId);
        int rows = familyMemberMapper.updateByMemberId(member);
        if (rows == 0) {
            throw new BusinessException("更新家庭成员失败");
        }
        
        log.info("更新家庭成员成功: memberId={}", memberId);
        return getFamilyMember(memberId);
    }

    @Override
    public void deleteFamilyMember(String memberId) {
        int rows = familyMemberMapper.deleteByMemberId(memberId);
        if (rows == 0) {
            throw new BusinessException("删除家庭成员失败");
        }
        log.info("删除家庭成员成功: memberId={}", memberId);
    }
}

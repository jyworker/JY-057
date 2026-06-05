package com.hugmom.back.service;

import com.hugmom.back.entity.FamilyMember;

import java.util.List;

/**
 * 家庭成员服务接口
 * 
 * @author HugMom Team
 */
public interface FamilyMemberService {

    /**
     * 获取用户的所有家庭成员
     */
    List<FamilyMember> getFamilyMembers(String userId);

    /**
     * 获取单个家庭成员详情
     */
    FamilyMember getFamilyMember(String memberId);

    /**
     * 添加家庭成员
     */
    FamilyMember addFamilyMember(String userId, FamilyMember member);

    /**
     * 更新家庭成员信息
     */
    FamilyMember updateFamilyMember(String memberId, FamilyMember member);

    /**
     * 删除家庭成员
     */
    void deleteFamilyMember(String memberId);
}

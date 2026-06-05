package com.hugmom.back.mapper;

import com.hugmom.back.entity.FamilyMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 家庭成员Mapper接口
 * 
 * @author HugMom Team
 */
@Mapper
public interface FamilyMemberMapper {

    /**
     * 根据用户ID查询所有家庭成员
     */
    List<FamilyMember> selectByUserId(@Param("userId") String userId);

    /**
     * 根据成员ID查询成员
     */
    FamilyMember selectByMemberId(@Param("memberId") String memberId);

    /**
     * 插入家庭成员
     */
    int insert(FamilyMember member);

    /**
     * 更新家庭成员信息
     */
    int updateByMemberId(FamilyMember member);

    /**
     * 更新最后拥抱时间
     */
    int updateLastHugTime(@Param("memberId") String memberId);

    /**
     * 删除家庭成员
     */
    int deleteByMemberId(@Param("memberId") String memberId);
}

package com.hugmom.back.mapper;

import com.hugmom.back.entity.MotherReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 妈妈回复Mapper接口 - 爱的回响功能
 * 
 * @author HugMom Team
 */
@Mapper
public interface MotherReplyMapper {

    /**
     * 插入妈妈回复
     */
    int insert(MotherReply reply);

    /**
     * 根据成员ID查询回复列表
     */
    List<MotherReply> selectByMemberId(@Param("memberId") String memberId);

    /**
     * 根据用户ID查询回复列表
     */
    List<MotherReply> selectByUserId(@Param("userId") String userId);

    /**
     * 根据回复ID查询
     */
    MotherReply selectByReplyId(@Param("replyId") String replyId);

    /**
     * 标记为已读
     */
    int updateReadStatus(@Param("replyId") String replyId);

    /**
     * 统计未读回复数量
     */
    Integer countUnreadByUserId(@Param("userId") String userId);
}

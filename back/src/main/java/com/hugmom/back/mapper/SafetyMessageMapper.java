package com.hugmom.back.mapper;

import com.hugmom.back.entity.SafetyMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 报平安消息Mapper接口
 * 
 * @author HugMom Team
 */
@Mapper
public interface SafetyMessageMapper {

    /**
     * 插入报平安消息
     */
    int insert(SafetyMessage message);

    /**
     * 根据成员ID查询消息历史
     */
    List<SafetyMessage> selectByMemberId(@Param("memberId") String memberId);

    /**
     * 根据消息ID查询消息
     */
    SafetyMessage selectByMessageId(@Param("messageId") String messageId);

    /**
     * 更新消息状态
     */
    int updateStatus(@Param("messageId") String messageId, @Param("status") String status);

    /**
     * 删除消息（取消定时消息）
     */
    int deleteByMessageId(@Param("messageId") String messageId);
}

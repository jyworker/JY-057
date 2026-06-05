package com.hugmom.back.mapper;

import com.hugmom.back.entity.MemorialMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 纪念留言Mapper接口
 * 
 * @author HugMom Team
 */
@Mapper
public interface MemorialMessageMapper {

    /**
     * 插入纪念留言
     */
    int insert(MemorialMessage message);

    /**
     * 根据成员ID查询留言列表
     */
    List<MemorialMessage> selectByMemberId(@Param("memberId") String memberId);

    /**
     * 根据留言ID查询留言
     */
    MemorialMessage selectByMessageId(@Param("messageId") String messageId);

    /**
     * 删除留言
     */
    int deleteByMessageId(@Param("messageId") String messageId);
}

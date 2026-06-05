package com.hugmom.back.mapper;

import com.hugmom.back.entity.HugRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 拥抱记录Mapper接口
 * 
 * @author HugMom Team
 */
@Mapper
public interface HugRecordMapper {

    /**
     * 插入拥抱记录
     */
    int insert(HugRecord record);

    /**
     * 根据成员ID查询拥抱历史
     */
    List<HugRecord> selectByMemberId(@Param("memberId") String memberId);

    /**
     * 获取拥抱统计数据
     */
    Map<String, Object> selectStatsByMemberId(@Param("memberId") String memberId);
}

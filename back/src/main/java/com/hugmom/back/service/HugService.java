package com.hugmom.back.service;

import com.hugmom.back.entity.HugRecord;

import java.util.List;
import java.util.Map;

/**
 * 拥抱服务接口
 * 
 * @author HugMom Team
 */
public interface HugService {

    /**
     * 发送拥抱
     */
    HugRecord sendHug(String userId, String memberId, Integer duration, String timestamp);

    /**
     * 获取拥抱历史记录
     */
    List<HugRecord> getHugHistory(String memberId);

    /**
     * 获取拥抱统计数据
     */
    Map<String, Object> getHugStats(String memberId);
}

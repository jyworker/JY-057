package com.hugmom.back.service;

import com.hugmom.back.entity.MemorialMessage;

import java.util.List;

/**
 * 纪念空间服务接口
 * 
 * @author HugMom Team
 */
public interface MemorialService {

    /**
     * 创建纪念留言
     */
    MemorialMessage createMemorialMessage(String userId, String memberId, String content, List<String> photos);

    /**
     * 获取纪念留言列表
     */
    List<MemorialMessage> getMemorialMessages(String memberId);

    /**
     * 删除纪念留言
     */
    void deleteMemorialMessage(String messageId);

    /**
     * 获取纪念相册（老照片）
     */
    List<String> getMemorialPhotos(String memberId);
}

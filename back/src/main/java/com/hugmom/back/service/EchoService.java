package com.hugmom.back.service;

import com.hugmom.back.entity.AnnualReport;
import com.hugmom.back.entity.MotherReply;

import java.util.List;
import java.util.Map;

/**
 * 爱的回响服务接口 - 双向温暖闭环
 * 
 * @author HugMom Team
 */
public interface EchoService {

    // ========== 妈妈回复相关 ==========
    
    /**
     * 发送妈妈回复
     */
    MotherReply sendMotherReply(String userId, String memberId, String originalMessageId, 
                               String replyType, String voiceUrl, String textContent);

    /**
     * 获取收到的妈妈回复列表
     */
    List<MotherReply> getMotherReplies(String memberId);

    /**
     * 标记回复为已读
     */
    void markReplyAsRead(String replyId);

    /**
     * 获取未读回复数量
     */
    Integer getUnreadReplyCount(String userId);

    // ========== 年度报告相关 ==========
    
    /**
     * 生成年度爱的报告
     */
    AnnualReport generateAnnualReport(String userId, Integer year);

    /**
     * 获取历史年度报告列表
     */
    List<AnnualReport> getAnnualReports(String userId);

    /**
     * 获取指定年度报告详情
     */
    AnnualReport getAnnualReportDetail(String reportId);

    /**
     * 分享年度报告（生成分享海报）
     */
    Map<String, Object> shareAnnualReport(String reportId);

    // ========== 公益联动相关 ==========
    
    /**
     * 获取公益联动实时数据
     */
    Map<String, Object> getPublicWelfareStats();

    /**
     * 获取个人公益贡献记录
     */
    Map<String, Object> getMyWelfareContribution(String userId);

    /**
     * 获取公益故事墙
     */
    Map<String, Object> getWelfareStories(Integer page, Integer pageSize);

    /**
     * 记录拥抱公益贡献（在发送拥抱时调用）
     */
    void recordHugContribution(String userId);
}

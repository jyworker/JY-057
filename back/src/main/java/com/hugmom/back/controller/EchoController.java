package com.hugmom.back.controller;

import com.hugmom.back.common.Result;
import com.hugmom.back.entity.AnnualReport;
import com.hugmom.back.entity.MotherReply;
import com.hugmom.back.service.EchoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 爱的回响控制器 - 双向温暖闭环
 * 
 * @author HugMom Team
 */
@Slf4j
@RestController
@RequestMapping("/api/echo")
public class EchoController {

    @Autowired
    private EchoService echoService;

    // ========== 妈妈回复相关接口 ==========

    /**
     * 妈妈端回复 - 一键回发"妈妈也想你了"
     */
    @PostMapping("/mother-reply")
    public Result<Map<String, Object>> sendMotherReply(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {
        
        String userId = (String) request.getAttribute("userId");
        String originalMessageId = (String) params.get("originalMessageId");
        String replyType = (String) params.get("replyType");
        String voiceUrl = (String) params.get("voiceUrl");
        String textContent = (String) params.get("textContent");
        
        // 这里的userId实际应该是发送回复的成员ID，为了演示简化处理
        String memberId = "member_001";  // 实际应从参数或token中获取
        
        MotherReply reply = echoService.sendMotherReply(userId, memberId, originalMessageId, 
                                                        replyType, voiceUrl, textContent);
        
        // 构造返回数据
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("replyId", reply.getReplyId());
        result.put("aiTranscript", reply.getAiTranscript());
        result.put("sendTime", reply.getReceiveTime().toString());
        result.put("emotion", reply.getEmotion());
        
        return Result.success("回复发送成功", result);
    }

    /**
     * 获取收到的妈妈回复列表
     */
    @GetMapping("/replies/{memberId}")
    public Result<List<MotherReply>> getMotherReplies(@PathVariable String memberId) {
        List<MotherReply> replies = echoService.getMotherReplies(memberId);
        return Result.success("获取成功", replies);
    }

    /**
     * 标记妈妈回复为已读
     */
    @PutMapping("/reply/{replyId}/read")
    public Result<Void> markReplyAsRead(@PathVariable String replyId) {
        echoService.markReplyAsRead(replyId);
        return Result.success("标记成功", null);
    }

    /**
     * 获取未读回复数量
     */
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadReplyCount(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        Integer count = echoService.getUnreadReplyCount(userId);
        return Result.success("获取成功", count);
    }

    // ========== 年度报告相关接口 ==========

    /**
     * 生成年度爱的报告
     */
    @PostMapping("/annual-report")
    public Result<AnnualReport> generateAnnualReport(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {
        
        String userId = (String) request.getAttribute("userId");
        Integer year = params.get("year") != null ? (Integer) params.get("year") : null;
        
        AnnualReport report = echoService.generateAnnualReport(userId, year);
        return Result.success("报告生成成功", report);
    }

    /**
     * 获取历史年度报告列表
     */
    @GetMapping("/annual-reports")
    public Result<List<AnnualReport>> getAnnualReports(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        List<AnnualReport> reports = echoService.getAnnualReports(userId);
        return Result.success("获取成功", reports);
    }

    /**
     * 获取指定年度报告详情
     */
    @GetMapping("/annual-report/{reportId}")
    public Result<AnnualReport> getAnnualReportDetail(@PathVariable String reportId) {
        AnnualReport report = echoService.getAnnualReportDetail(reportId);
        return Result.success("获取成功", report);
    }

    /**
     * 分享年度报告（生成分享海报）
     */
    @PostMapping("/annual-report/{reportId}/share")
    public Result<Map<String, Object>> shareAnnualReport(@PathVariable String reportId) {
        Map<String, Object> shareInfo = echoService.shareAnnualReport(reportId);
        return Result.success("分享信息生成成功", shareInfo);
    }

    // ========== 公益联动相关接口 ==========

    /**
     * 获取公益联动实时数据
     */
    @GetMapping("/public-welfare/stats")
    public Result<Map<String, Object>> getPublicWelfareStats() {
        Map<String, Object> stats = echoService.getPublicWelfareStats();
        return Result.success("获取成功", stats);
    }

    /**
     * 获取个人公益贡献记录
     */
    @GetMapping("/public-welfare/my-contribution")
    public Result<Map<String, Object>> getMyWelfareContribution(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        Map<String, Object> contribution = echoService.getMyWelfareContribution(userId);
        return Result.success("获取成功", contribution);
    }

    /**
     * 获取公益故事墙
     */
    @GetMapping("/public-welfare/stories")
    public Result<Map<String, Object>> getWelfareStories(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        Map<String, Object> stories = echoService.getWelfareStories(page, pageSize);
        return Result.success("获取成功", stories);
    }
}

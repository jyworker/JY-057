package com.hugmom.back.controller;

import com.hugmom.back.common.Result;
import com.hugmom.back.entity.SafetyMessage;
import com.hugmom.back.service.SafetyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 报平安控制器
 * 
 * @author HugMom Team
 */
@Slf4j
@RestController
@RequestMapping("/api/safety")
public class SafetyController {

    @Autowired
    private SafetyService safetyService;

    /**
     * 发送平安消息
     */
    @PostMapping("/send")
    public Result<SafetyMessage> sendSafetyMessage(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {
        
        String userId = (String) request.getAttribute("userId");
        String memberId = (String) params.get("memberId");
        String content = (String) params.get("content");
        Boolean isScheduled = (Boolean) params.get("isScheduled");
        String scheduledTime = (String) params.get("scheduledTime");
        
        SafetyMessage message = safetyService.sendSafetyMessage(
                userId, memberId, content, isScheduled, scheduledTime);
        
        return Result.success("消息发送成功", message);
    }

    /**
     * 获取平安消息历史
     */
    @GetMapping("/history/{memberId}")
    public Result<List<SafetyMessage>> getSafetyHistory(@PathVariable String memberId) {
        List<SafetyMessage> history = safetyService.getSafetyHistory(memberId);
        return Result.success("获取成功", history);
    }

    /**
     * 取消定时消息
     */
    @DeleteMapping("/scheduled/{messageId}")
    public Result<Void> cancelScheduledMessage(@PathVariable String messageId) {
        safetyService.cancelScheduledMessage(messageId);
        return Result.success("定时消息已取消", null);
    }
}

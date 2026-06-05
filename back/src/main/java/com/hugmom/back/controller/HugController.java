package com.hugmom.back.controller;

import com.hugmom.back.common.Result;
import com.hugmom.back.entity.HugRecord;
import com.hugmom.back.service.HugService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 拥抱控制器
 * 
 * @author HugMom Team
 */
@Slf4j
@RestController
@RequestMapping("/api/hug")
public class HugController {

    @Autowired
    private HugService hugService;

    /**
     * 发送拥抱
     */
    @PostMapping("/send")
    public Result<HugRecord> sendHug(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {
        
        String userId = (String) request.getAttribute("userId");
        String memberId = (String) params.get("memberId");
        Integer duration = (Integer) params.get("duration");
        String timestamp = (String) params.get("timestamp");
        
        HugRecord record = hugService.sendHug(userId, memberId, duration, timestamp);
        return Result.success("拥抱记录成功", record);
    }

    /**
     * 获取拥抱历史记录
     */
    @GetMapping("/history/{memberId}")
    public Result<List<HugRecord>> getHugHistory(@PathVariable String memberId) {
        List<HugRecord> history = hugService.getHugHistory(memberId);
        return Result.success("获取成功", history);
    }

    /**
     * 获取拥抱统计数据
     */
    @GetMapping("/stats/{memberId}")
    public Result<Map<String, Object>> getHugStats(@PathVariable String memberId) {
        Map<String, Object> stats = hugService.getHugStats(memberId);
        return Result.success("获取成功", stats);
    }
}

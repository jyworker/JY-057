package com.hugmom.back.controller;

import com.hugmom.back.common.Result;
import com.hugmom.back.entity.MemorialMessage;
import com.hugmom.back.service.MemorialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 纪念空间控制器
 * 
 * @author HugMom Team
 */
@Slf4j
@RestController
@RequestMapping("/api/memorial")
public class MemorialController {

    @Autowired
    private MemorialService memorialService;

    /**
     * 创建纪念留言
     */
    @PostMapping("/message")
    public Result<MemorialMessage> createMemorialMessage(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {
        
        String userId = (String) request.getAttribute("userId");
        String memberId = (String) params.get("memberId");
        String content = (String) params.get("content");
        @SuppressWarnings("unchecked")
        List<String> photos = (List<String>) params.get("photos");
        
        MemorialMessage message = memorialService.createMemorialMessage(
                userId, memberId, content, photos);
        
        return Result.success("留言创建成功", message);
    }

    /**
     * 获取纪念留言列表
     */
    @GetMapping("/messages/{memberId}")
    public Result<List<MemorialMessage>> getMemorialMessages(@PathVariable String memberId) {
        List<MemorialMessage> messages = memorialService.getMemorialMessages(memberId);
        return Result.success("获取成功", messages);
    }

    /**
     * 删除纪念留言
     */
    @DeleteMapping("/message/{messageId}")
    public Result<Void> deleteMemorialMessage(@PathVariable String messageId) {
        memorialService.deleteMemorialMessage(messageId);
        return Result.success("留言删除成功", null);
    }

    /**
     * 获取纪念相册（老照片）
     */
    @GetMapping("/photos/{memberId}")
    public Result<List<String>> getMemorialPhotos(@PathVariable String memberId) {
        List<String> photos = memorialService.getMemorialPhotos(memberId);
        return Result.success("获取成功", photos);
    }
}

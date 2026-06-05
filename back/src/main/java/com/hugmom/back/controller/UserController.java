package com.hugmom.back.controller;

import com.hugmom.back.common.Result;
import com.hugmom.back.entity.User;
import com.hugmom.back.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 
 * @author HugMom Team
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        // 从拦截器中获取已验证的userId
        String userId = (String) request.getAttribute("userId");
        
        User user = userService.getUserInfo(userId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("nickName", user.getNickName());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("phoneNumber", user.getPhoneNumber());
        
        return Result.success("获取成功", data);
    }

    /**
     * 更新用户设置
     */
    @PutMapping("/settings")
    public Result<Map<String, Object>> updateSettings(
            HttpServletRequest request,
            @RequestBody Map<String, Object> params) {
        
        // 从拦截器中获取已验证的userId
        String userId = (String) request.getAttribute("userId");
        
        Boolean emotionHighlight = (Boolean) params.get("emotionHighlight");
        Boolean vibration = (Boolean) params.get("vibration");
        
        User user = userService.updateSettings(userId, emotionHighlight, vibration);
        
        Map<String, Object> data = new HashMap<>();
        data.put("emotionHighlight", user.getEmotionHighlight());
        data.put("vibration", user.getVibration());
        
        return Result.success("设置更新成功", data);
    }
}

package com.hugmom.back.controller;

import com.hugmom.back.common.JwtUtil;
import com.hugmom.back.common.Result;
import com.hugmom.back.entity.User;
import com.hugmom.back.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author HugMom Team
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 微信登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String code = params.get("code");
        
        if (code == null || code.isEmpty()) {
            return Result.fail(400, "登录code不能为空");
        }
        
        // 调用登录服务
        User user = userService.login(code);
        
        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getUserId());
        log.info("生成Token成功: userId={}", user.getUserId());
        
        // 构建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("nickName", user.getNickName());
        userInfo.put("avatarUrl", user.getAvatarUrl());
        userInfo.put("phoneNumber", user.getPhoneNumber());
        data.put("userInfo", userInfo);
        
        return Result.success("登录成功", data);
    }

    /**
     * 验证Token（可选，用于测试）
     */
    @GetMapping("/validate")
    public Result<Map<String, Object>> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.fail(401, "Token格式错误");
        }
        
        String token = authHeader.substring(7);
        boolean valid = jwtUtil.validateToken(token);
        
        if (!valid) {
            return Result.fail(401, "Token无效或已过期");
        }
        
        String userId = jwtUtil.getUserIdFromToken(token);
        
        Map<String, Object> data = new HashMap<>();
        data.put("valid", true);
        data.put("userId", userId);
        
        return Result.success("Token有效", data);
    }
}

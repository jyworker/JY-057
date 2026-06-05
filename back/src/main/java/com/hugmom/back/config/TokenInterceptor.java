package com.hugmom.back.config;

import com.alibaba.fastjson2.JSON;
import com.hugmom.back.common.JwtUtil;
import com.hugmom.back.common.Result;
import com.hugmom.back.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Token 拦截器
 * 用于验证请求中的 Token 是否有效
 * 
 * @author HugMom Team
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("正在进行请求头检验");
        // 获取请求头中的 Token
        String authHeader = request.getHeader("Authorization");

        // 如果没有 Token，返回未授权
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            responseUnauthorized(response, "请先登录");
            return false;
        }

        // 提取 Token
        String token = authHeader.substring(7);

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            responseUnauthorized(response, "Token无效或已过期，请重新登录");
            return false;
        }

        // 从 Token 中提取用户ID，存入请求属性
        String userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        log.debug("Token验证通过: userId={}", userId);
        return true;
    }

    /**
     * 返回未授权响应
     */
    private void responseUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<Void> result = Result.fail(ResultCode.UNAUTHORIZED.getCode(), message);
        
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(result));
        writer.flush();
    }
}

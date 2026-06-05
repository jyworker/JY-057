package com.hugmom.back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器
 * 
 * @author HugMom Team
 */
@RestController
public class IndexController {

    /**
     * 欢迎页面
     */
    @GetMapping("/")
    public Map<String, Object> index() {
        Map<String, Object> result = new HashMap<>();
        result.put("service", "拥抱妈妈·爱在平安 后端服务");
        result.put("version", "1.0.0");
        result.put("status", "running");
        result.put("message", "AI向善 · 技术隐形于爱");
        result.put("api_docs", "请访问 /api/ 路径下的接口");
        result.put("example", "http://localhost:8080/api/family/members");
        return result;
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return result;
    }
}

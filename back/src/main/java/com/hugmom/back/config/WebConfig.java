package com.hugmom.back.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置（注册拦截器）
 * 
 * @author HugMom Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有API请求
                .excludePathPatterns(
                        "/api/auth/login",           // 放行登录接口
                        "/api/auth/validate",        // 放行Token验证接口
                        "/",                         // 放行首页
                        "/health"                    // 放行健康检查
                );
    }
}

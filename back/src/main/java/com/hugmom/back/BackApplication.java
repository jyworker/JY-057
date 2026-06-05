package com.hugmom.back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 拥抱妈妈·爱在平安 - 后端服务启动类
 * 核心理念：AI向善 - 技术隐形于爱，伦理嵌入代码
 * 
 * @author HugMom Team
 * @since 2026-02-02
 */
@SpringBootApplication
@MapperScan("com.hugmom.back.mapper")
public class BackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  拥抱妈妈·爱在平安 后端服务启动成功！");
        System.out.println("  AI向善 · 技术隐形于爱");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================\n");
    }
}

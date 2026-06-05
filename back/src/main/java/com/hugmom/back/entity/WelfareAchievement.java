package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公益成就实体类 - 爱的回响 · 双向温暖闭环
 * 
 * @author HugMom Team
 */
@Data
public class WelfareAchievement implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 成就唯一标识
     */
    private String achievementId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 成就名称
     */
    private String name;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 成就图标URL
     */
    private String icon;

    /**
     * 解锁时间
     */
    private LocalDateTime unlockedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

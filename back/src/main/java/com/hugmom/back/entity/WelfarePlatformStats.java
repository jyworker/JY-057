package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 平台公益统计实体类 - 爱的回响 · 双向温暖闭环
 * 
 * @author HugMom Team
 */
@Data
public class WelfarePlatformStats implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 平台累计拥抱次数
     */
    private Long totalPlatformHugs;

    /**
     * 累计公益价值（元）
     */
    private BigDecimal totalWelfareValue;

    /**
     * 已帮助的孤寡妈妈家庭数
     */
    private Integer helpedFamiliesCount;

    /**
     * 本月拥抱次数
     */
    private Integer currentMonthHugs;

    /**
     * 当前活跃贡献者数
     */
    private Integer realtimeContributors;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

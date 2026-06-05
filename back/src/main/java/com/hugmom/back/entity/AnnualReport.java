package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 年度爱的报告实体类 - 爱的回响 · 双向温暖闭环
 * 
 * @author HugMom Team
 */
@Data
public class AnnualReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 报告唯一标识（业务主键）
     */
    private String reportId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 报告年份
     */
    private Integer year;

    /**
     * 总拥抱次数
     */
    private Integer totalHugs;

    /**
     * 总平安消息次数
     */
    private Integer totalSafetyMessages;

    /**
     * 跨越的总里程（公里）
     */
    private BigDecimal totalDistance;

    /**
     * 温暖的日夜数
     */
    private Integer warmDays;

    /**
     * 最活跃月份
     */
    private String mostActiveMonth;

    /**
     * 最常发送的话语
     */
    private String favoriteMessage;

    /**
     * 成员统计数据（JSON格式）
     */
    private String memberStats;

    /**
     * 里程碑记录（JSON格式）
     */
    private String milestones;

    /**
     * 公益贡献值
     */
    private BigDecimal publicWelfareContribution;

    /**
     * 帮助的孤寡妈妈家庭数
     */
    private Integer helpedFamiliesCount;

    /**
     * 生成时间
     */
    private LocalDateTime generateTime;

    /**
     * 分享海报URL
     */
    private String sharePosterUrl;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公益贡献记录实体类 - 爱的回响 · 双向温暖闭环
 * 
 * @author HugMom Team
 */
@Data
public class WelfareContribution implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 贡献记录唯一标识
     */
    private String contributionId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 贡献日期
     */
    private LocalDate date;

    /**
     * 当日拥抱次数
     */
    private Integer hugCount;

    /**
     * 贡献值（每次拥抱0.001）
     */
    private BigDecimal contributionValue;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

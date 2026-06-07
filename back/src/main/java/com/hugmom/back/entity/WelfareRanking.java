package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WelfareRanking implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String statId;

    private String statPeriod;

    private LocalDate statDate;

    private Integer year;

    private Integer month;

    private Integer week;

    private String userId;

    private String nickName;

    private String avatarUrl;

    private Integer hugCount;

    private BigDecimal totalContribution;

    private Integer ranking;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

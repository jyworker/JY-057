package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HugStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String statId;

    private String statPeriod;

    private String statType;

    private LocalDate statDate;

    private Integer year;

    private Integer month;

    private Integer week;

    private Integer hugCount;

    private Integer userCount;

    private Integer memberCount;

    private Long totalDuration;

    private Double avgDuration;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

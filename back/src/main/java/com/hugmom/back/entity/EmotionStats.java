package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmotionStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String statId;

    private String statPeriod;

    private LocalDate statDate;

    private Integer year;

    private Integer month;

    private Integer week;

    private String emotion;

    private Integer count;

    private Double ratio;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

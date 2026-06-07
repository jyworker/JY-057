package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserRetentionStats implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String statId;

    private LocalDate statDate;

    private Integer day1;

    private Integer day3;

    private Integer day7;

    private Integer day14;

    private Integer day30;

    private Integer newUserCount;

    private Integer activeUserCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

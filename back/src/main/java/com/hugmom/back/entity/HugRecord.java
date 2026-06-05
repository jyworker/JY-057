package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 拥抱记录实体类
 * 
 * @author HugMom Team
 */
@Data
public class HugRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 拥抱记录唯一标识（业务主键）
     */
    private String hugId;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 成员ID
     */
    private String memberId;

    /**
     * 拥抱时长（毫秒）
     */
    private Integer duration;

    /**
     * 拥抱时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 情感标签（可选）
     */
    private String emotion;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

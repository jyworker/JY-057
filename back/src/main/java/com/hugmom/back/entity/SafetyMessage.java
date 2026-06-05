package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报平安消息实体类
 * 
 * @author HugMom Team
 */
@Data
public class SafetyMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 消息唯一标识（业务主键）
     */
    private String messageId;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 成员ID
     */
    private String memberId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 是否定时发送
     */
    private Boolean isScheduled;

    /**
     * 定时发送时间
     */
    private LocalDateTime scheduledTime;

    /**
     * 消息状态
     * pending: 待发送
     * sent: 已发送
     * failed: 发送失败
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

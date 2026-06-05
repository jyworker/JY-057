package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 纪念留言实体类
 * 
 * @author HugMom Team
 */
@Data
public class MemorialMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 留言唯一标识（业务主键）
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
     * 留言内容
     */
    private String content;

    /**
     * 附加照片Hash数组（JSON格式存储）
     */
    private String photos;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

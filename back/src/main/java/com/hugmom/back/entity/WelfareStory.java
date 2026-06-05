package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 公益故事实体类 - 爱的回响 · 双向温暖闭环
 * 
 * @author HugMom Team
 */
@Data
public class WelfareStory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 故事唯一标识
     */
    private String storyId;

    /**
     * 故事标题
     */
    private String title;

    /**
     * 故事内容（匿名化处理）
     */
    private String content;

    /**
     * 地区（脱敏）
     */
    private String location;

    /**
     * 帮助日期
     */
    private LocalDate helpDate;

    /**
     * 图片URL数组（JSON格式）
     */
    private String imageUrls;

    /**
     * 是否发布（1:已发布 0:未发布）
     */
    private Boolean isPublished;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 家庭成员实体类
 * 
 * @author HugMom Team
 */
@Data
public class FamilyMember implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 成员唯一标识（业务主键）
     */
    private String memberId;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 昵称（如"妈妈"、"爷爷"）
     */
    private String nickname;

    /**
     * 成员状态（核心字段）
     * alive: 健在
     * deceased: 已故
     */
    private String status;

    /**
     * 照片Hash值（隐私保护）
     */
    private String photoHash;

    /**
     * 最后拥抱时间
     */
    private LocalDateTime lastHugTime;

    /**
     * 关系（如"母亲"、"父亲"）
     */
    private String relationship;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

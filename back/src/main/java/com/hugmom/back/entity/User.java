package com.hugmom.back.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author HugMom Team
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（主键）
     */
    private Long id;

    /**
     * 用户唯一标识（业务主键）
     */
    private String userId;

    /**
     * 微信OpenID
     */
    private String openid;

    /**
     * 微信UnionID（可选）
     */
    private String unionid;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 手机号（脱敏）
     */
    private String phoneNumber;

    /**
     * 是否开启暖心词高亮
     */
    private Boolean emotionHighlight;

    /**
     * 是否开启震动反馈
     */
    private Boolean vibration;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

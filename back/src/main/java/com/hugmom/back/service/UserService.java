package com.hugmom.back.service;

import com.hugmom.back.entity.User;

/**
 * 用户服务接口
 * 
 * @author HugMom Team
 */
public interface UserService {

    /**
     * 微信登录
     * @param code 微信登录code
     * @return 用户信息和Token
     */
    User login(String code);

    /**
     * 根据用户ID获取用户信息
     */
    User getUserInfo(String userId);

    /**
     * 更新用户设置
     */
    User updateSettings(String userId, Boolean emotionHighlight, Boolean vibration);
}

package com.hugmom.back.service;

/**
 * 微信服务接口
 * 
 * @author HugMom Team
 */
public interface WechatService {

    /**
     * 使用微信登录code换取openid
     * 
     * @param code 微信登录code
     * @return openid
     */
    String code2Session(String code);
}

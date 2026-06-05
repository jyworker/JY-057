package com.hugmom.back.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hugmom.back.common.BusinessException;
import com.hugmom.back.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 微信服务实现类
 * 
 * @author HugMom Team
 */
@Slf4j
@Service
public class WechatServiceImpl implements WechatService {

    @Value("${wechat.mini.appid}")
    private String appid;

    @Value("${wechat.mini.secret}")
    private String secret;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.weixin.qq.com")
            .build();

    @Override
    public String code2Session(String code) {
        try {
            // 调用微信 API
            String url = String.format(
                    "/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    appid, secret, code
            );

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("微信 code2session 响应: {}", response);

            JSONObject result = JSON.parseObject(response);

            // 检查是否有错误
            if (result.containsKey("errcode") && result.getInteger("errcode") != 0) {
                String errmsg = result.getString("errmsg");
                log.error("微信登录失败: errcode={}, errmsg={}", result.getInteger("errcode"), errmsg);
                throw new BusinessException("微信登录失败: " + errmsg);
            }

            // 返回 openid
            String openid = result.getString("openid");
            if (openid == null || openid.isEmpty()) {
                throw new BusinessException("获取 openid 失败");
            }

            log.info("微信登录成功: openid={}", openid);
            return openid;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信API异常", e);
            throw new BusinessException("微信登录服务异常，请稍后重试");
        }
    }
}

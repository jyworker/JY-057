package com.hugmom.back.service.impl;

import cn.hutool.core.util.IdUtil;
import com.hugmom.back.common.BusinessException;
import com.hugmom.back.common.JwtUtil;
import com.hugmom.back.common.ResultCode;
import com.hugmom.back.entity.User;
import com.hugmom.back.mapper.UserMapper;
import com.hugmom.back.service.UserService;
import com.hugmom.back.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 
 * @author HugMom Team
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${wechat.mini.enable:false}")
    private Boolean wechatEnable;

    @Override
    public User login(String code) {
        String openid;

        // 判断是否启用真实微信登录
        if (wechatEnable) {
            // 真实环境：调用微信API换取openid
            openid = wechatService.code2Session(code);
        } else {
            // 开发环境：使用模拟数据
            openid = "mock_openid_" + code;
            log.warn("开发模式：使用模拟 openid={}", openid);
        }
        
        User user = userMapper.selectByOpenid(openid);
        
        if (user == null) {
            // 首次登录，创建新用户
            user = new User();
            user.setUserId("user_" + IdUtil.simpleUUID());
            user.setOpenid(openid);
            user.setNickName("用户" + System.currentTimeMillis() % 10000);
            user.setAvatarUrl("https://thirdwx.qlogo.cn/default.png");
            user.setEmotionHighlight(true);
            user.setVibration(true);
            
            userMapper.insert(user);
            log.info("新用户注册成功: userId={}, openid={}", user.getUserId(), openid);
        } else {
            log.info("老用户登录: userId={}, openid={}", user.getUserId(), openid);
        }
        
        log.info("用户登录成功: userId={}", user.getUserId());
        return user;
    }

    @Override
    public User getUserInfo(String userId) {
        User user = userMapper.selectByUserId(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }
        return user;
    }

    @Override
    public User updateSettings(String userId, Boolean emotionHighlight, Boolean vibration) {
        int rows = userMapper.updateSettings(userId, emotionHighlight, vibration);
        if (rows == 0) {
            throw new BusinessException("更新用户设置失败");
        }
        return getUserInfo(userId);
    }
}

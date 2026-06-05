package com.hugmom.back.mapper;

import com.hugmom.back.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 * 
 * @author HugMom Team
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户ID查询用户
     */
    User selectByUserId(@Param("userId") String userId);

    /**
     * 根据OpenID查询用户
     */
    User selectByOpenid(@Param("openid") String openid);

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 更新用户信息
     */
    int updateByUserId(User user);

    /**
     * 更新用户设置
     */
    int updateSettings(@Param("userId") String userId, 
                       @Param("emotionHighlight") Boolean emotionHighlight,
                       @Param("vibration") Boolean vibration);
}

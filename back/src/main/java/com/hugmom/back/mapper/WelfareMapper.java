package com.hugmom.back.mapper;

import com.hugmom.back.entity.WelfareAchievement;
import com.hugmom.back.entity.WelfareContribution;
import com.hugmom.back.entity.WelfarePlatformStats;
import com.hugmom.back.entity.WelfareStory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 公益功能Mapper接口 - 爱的回响功能
 * 
 * @author HugMom Team
 */
@Mapper
public interface WelfareMapper {

    // ========== 公益贡献相关 ==========
    
    /**
     * 插入或更新公益贡献记录
     */
    int insertOrUpdateContribution(WelfareContribution contribution);

    /**
     * 查询用户总贡献值
     */
    BigDecimal selectTotalContributionByUserId(@Param("userId") String userId);

    /**
     * 查询用户贡献历史
     */
    List<WelfareContribution> selectContributionHistory(@Param("userId") String userId, 
                                                         @Param("limit") Integer limit);

    /**
     * 查询用户贡献排名
     */
    Integer selectUserRank(@Param("userId") String userId);

    // ========== 公益成就相关 ==========
    
    /**
     * 插入成就
     */
    int insertAchievement(WelfareAchievement achievement);

    /**
     * 查询用户所有成就
     */
    List<WelfareAchievement> selectAchievementsByUserId(@Param("userId") String userId);

    /**
     * 检查用户是否已解锁某成就
     */
    Integer checkAchievementExists(@Param("userId") String userId, 
                                   @Param("achievementId") String achievementId);

    // ========== 公益故事相关 ==========
    
    /**
     * 查询已发布的公益故事列表（分页）
     */
    List<WelfareStory> selectPublishedStories(@Param("offset") Integer offset, 
                                              @Param("pageSize") Integer pageSize);

    /**
     * 统计已发布故事总数
     */
    Integer countPublishedStories();

    // ========== 平台统计相关 ==========
    
    /**
     * 查询最新的平台统计数据
     */
    WelfarePlatformStats selectLatestPlatformStats();

    /**
     * 插入或更新平台统计数据
     */
    int insertOrUpdatePlatformStats(WelfarePlatformStats stats);
}

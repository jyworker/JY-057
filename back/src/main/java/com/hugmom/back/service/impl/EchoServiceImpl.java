package com.hugmom.back.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hugmom.back.common.BusinessException;
import com.hugmom.back.common.ResultCode;
import com.hugmom.back.entity.*;
import com.hugmom.back.mapper.*;
import com.hugmom.back.service.EchoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 爱的回响服务实现类 - 双向温暖闭环
 * 
 * @author HugMom Team
 */
@Slf4j
@Service
public class EchoServiceImpl implements EchoService {

    @Autowired
    private MotherReplyMapper motherReplyMapper;

    @Autowired
    private AnnualReportMapper annualReportMapper;

    @Autowired
    private WelfareMapper welfareMapper;

    @Autowired
    private HugRecordMapper hugRecordMapper;

    @Autowired
    private SafetyMessageMapper safetyMessageMapper;

    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    // ========== 妈妈回复相关实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MotherReply sendMotherReply(String userId, String memberId, String originalMessageId,
                                       String replyType, String voiceUrl, String textContent) {
        log.info("妈妈回复 - 用户:{}, 成员:{}, 回复类型:{}", userId, memberId, replyType);

        // 创建回复记录
        MotherReply reply = new MotherReply();
        reply.setReplyId("reply_" + DateUtil.format(DateUtil.date(), "yyyyMMdd") + "_" + IdUtil.randomUUID().substring(0, 8));
        reply.setUserId(userId);
        reply.setMemberId(memberId);
        reply.setOriginalMessageId(originalMessageId);
        
        // 判断原始消息类型（简化处理，实际可根据ID前缀判断）
        reply.setOriginalMessageType(originalMessageId.startsWith("hug_") ? "hug" : "safety");
        reply.setReplyType(replyType);
        reply.setIsRead(false);
        reply.setReceiveTime(LocalDateTime.now());

        // 根据回复类型设置内容
        if ("voice".equals(replyType)) {
            reply.setVoiceUrl(voiceUrl);
            // AI转文字（模拟实现，实际需调用腾讯云语音识别）
            reply.setAiTranscript(mockVoiceToText(voiceUrl));
            reply.setContent(reply.getAiTranscript());
        } else if ("text".equals(replyType)) {
            reply.setContent(textContent);
        } else if ("emoji".equals(replyType)) {
            reply.setContent("❤️"); // 表情回复
        }

        // AI情感识别（简化实现）
        reply.setEmotion(detectEmotion(reply.getContent()));

        motherReplyMapper.insert(reply);
        log.info("妈妈回复发送成功 - replyId:{}", reply.getReplyId());

        return reply;
    }

    @Override
    public List<MotherReply> getMotherReplies(String memberId) {
        return motherReplyMapper.selectByMemberId(memberId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markReplyAsRead(String replyId) {
        motherReplyMapper.updateReadStatus(replyId);
    }

    @Override
    public Integer getUnreadReplyCount(String userId) {
        Integer count = motherReplyMapper.countUnreadByUserId(userId);
        return count != null ? count : 0;
    }

    // ========== 年度报告相关实现 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnnualReport generateAnnualReport(String userId, Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }

        log.info("生成年度报告 - 用户:{}, 年份:{}", userId, year);

        // 检查是否已生成过该年度报告
        AnnualReport existingReport = annualReportMapper.selectByUserIdAndYear(userId, year);
        if (existingReport != null) {
            log.info("年度报告已存在，返回现有报告");
            return existingReport;
        }

        // 创建报告对象
        AnnualReport report = new AnnualReport();
        report.setReportId("report_" + year + "_" + IdUtil.randomUUID().substring(0, 8));
        report.setUserId(userId);
        report.setYear(year);
        report.setGenerateTime(LocalDateTime.now());

        // 统计拥抱数据
        Map<String, Object> hugStats = calculateHugStats(userId, year);
        report.setTotalHugs((Integer) hugStats.get("totalHugs"));
        report.setWarmDays((Integer) hugStats.get("warmDays"));
        report.setMostActiveMonth((String) hugStats.get("mostActiveMonth"));

        // 统计平安消息数据
        report.setTotalSafetyMessages(calculateSafetyMessageCount(userId, year));

        // 计算跨越里程（模拟：每次拥抱平均跨越100公里）
        report.setTotalDistance(BigDecimal.valueOf(report.getTotalHugs() * 100));

        // 最常发送的话语
        report.setFavoriteMessage("妈妈我平安，您放心");

        // 成员统计数据
        report.setMemberStats(generateMemberStats(userId, year));

        // 里程碑记录
        report.setMilestones(generateMilestones(userId, year));

        // 公益贡献统计
        BigDecimal contribution = welfareMapper.selectTotalContributionByUserId(userId);
        report.setPublicWelfareContribution(contribution != null ? contribution : BigDecimal.ZERO);
        
        // 帮助的家庭数（贡献值 * 1000）
        report.setHelpedFamiliesCount(contribution != null ? 
            contribution.multiply(BigDecimal.valueOf(1000)).intValue() : 0);

        annualReportMapper.insert(report);
        log.info("年度报告生成成功 - reportId:{}", report.getReportId());

        return report;
    }

    @Override
    public List<AnnualReport> getAnnualReports(String userId) {
        return annualReportMapper.selectByUserId(userId);
    }

    @Override
    public AnnualReport getAnnualReportDetail(String reportId) {
        AnnualReport report = annualReportMapper.selectByReportId(reportId);
        if (report == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "年度报告不存在");
        }
        return report;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> shareAnnualReport(String reportId) {
        AnnualReport report = annualReportMapper.selectByReportId(reportId);
        if (report == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "年度报告不存在");
        }

        // 生成分享海报（模拟）
        String posterUrl = "https://example.com/poster/" + reportId + ".png";
        String shareCode = IdUtil.randomUUID().substring(0, 6).toUpperCase();

        // 更新报告
        annualReportMapper.updateShareInfo(reportId, posterUrl, shareCode);

        Map<String, Object> result = new HashMap<>();
        result.put("posterUrl", posterUrl);
        result.put("shareCode", shareCode);
        result.put("expiresIn", 7 * 24 * 3600); // 7天有效期

        return result;
    }

    // ========== 公益联动相关实现 ==========

    @Override
    public Map<String, Object> getPublicWelfareStats() {
        WelfarePlatformStats stats = welfareMapper.selectLatestPlatformStats();
        
        if (stats == null) {
            // 如果没有统计数据，返回默认值
            stats = new WelfarePlatformStats();
            stats.setTotalPlatformHugs(0L);
            stats.setTotalWelfareValue(BigDecimal.ZERO);
            stats.setHelpedFamiliesCount(0);
            stats.setCurrentMonthHugs(0);
            stats.setRealtimeContributors(0);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalPlatformHugs", stats.getTotalPlatformHugs());
        result.put("totalWelfareValue", stats.getTotalWelfareValue());
        result.put("helpedFamiliesCount", stats.getHelpedFamiliesCount());
        result.put("currentMonthHugs", stats.getCurrentMonthHugs());
        result.put("realtimeContributors", stats.getRealtimeContributors());

        // 里程碑数据
        List<Map<String, Object>> milestones = new ArrayList<>();
        milestones.add(createMilestone(10000, "首个万次拥抱", stats.getTotalPlatformHugs() >= 10000));
        milestones.add(createMilestone(50000, "五万次温暖传递", stats.getTotalPlatformHugs() >= 50000));
        milestones.add(createMilestone(100000, "十万次爱的回响", stats.getTotalPlatformHugs() >= 100000));
        result.put("milestones", milestones);

        return result;
    }

    @Override
    public Map<String, Object> getMyWelfareContribution(String userId) {
        // 总贡献值
        BigDecimal totalContribution = welfareMapper.selectTotalContributionByUserId(userId);
        if (totalContribution == null) {
            totalContribution = BigDecimal.ZERO;
        }

        // 贡献排名
        Integer rank = welfareMapper.selectUserRank(userId);

        // 总拥抱次数（通过贡献值反推：每次0.001）
        Integer totalHugs = totalContribution.multiply(BigDecimal.valueOf(1000)).intValue();

        // 贡献历史
        List<WelfareContribution> history = welfareMapper.selectContributionHistory(userId, 30);
        List<Map<String, Object>> contributionHistory = history.stream().map(c -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", c.getDate().toString());
            item.put("hugCount", c.getHugCount());
            item.put("contributionValue", c.getContributionValue());
            return item;
        }).collect(Collectors.toList());

        // 成就列表
        List<WelfareAchievement> achievements = welfareMapper.selectAchievementsByUserId(userId);
        List<Map<String, Object>> achievementList = achievements.stream().map(a -> {
            Map<String, Object> item = new HashMap<>();
            item.put("achievementId", a.getAchievementId());
            item.put("name", a.getName());
            item.put("description", a.getDescription());
            item.put("unlockedAt", a.getUnlockedAt().toString());
            item.put("icon", a.getIcon());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("totalContribution", totalContribution);
        result.put("rank", rank);
        result.put("totalHugs", totalHugs);
        result.put("contributionHistory", contributionHistory);
        result.put("achievements", achievementList);

        return result;
    }

    @Override
    public Map<String, Object> getWelfareStories(Integer page, Integer pageSize) {
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        Integer offset = (page - 1) * pageSize;
        List<WelfareStory> stories = welfareMapper.selectPublishedStories(offset, pageSize);
        Integer total = welfareMapper.countPublishedStories();

        // 转换故事数据
        List<Map<String, Object>> storyList = stories.stream().map(s -> {
            Map<String, Object> item = new HashMap<>();
            item.put("storyId", s.getStoryId());
            item.put("title", s.getTitle());
            item.put("content", s.getContent());
            item.put("location", s.getLocation());
            item.put("helpDate", s.getHelpDate().toString());
            
            // 解析图片URL数组
            List<String> imageUrls = new ArrayList<>();
            if (s.getImageUrls() != null) {
                try {
                    JSONArray array = JSONUtil.parseArray(s.getImageUrls());
                    for (Object url : array) {
                        imageUrls.add(url.toString());
                    }
                } catch (Exception e) {
                    log.warn("解析图片URL失败", e);
                }
            }
            item.put("imageUrls", imageUrls);
            
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("stories", storyList);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordHugContribution(String userId) {
        LocalDate today = LocalDate.now();
        
        // 创建或更新贡献记录
        WelfareContribution contribution = new WelfareContribution();
        contribution.setContributionId(userId + "_" + today.toString());
        contribution.setUserId(userId);
        contribution.setDate(today);
        contribution.setHugCount(1);
        contribution.setContributionValue(new BigDecimal("0.001")); // 每次拥抱0.001

        welfareMapper.insertOrUpdateContribution(contribution);

        // 检查并解锁成就
        checkAndUnlockAchievements(userId);

        log.info("记录公益贡献 - 用户:{}, 日期:{}", userId, today);
    }

    // ========== 私有辅助方法 ==========

    /**
     * 模拟语音转文字（实际应调用腾讯云API）
     */
    private String mockVoiceToText(String voiceUrl) {
        return "妈妈也想你了，注意保暖！";
    }

    /**
     * 检测情感标签
     */
    private String detectEmotion(String content) {
        if (content == null) {
            return "warm";
        }
        if (content.contains("想你") || content.contains("爱你")) {
            return "warm";
        } else if (content.contains("注意") || content.contains("保重")) {
            return "caring";
        } else if (content.contains("棒") || content.contains("优秀")) {
            return "proud";
        }
        return "warm";
    }

    /**
     * 计算拥抱统计数据
     */
    private Map<String, Object> calculateHugStats(String userId, Integer year) {
        // 实际应查询数据库，这里简化处理
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalHugs", 36); // 示例数据
        stats.put("warmDays", 365);
        stats.put("mostActiveMonth", "12月");
        return stats;
    }

    /**
     * 计算平安消息数量
     */
    private Integer calculateSafetyMessageCount(String userId, Integer year) {
        // 实际应查询数据库
        return 48;
    }

    /**
     * 生成成员统计数据（JSON格式）
     */
    private String generateMemberStats(String userId, Integer year) {
        JSONArray array = new JSONArray();
        
        JSONObject member = new JSONObject();
        member.put("memberId", "member_001");
        member.put("memberName", "妈妈");
        member.put("hugCount", 36);
        member.put("messageCount", 48);
        member.put("firstInteraction", year + "-01-15");
        member.put("lastInteraction", year + "-12-25");
        
        array.add(member);
        return array.toString();
    }

    /**
     * 生成里程碑记录（JSON格式）
     */
    private String generateMilestones(String userId, Integer year) {
        JSONArray array = new JSONArray();
        
        JSONObject milestone = new JSONObject();
        milestone.put("date", year + "-01-15");
        milestone.put("type", "first_hug");
        milestone.put("description", "第一次发送拥抱");
        
        array.add(milestone);
        return array.toString();
    }

    /**
     * 创建里程碑对象
     */
    private Map<String, Object> createMilestone(Integer value, String description, boolean reached) {
        Map<String, Object> milestone = new HashMap<>();
        milestone.put("value", value);
        milestone.put("description", description);
        if (reached) {
            milestone.put("reachedAt", LocalDateTime.now().toString());
        }
        return milestone;
    }

    /**
     * 检查并解锁成就
     */
    private void checkAndUnlockAchievements(String userId) {
        BigDecimal totalContribution = welfareMapper.selectTotalContributionByUserId(userId);
        if (totalContribution == null) {
            return;
        }

        Integer totalHugs = totalContribution.multiply(BigDecimal.valueOf(1000)).intValue();

        // 检查"爱心传递者"成就（10次拥抱）
        if (totalHugs >= 10) {
            unlockAchievement(userId, "achievement_love_sender", "爱心传递者", 
                            "发送了10次温暖拥抱", "icon_love_sender.png");
        }

        // 检查"温暖守护者"成就（50次拥抱）
        if (totalHugs >= 50) {
            unlockAchievement(userId, "achievement_warm_guardian", "温暖守护者", 
                            "发送了50次温暖拥抱", "icon_warm_guardian.png");
        }

        // 检查"公益使者"成就（100次拥抱）
        if (totalHugs >= 100) {
            unlockAchievement(userId, "achievement_welfare_ambassador", "公益使者", 
                            "发送了100次温暖拥抱，帮助了更多家庭", "icon_welfare_ambassador.png");
        }
    }

    /**
     * 解锁成就
     */
    private void unlockAchievement(String userId, String achievementId, String name, 
                                  String description, String icon) {
        // 检查是否已解锁
        Integer exists = welfareMapper.checkAchievementExists(userId, achievementId);
        if (exists != null && exists > 0) {
            return;
        }

        WelfareAchievement achievement = new WelfareAchievement();
        achievement.setAchievementId(achievementId);
        achievement.setUserId(userId);
        achievement.setName(name);
        achievement.setDescription(description);
        achievement.setIcon(icon);
        achievement.setUnlockedAt(LocalDateTime.now());

        welfareMapper.insertAchievement(achievement);
        log.info("解锁成就 - 用户:{}, 成就:{}", userId, name);
    }
}

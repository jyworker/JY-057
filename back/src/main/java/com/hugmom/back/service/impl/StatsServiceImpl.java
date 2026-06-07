package com.hugmom.back.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.hugmom.back.entity.EmotionStats;
import com.hugmom.back.entity.HugStats;
import com.hugmom.back.entity.UserRetentionStats;
import com.hugmom.back.entity.WelfareRanking;
import com.hugmom.back.mapper.EmotionStatsMapper;
import com.hugmom.back.mapper.HugStatsMapper;
import com.hugmom.back.mapper.UserRetentionStatsMapper;
import com.hugmom.back.mapper.WelfareRankingMapper;
import com.hugmom.back.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private HugStatsMapper hugStatsMapper;

    @Autowired
    private EmotionStatsMapper emotionStatsMapper;

    @Autowired
    private WelfareRankingMapper welfareRankingMapper;

    @Autowired
    private UserRetentionStatsMapper userRetentionStatsMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_PREFIX = "hugmom:stats:";
    private static final long CACHE_EXPIRE_HOURS = 12;

    @Override
    public Map<String, Object> getHugTrend(String period, LocalDate startDate, LocalDate endDate) {
        String cacheKey = CACHE_PREFIX + "hug:" + period + ":" + startDate + ":" + endDate;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (Map<String, Object>) cached;
        }

        List<HugStats> statsList = hugStatsMapper.selectByDateRange(period, "platform", startDate, endDate);

        List<String> xAxis = new ArrayList<>();
        List<Integer> hugCount = new ArrayList<>();
        List<Integer> userCount = new ArrayList<>();
        List<Double> avgDuration = new ArrayList<>();

        for (HugStats stats : statsList) {
            xAxis.add(stats.getStatDate().toString());
            hugCount.add(stats.getHugCount());
            userCount.add(stats.getUserCount());
            avgDuration.add(stats.getAvgDuration() != null ? stats.getAvgDuration() / 1000.0 : 0.0);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("xAxis", xAxis);
        result.put("hugCount", hugCount);
        result.put("userCount", userCount);
        result.put("avgDuration", avgDuration);

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return result;
    }

    @Override
    public Map<String, Object> getEmotionDistribution(String period, LocalDate startDate, LocalDate endDate) {
        String cacheKey = CACHE_PREFIX + "emotion:" + period + ":" + startDate + ":" + endDate;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (Map<String, Object>) cached;
        }

        List<EmotionStats> statsList = emotionStatsMapper.selectByDateRange(period, startDate, endDate);

        Map<String, Integer> emotionTotal = new HashMap<>();
        Set<String> allDates = new TreeSet<>();
        Map<String, Map<String, Integer>> dateEmotionMap = new HashMap<>();

        for (EmotionStats stats : statsList) {
            String date = stats.getStatDate().toString();
            allDates.add(date);
            emotionTotal.merge(stats.getEmotion(), stats.getCount(), Integer::sum);

            dateEmotionMap.computeIfAbsent(date, k -> new HashMap<>())
                    .put(stats.getEmotion(), stats.getCount());
        }

        List<String> emotions = new ArrayList<>(emotionTotal.keySet());
        List<Integer> totalCounts = new ArrayList<>(emotionTotal.values());

        List<Map<String, Object>> heatmapData = new ArrayList<>();
        List<String> xAxis = new ArrayList<>(allDates);

        for (int i = 0; i < xAxis.size(); i++) {
            String date = xAxis.get(i);
            Map<String, Integer> emotionMap = dateEmotionMap.getOrDefault(date, new HashMap<>());
            for (int j = 0; j < emotions.size(); j++) {
                Map<String, Object> point = new HashMap<>();
                point.put("x", i);
                point.put("y", j);
                point.put("value", emotionMap.getOrDefault(emotions.get(j), 0));
                heatmapData.add(point);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("emotions", emotions);
        result.put("totalCounts", totalCounts);
        result.put("xAxis", xAxis);
        result.put("heatmapData", heatmapData);

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return result;
    }

    @Override
    public Map<String, Object> getWelfareRanking(String period, LocalDate date, int limit) {
        String cacheKey = CACHE_PREFIX + "ranking:" + period + ":" + date + ":" + limit;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (Map<String, Object>) cached;
        }

        List<WelfareRanking> rankingList = welfareRankingMapper.selectTopNByPeriod(period, date, limit);

        List<String> userNames = new ArrayList<>();
        List<BigDecimal> contributions = new ArrayList<>();
        List<Integer> hugCounts = new ArrayList<>();
        List<Map<String, Object>> detailList = new ArrayList<>();

        for (WelfareRanking ranking : rankingList) {
            userNames.add(ranking.getNickName() != null ? ranking.getNickName() : "匿名用户");
            contributions.add(ranking.getTotalContribution());
            hugCounts.add(ranking.getHugCount());

            Map<String, Object> detail = new HashMap<>();
            detail.put("ranking", ranking.getRanking());
            detail.put("nickName", ranking.getNickName());
            detail.put("avatarUrl", ranking.getAvatarUrl());
            detail.put("hugCount", ranking.getHugCount());
            detail.put("totalContribution", ranking.getTotalContribution());
            detailList.add(detail);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userNames", userNames);
        result.put("contributions", contributions);
        result.put("hugCounts", hugCounts);
        result.put("detailList", detailList);

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return result;
    }

    @Override
    public Map<String, Object> getUserRetention(LocalDate startDate, LocalDate endDate) {
        String cacheKey = CACHE_PREFIX + "retention:" + startDate + ":" + endDate;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return (Map<String, Object>) cached;
        }

        List<UserRetentionStats> statsList = userRetentionStatsMapper.selectByDateRange(startDate, endDate);

        List<String> xAxis = new ArrayList<>();
        List<Integer> newUsers = new ArrayList<>();
        List<Integer> activeUsers = new ArrayList<>();
        List<Double> day1Rates = new ArrayList<>();
        List<Double> day7Rates = new ArrayList<>();
        List<Double> day30Rates = new ArrayList<>();

        for (UserRetentionStats stats : statsList) {
            xAxis.add(stats.getStatDate().toString());
            newUsers.add(stats.getNewUserCount());
            activeUsers.add(stats.getActiveUserCount());

            int newCount = stats.getNewUserCount() != null ? stats.getNewUserCount() : 0;
            if (newCount > 0) {
                day1Rates.add(stats.getDay1() * 100.0 / newCount);
                day7Rates.add(stats.getDay7() * 100.0 / newCount);
                day30Rates.add(stats.getDay30() * 100.0 / newCount);
            } else {
                day1Rates.add(0.0);
                day7Rates.add(0.0);
                day30Rates.add(0.0);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("xAxis", xAxis);
        result.put("newUsers", newUsers);
        result.put("activeUsers", activeUsers);
        result.put("day1Rates", day1Rates);
        result.put("day7Rates", day7Rates);
        result.put("day30Rates", day30Rates);

        redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return result;
    }

    @Override
    public void aggregateDailyStats(LocalDate date) {
        log.info("开始聚合日统计数据: {}", date);
        try {
            aggregateHugStats("day", date, date);
            aggregateEmotionStats("day", date, date);
            aggregateWelfareRanking("day", date, date);
            updateRetentionStats(date);
            clearCache();
            log.info("日统计数据聚合完成: {}", date);
        } catch (Exception e) {
            log.error("日统计数据聚合失败: {}", date, e);
        }
    }

    @Override
    public void aggregateWeeklyStats(LocalDate date) {
        log.info("开始聚合周统计数据: {}", date);
        try {
            LocalDate weekStart = date.with(WeekFields.ISO.dayOfWeek(), 1);
            LocalDate weekEnd = weekStart.plusDays(6);

            aggregateHugStats("week", weekStart, weekEnd);
            aggregateEmotionStats("week", weekStart, weekEnd);
            aggregateWelfareRanking("week", weekStart, weekEnd);
            clearCache();
            log.info("周统计数据聚合完成: {}", date);
        } catch (Exception e) {
            log.error("周统计数据聚合失败: {}", date, e);
        }
    }

    @Override
    public void aggregateMonthlyStats(LocalDate date) {
        log.info("开始聚合月统计数据: {}", date);
        try {
            YearMonth yearMonth = YearMonth.from(date);
            LocalDate monthStart = yearMonth.atDay(1);
            LocalDate monthEnd = yearMonth.atEndOfMonth();

            aggregateHugStats("month", monthStart, monthEnd);
            aggregateEmotionStats("month", monthStart, monthEnd);
            aggregateWelfareRanking("month", monthStart, monthEnd);
            clearCache();
            log.info("月统计数据聚合完成: {}", date);
        } catch (Exception e) {
            log.error("月统计数据聚合失败: {}", date, e);
        }
    }

    private void aggregateHugStats(String period, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> aggregated;
        switch (period) {
            case "week":
                aggregated = hugStatsMapper.aggregateHugStatsByWeek(startDate, endDate);
                break;
            case "month":
                aggregated = hugStatsMapper.aggregateHugStatsByMonth(startDate, endDate);
                break;
            default:
                aggregated = hugStatsMapper.aggregateHugStatsByDay(startDate, endDate);
        }

        for (Map<String, Object> row : aggregated) {
            LocalDate statDate = ((java.sql.Date) row.get("stat_date")).toLocalDate();
            HugStats existing = hugStatsMapper.selectByPeriodTypeDate(period, "platform", statDate);

            HugStats stats = new HugStats();
            stats.setStatPeriod(period);
            stats.setStatType("platform");
            stats.setStatDate(statDate);
            stats.setHugCount(((Number) row.get("hug_count")).intValue());
            stats.setUserCount(((Number) row.get("user_count")).intValue());
            stats.setMemberCount(((Number) row.get("member_count")).intValue());
            stats.setTotalDuration(((Number) row.get("total_duration")).longValue());
            stats.setAvgDuration(((Number) row.get("avg_duration")).doubleValue());

            if (row.get("year") != null) {
                stats.setYear(((Number) row.get("year")).intValue());
            }
            if (row.get("month") != null) {
                stats.setMonth(((Number) row.get("month")).intValue());
            }
            if (row.get("week") != null) {
                stats.setWeek(((Number) row.get("week")).intValue());
            }

            if (existing != null) {
                stats.setId(existing.getId());
                hugStatsMapper.update(stats);
            } else {
                stats.setStatId(IdUtil.simpleUUID());
                hugStatsMapper.insert(stats);
            }
        }
    }

    private void aggregateEmotionStats(String period, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> aggregated;
        switch (period) {
            case "week":
                aggregated = emotionStatsMapper.aggregateEmotionStatsByWeek(startDate, endDate);
                break;
            case "month":
                aggregated = emotionStatsMapper.aggregateEmotionStatsByMonth(startDate, endDate);
                break;
            default:
                aggregated = emotionStatsMapper.aggregateEmotionStatsByDay(startDate, endDate);
        }

        Map<LocalDate, Integer> totalMap = new HashMap<>();
        for (Map<String, Object> row : aggregated) {
            LocalDate statDate = ((java.sql.Date) row.get("stat_date")).toLocalDate();
            totalMap.merge(statDate, ((Number) row.get("count")).intValue(), Integer::sum);
        }

        emotionStatsMapper.deleteByPeriodDate(period, startDate);

        for (Map<String, Object> row : aggregated) {
            LocalDate statDate = ((java.sql.Date) row.get("stat_date")).toLocalDate();
            String emotion = (String) row.get("emotion");
            int count = ((Number) row.get("count")).intValue();
            int total = totalMap.getOrDefault(statDate, 1);

            EmotionStats stats = new EmotionStats();
            stats.setStatId(IdUtil.simpleUUID());
            stats.setStatPeriod(period);
            stats.setStatDate(statDate);
            stats.setEmotion(emotion);
            stats.setCount(count);
            stats.setRatio(count * 1.0 / total);

            if (row.get("year") != null) {
                stats.setYear(((Number) row.get("year")).intValue());
            }
            if (row.get("month") != null) {
                stats.setMonth(((Number) row.get("month")).intValue());
            }
            if (row.get("week") != null) {
                stats.setWeek(((Number) row.get("week")).intValue());
            }

            emotionStatsMapper.insert(stats);
        }
    }

    private void aggregateWelfareRanking(String period, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> aggregated;
        switch (period) {
            case "week":
                aggregated = welfareRankingMapper.aggregateWelfareRankingByWeek(startDate, endDate);
                break;
            case "month":
                aggregated = welfareRankingMapper.aggregateWelfareRankingByMonth(startDate, endDate);
                break;
            default:
                aggregated = welfareRankingMapper.aggregateWelfareRankingByDay(startDate, endDate);
        }

        welfareRankingMapper.deleteByPeriodDate(period, startDate);

        int ranking = 1;
        for (Map<String, Object> row : aggregated) {
            LocalDate statDate = ((java.sql.Date) row.get("stat_date")).toLocalDate();
            String userId = (String) row.get("user_id");

            WelfareRanking stats = new WelfareRanking();
            stats.setStatId(IdUtil.simpleUUID());
            stats.setStatPeriod(period);
            stats.setStatDate(statDate);
            stats.setUserId(userId);
            stats.setNickName((String) row.get("nick_name"));
            stats.setAvatarUrl((String) row.get("avatar_url"));
            stats.setHugCount(((Number) row.get("hug_count")).intValue());
            stats.setTotalContribution((BigDecimal) row.get("total_contribution"));
            stats.setRanking(ranking++);

            if (row.get("year") != null) {
                stats.setYear(((Number) row.get("year")).intValue());
            }
            if (row.get("month") != null) {
                stats.setMonth(((Number) row.get("month")).intValue());
            }
            if (row.get("week") != null) {
                stats.setWeek(((Number) row.get("week")).intValue());
            }

            welfareRankingMapper.insert(stats);
        }
    }

    @Override
    public void updateRetentionStats(LocalDate date) {
        log.info("开始更新留存统计: {}", date);
        try {
            for (int i = 0; i <= 30; i++) {
                LocalDate cohortDate = date.minusDays(i);
                calculateAndSaveRetention(cohortDate);
            }
        } catch (Exception e) {
            log.error("更新留存统计失败: {}", date, e);
        }
    }

    private void calculateAndSaveRetention(LocalDate cohortDate) {
        UserRetentionStats existing = userRetentionStatsMapper.selectByStatDate(cohortDate);

        List<Map<String, Object>> newUsersList = userRetentionStatsMapper.aggregateNewUsersByDay(cohortDate, cohortDate);
        int newUserCount = newUsersList.isEmpty() ? 0 : ((Number) newUsersList.get(0).get("new_user_count")).intValue();

        List<Map<String, Object>> activeUsersList = userRetentionStatsMapper.aggregateActiveUsersByDay(cohortDate, cohortDate);
        int activeUserCount = activeUsersList.isEmpty() ? 0 : ((Number) activeUsersList.get(0).get("active_user_count")).intValue();

        List<String> cohortUsers = getNewUserIds(cohortDate);
        Set<String> cohortSet = new HashSet<>(cohortUsers);

        int day1 = 0, day3 = 0, day7 = 0, day14 = 0, day30 = 0;

        for (int day : new int[]{1, 3, 7, 14, 30}) {
            LocalDate activeDate = cohortDate.plusDays(day);
            if (!activeDate.isAfter(LocalDate.now())) {
                List<String> activeUsers = userRetentionStatsMapper.selectActiveUserIdsByDateRange(activeDate, activeDate);
                int retained = 0;
                for (String user : activeUsers) {
                    if (cohortSet.contains(user)) {
                        retained++;
                    }
                }
                switch (day) {
                    case 1: day1 = retained; break;
                    case 3: day3 = retained; break;
                    case 7: day7 = retained; break;
                    case 14: day14 = retained; break;
                    case 30: day30 = retained; break;
                }
            }
        }

        UserRetentionStats stats = new UserRetentionStats();
        stats.setStatDate(cohortDate);
        stats.setNewUserCount(newUserCount);
        stats.setActiveUserCount(activeUserCount);
        stats.setDay1(day1);
        stats.setDay3(day3);
        stats.setDay7(day7);
        stats.setDay14(day14);
        stats.setDay30(day30);

        if (existing != null) {
            stats.setId(existing.getId());
            userRetentionStatsMapper.update(stats);
        } else {
            stats.setStatId(IdUtil.simpleUUID());
            userRetentionStatsMapper.insert(stats);
        }
    }

    private List<String> getNewUserIds(LocalDate date) {
        return new ArrayList<>();
    }

    private void clearCache() {
        Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public byte[] exportCsv(String type, LocalDate startDate, LocalDate endDate) {
        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');

        switch (type) {
            case "hug":
                csv.append("日期,拥抱次数,参与用户数,参与成员数,平均时长(秒)\n");
                List<HugStats> hugStats = hugStatsMapper.selectByDateRange("day", "platform", startDate, endDate);
                for (HugStats stats : hugStats) {
                    csv.append(stats.getStatDate()).append(",")
                       .append(stats.getHugCount()).append(",")
                       .append(stats.getUserCount()).append(",")
                       .append(stats.getMemberCount()).append(",")
                       .append(String.format("%.2f", stats.getAvgDuration() != null ? stats.getAvgDuration() / 1000.0 : 0.0)).append("\n");
                }
                break;

            case "emotion":
                csv.append("日期,情感标签,次数,占比\n");
                List<EmotionStats> emotionStats = emotionStatsMapper.selectByDateRange("day", startDate, endDate);
                for (EmotionStats stats : emotionStats) {
                    csv.append(stats.getStatDate()).append(",")
                       .append(stats.getEmotion()).append(",")
                       .append(stats.getCount()).append(",")
                       .append(String.format("%.2f%%", stats.getRatio() * 100)).append("\n");
                }
                break;

            case "ranking":
                csv.append("排名,用户昵称,拥抱次数,贡献值\n");
                List<WelfareRanking> rankings = welfareRankingMapper.selectTopNByPeriod("month", endDate, 100);
                for (WelfareRanking ranking : rankings) {
                    csv.append(ranking.getRanking()).append(",")
                       .append(ranking.getNickName() != null ? ranking.getNickName() : "匿名").append(",")
                       .append(ranking.getHugCount()).append(",")
                       .append(ranking.getTotalContribution()).append("\n");
                }
                break;

            case "retention":
                csv.append("日期,新增用户数,活跃用户数,次日留存,7日留存,30日留存\n");
                List<UserRetentionStats> retentionStats = userRetentionStatsMapper.selectByDateRange(startDate, endDate);
                for (UserRetentionStats stats : retentionStats) {
                    int newCount = stats.getNewUserCount() != null ? stats.getNewUserCount() : 0;
                    csv.append(stats.getStatDate()).append(",")
                       .append(stats.getNewUserCount()).append(",")
                       .append(stats.getActiveUserCount()).append(",")
                       .append(newCount > 0 ? String.format("%.2f%%", stats.getDay1() * 100.0 / newCount) : "0%").append(",")
                       .append(newCount > 0 ? String.format("%.2f%%", stats.getDay7() * 100.0 / newCount) : "0%").append(",")
                       .append(newCount > 0 ? String.format("%.2f%%", stats.getDay30() * 100.0 / newCount) : "0%").append("\n");
                }
                break;

            default:
                throw new IllegalArgumentException("不支持的导出类型: " + type);
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}

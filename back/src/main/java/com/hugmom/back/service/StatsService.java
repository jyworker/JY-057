package com.hugmom.back.service;

import java.time.LocalDate;
import java.util.Map;

public interface StatsService {

    Map<String, Object> getHugTrend(String period, LocalDate startDate, LocalDate endDate);

    Map<String, Object> getEmotionDistribution(String period, LocalDate startDate, LocalDate endDate);

    Map<String, Object> getWelfareRanking(String period, LocalDate date, int limit);

    Map<String, Object> getUserRetention(LocalDate startDate, LocalDate endDate);

    void aggregateDailyStats(LocalDate date);

    void aggregateWeeklyStats(LocalDate date);

    void aggregateMonthlyStats(LocalDate date);

    void updateRetentionStats(LocalDate date);

    byte[] exportCsv(String type, LocalDate startDate, LocalDate endDate);
}

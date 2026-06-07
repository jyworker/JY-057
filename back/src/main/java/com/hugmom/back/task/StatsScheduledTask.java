package com.hugmom.back.task;

import com.hugmom.back.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class StatsScheduledTask {

    @Autowired
    private StatsService statsService;

    @Scheduled(cron = "0 10 0 * * ?")
    public void dailyStatsTask() {
        log.info("开始执行每日统计任务...");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            statsService.aggregateDailyStats(yesterday);
            log.info("每日统计任务执行完成: {}", yesterday);
        } catch (Exception e) {
            log.error("每日统计任务执行失败", e);
        }
    }

    @Scheduled(cron = "0 30 0 ? * MON")
    public void weeklyStatsTask() {
        log.info("开始执行每周统计任务...");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            statsService.aggregateWeeklyStats(yesterday);
            log.info("每周统计任务执行完成");
        } catch (Exception e) {
            log.error("每周统计任务执行失败", e);
        }
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void monthlyStatsTask() {
        log.info("开始执行每月统计任务...");
        try {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            statsService.aggregateMonthlyStats(lastMonth);
            log.info("每月统计任务执行完成");
        } catch (Exception e) {
            log.error("每月统计任务执行失败", e);
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void retentionStatsTask() {
        log.info("开始执行留存统计任务...");
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            statsService.updateRetentionStats(yesterday);
            log.info("留存统计任务执行完成");
        } catch (Exception e) {
            log.error("留存统计任务执行失败", e);
        }
    }
}

package com.hugmom.back.controller;

import com.hugmom.back.common.Result;
import com.hugmom.back.service.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/hug/trend")
    public Result<Map<String, Object>> getHugTrend(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Map<String, Object> data = statsService.getHugTrend(period, startDate, endDate);
        return Result.success("获取拥抱趋势成功", data);
    }

    @GetMapping("/emotion/distribution")
    public Result<Map<String, Object>> getEmotionDistribution(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Map<String, Object> data = statsService.getEmotionDistribution(period, startDate, endDate);
        return Result.success("获取情感分布成功", data);
    }

    @GetMapping("/welfare/ranking")
    public Result<Map<String, Object>> getWelfareRanking(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(defaultValue = "20") int limit) {

        Map<String, Object> data = statsService.getWelfareRanking(period, date, limit);
        return Result.success("获取公益排行榜成功", data);
    }

    @GetMapping("/user/retention")
    public Result<Map<String, Object>> getUserRetention(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Map<String, Object> data = statsService.getUserRetention(startDate, endDate);
        return Result.success("获取用户留存成功", data);
    }

    @GetMapping("/export/{type}")
    public ResponseEntity<byte[]> exportCsv(
            @PathVariable String type,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        byte[] csvData = statsService.exportCsv(type, startDate, endDate);

        String fileName = type + "_stats_" + startDate + "_" + endDate + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv;charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    @PostMapping("/aggregate/daily")
    public Result<String> aggregateDaily(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        statsService.aggregateDailyStats(date);
        return Result.success("日统计聚合成功");
    }

    @PostMapping("/aggregate/weekly")
    public Result<String> aggregateWeekly(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        statsService.aggregateWeeklyStats(date);
        return Result.success("周统计聚合成功");
    }

    @PostMapping("/aggregate/monthly")
    public Result<String> aggregateMonthly(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        statsService.aggregateMonthlyStats(date);
        return Result.success("月统计聚合成功");
    }
}

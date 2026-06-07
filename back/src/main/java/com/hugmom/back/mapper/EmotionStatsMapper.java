package com.hugmom.back.mapper;

import com.hugmom.back.entity.EmotionStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface EmotionStatsMapper {

    int insert(EmotionStats stats);

    int update(EmotionStats stats);

    EmotionStats selectByPeriodDateEmotion(@Param("statPeriod") String statPeriod,
                                            @Param("statDate") LocalDate statDate,
                                            @Param("emotion") String emotion);

    List<EmotionStats> selectByDateRange(@Param("statPeriod") String statPeriod,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateEmotionStatsByDay(@Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateEmotionStatsByWeek(@Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateEmotionStatsByMonth(@Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    int deleteByPeriodDate(@Param("statPeriod") String statPeriod,
                            @Param("statDate") LocalDate statDate);
}

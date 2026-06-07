package com.hugmom.back.mapper;

import com.hugmom.back.entity.HugStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface HugStatsMapper {

    int insert(HugStats stats);

    int update(HugStats stats);

    HugStats selectByPeriodTypeDate(@Param("statPeriod") String statPeriod,
                                     @Param("statType") String statType,
                                     @Param("statDate") LocalDate statDate);

    List<HugStats> selectByDateRange(@Param("statPeriod") String statPeriod,
                                      @Param("statType") String statType,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateHugStatsByDay(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateHugStatsByWeek(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateHugStatsByMonth(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    int deleteByPeriodTypeDate(@Param("statPeriod") String statPeriod,
                                @Param("statType") String statType,
                                @Param("statDate") LocalDate statDate);
}

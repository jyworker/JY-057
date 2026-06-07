package com.hugmom.back.mapper;

import com.hugmom.back.entity.WelfareRanking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface WelfareRankingMapper {

    int insert(WelfareRanking ranking);

    int update(WelfareRanking ranking);

    WelfareRanking selectByPeriodDateUser(@Param("statPeriod") String statPeriod,
                                           @Param("statDate") LocalDate statDate,
                                           @Param("userId") String userId);

    List<WelfareRanking> selectTopNByPeriod(@Param("statPeriod") String statPeriod,
                                             @Param("statDate") LocalDate statDate,
                                             @Param("limit") int limit);

    List<Map<String, Object>> aggregateWelfareRankingByDay(@Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateWelfareRankingByWeek(@Param("startDate") LocalDate startDate,
                                                             @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateWelfareRankingByMonth(@Param("startDate") LocalDate startDate,
                                                              @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateWelfareRankingAll();

    int deleteByPeriodDate(@Param("statPeriod") String statPeriod,
                            @Param("statDate") LocalDate statDate);
}

package com.hugmom.back.mapper;

import com.hugmom.back.entity.UserRetentionStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserRetentionStatsMapper {

    int insert(UserRetentionStats stats);

    int update(UserRetentionStats stats);

    UserRetentionStats selectByStatDate(@Param("statDate") LocalDate statDate);

    List<UserRetentionStats> selectByDateRange(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateNewUsersByDay(@Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    List<Map<String, Object>> aggregateActiveUsersByDay(@Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);

    List<String> selectActiveUserIdsByDateRange(@Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}

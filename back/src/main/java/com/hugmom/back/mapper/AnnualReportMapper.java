package com.hugmom.back.mapper;

import com.hugmom.back.entity.AnnualReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 年度报告Mapper接口 - 爱的回响功能
 * 
 * @author HugMom Team
 */
@Mapper
public interface AnnualReportMapper {

    /**
     * 插入年度报告
     */
    int insert(AnnualReport report);

    /**
     * 根据用户ID和年份查询报告
     */
    AnnualReport selectByUserIdAndYear(@Param("userId") String userId, @Param("year") Integer year);

    /**
     * 根据报告ID查询
     */
    AnnualReport selectByReportId(@Param("reportId") String reportId);

    /**
     * 根据用户ID查询所有报告（按年份降序）
     */
    List<AnnualReport> selectByUserId(@Param("userId") String userId);

    /**
     * 更新分享信息
     */
    int updateShareInfo(@Param("reportId") String reportId, 
                       @Param("posterUrl") String posterUrl, 
                       @Param("shareCode") String shareCode);
}

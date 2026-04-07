package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.GreenhouseAlertHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GreenhouseAlertHistoryMapper extends BaseMapper<GreenhouseAlertHistory> {
    
    /**
     * 检查用户是否是告警记录的所有者（通过温室关联）
     */
    @Select("SELECT COUNT(*) FROM greenhouse_alert_history h " +
            "INNER JOIN greenhouse g ON h.greenhouse_id = g.id " +
            "WHERE h.id = #{alertId} AND g.user_id = #{userId}")
    int checkUserOwnership(@Param("alertId") Integer alertId, @Param("userId") Integer userId);
}

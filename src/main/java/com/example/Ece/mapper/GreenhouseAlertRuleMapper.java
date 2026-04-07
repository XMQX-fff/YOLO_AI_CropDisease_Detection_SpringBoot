package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.GreenhouseAlertRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GreenhouseAlertRuleMapper extends BaseMapper<GreenhouseAlertRule> {
    
    /**
     * 检查用户是否是告警规则的所有者
     */
    @Select("SELECT COUNT(*) FROM greenhouse_alert_rule WHERE id = #{ruleId} AND user_id = #{userId}")
    int checkUserOwnership(@Param("ruleId") Integer ruleId, @Param("userId") Integer userId);
}

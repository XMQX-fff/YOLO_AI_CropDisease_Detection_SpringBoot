package com.example.Ece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("greenhouse_alert_rule")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GreenhouseAlertRule {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer greenhouseId;
    private String ruleName;
    private String ruleCode;
    private String dataType;
    private String alertLevel;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private BigDecimal targetValue;
    private BigDecimal tolerance;
    private Integer enableMin;
    private Integer enableMax;
    private Integer alertDuration;
    private Integer alertInterval;
    private String alertMessage;
    private String notifyType;
    private Integer autoHandle;
    private String autoHandleAction;
    private Integer isEnabled;
    private String effectiveTime;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}

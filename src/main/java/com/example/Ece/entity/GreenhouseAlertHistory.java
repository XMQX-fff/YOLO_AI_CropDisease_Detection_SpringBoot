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

@TableName("greenhouse_alert_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GreenhouseAlertHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer greenhouseId;
    private Integer ruleId;
    private Integer deviceId;

    // 告警基本信息
    private String alertCode;
    private String alertTitle;
    private String alertContent;
    private String alertLevel;
    private String alertType;
    private String dataType;

    // 触发时的数值
    private BigDecimal currentValue;
    private BigDecimal thresholdValue;
    private String thresholdType;
    private BigDecimal standardValue;
    private BigDecimal deviationRate;

    // 时间记录
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime triggerTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime confirmTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime handleTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime recoverTime;
    private Integer duration;

    // 处理信息
    private String status;
    private String confirmBy;
    private String handleBy;
    private String handleMethod;
    private String handleAction;
    private String handleResult;
    private String resolveBy;
    private String resolveMethod;
    private String resolveRemark;

    // 通知记录
    private String notifyChannels;
    private String notifyStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime notifyTime;
    private Integer notifyCount;

    // 关联信息
    private String relatedAlerts;
    private String snapshotData;
    private String weatherInfo;

    // 扩展字段
    private Integer isFalseAlarm;
    private Integer isArchived;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime archiveTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}

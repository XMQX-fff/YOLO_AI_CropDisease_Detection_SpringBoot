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

@TableName("greenhouse_device")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GreenhouseDevice {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer greenhouseId;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String controlMode;
    private Integer currentStatus;
    private BigDecimal currentValue;
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private String unit;
    private Integer isAuto;
    private String autoRules;
    private Integer sortOrder;
    private Integer isEnabled;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastOperateTime;
    private String lastOperateUser;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}

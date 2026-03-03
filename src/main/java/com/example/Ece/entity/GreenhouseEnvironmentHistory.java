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

@TableName("greenhouse_environment_history")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GreenhouseEnvironmentHistory {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer greenhouseId;
    private BigDecimal temperature;
    private Integer airHumidity;
    private Integer soilHumidity;
    private Integer co2Concentration;
    private BigDecimal soilPh;
    private Integer lightIntensity;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime recordTime;
    private String dataSource;
}

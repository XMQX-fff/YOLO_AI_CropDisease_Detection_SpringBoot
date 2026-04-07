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

@TableName("greenhouse")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Greenhouse {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private String greenhouseName;
    private String cropType;
    private Integer quantity;
    private String growthStatus;
    private Double temperature;
    private Integer airHumidity;
    private Integer soilHumidity;
    private Integer co2Concentration;
    private Double soilPh;
    private Integer lightIntensity;
    private String manager;
    private String recordTime;
    
    // 新增地理位置字段
    private String province;
    private String city;
    private String district;
    private String town;
    private String village;
    private String detailedAddress;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal altitude;
    private String coordinateSystem;
    private String regionId;
    private String regionName;
    private String climateZone;
    private String terrainType;
    private String soilType;
    
    // 新增在线状态字段
    private Integer onlineStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}

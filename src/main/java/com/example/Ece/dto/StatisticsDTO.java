package com.example.Ece.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsDTO {
    private Integer userCount;
    private Integer greenhouseCount;
    private Integer diseaseCount;
    private Integer totalProduction;
    private Double detectionRate;
    private Double preventionRate;

    public StatisticsDTO() {
    }

    public StatisticsDTO(Integer userCount, Integer greenhouseCount, Integer diseaseCount,
                         Integer totalProduction, Double detectionRate, Double preventionRate) {
        this.userCount = userCount;
        this.greenhouseCount = greenhouseCount;
        this.diseaseCount = diseaseCount;
        this.totalProduction = totalProduction;
        this.detectionRate = detectionRate;
        this.preventionRate = preventionRate;
    }
}

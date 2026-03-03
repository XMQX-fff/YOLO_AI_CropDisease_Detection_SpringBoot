package com.example.Ece.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class GreenhouseAbnormalStatsDTO {
    private Integer greenhouseId;
    private String greenhouseName;
    private LocalDate date;
    private Map<String, Integer> abnormalTypeCount;
    private Integer totalAbnormalCount;

    public GreenhouseAbnormalStatsDTO() {
    }

    public GreenhouseAbnormalStatsDTO(Integer greenhouseId, String greenhouseName, LocalDate date,
                                      Map<String, Integer> abnormalTypeCount) {
        this.greenhouseId = greenhouseId;
        this.greenhouseName = greenhouseName;
        this.date = date;
        this.abnormalTypeCount = abnormalTypeCount;
        this.totalAbnormalCount = abnormalTypeCount.values().stream().mapToInt(Integer::intValue).sum();
    }
}

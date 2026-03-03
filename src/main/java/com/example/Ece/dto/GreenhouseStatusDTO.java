package com.example.Ece.dto;

import com.example.Ece.entity.Greenhouse;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.entity.GreenhouseEnvironmentHistory;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GreenhouseStatusDTO {
    private Greenhouse greenhouse;
    private List<GreenhouseDetection> diseaseData;
    private GreenhouseEnvironmentHistory latestEnvironmentData;
    private Map<String, Integer> abnormalData;
    private Integer totalAbnormalCount;
    private boolean hasDisease;

    public GreenhouseStatusDTO() {
    }

    public GreenhouseStatusDTO(Greenhouse greenhouse, List<GreenhouseDetection> diseaseData,
                              GreenhouseEnvironmentHistory latestEnvironmentData, Map<String, Integer> abnormalData) {
        this.greenhouse = greenhouse;
        this.diseaseData = diseaseData;
        this.latestEnvironmentData = latestEnvironmentData;
        this.abnormalData = abnormalData;
        this.totalAbnormalCount = abnormalData.values().stream().mapToInt(Integer::intValue).sum();
        this.hasDisease = !diseaseData.isEmpty();
    }
}

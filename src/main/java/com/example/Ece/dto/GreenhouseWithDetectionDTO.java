package com.example.Ece.dto;

import com.example.Ece.entity.Greenhouse;
import com.example.Ece.entity.GreenhouseDetection;
import lombok.Data;

import java.util.List;

@Data
public class GreenhouseWithDetectionDTO {
    private Greenhouse greenhouse;
    private List<GreenhouseDetection> detections;
    private Integer totalDiseaseCount;
    
    public GreenhouseWithDetectionDTO() {
    }
    
    public GreenhouseWithDetectionDTO(Greenhouse greenhouse, List<GreenhouseDetection> detections) {
        this.greenhouse = greenhouse;
        this.detections = detections;
        this.totalDiseaseCount = calculateTotalDiseaseCount(detections);
    }
    
    private Integer calculateTotalDiseaseCount(List<GreenhouseDetection> detections) {
        if (detections == null || detections.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (GreenhouseDetection detection : detections) {
            if (detection.getDiseaseCount() != null) {
                total += detection.getDiseaseCount();
            }
        }
        return total;
    }
}

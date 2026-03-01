package com.example.Ece.dto;

import com.example.Ece.entity.Greenhouse;
import lombok.Data;

@Data
public class GreenhouseWithDiseaseCountDTO {
    private Greenhouse greenhouse;
    private Integer totalDiseaseCount;
    
    public GreenhouseWithDiseaseCountDTO() {
    }
    
    public GreenhouseWithDiseaseCountDTO(Greenhouse greenhouse, Integer totalDiseaseCount) {
        this.greenhouse = greenhouse;
        this.totalDiseaseCount = totalDiseaseCount;
    }
}

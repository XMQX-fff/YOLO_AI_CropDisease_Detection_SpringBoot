package com.example.Ece.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PestDetectionDTO {
    private Integer id;
    private String sourceType;
    private String weight;
    private String inputPath;
    private String outputPath;
    private String conf;
    private String username;
    private String kind;
    private String startTime;
    private String confidence;
    private String allTime;
    private String label;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}

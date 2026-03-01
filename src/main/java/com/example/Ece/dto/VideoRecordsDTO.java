package com.example.Ece.dto;

import lombok.Data;

@Data
public class VideoRecordsDTO {
    private Integer id;
    private String weight;
    private String inputVideo;
    private String outVideo;
    private String conf;
    private String username;
    private String kind;
    private String startTime;
    private Integer greenhouseId;
}

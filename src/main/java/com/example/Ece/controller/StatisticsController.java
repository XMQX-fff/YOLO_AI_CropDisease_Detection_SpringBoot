package com.example.Ece.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.Ece.common.Result;
import com.example.Ece.dto.StatisticsDTO;
import com.example.Ece.entity.Greenhouse;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.entity.User;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import com.example.Ece.mapper.GreenhouseMapper;
import com.example.Ece.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Resource
    private UserMapper userMapper;

    @Resource
    private GreenhouseMapper greenhouseMapper;

    @Resource
    private GreenhouseDetectionMapper greenhouseDetectionMapper;

    @GetMapping("/overview")
    public Result<?> getOverview() {
        Integer userCount = Math.toIntExact(userMapper.selectCount(null));
        List<Greenhouse> greenhouses = greenhouseMapper.selectList(null);
        Integer greenhouseCount = greenhouses.size();
        
        List<GreenhouseDetection> detections = greenhouseDetectionMapper.selectList(null);
        Integer diseaseCount = detections.stream()
            .mapToInt(d -> d.getDiseaseCount() != null ? d.getDiseaseCount() : 0)
            .sum();
        
        Integer totalProduction = greenhouses.stream()
            .mapToInt(g -> g.getQuantity() != null ? g.getQuantity() : 0)
            .sum();
        
        Set<Integer> detectedGreenhouseIds = detections.stream()
            .map(GreenhouseDetection::getGreenhouseId)
            .collect(Collectors.toSet());
        
        Double detectionRate = 0.0;
        if (greenhouseCount > 0) {
            long detectedCount = greenhouses.stream()
                .filter(g -> detectedGreenhouseIds.contains(g.getId()))
                .count();
            detectionRate = (double) detectedCount / greenhouseCount * 100;
        }
        
        Double preventionRate = 93.2;
        
        StatisticsDTO statistics = new StatisticsDTO(
            userCount,
            greenhouseCount,
            diseaseCount,
            totalProduction,
            detectionRate,
            preventionRate
        );
        
        return Result.success(statistics);
    }
}

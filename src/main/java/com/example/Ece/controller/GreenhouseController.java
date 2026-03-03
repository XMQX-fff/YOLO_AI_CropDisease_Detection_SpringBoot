package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.GreenhouseStatusDTO;
import com.example.Ece.dto.GreenhouseWithDetectionDTO;
import com.example.Ece.dto.GreenhouseWithDiseaseCountDTO;
import com.example.Ece.entity.Greenhouse;
import com.example.Ece.entity.GreenhouseAlertRule;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.entity.GreenhouseEnvironmentHistory;
import com.example.Ece.mapper.GreenhouseAlertRuleMapper;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import com.example.Ece.mapper.GreenhouseEnvironmentHistoryMapper;
import com.example.Ece.mapper.GreenhouseMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/greenhouse")
public class GreenhouseController {

    @Resource
    private GreenhouseMapper greenhouseMapper;
    @Resource
    private GreenhouseDetectionMapper greenhouseDetectionMapper;
    @Resource
    private GreenhouseEnvironmentHistoryMapper greenhouseEnvironmentHistoryMapper;
    @Resource
    private GreenhouseAlertRuleMapper greenhouseAlertRuleMapper;

    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(greenhouseMapper.selectList(null));
    }

    @GetMapping("/withDiseaseCount")
    public Result<?> getWithDiseaseCountPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestParam(defaultValue = "") String search,
                                            @RequestParam(defaultValue = "") String cropType,
                                            @RequestParam(defaultValue = "") String growthStatus,
                                            @RequestParam(defaultValue = "") String manager,
                                            @RequestParam(defaultValue = "") String recordTime) {
        LambdaQueryWrapper<Greenhouse> wrapper = Wrappers.<Greenhouse>lambdaQuery();
        wrapper.orderByDesc(Greenhouse::getCreateTime);
        
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Greenhouse::getGreenhouseName, search);
        }
        if (StrUtil.isNotBlank(cropType)) {
            wrapper.like(Greenhouse::getCropType, cropType);
        }
        if (StrUtil.isNotBlank(growthStatus)) {
            wrapper.like(Greenhouse::getGrowthStatus, growthStatus);
        }
        if (StrUtil.isNotBlank(manager)) {
            wrapper.like(Greenhouse::getManager, manager);
        }
        if (StrUtil.isNotBlank(recordTime)) {
            wrapper.like(Greenhouse::getRecordTime, recordTime);
        }
        
        Page<Greenhouse> greenhousePage = greenhouseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        
        List<GreenhouseDetection> allDetections = greenhouseDetectionMapper.selectList(null);
        
        Map<Integer, Integer> diseaseCountMap = allDetections.stream()
            .collect(Collectors.groupingBy(
                GreenhouseDetection::getGreenhouseId,
                Collectors.summingInt(d -> d.getDiseaseCount() != null ? d.getDiseaseCount() : 0)
            ));
        
        List<GreenhouseWithDiseaseCountDTO> result = new ArrayList<>();
        for (Greenhouse greenhouse : greenhousePage.getRecords()) {
            Integer count = diseaseCountMap.getOrDefault(greenhouse.getId(), 0);
            result.add(new GreenhouseWithDiseaseCountDTO(greenhouse, count));
        }
        
        Page<GreenhouseWithDiseaseCountDTO> resultPage = new Page<>(pageNum, pageSize, greenhousePage.getTotal());
        resultPage.setRecords(result);
        
        return Result.success(resultPage);
    }

    @GetMapping("/status/all")
    public Result<?> getAllGreenhouseStatus() {
        List<Greenhouse> greenhouses = greenhouseMapper.selectList(null);
        List<GreenhouseStatusDTO> result = new ArrayList<>();
        
        // 获取所有告警规则
        LambdaQueryWrapper<GreenhouseAlertRule> ruleWrapper = Wrappers.<GreenhouseAlertRule>lambdaQuery();
        ruleWrapper.eq(GreenhouseAlertRule::getIsEnabled, 1);
        List<GreenhouseAlertRule> allAlertRules = greenhouseAlertRuleMapper.selectList(ruleWrapper);
        
        for (Greenhouse greenhouse : greenhouses) {
            // 获取病虫害数据
            LambdaQueryWrapper<GreenhouseDetection> diseaseWrapper = Wrappers.<GreenhouseDetection>lambdaQuery();
            diseaseWrapper.eq(GreenhouseDetection::getGreenhouseId, greenhouse.getId());
            diseaseWrapper.orderByDesc(GreenhouseDetection::getRecordTime);
            List<GreenhouseDetection> diseaseData = greenhouseDetectionMapper.selectList(diseaseWrapper);
            
            // 获取最新环境数据
            LambdaQueryWrapper<GreenhouseEnvironmentHistory> envWrapper = Wrappers.<GreenhouseEnvironmentHistory>lambdaQuery();
            envWrapper.eq(GreenhouseEnvironmentHistory::getGreenhouseId, greenhouse.getId());
            envWrapper.orderByDesc(GreenhouseEnvironmentHistory::getRecordTime);
            envWrapper.last("LIMIT 1");
            GreenhouseEnvironmentHistory latestEnvironmentData = greenhouseEnvironmentHistoryMapper.selectOne(envWrapper);
            
            // 获取该温室的告警规则（包含通用规则）
            List<GreenhouseAlertRule> alertRules = allAlertRules.stream()
                .filter(r -> r.getGreenhouseId() == null || r.getGreenhouseId().equals(greenhouse.getId()))
                .collect(Collectors.toList());
            
            // 检查异常数据
            Map<String, Integer> abnormalData = new HashMap<>();
            
            // 检查病虫害
            if (!diseaseData.isEmpty()) {
                abnormalData.put("病虫害", diseaseData.size());
            }
            
            // 检查环境数据异常
            if (latestEnvironmentData != null) {
                for (GreenhouseAlertRule rule : alertRules) {
                    String dataType = rule.getDataType();
                    double value = 0;
                    boolean isAbnormal = false;
                    
                    switch (dataType) {
                        case "temperature":
                            if (latestEnvironmentData.getTemperature() != null) {
                                value = latestEnvironmentData.getTemperature().doubleValue();
                            }
                            break;
                        case "air_humidity":
                            if (latestEnvironmentData.getAirHumidity() != null) {
                                value = latestEnvironmentData.getAirHumidity().doubleValue();
                            }
                            break;
                        case "soil_humidity":
                            if (latestEnvironmentData.getSoilHumidity() != null) {
                                value = latestEnvironmentData.getSoilHumidity().doubleValue();
                            }
                            break;
                        case "co2_concentration":
                            if (latestEnvironmentData.getCo2Concentration() != null) {
                                value = latestEnvironmentData.getCo2Concentration().doubleValue();
                            }
                            break;
                        case "soil_ph":
                            if (latestEnvironmentData.getSoilPh() != null) {
                                value = latestEnvironmentData.getSoilPh().doubleValue();
                            }
                            break;
                        case "light_intensity":
                            if (latestEnvironmentData.getLightIntensity() != null) {
                                value = latestEnvironmentData.getLightIntensity().doubleValue();
                            }
                            break;
                    }
                    
                    // 检查阈值
                    if (rule.getEnableMin() == 1 && rule.getMinValue() != null) {
                        if (value < rule.getMinValue().doubleValue()) {
                            isAbnormal = true;
                        }
                    }
                    if (rule.getEnableMax() == 1 && rule.getMaxValue() != null) {
                        if (value > rule.getMaxValue().doubleValue()) {
                            isAbnormal = true;
                        }
                    }
                    
                    if (isAbnormal) {
                        abnormalData.put(dataType, abnormalData.getOrDefault(dataType, 0) + 1);
                    }
                }
            }
            
            GreenhouseStatusDTO statusDTO = new GreenhouseStatusDTO(
                greenhouse, diseaseData, latestEnvironmentData, abnormalData
            );
            
            result.add(statusDTO);
        }
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(greenhouseMapper.selectById(id));
    }

    @GetMapping("/{id}/withDetections")
    public Result<?> getWithDetections(@PathVariable Integer id) {
        Greenhouse greenhouse = greenhouseMapper.selectById(id);
        if (greenhouse == null) {
            return Result.error("-1", "温室不存在");
        }
        
        LambdaQueryWrapper<GreenhouseDetection> wrapper = Wrappers.<GreenhouseDetection>lambdaQuery();
        wrapper.eq(GreenhouseDetection::getGreenhouseId, id);
        wrapper.orderByDesc(GreenhouseDetection::getRecordTime);
        List<GreenhouseDetection> detections = greenhouseDetectionMapper.selectList(wrapper);
        
        GreenhouseWithDetectionDTO dto = new GreenhouseWithDetectionDTO(greenhouse, detections);
        return Result.success(dto);
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "") String search,
                             @RequestParam(defaultValue = "") String cropType,
                             @RequestParam(defaultValue = "") String growthStatus,
                             @RequestParam(defaultValue = "") String manager,
                             @RequestParam(defaultValue = "") String recordTime) {
        LambdaQueryWrapper<Greenhouse> wrapper = Wrappers.<Greenhouse>lambdaQuery();
        wrapper.orderByDesc(Greenhouse::getCreateTime);
        
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Greenhouse::getGreenhouseName, search);
        }
        if (StrUtil.isNotBlank(cropType)) {
            wrapper.like(Greenhouse::getCropType, cropType);
        }
        if (StrUtil.isNotBlank(growthStatus)) {
            wrapper.like(Greenhouse::getGrowthStatus, growthStatus);
        }
        if (StrUtil.isNotBlank(manager)) {
            wrapper.like(Greenhouse::getManager, manager);
        }
        if (StrUtil.isNotBlank(recordTime)) {
            wrapper.like(Greenhouse::getRecordTime, recordTime);
        }
        
        Page<Greenhouse> greenhousePage = greenhouseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(greenhousePage);
    }

    @PostMapping
    public Result<?> save(@RequestBody Greenhouse greenhouse) {
        greenhouse.setCreateTime(LocalDateTime.now());
        greenhouse.setUpdateTime(LocalDateTime.now());
        greenhouseMapper.insert(greenhouse);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody Greenhouse greenhouse) {
        greenhouse.setUpdateTime(LocalDateTime.now());
        greenhouseMapper.updateById(greenhouse);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        greenhouseMapper.deleteById(id);
        return Result.success();
    }
} 

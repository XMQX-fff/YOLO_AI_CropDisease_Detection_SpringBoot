package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.GreenhouseWithDetectionDTO;
import com.example.Ece.dto.GreenhouseWithDiseaseCountDTO;
import com.example.Ece.entity.Greenhouse;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import com.example.Ece.mapper.GreenhouseMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 温室管理控制器
 */
@RestController
@RequestMapping("/greenhouse")
public class GreenhouseController {

    @Resource
    private GreenhouseMapper greenhouseMapper;
    @Resource
    private GreenhouseDetectionMapper greenhouseDetectionMapper;

    /**
     * 获取所有温室信息
     */
    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(greenhouseMapper.selectList(null));
    }

    /**
     * 分页查询温室信息，附带每个温室的病虫害数量
     */
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

    /**
     * 根据ID获取温室信息
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(greenhouseMapper.selectById(id));
    }

    /**
     * 根据ID获取温室信息和对应的病虫害检测信息
     */
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

    /**
     * 分页查询温室信息
     */
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

    /**
     * 新增温室信息
     */
    @PostMapping
    public Result<?> save(@RequestBody Greenhouse greenhouse) {
        greenhouse.setCreateTime(LocalDateTime.now());
        greenhouse.setUpdateTime(LocalDateTime.now());
        greenhouseMapper.insert(greenhouse);
        return Result.success();
    }

    /**
     * 更新温室信息
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Greenhouse greenhouse) {
        greenhouse.setUpdateTime(LocalDateTime.now());
        greenhouseMapper.updateById(greenhouse);
        return Result.success();
    }

    /**
     * 删除温室信息
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        greenhouseMapper.deleteById(id);
        return Result.success();
    }
} 
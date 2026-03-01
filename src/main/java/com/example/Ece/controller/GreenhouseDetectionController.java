package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/greenhouseDetection")
public class GreenhouseDetectionController {

    @Resource
    private GreenhouseDetectionMapper greenhouseDetectionMapper;

    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(greenhouseDetectionMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(greenhouseDetectionMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "") String greenhouseId,
                             @RequestParam(defaultValue = "") String detectionType,
                             @RequestParam(defaultValue = "") String severityLevel,
                             @RequestParam(defaultValue = "") String detectionArea) {
        LambdaQueryWrapper<GreenhouseDetection> wrapper = Wrappers.<GreenhouseDetection>lambdaQuery();
        wrapper.orderByDesc(GreenhouseDetection::getRecordTime);
        
        if (StrUtil.isNotBlank(greenhouseId)) {
            wrapper.eq(GreenhouseDetection::getGreenhouseId, Integer.parseInt(greenhouseId));
        }
        if (StrUtil.isNotBlank(detectionType)) {
            wrapper.eq(GreenhouseDetection::getDetectionType, detectionType);
        }
        if (StrUtil.isNotBlank(severityLevel)) {
            wrapper.eq(GreenhouseDetection::getSeverityLevel, severityLevel);
        }
        if (StrUtil.isNotBlank(detectionArea)) {
            wrapper.like(GreenhouseDetection::getDetectionArea, detectionArea);
        }
        
        Page<GreenhouseDetection> page = greenhouseDetectionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @PostMapping
    public Result<?> save(@RequestBody GreenhouseDetection greenhouseDetection) {
        if (greenhouseDetection.getRecordTime() == null) {
            greenhouseDetection.setRecordTime(LocalDateTime.now());
        }
        greenhouseDetectionMapper.insert(greenhouseDetection);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody GreenhouseDetection greenhouseDetection) {
        greenhouseDetectionMapper.updateById(greenhouseDetection);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        greenhouseDetectionMapper.deleteById(id);
        return Result.success();
    }
}

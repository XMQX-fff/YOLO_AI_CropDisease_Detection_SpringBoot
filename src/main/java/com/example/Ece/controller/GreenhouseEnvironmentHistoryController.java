package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.GreenhouseAbnormalStatsDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/greenhouse-environment-history")
public class GreenhouseEnvironmentHistoryController {

    @Resource
    private GreenhouseEnvironmentHistoryMapper greenhouseEnvironmentHistoryMapper;
    @Resource
    private GreenhouseMapper greenhouseMapper;
    @Resource
    private GreenhouseDetectionMapper greenhouseDetectionMapper;
    @Resource
    private GreenhouseAlertRuleMapper greenhouseAlertRuleMapper;

    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(greenhouseEnvironmentHistoryMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(greenhouseEnvironmentHistoryMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String greenhouseId,
                              @RequestParam(defaultValue = "") String dataSource,
                              @RequestParam(defaultValue = "") String startTime,
                              @RequestParam(defaultValue = "") String endTime) {
        LambdaQueryWrapper<GreenhouseEnvironmentHistory> wrapper = Wrappers.<GreenhouseEnvironmentHistory>lambdaQuery();
        wrapper.orderByDesc(GreenhouseEnvironmentHistory::getRecordTime);

        if (StrUtil.isNotBlank(greenhouseId)) {
            wrapper.eq(GreenhouseEnvironmentHistory::getGreenhouseId, greenhouseId);
        }
        if (StrUtil.isNotBlank(dataSource)) {
            wrapper.eq(GreenhouseEnvironmentHistory::getDataSource, dataSource);
        }
        if (StrUtil.isNotBlank(startTime)) {
            wrapper.ge(GreenhouseEnvironmentHistory::getRecordTime, startTime);
        }
        if (StrUtil.isNotBlank(endTime)) {
            wrapper.le(GreenhouseEnvironmentHistory::getRecordTime, endTime);
        }

        Page<GreenhouseEnvironmentHistory> page = greenhouseEnvironmentHistoryMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @PostMapping
    public Result<?> save(@RequestBody GreenhouseEnvironmentHistory history) {
        if (history.getRecordTime() == null) {
            history.setRecordTime(LocalDateTime.now());
        }
        greenhouseEnvironmentHistoryMapper.insert(history);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody GreenhouseEnvironmentHistory history) {
        greenhouseEnvironmentHistoryMapper.updateById(history);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        greenhouseEnvironmentHistoryMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/greenhouse/{greenhouseId}")
    public Result<?> getByGreenhouseId(@PathVariable Integer greenhouseId) {
        LambdaQueryWrapper<GreenhouseEnvironmentHistory> wrapper = Wrappers.<GreenhouseEnvironmentHistory>lambdaQuery();
        wrapper.eq(GreenhouseEnvironmentHistory::getGreenhouseId, greenhouseId);
        wrapper.orderByDesc(GreenhouseEnvironmentHistory::getRecordTime);
        List<GreenhouseEnvironmentHistory> list = greenhouseEnvironmentHistoryMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/chart")
    public Result<?> getChartData(@RequestParam Integer greenhouseId,
                                   @RequestParam String timeRange) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime;

        switch (timeRange) {
            case "8h":
                startTime = endTime.minus(8, ChronoUnit.HOURS);
                break;
            case "1d":
                startTime = endTime.minus(1, ChronoUnit.DAYS);
                break;
            case "1w":
                startTime = endTime.minus(1, ChronoUnit.WEEKS);
                break;
            case "1m":
                startTime = endTime.minus(1, ChronoUnit.MONTHS);
                break;
            default:
                startTime = endTime.minus(1, ChronoUnit.DAYS);
        }

        LambdaQueryWrapper<GreenhouseEnvironmentHistory> wrapper = Wrappers.<GreenhouseEnvironmentHistory>lambdaQuery();
        wrapper.eq(GreenhouseEnvironmentHistory::getGreenhouseId, greenhouseId);
        wrapper.ge(GreenhouseEnvironmentHistory::getRecordTime, startTime);
        wrapper.le(GreenhouseEnvironmentHistory::getRecordTime, endTime);
        wrapper.orderByAsc(GreenhouseEnvironmentHistory::getRecordTime);

        List<GreenhouseEnvironmentHistory> list = greenhouseEnvironmentHistoryMapper.selectList(wrapper);
        return Result.success(list);
    }

    
}

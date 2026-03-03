package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.GreenhouseAlertHistory;
import com.example.Ece.mapper.GreenhouseAlertHistoryMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/greenhouse-alert-history")
public class GreenhouseAlertHistoryController {

    @Resource
    private GreenhouseAlertHistoryMapper greenhouseAlertHistoryMapper;

    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(greenhouseAlertHistoryMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(greenhouseAlertHistoryMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String greenhouseId,
                              @RequestParam(defaultValue = "") String ruleId,
                              @RequestParam(defaultValue = "") String deviceId,
                              @RequestParam(defaultValue = "") String alertCode,
                              @RequestParam(defaultValue = "") String alertTitle,
                              @RequestParam(defaultValue = "") String alertLevel,
                              @RequestParam(defaultValue = "") String alertType,
                              @RequestParam(defaultValue = "") String dataType,
                              @RequestParam(defaultValue = "") String status,
                              @RequestParam(defaultValue = "") String isFalseAlarm,
                              @RequestParam(defaultValue = "") String isArchived,
                              @RequestParam(defaultValue = "") String startTime,
                              @RequestParam(defaultValue = "") String endTime) {
        LambdaQueryWrapper<GreenhouseAlertHistory> wrapper = Wrappers.<GreenhouseAlertHistory>lambdaQuery();
        wrapper.orderByDesc(GreenhouseAlertHistory::getTriggerTime);

        if (StrUtil.isNotBlank(greenhouseId)) {
            wrapper.eq(GreenhouseAlertHistory::getGreenhouseId, greenhouseId);
        }
        if (StrUtil.isNotBlank(ruleId)) {
            wrapper.eq(GreenhouseAlertHistory::getRuleId, ruleId);
        }
        if (StrUtil.isNotBlank(deviceId)) {
            wrapper.eq(GreenhouseAlertHistory::getDeviceId, deviceId);
        }
        if (StrUtil.isNotBlank(alertCode)) {
            wrapper.like(GreenhouseAlertHistory::getAlertCode, alertCode);
        }
        if (StrUtil.isNotBlank(alertTitle)) {
            wrapper.like(GreenhouseAlertHistory::getAlertTitle, alertTitle);
        }
        if (StrUtil.isNotBlank(alertLevel)) {
            wrapper.eq(GreenhouseAlertHistory::getAlertLevel, alertLevel);
        }
        if (StrUtil.isNotBlank(alertType)) {
            wrapper.eq(GreenhouseAlertHistory::getAlertType, alertType);
        }
        if (StrUtil.isNotBlank(dataType)) {
            wrapper.eq(GreenhouseAlertHistory::getDataType, dataType);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(GreenhouseAlertHistory::getStatus, status);
        }
        if (StrUtil.isNotBlank(isFalseAlarm)) {
            wrapper.eq(GreenhouseAlertHistory::getIsFalseAlarm, Integer.parseInt(isFalseAlarm));
        }
        if (StrUtil.isNotBlank(isArchived)) {
            wrapper.eq(GreenhouseAlertHistory::getIsArchived, Integer.parseInt(isArchived));
        }
        if (StrUtil.isNotBlank(startTime)) {
            wrapper.ge(GreenhouseAlertHistory::getTriggerTime, startTime);
        }
        if (StrUtil.isNotBlank(endTime)) {
            wrapper.le(GreenhouseAlertHistory::getTriggerTime, endTime);
        }

        Page<GreenhouseAlertHistory> page = greenhouseAlertHistoryMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @PostMapping
    public Result<?> save(@RequestBody GreenhouseAlertHistory history) {
        if (history.getTriggerTime() == null) {
            history.setTriggerTime(LocalDateTime.now());
        }
        history.setCreateTime(LocalDateTime.now());
        history.setUpdateTime(LocalDateTime.now());
        greenhouseAlertHistoryMapper.insert(history);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody GreenhouseAlertHistory history) {
        history.setUpdateTime(LocalDateTime.now());
        greenhouseAlertHistoryMapper.updateById(history);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        greenhouseAlertHistoryMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/greenhouse/{greenhouseId}")
    public Result<?> getByGreenhouseId(@PathVariable Integer greenhouseId) {
        LambdaQueryWrapper<GreenhouseAlertHistory> wrapper = Wrappers.<GreenhouseAlertHistory>lambdaQuery();
        wrapper.eq(GreenhouseAlertHistory::getGreenhouseId, greenhouseId);
        wrapper.orderByDesc(GreenhouseAlertHistory::getTriggerTime);
        List<GreenhouseAlertHistory> list = greenhouseAlertHistoryMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/unresolved")
    public Result<?> getUnresolvedAlerts() {
        LambdaQueryWrapper<GreenhouseAlertHistory> wrapper = Wrappers.<GreenhouseAlertHistory>lambdaQuery();
        wrapper.in(GreenhouseAlertHistory::getStatus, "unconfirmed", "confirmed", "handling");
        wrapper.orderByDesc(GreenhouseAlertHistory::getTriggerTime);
        List<GreenhouseAlertHistory> list = greenhouseAlertHistoryMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/greenhouse/{greenhouseId}/unresolved")
    public Result<?> getUnresolvedAlertsByGreenhouse(@PathVariable Integer greenhouseId) {
        LambdaQueryWrapper<GreenhouseAlertHistory> wrapper = Wrappers.<GreenhouseAlertHistory>lambdaQuery();
        wrapper.eq(GreenhouseAlertHistory::getGreenhouseId, greenhouseId);
        wrapper.in(GreenhouseAlertHistory::getStatus, "unconfirmed", "confirmed", "handling");
        wrapper.orderByDesc(GreenhouseAlertHistory::getTriggerTime);
        List<GreenhouseAlertHistory> list = greenhouseAlertHistoryMapper.selectList(wrapper);
        return Result.success(list);
    }

    @PostMapping("/{id}/confirm")
    public Result<?> confirmAlert(@PathVariable Integer id, @RequestParam String confirmBy) {
        GreenhouseAlertHistory history = greenhouseAlertHistoryMapper.selectById(id);
        if (history == null) {
            return Result.error("404", "告警记录不存在");
        }
        history.setStatus("confirmed");
        history.setConfirmBy(confirmBy);
        history.setConfirmTime(LocalDateTime.now());
        history.setUpdateTime(LocalDateTime.now());
        greenhouseAlertHistoryMapper.updateById(history);
        return Result.success();
    }

    @PostMapping("/{id}/handle")
    public Result<?> handleAlert(@PathVariable Integer id, 
                                  @RequestParam String handleBy,
                                  @RequestParam String handleMethod,
                                  @RequestParam(required = false) String handleAction,
                                  @RequestParam(required = false) String handleResult) {
        GreenhouseAlertHistory history = greenhouseAlertHistoryMapper.selectById(id);
        if (history == null) {
            return Result.error("404", "告警记录不存在");
        }
        history.setStatus("handling");
        history.setHandleBy(handleBy);
        history.setHandleMethod(handleMethod);
        history.setHandleAction(handleAction);
        history.setHandleResult(handleResult);
        history.setHandleTime(LocalDateTime.now());
        history.setUpdateTime(LocalDateTime.now());
        greenhouseAlertHistoryMapper.updateById(history);
        return Result.success();
    }

    @PostMapping("/{id}/resolve")
    public Result<?> resolveAlert(@PathVariable Integer id,
                                   @RequestParam String resolveBy,
                                   @RequestParam String resolveMethod,
                                   @RequestParam(required = false) String resolveRemark) {
        GreenhouseAlertHistory history = greenhouseAlertHistoryMapper.selectById(id);
        if (history == null) {
            return Result.error("404", "告警记录不存在");
        }
        history.setStatus("resolved");
        history.setResolveBy(resolveBy);
        history.setResolveMethod(resolveMethod);
        history.setResolveRemark(resolveRemark);
        history.setRecoverTime(LocalDateTime.now());
        
        // 计算持续时长（秒）
        if (history.getTriggerTime() != null && history.getRecoverTime() != null) {
            history.setDuration((int) java.time.Duration.between(history.getTriggerTime(), history.getRecoverTime()).getSeconds());
        }
        
        history.setUpdateTime(LocalDateTime.now());
        greenhouseAlertHistoryMapper.updateById(history);
        return Result.success();
    }

    @PostMapping("/{id}/archive")
    public Result<?> archiveAlert(@PathVariable Integer id) {
        GreenhouseAlertHistory history = greenhouseAlertHistoryMapper.selectById(id);
        if (history == null) {
            return Result.error("404", "告警记录不存在");
        }
        history.setIsArchived(1);
        history.setArchiveTime(LocalDateTime.now());
        history.setUpdateTime(LocalDateTime.now());
        greenhouseAlertHistoryMapper.updateById(history);
        return Result.success();
    }

    @PostMapping("/{id}/mark-false-alarm")
    public Result<?> markAsFalseAlarm(@PathVariable Integer id, @RequestParam(required = false) String remark) {
        GreenhouseAlertHistory history = greenhouseAlertHistoryMapper.selectById(id);
        if (history == null) {
            return Result.error("404", "告警记录不存在");
        }
        history.setIsFalseAlarm(1);
        history.setStatus("ignored");
        if (StrUtil.isNotBlank(remark)) {
            history.setResolveRemark(remark);
        }
        history.setUpdateTime(LocalDateTime.now());
        greenhouseAlertHistoryMapper.updateById(history);
        return Result.success();
    }
}

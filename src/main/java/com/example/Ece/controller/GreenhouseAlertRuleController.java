package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.GreenhouseAlertRule;
import com.example.Ece.mapper.GreenhouseAlertRuleMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/greenhouse-alert-rule")
public class GreenhouseAlertRuleController {

    @Resource
    private GreenhouseAlertRuleMapper greenhouseAlertRuleMapper;

    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(greenhouseAlertRuleMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(greenhouseAlertRuleMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String greenhouseId,
                              @RequestParam(defaultValue = "") String ruleName,
                              @RequestParam(defaultValue = "") String ruleCode,
                              @RequestParam(defaultValue = "") String dataType,
                              @RequestParam(defaultValue = "") String alertLevel,
                              @RequestParam(defaultValue = "") String isEnabled) {
        LambdaQueryWrapper<GreenhouseAlertRule> wrapper = Wrappers.<GreenhouseAlertRule>lambdaQuery();
        wrapper.orderByDesc(GreenhouseAlertRule::getCreateTime);

        if (StrUtil.isNotBlank(greenhouseId)) {
            wrapper.eq(GreenhouseAlertRule::getGreenhouseId, greenhouseId);
        }
        if (StrUtil.isNotBlank(ruleName)) {
            wrapper.like(GreenhouseAlertRule::getRuleName, ruleName);
        }
        if (StrUtil.isNotBlank(ruleCode)) {
            wrapper.like(GreenhouseAlertRule::getRuleCode, ruleCode);
        }
        if (StrUtil.isNotBlank(dataType)) {
            wrapper.eq(GreenhouseAlertRule::getDataType, dataType);
        }
        if (StrUtil.isNotBlank(alertLevel)) {
            wrapper.eq(GreenhouseAlertRule::getAlertLevel, alertLevel);
        }
        if (StrUtil.isNotBlank(isEnabled)) {
            wrapper.eq(GreenhouseAlertRule::getIsEnabled, Integer.parseInt(isEnabled));
        }

        Page<GreenhouseAlertRule> page = greenhouseAlertRuleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @PostMapping
    public Result<?> save(@RequestBody GreenhouseAlertRule rule) {
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        greenhouseAlertRuleMapper.insert(rule);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody GreenhouseAlertRule rule) {
        rule.setUpdateTime(LocalDateTime.now());
        greenhouseAlertRuleMapper.updateById(rule);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        greenhouseAlertRuleMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/greenhouse/{greenhouseId}")
    public Result<?> getByGreenhouseId(@PathVariable Integer greenhouseId) {
        LambdaQueryWrapper<GreenhouseAlertRule> wrapper = Wrappers.<GreenhouseAlertRule>lambdaQuery();
        wrapper.and(w -> w.eq(GreenhouseAlertRule::getGreenhouseId, greenhouseId).or().isNull(GreenhouseAlertRule::getGreenhouseId));
        wrapper.eq(GreenhouseAlertRule::getIsEnabled, 1);
        wrapper.orderByDesc(GreenhouseAlertRule::getCreateTime);
        List<GreenhouseAlertRule> list = greenhouseAlertRuleMapper.selectList(wrapper);
        return Result.success(list);
    }

    @GetMapping("/enabled")
    public Result<?> getEnabledRules() {
        LambdaQueryWrapper<GreenhouseAlertRule> wrapper = Wrappers.<GreenhouseAlertRule>lambdaQuery();
        wrapper.eq(GreenhouseAlertRule::getIsEnabled, 1);
        wrapper.orderByDesc(GreenhouseAlertRule::getCreateTime);
        List<GreenhouseAlertRule> list = greenhouseAlertRuleMapper.selectList(wrapper);
        return Result.success(list);
    }

    @PostMapping("/toggle/{id}")
    public Result<?> toggle(@PathVariable Integer id) {
        GreenhouseAlertRule rule = greenhouseAlertRuleMapper.selectById(id);
        if (rule == null) {
            return Result.error("-1", "规则不存在");
        }
        rule.setIsEnabled(rule.getIsEnabled() == 1 ? 0 : 1);
        rule.setUpdateTime(LocalDateTime.now());
        greenhouseAlertRuleMapper.updateById(rule);
        return Result.success();
    }
}

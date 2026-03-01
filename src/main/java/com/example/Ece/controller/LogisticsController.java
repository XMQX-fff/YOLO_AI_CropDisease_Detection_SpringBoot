package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.LogisticsDTO;
import com.example.Ece.entity.Logistics;
import com.example.Ece.mapper.LogisticsMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 物流状态映射常量
class LogisticsStatusMapping {
    public static final Map<String, String> STATUS_MAP = new HashMap<String, String>() {
        {
            put("pending", "待发货");
            put("shipped", "已发货");
            put("transit", "运输中");
            put("delivered", "已送达");
            put("signed", "已签收");
        }
    };
}

@RestController
@RequestMapping("/logistics")
public class LogisticsController {
    @Resource
    LogisticsMapper logisticsMapper;

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        LambdaQueryWrapper<Logistics> wrapper = Wrappers.<Logistics>lambdaQuery();
        wrapper.orderByDesc(Logistics::getId);
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Logistics::getLogisticsNo, search);
        }
        Page<Logistics> logisticsPage = logisticsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        
        // 获取物流列表
        List<Logistics> logisticsList = logisticsPage.getRecords();
        
        // 如果没有物流信息，直接返回
        if (logisticsList.isEmpty()) {
            return Result.success(logisticsPage);
        }
        
        // 转换为DTO并添加中文物流状态
        List<LogisticsDTO> logisticsDTOs = logisticsList.stream().map(logistics -> {
            LogisticsDTO dto = new LogisticsDTO(logistics);
            // 将英文物流状态转换为中文
            dto.setChineseStatus(LogisticsStatusMapping.STATUS_MAP.getOrDefault(
                logistics.getStatus(), logistics.getStatus()));
            return dto;
        }).collect(Collectors.toList());
        
        // 构建新的分页对象
        Page<LogisticsDTO> dtoPage = new Page<>(pageNum, pageSize);
        dtoPage.setRecords(logisticsDTOs);
        dtoPage.setTotal(logisticsPage.getTotal());
        dtoPage.setCurrent(logisticsPage.getCurrent());
        dtoPage.setSize(logisticsPage.getSize());
        dtoPage.setPages(logisticsPage.getPages());
        
        return Result.success(dtoPage);
    }

    @PostMapping
    public Result<?> save(@RequestBody Logistics logistics) {
        logisticsMapper.insert(logistics);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Logistics logistics) {
        logisticsMapper.updateById(logistics);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        logisticsMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        Logistics logistics = logisticsMapper.selectById(id);
        if (logistics == null) {
            return Result.success(null);
        }
        
        // 转换为DTO并添加中文物流状态
        LogisticsDTO dto = new LogisticsDTO(logistics);
        dto.setChineseStatus(LogisticsStatusMapping.STATUS_MAP.getOrDefault(
            logistics.getStatus(), logistics.getStatus()));
        return Result.success(dto);
    }

    @GetMapping("/order/{orderId}")
    public Result<?> getByOrderId(@PathVariable Integer orderId) {
        LambdaQueryWrapper<Logistics> wrapper = Wrappers.<Logistics>lambdaQuery();
        wrapper.eq(Logistics::getOrderId, orderId);
        Logistics logistics = logisticsMapper.selectOne(wrapper);
        
        if (logistics == null) {
            return Result.success(null);
        }
        
        // 转换为DTO并添加中文物流状态
        LogisticsDTO dto = new LogisticsDTO(logistics);
        dto.setChineseStatus(LogisticsStatusMapping.STATUS_MAP.getOrDefault(
            logistics.getStatus(), logistics.getStatus()));
        return Result.success(dto);
    }
}
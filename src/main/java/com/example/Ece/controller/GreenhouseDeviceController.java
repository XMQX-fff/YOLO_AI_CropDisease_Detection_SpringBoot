package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.GreenhouseDevice;
import com.example.Ece.mapper.GreenhouseDeviceMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/greenhouse-device")
public class GreenhouseDeviceController {

    @Resource
    private GreenhouseDeviceMapper greenhouseDeviceMapper;

    @GetMapping("/all")
    public Result<?> getAll() {
        return Result.success(greenhouseDeviceMapper.selectList(null));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(greenhouseDeviceMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String greenhouseId,
                              @RequestParam(defaultValue = "") String deviceCode,
                              @RequestParam(defaultValue = "") String deviceName,
                              @RequestParam(defaultValue = "") String deviceType,
                              @RequestParam(defaultValue = "") String isEnabled,
                              @RequestParam(defaultValue = "") String isAuto) {
        LambdaQueryWrapper<GreenhouseDevice> wrapper = Wrappers.<GreenhouseDevice>lambdaQuery();
        wrapper.orderByAsc(GreenhouseDevice::getSortOrder);

        if (StrUtil.isNotBlank(greenhouseId)) {
            wrapper.eq(GreenhouseDevice::getGreenhouseId, greenhouseId);
        }
        if (StrUtil.isNotBlank(deviceCode)) {
            wrapper.like(GreenhouseDevice::getDeviceCode, deviceCode);
        }
        if (StrUtil.isNotBlank(deviceName)) {
            wrapper.like(GreenhouseDevice::getDeviceName, deviceName);
        }
        if (StrUtil.isNotBlank(deviceType)) {
            wrapper.eq(GreenhouseDevice::getDeviceType, deviceType);
        }
        if (StrUtil.isNotBlank(isEnabled)) {
            wrapper.eq(GreenhouseDevice::getIsEnabled, Integer.parseInt(isEnabled));
        }
        if (StrUtil.isNotBlank(isAuto)) {
            wrapper.eq(GreenhouseDevice::getIsAuto, Integer.parseInt(isAuto));
        }

        Page<GreenhouseDevice> page = greenhouseDeviceMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @PostMapping
    public Result<?> save(@RequestBody GreenhouseDevice device) {
        device.setCreateTime(LocalDateTime.now());
        device.setUpdateTime(LocalDateTime.now());
        greenhouseDeviceMapper.insert(device);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody GreenhouseDevice device) {
        device.setUpdateTime(LocalDateTime.now());
        greenhouseDeviceMapper.updateById(device);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        greenhouseDeviceMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/greenhouse/{greenhouseId}")
    public Result<?> getByGreenhouseId(@PathVariable Integer greenhouseId) {
        LambdaQueryWrapper<GreenhouseDevice> wrapper = Wrappers.<GreenhouseDevice>lambdaQuery();
        wrapper.eq(GreenhouseDevice::getGreenhouseId, greenhouseId);
        wrapper.orderByAsc(GreenhouseDevice::getSortOrder);
        List<GreenhouseDevice> list = greenhouseDeviceMapper.selectList(wrapper);
        return Result.success(list);
    }

    @PostMapping("/control/{id}")
    public Result<?> controlDevice(@PathVariable Integer id,
                                   @RequestParam Integer status,
                                   @RequestParam(required = false) String value,
                                   @RequestParam(required = false) String operateUser) {
        GreenhouseDevice device = greenhouseDeviceMapper.selectById(id);
        if (device == null) {
            return Result.error("-1", "设备不存在");
        }

        device.setCurrentStatus(status);
        if (StrUtil.isNotBlank(value)) {
            device.setCurrentValue(new java.math.BigDecimal(value));
        }
        device.setLastOperateTime(LocalDateTime.now());
        if (StrUtil.isNotBlank(operateUser)) {
            device.setLastOperateUser(operateUser);
        }
        device.setUpdateTime(LocalDateTime.now());

        greenhouseDeviceMapper.updateById(device);
        return Result.success();
    }

    @PostMapping("/toggle-auto/{id}")
    public Result<?> toggleAuto(@PathVariable Integer id,
                                @RequestParam Integer isAuto) {
        GreenhouseDevice device = greenhouseDeviceMapper.selectById(id);
        if (device == null) {
            return Result.error("-1", "设备不存在");
        }

        device.setIsAuto(isAuto);
        device.setUpdateTime(LocalDateTime.now());
        greenhouseDeviceMapper.updateById(device);
        return Result.success();
    }
}

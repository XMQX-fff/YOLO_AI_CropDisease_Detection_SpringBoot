package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.PaymentMethod;
import com.example.Ece.mapper.PaymentMethodMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/paymentMethod")
public class PaymentMethodController {
    @Resource
    PaymentMethodMapper paymentMethodMapper;

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        LambdaQueryWrapper<PaymentMethod> wrapper = Wrappers.<PaymentMethod>lambdaQuery();
        wrapper.orderByDesc(PaymentMethod::getId);
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(PaymentMethod::getName, search);
        }
        Page<PaymentMethod> paymentMethodPage = paymentMethodMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(paymentMethodPage);
    }

    @PostMapping
    public Result<?> save(@RequestBody PaymentMethod paymentMethod) {
        paymentMethodMapper.insert(paymentMethod);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody PaymentMethod paymentMethod) {
        paymentMethodMapper.updateById(paymentMethod);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        paymentMethodMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(paymentMethodMapper.selectById(id));
    }

    @GetMapping("/enabled")
    public Result<?> getEnabled() {
        LambdaQueryWrapper<PaymentMethod> wrapper = Wrappers.<PaymentMethod>lambdaQuery();
        wrapper.eq(PaymentMethod::getIsEnabled, true);
        return Result.success(paymentMethodMapper.selectList(wrapper));
    }
}
package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.PaymentRecordDTO;
import com.example.Ece.entity.Order;
import com.example.Ece.entity.PaymentMethod;
import com.example.Ece.entity.PaymentRecord;
import com.example.Ece.mapper.OrderMapper;
import com.example.Ece.mapper.PaymentMethodMapper;
import com.example.Ece.mapper.PaymentRecordMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/paymentRecord")
public class PaymentRecordController {
    @Resource
    PaymentRecordMapper paymentRecordMapper;
    @Resource
    OrderMapper orderMapper;
    @Resource
    PaymentMethodMapper paymentMethodMapper;

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "0") Integer userId) {
        LambdaQueryWrapper<PaymentRecord> wrapper = Wrappers.<PaymentRecord>lambdaQuery();
        
        // 如果传入了userId参数，先查询该用户的所有订单ID
        if (userId != 0) {
            // 查询该用户的所有订单
            LambdaQueryWrapper<Order> orderWrapper = Wrappers.<Order>lambdaQuery();
            orderWrapper.eq(Order::getUserId, userId);
            List<Order> orders = orderMapper.selectList(orderWrapper);
            
            // 获取所有订单ID
            List<Integer> orderIds = orders.stream()
                    .map(Order::getId)
                    .collect(Collectors.toList());
            
            // 如果该用户有订单，过滤支付记录
            if (!orderIds.isEmpty()) {
                wrapper.in(PaymentRecord::getOrderId, orderIds);
            } else {
                // 如果该用户没有订单，直接返回空列表
                return Result.success(new Page<>(pageNum, pageSize));
            }
        }
        
        // 搜索条件
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(PaymentRecord::getPaymentNo, search);
        }
        
        // 排序
        wrapper.orderByDesc(PaymentRecord::getId);
        
        // 分页查询
        Page<PaymentRecord> paymentRecordPage = paymentRecordMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        
        // 获取支付记录列表
        List<PaymentRecord> paymentRecords = paymentRecordPage.getRecords();
        
        // 如果没有支付记录，直接返回
        if (paymentRecords.isEmpty()) {
            return Result.success(paymentRecordPage);
        }
        
        // 获取所有支付方式ID
        List<Integer> paymentMethodIds = paymentRecords.stream()
                .map(PaymentRecord::getPaymentMethodId)
                .distinct()
                .collect(Collectors.toList());
        
        // 查询所有支付方式
        LambdaQueryWrapper<PaymentMethod> paymentMethodWrapper = Wrappers.<PaymentMethod>lambdaQuery();
        paymentMethodWrapper.in(PaymentMethod::getId, paymentMethodIds);
        List<PaymentMethod> paymentMethods = paymentMethodMapper.selectList(paymentMethodWrapper);
        
        // 将支付方式转换为Map便于查询
        Map<Integer, String> paymentMethodMap = paymentMethods.stream()
                .collect(Collectors.toMap(PaymentMethod::getId, PaymentMethod::getName));
        
        // 转换为DTO并添加支付方式名称
        List<PaymentRecordDTO> paymentRecordDTOs = paymentRecords.stream().map(record -> {
            PaymentRecordDTO dto = new PaymentRecordDTO(record);
            // 添加支付方式名称
            dto.setPaymentMethodName(paymentMethodMap.getOrDefault(record.getPaymentMethodId(), "未知支付方式"));
            return dto;
        }).collect(Collectors.toList());
        
        // 构建新的分页对象
        Page<PaymentRecordDTO> dtoPage = new Page<>(pageNum, pageSize);
        dtoPage.setRecords(paymentRecordDTOs);
        dtoPage.setTotal(paymentRecordPage.getTotal());
        dtoPage.setCurrent(paymentRecordPage.getCurrent());
        dtoPage.setSize(paymentRecordPage.getSize());
        dtoPage.setPages(paymentRecordPage.getPages());
        
        return Result.success(dtoPage);
    }

    @PostMapping
    public Result<?> save(@RequestBody PaymentRecord paymentRecord) {
        paymentRecordMapper.insert(paymentRecord);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody PaymentRecord paymentRecord) {
        paymentRecordMapper.updateById(paymentRecord);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        paymentRecordMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(paymentRecordMapper.selectById(id));
    }

    @GetMapping("/order/{orderId}")
    public Result<?> getByOrderId(@PathVariable Integer orderId) {
        LambdaQueryWrapper<PaymentRecord> wrapper = Wrappers.<PaymentRecord>lambdaQuery();
        wrapper.eq(PaymentRecord::getOrderId, orderId);
        return Result.success(paymentRecordMapper.selectList(wrapper));
    }
}
package com.example.Ece.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.OrderItem;
import com.example.Ece.mapper.OrderItemMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/orderItem")
public class OrderItemController {
    @Resource
    OrderItemMapper orderItemMapper;

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<OrderItem> wrapper = Wrappers.<OrderItem>lambdaQuery();
        wrapper.orderByDesc(OrderItem::getId);
        Page<OrderItem> orderItemPage = orderItemMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(orderItemPage);
    }

    @PostMapping
    public Result<?> save(@RequestBody OrderItem orderItem) {
        orderItemMapper.insert(orderItem);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody OrderItem orderItem) {
        orderItemMapper.updateById(orderItem);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        orderItemMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(orderItemMapper.selectById(id));
    }

    @GetMapping("/order/{orderId}")
    public Result<?> getByOrderId(@PathVariable Integer orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = Wrappers.<OrderItem>lambdaQuery();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return Result.success(orderItemMapper.selectList(wrapper));
    }
}
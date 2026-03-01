package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.OrderDTO;
import com.example.Ece.entity.Logistics;
import com.example.Ece.entity.Order;
import com.example.Ece.entity.OrderItem;
import com.example.Ece.entity.Product;
import com.example.Ece.mapper.LogisticsMapper;
import com.example.Ece.mapper.OrderItemMapper;
import com.example.Ece.mapper.OrderMapper;
import com.example.Ece.mapper.ProductMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 物流状态映射常量
class LogisticsStatusMap {
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
@RequestMapping("/order")
public class OrderController {
    @Resource
    OrderMapper orderMapper;
    @Resource
    OrderItemMapper orderItemMapper;
    @Resource
    ProductMapper productMapper;
    @Resource
    LogisticsMapper logisticsMapper;

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "0") Integer userId) {
        LambdaQueryWrapper<Order> wrapper = Wrappers.<Order>lambdaQuery();
        
        // 根据userId过滤订单
        if (userId != 0) {
            wrapper.eq(Order::getUserId, userId);
        }
        
        // 搜索条件
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Order::getOrderNo, search);
        }
        
        // 排序
        wrapper.orderByDesc(Order::getId);
        
        // 分页查询
        Page<Order> orderPage = orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        
        // 获取订单列表
        List<Order> orders = orderPage.getRecords();
        
        // 如果没有订单，直接返回
        if (orders.isEmpty()) {
            return Result.success(orderPage);
        }
        
        // 获取所有订单ID
        List<Integer> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        
        // 查询所有物流信息
        LambdaQueryWrapper<Logistics> logisticsWrapper = Wrappers.<Logistics>lambdaQuery();
        logisticsWrapper.in(Logistics::getOrderId, orderIds);
        List<Logistics> logisticsList = logisticsMapper.selectList(logisticsWrapper);
        
        // 将物流信息转换为Map便于查询（orderId -> logistics）
        Map<Integer, Logistics> logisticsMap = logisticsList.stream()
                .collect(Collectors.toMap(Logistics::getOrderId, logistics -> logistics));
        
        // 转换为DTO并添加物流信息
        List<OrderDTO> orderDTOs = orders.stream().map(order -> {
            OrderDTO dto = new OrderDTO(order);
            
            // 添加物流信息
            Logistics logistics = logisticsMap.get(order.getId());
            if (logistics != null) {
                // 将英文物流状态转换为中文
                String logisticsStatus = LogisticsStatusMap.STATUS_MAP.getOrDefault(
                    logistics.getStatus(), logistics.getStatus());
                dto.setLogisticsStatus(logisticsStatus);
                dto.setLogisticsNo(logistics.getLogisticsNo());
                dto.setLogisticsCompany(logistics.getLogisticsCompany());
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        // 构建新的分页对象
        Page<OrderDTO> dtoPage = new Page<>(pageNum, pageSize);
        dtoPage.setRecords(orderDTOs);
        dtoPage.setTotal(orderPage.getTotal());
        dtoPage.setCurrent(orderPage.getCurrent());
        dtoPage.setSize(orderPage.getSize());
        dtoPage.setPages(orderPage.getPages());
        
        return Result.success(dtoPage);
    }

    @PostMapping
    public Result<?> save(@RequestBody Order order) {
        orderMapper.insert(order);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Order order) {
        orderMapper.updateById(order);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        orderMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        // 查询订单基本信息
        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.success(null);
        }

        // 查询物流信息
        LambdaQueryWrapper<Logistics> logisticsWrapper = Wrappers.<Logistics>lambdaQuery();
        logisticsWrapper.eq(Logistics::getOrderId, id);
        Logistics logistics = logisticsMapper.selectOne(logisticsWrapper);

        // 转换为DTO并添加物流信息
        OrderDTO orderDTO = new OrderDTO(order);
        if (logistics != null) {
            // 将英文物流状态转换为中文
            String logisticsStatus = LogisticsStatusMap.STATUS_MAP.getOrDefault(
                logistics.getStatus(), logistics.getStatus());
            orderDTO.setLogisticsStatus(logisticsStatus);
            orderDTO.setLogisticsNo(logistics.getLogisticsNo());
            orderDTO.setLogisticsCompany(logistics.getLogisticsCompany());
        }

        // 查询该订单的所有订单项
        LambdaQueryWrapper<OrderItem> itemWrapper = Wrappers.<OrderItem>lambdaQuery();
        itemWrapper.eq(OrderItem::getOrderId, id);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemWrapper);

        // 获取所有订单项中的产品ID
        List<Integer> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .distinct()
                .collect(Collectors.toList());

        // 查询所有相关产品信息
        LambdaQueryWrapper<Product> productWrapper = Wrappers.<Product>lambdaQuery();
        productWrapper.in(Product::getId, productIds);
        List<Product> products = productMapper.selectList(productWrapper);

        // 将产品信息转换为Map便于查询
        Map<Integer, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        // 构建包含产品信息的订单项列表
        List<Map<String, Object>> itemList = orderItems.stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("productId", item.getProductId());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("price", item.getPrice());
            
            // 添加产品信息
            Product product = productMap.get(item.getProductId());
            if (product != null) {
                itemMap.put("productName", product.getProductName());
                itemMap.put("productDescription", product.getDescription());
            }
            
            return itemMap;
        }).collect(Collectors.toList());

        // 构建完整的订单详情响应
        Map<String, Object> orderDetail = new HashMap<>();
        orderDetail.put("order", orderDTO); // 使用DTO替换原Order
        orderDetail.put("orderItems", itemList);
        
        return Result.success(orderDetail);
    }
}
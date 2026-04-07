package com.example.Ece.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.Order;
import com.example.Ece.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @PostMapping
    public Result<Order> create(
            @RequestParam Integer userId,
            @RequestParam Integer addressId,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String buyerMessage) {
        try {
            Order order = orderService.createOrder(userId, addressId, remark, buyerMessage);
            return Result.success(order);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Integer id) {
        Order order = orderService.getOrderDetail(id);
        if (order != null) {
            return Result.success(order);
        }
        return Result.error("订单不存在");
    }
    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam Integer userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return Result.success(orders);
    }
    @GetMapping("/page")
    public Result<IPage<Order>> page(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> result = orderService.getOrderPage(page, userId, status);
        return Result.success(result);
    }
    @PutMapping("/{id}/pay")
    public Result<Boolean> pay(
            @PathVariable Integer id,
            @RequestParam String payMethod) {
        try {
            boolean success = orderService.payOrder(id, payMethod);
            return Result.success(success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    @PutMapping("/{id}/ship")
    public Result<Boolean> ship(
            @PathVariable Integer id,
            @RequestParam String trackingNo,
            @RequestParam String logisticsCompany) {
        try {
            boolean success = orderService.shipOrder(id, trackingNo, logisticsCompany);
            return Result.success(success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    @PutMapping("/{id}/confirm")
    public Result<Boolean> confirm(@PathVariable Integer id) {
        try {
            boolean success = orderService.confirmOrder(id);
            return Result.success(success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancel(
            @PathVariable Integer id,
            @RequestParam(required = false) String cancelReason) {
        try {
            boolean success = orderService.cancelOrder(id, cancelReason);
            return Result.success(success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean success = orderService.deleteOrder(id);
        return Result.success(success);
    }
    @GetMapping("/byOrderNo")
    public Result<Order> getByOrderNo(@RequestParam String orderNo) {
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order != null) {
            return Result.success(order);
        }
        return Result.error("订单不存在");
    }
}



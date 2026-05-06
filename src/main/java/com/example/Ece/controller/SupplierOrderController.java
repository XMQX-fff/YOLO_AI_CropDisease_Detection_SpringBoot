package com.example.Ece.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.Order;
import com.example.Ece.entity.Product;
import com.example.Ece.entity.User;
import com.example.Ece.service.OrderService;
import com.example.Ece.service.ProductService;
import com.example.Ece.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 供应商订单管理Controller
 * 供应商只能查看和管理包含自己商品的订单
 */
@RestController
@RequestMapping("/supplier/order")
public class SupplierOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 从请求中获取当前登录用户
     */
    private User getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                String token = jwtUtils.extractTokenFromHeader(authHeader);
                if (token != null && jwtUtils.validateToken(token)) {
                    Integer userId = jwtUtils.getUserIdFromToken(token);
                    String username = jwtUtils.getUsernameFromToken(token);
                    String role = jwtUtils.getRoleFromToken(token);
                    if (userId != null) {
                        currentUser = new User();
                        currentUser.setId(userId);
                        currentUser.setUsername(username);
                        currentUser.setRole(role);
                    }
                }
            }
        }
        return currentUser;
    }

    /**
     * 检查用户是否已登录
     */
    private boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    /**
     * 获取供应商订单列表（分页）
     * 查询包含该供应商商品的订单
     */
    @GetMapping("/page")
    public Result<IPage<Order>> getSupplierOrderPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }

        // 获取当前用户ID（供应商用户ID）
        User currentUser = getCurrentUser(request);
        Integer supplierId = currentUser.getId();

        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> result = orderService.getOrderPageBySupplierId(page, supplierId, status);
        return Result.success(result);
    }

    /**
     * 获取供应商订单详情
     */
    @GetMapping("/{orderId}")
    public Result<Order> getSupplierOrderDetail(
            @PathVariable Integer orderId,
            HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }

        // 获取当前用户ID（供应商用户ID）
        User currentUser = getCurrentUser(request);
        Integer supplierId = currentUser.getId();

        Order order = orderService.getOrderDetail(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        // 验证订单是否包含该供应商的商品
        boolean hasSupplierProduct = order.getOrderItems().stream()
                .anyMatch(item -> {
                    Product product = productService.getById(item.getProductId());
                    return product != null && product.getSupplierId().equals(supplierId);
                });

        if (!hasSupplierProduct) {
            return Result.error("403", "您无权查看此订单");
        }

        return Result.success(order);
    }

    /**
     * 供应商发货
     */
    @PutMapping("/{orderId}/ship")
    public Result<Boolean> shipOrder(
            @PathVariable Integer orderId,
            @RequestParam String trackingNo,
            @RequestParam String logisticsCompany,
            HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }

        // 获取当前用户ID（供应商用户ID）
        User currentUser = getCurrentUser(request);
        Integer supplierId = currentUser.getId();

        // 验证订单是否包含该供应商的商品
        Order order = orderService.getOrderDetail(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }

        boolean hasSupplierProduct = order.getOrderItems().stream()
                .anyMatch(item -> {
                    Product product = productService.getById(item.getProductId());
                    return product != null && product.getSupplierId().equals(supplierId);
                });

        if (!hasSupplierProduct) {
            return Result.error("403", "您无权操作此订单");
        }

        try {
            boolean success = orderService.shipOrder(orderId, trackingNo, logisticsCompany);
            return Result.success(success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}

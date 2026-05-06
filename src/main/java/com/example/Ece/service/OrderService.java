package com.example.Ece.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Ece.entity.Order;

import java.util.List;

/**
 * 订单Service接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Order createOrder(Integer userId, Integer addressId, String remark, String buyerMessage);

    /**
     * 获取订单详情
     */
    Order getOrderDetail(Integer orderId);

    /**
     * 获取用户的订单列表
     */
    List<Order> getOrdersByUserId(Integer userId);

    /**
     * 分页获取用户订单
     */
    IPage<Order> getOrderPage(Page<Order> page, Integer userId, String status);

    /**
     * 支付订单
     */
    boolean payOrder(Integer orderId, String payMethod);

    /**
     * 发货
     */
    boolean shipOrder(Integer orderId, String trackingNo, String logisticsCompany);

    /**
     * 确认收货
     */
    boolean confirmOrder(Integer orderId);

    /**
     * 取消订单
     */
    boolean cancelOrder(Integer orderId, String cancelReason);

    /**
     * 删除订单
     */
    boolean deleteOrder(Integer orderId);

    /**
     * 根据订单号获取订单
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * 获取供应商的订单列表
     */
    List<Order> getOrdersBySupplierId(Integer supplierId);

    /**
     * 分页获取供应商订单
     * 查询包含该供应商商品的订单
     */
    IPage<Order> getOrderPageBySupplierId(Page<Order> page, Integer supplierId, String status);
}

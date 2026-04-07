package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单号查询订单
     */
    default Order selectByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return selectOne(wrapper);
    }

    /**
     * 更新订单状态为已支付
     */
    default int updateStatusToPaid(Integer orderId) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .set(Order::getStatus, "paid")
               .set(Order::getPaidTime, LocalDateTime.now());
        return update(null, wrapper);
    }

    /**
     * 更新订单状态为已发货
     */
    default int updateStatusToShipped(Integer orderId, String trackingNo, String logisticsCompany) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .set(Order::getStatus, "shipped")
               .set(Order::getTrackingNo, trackingNo)
               .set(Order::getLogisticsCompany, logisticsCompany)
               .set(Order::getShippedTime, LocalDateTime.now());
        return update(null, wrapper);
    }

    /**
     * 更新订单状态为已完成
     */
    default int updateStatusToCompleted(Integer orderId) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .set(Order::getStatus, "completed")
               .set(Order::getCompletedTime, LocalDateTime.now());
        return update(null, wrapper);
    }

    /**
     * 更新订单状态为已取消
     */
    default int updateStatusToCancelled(Integer orderId, String cancelReason) {
        LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Order::getId, orderId)
               .set(Order::getStatus, "cancelled")
               .set(Order::getCancelReason, cancelReason)
               .set(Order::getCancelledTime, LocalDateTime.now());
        return update(null, wrapper);
    }

    /**
     * 获取用户的订单列表
     */
    default List<Order> selectByUserId(Integer userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getOrderTime);
        return selectList(wrapper);
    }

    /**
     * 根据状态和用户信息查询订单
     */
    default List<Order> selectByUserIdAndStatus(Integer userId, String status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getOrderTime);
        return selectList(wrapper);
    }
}

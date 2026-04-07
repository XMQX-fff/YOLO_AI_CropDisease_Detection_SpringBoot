package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单商品Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单ID获取订单商品列表
     */
    default List<OrderItem> selectByOrderId(Integer orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return selectList(wrapper);
    }
}

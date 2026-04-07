package com.example.Ece.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Ece.entity.*;
import com.example.Ece.mapper.*;
import com.example.Ece.service.OrderService;
import com.example.Ece.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * 订单Service实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public Order createOrder(Integer userId, Integer addressId, String remark, String buyerMessage) {
        // 获取购物车中选中的商品
        List<ShoppingCart> cartList = shoppingCartMapper.selectSelectedCartByUserId(userId);
        if (cartList.isEmpty()) {
            throw new RuntimeException("购物车中没有选中的商品");
        }

        // 获取收货地址
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null) {
            throw new RuntimeException("收货地址不存在");
        }

        // 检查库存并计算总价
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (ShoppingCart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品【" + product.getProductName() + "】库存不足");
            }
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setTotalPrice(totalPrice);
        order.setDeliveryFee(BigDecimal.ZERO); // 简化处理，实际应根据规则计算
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(totalPrice);
        order.setStatus("pending");
        order.setAddress(address.getFullAddress());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setProvince(address.getProvince());
        order.setCity(address.getCity());
        order.setDistrict(address.getDistrict());
        order.setDeliveryMethod("快递配送");
        order.setRemark(remark);
        order.setBuyerMessage(buyerMessage);
        order.setOrderTime(LocalDateTime.now());

        save(order);

        // 创建订单商品
        for (ShoppingCart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cart.getProductId());
            orderItem.setProductName(product.getProductName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItemMapper.insert(orderItem);

            // 扣减库存
            product.setStock(product.getStock() - cart.getQuantity());
            productMapper.updateById(product);
        }

        // 清空购物车中选中的商品
        shoppingCartMapper.deleteSelected(userId);

        return order;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = String.format("%04d", new Random().nextInt(10000));
        return "ORD" + dateStr + randomStr;
    }

    @Override
    public Order getOrderDetail(Integer orderId) {
        Order order = getById(orderId);
        if (order != null) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
            // 加载订单商品的图片
            for (OrderItem item : items) {
                if (item.getProductImage() == null || item.getProductImage().isEmpty()) {
                    ProductImage mainImage = productImageMapper.selectMainImage(item.getProductId());
                    if (mainImage != null) {
                        item.setProductImage(mainImage.getImageUrl());
                    }
                }
            }
            order.setOrderItems(items);
        }
        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public IPage<Order> getOrderPage(Page<Order> page, Integer userId, String status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getOrderTime);
        IPage<Order> result = page(page, wrapper);

        // 加载每个订单的商品信息
        for (Order order : result.getRecords()) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            // 加载订单商品的图片
            for (OrderItem item : items) {
                if (item.getProductImage() == null || item.getProductImage().isEmpty()) {
                    ProductImage mainImage = productImageMapper.selectMainImage(item.getProductId());
                    if (mainImage != null) {
                        item.setProductImage(mainImage.getImageUrl());
                    }
                }
            }
            order.setOrderItems(items);
        }

        return result;
    }

    @Override
    public boolean payOrder(Integer orderId, String payMethod) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("paid");
        order.setPayMethod(payMethod);
        order.setPaidTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    public boolean shipOrder(Integer orderId, String trackingNo, String logisticsCompany) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("shipped");
        order.setTrackingNo(trackingNo);
        order.setLogisticsCompany(logisticsCompany);
        order.setShippedTime(LocalDateTime.now());
        return updateById(order);
    }

    @Override
    public boolean confirmOrder(Integer orderId) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"shipped".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("completed");
        order.setCompletedTime(LocalDateTime.now());

        // 增加商品销量
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setSalesCount(product.getSalesCount() + item.getQuantity());
                productMapper.updateById(product);
            }
        }

        return updateById(order);
    }

    @Override
    public boolean cancelOrder(Integer orderId, String cancelReason) {
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付订单");
        }

        order.setStatus("cancelled");
        order.setCancelReason(cancelReason);
        order.setCancelledTime(LocalDateTime.now());

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.updateById(product);
            }
        }

        return updateById(order);
    }

    @Override
    public boolean deleteOrder(Integer orderId) {
        // 删除订单商品
        orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        return removeById(orderId);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return baseMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<Order> getOrdersBySupplierId(Integer supplierId) {
        // 通过订单商品中的商品ID关联供应商
        // 简化实现，实际应该通过更复杂的查询
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getOrderTime);
        return list(wrapper);
    }
}

package com.example.Ece.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Ece.entity.Product;
import com.example.Ece.entity.ProductImage;
import com.example.Ece.entity.ShoppingCart;
import com.example.Ece.mapper.ProductImageMapper;
import com.example.Ece.mapper.ProductMapper;
import com.example.Ece.mapper.ShoppingCartMapper;
import com.example.Ece.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车Service实现类
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImageMapper productImageMapper;

    @Override
    public List<ShoppingCart> getCartByUserId(Integer userId) {
        List<ShoppingCart> cartList = baseMapper.selectByUserId(userId);
        // 加载商品信息
        for (ShoppingCart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product != null) {
                // 加载商品主图
                ProductImage mainImage = productImageMapper.selectMainImage(product.getId());
                if (mainImage != null) {
                    product.setMainImage(mainImage.getImageUrl());
                }
                cart.setProduct(product);
                cart.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
                cart.setIsValid(product.getStock() >= cart.getQuantity());
                if (!cart.getIsValid()) {
                    cart.setInvalidReason("库存不足");
                }
            }
        }
        return cartList;
    }

    @Override
    @Transactional
    public boolean addToCart(Integer userId, Integer productId, Integer quantity) {
        // 检查商品是否存在且有库存
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        // 检查购物车中是否已有该商品
        ShoppingCart existingCart = baseMapper.selectByUserIdAndProductId(userId, productId);
        if (existingCart != null) {
            // 更新数量
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            existingCart.setUpdateTime(LocalDateTime.now());
            return updateById(existingCart);
        } else {
            // 新增
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setSelected(true);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            return save(cart);
        }
    }

    @Override
    public boolean updateQuantity(Integer cartId, Integer quantity) {
        ShoppingCart cart = getById(cartId);
        if (cart == null) {
            throw new RuntimeException("购物车商品不存在");
        }
        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());
        return updateById(cart);
    }

    @Override
    public boolean removeFromCart(Integer cartId) {
        return removeById(cartId);
    }

    @Override
    @Transactional
    public boolean batchRemove(List<Integer> cartIds) {
        return removeByIds(cartIds);
    }

    @Override
    public boolean clearCart(Integer userId) {
        baseMapper.clearCart(userId);
        return true;
    }

    @Override
    public boolean toggleSelected(Integer cartId, Boolean selected) {
        ShoppingCart cart = getById(cartId);
        if (cart == null) {
            return false;
        }
        cart.setSelected(selected);
        return updateById(cart);
    }

    @Override
    public boolean toggleAllSelected(Integer userId, Boolean selected) {
        baseMapper.updateAllSelected(userId, selected);
        return true;
    }

    @Override
    public List<ShoppingCart> getSelectedCart(Integer userId) {
        List<ShoppingCart> cartList = baseMapper.selectSelectedCartByUserId(userId);
        // 加载商品信息
        for (ShoppingCart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            cart.setProduct(product);
            if (product != null) {
                cart.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            }
        }
        return cartList;
    }

    @Override
    public Long getCartCount(Integer userId) {
        return baseMapper.getCartCount(userId);
    }

    @Override
    public BigDecimal calculateTotal(Integer userId) {
        List<ShoppingCart> selectedCart = getSelectedCart(userId);
        BigDecimal total = BigDecimal.ZERO;
        for (ShoppingCart cart : selectedCart) {
            if (cart.getSubtotal() != null) {
                total = total.add(cart.getSubtotal());
            }
        }
        return total;
    }

    @Override
    public boolean removeSelected(Integer userId) {
        baseMapper.deleteSelected(userId);
        return true;
    }
}

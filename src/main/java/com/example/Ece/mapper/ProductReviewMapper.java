package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.ProductReview;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品评价Mapper接口 - 纯MyBatis-Plus实现
 */
@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {

    /**
     * 获取商品的评价列表
     */
    default List<ProductReview> selectByProductId(Integer productId) {
        LambdaQueryWrapper<ProductReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductReview::getProductId, productId)
               .eq(ProductReview::getIsShow, true)
               .orderByDesc(ProductReview::getCreateTime);
        return selectList(wrapper);
    }

    /**
     * 获取用户的评价列表
     */
    default List<ProductReview> selectByUserId(Integer userId) {
        LambdaQueryWrapper<ProductReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductReview::getUserId, userId)
               .orderByDesc(ProductReview::getCreateTime);
        return selectList(wrapper);
    }

    /**
     * 商家回复评价
     */
    default int replyReview(Integer reviewId, String replyContent) {
        LambdaUpdateWrapper<ProductReview> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ProductReview::getId, reviewId)
               .set(ProductReview::getReplyContent, replyContent)
               .set(ProductReview::getReplyTime, java.time.LocalDateTime.now());
        return update(null, wrapper);
    }

    /**
     * 获取订单商品的评价
     */
    default ProductReview selectByOrderItemId(Integer orderItemId) {
        LambdaQueryWrapper<ProductReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductReview::getOrderItemId, orderItemId);
        return selectOne(wrapper);
    }
}

package com.example.Ece.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Ece.entity.Product;

import java.util.List;

/**
 * 商品Service接口
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表
     */
    IPage<Product> getProductPage(Page<Product> page, Integer categoryId, String keyword, Boolean isOnSale, String sort);

    /**
     * 获取商品详情
     */
    Product getProductDetail(Integer productId, Integer userId);

    /**
     * 获取推荐商品
     */
    List<Product> getRecommendProducts(Integer limit);

    /**
     * 获取热销商品
     */
    List<Product> getHotProducts(Integer limit);

    /**
     * 获取新品
     */
    List<Product> getNewProducts(Integer limit);

    /**
     * 添加商品
     */
    boolean addProduct(Product product);

    /**
     * 更新商品
     */
    boolean updateProduct(Product product);

    /**
     * 删除商品
     */
    boolean deleteProduct(Integer productId);

    /**
     * 增加浏览量
     */
    void increaseViewCount(Integer productId);

    /**
     * 增加销量
     */
    void increaseSales(Integer productId, Integer quantity);

    /**
     * 更新库存
     */
    boolean updateStock(Integer productId, Integer stock);

    /**
     * 上架/下架商品
     */
    boolean toggleOnSale(Integer productId, Boolean isOnSale);

    /**
     * 检查商品库存
     */
    boolean checkStock(Integer productId, Integer quantity);

    // ========== 供应商管理相关方法 ==========

    /**
     * 分页查询供应商的商品列表
     */
    IPage<Product> getSupplierProductPage(Page<Product> page, Integer supplierId, Integer categoryId, String keyword, Boolean isOnSale, String sort);

    /**
     * 检查商品是否属于指定供应商
     */
    boolean isProductBelongToSupplier(Integer productId, Integer supplierId);

    /**
     * 获取供应商的商品列表
     */
    List<Product> getProductsBySupplierId(Integer supplierId);

    /**
     * 获取供应商商品统计信息
     * @param supplierId 供应商ID（管理员传入null查看全部）
     * @param isAdmin 是否是管理员
     * @return 统计信息Map
     */
    java.util.Map<String, Object> getSupplierProductStatistics(Integer supplierId, boolean isAdmin);
}

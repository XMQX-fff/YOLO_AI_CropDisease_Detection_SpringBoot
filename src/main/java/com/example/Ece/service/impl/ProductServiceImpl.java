package com.example.Ece.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Ece.entity.Product;
import com.example.Ece.entity.ProductCategory;
import com.example.Ece.entity.ProductImage;
import com.example.Ece.mapper.ProductCategoryMapper;
import com.example.Ece.mapper.ProductFavoriteMapper;
import com.example.Ece.mapper.ProductImageMapper;
import com.example.Ece.mapper.ProductMapper;
import com.example.Ece.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品Service实现类
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private ProductCategoryMapper productCategoryMapper;

    @Autowired
    private ProductFavoriteMapper productFavoriteMapper;

    @Override
    public IPage<Product> getProductPage(Page<Product> page, Integer categoryId, String keyword, Boolean isOnSale, String sort) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null) {
            // 获取该分类及其所有子分类的ID列表
            List<Integer> categoryIds = getAllChildCategoryIds(categoryId);
            wrapper.in(Product::getCategoryId, categoryIds);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getProductName, keyword)
                   .or()
                   .like(Product::getSubTitle, keyword);
        }
        if (isOnSale != null) {
            wrapper.eq(Product::getIsOnSale, isOnSale);
        }

        // 处理排序
        applySort(wrapper, sort);

        IPage<Product> result = page(page, wrapper);
        loadMainImages(result.getRecords());
        return result;
    }

    /**
     * 应用排序规则
     */
    private void applySort(LambdaQueryWrapper<Product> wrapper, String sort) {
        if (sort == null || sort.isEmpty()) {
            // 默认排序：推荐 > 热销 > 创建时间
            wrapper.orderByDesc(Product::getIsRecommend)
                   .orderByDesc(Product::getIsHot)
                   .orderByDesc(Product::getCreatedAt);
            return;
        }

        switch (sort) {
            case "sales":
                // 销量优先
                wrapper.orderByDesc(Product::getSalesCount);
                break;
            case "price_asc":
                // 价格从低到高
                wrapper.orderByAsc(Product::getPrice);
                break;
            case "price_desc":
                // 价格从高到低
                wrapper.orderByDesc(Product::getPrice);
                break;
            case "new":
                // 最新上架
                wrapper.orderByDesc(Product::getCreatedAt);
                break;
            default:
                // 默认排序
                wrapper.orderByDesc(Product::getIsRecommend)
                       .orderByDesc(Product::getIsHot)
                       .orderByDesc(Product::getCreatedAt);
                break;
        }
    }

    @Override
    public Product getProductDetail(Integer productId, Integer userId) {
        Product product = getById(productId);
        if (product != null) {
            // 加载商品图片
            List<ProductImage> images = productImageMapper.selectByProductId(productId);
            product.setImages(images);
            if (!images.isEmpty()) {
                product.setMainImage(images.get(0).getImageUrl());
            }
            // 检查当前用户是否收藏了该商品
            if (userId != null) {
                boolean isFavorite = productFavoriteMapper.checkFavorite(userId, productId);
                product.setIsFavorite(isFavorite);
            }
        }
        return product;
    }

    @Override
    public List<Product> getRecommendProducts(Integer limit) {
        List<Product> products = baseMapper.selectRecommendProducts(limit);
        loadMainImages(products);
        return products;
    }

    @Override
    public List<Product> getHotProducts(Integer limit) {
        List<Product> products = baseMapper.selectHotProducts(limit);
        loadMainImages(products);
        return products;
    }

    @Override
    public List<Product> getNewProducts(Integer limit) {
        List<Product> products = baseMapper.selectNewProducts(limit);
        loadMainImages(products);
        return products;
    }

    /**
     * 为商品列表加载主图
     */
    private void loadMainImages(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        for (Product product : products) {
            if (product.getId() != null) {
                ProductImage mainImage = productImageMapper.selectMainImage(product.getId());
                if (mainImage != null) {
                    product.setMainImage(mainImage.getImageUrl());
                }
            }
        }
    }

    @Override
    @Transactional
    public boolean addProduct(Product product) {
        boolean saved = save(product);
        if (saved && product.getImages() != null) {
            // 保存商品图片
            for (ProductImage image : product.getImages()) {
                image.setProductId(product.getId());
                productImageMapper.insert(image);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public boolean updateProduct(Product product) {
        boolean updated = updateById(product);
        if (updated && product.getImages() != null) {
            // 删除旧图片，保存新图片
            productImageMapper.deleteByProductId(product.getId());
            for (ProductImage image : product.getImages()) {
                image.setProductId(product.getId());
                productImageMapper.insert(image);
            }
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean deleteProduct(Integer productId) {
        // 删除商品图片
        productImageMapper.deleteByProductId(productId);
        return removeById(productId);
    }

    @Override
    public void increaseViewCount(Integer productId) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId)
               .setSql("view_count = view_count + 1");
        update(null, wrapper);
    }

    @Override
    public void increaseSales(Integer productId, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId)
               .setSql("sales_count = sales_count + " + quantity)
               .setSql("stock = stock - " + quantity);
        update(null, wrapper);
    }

    @Override
    public boolean updateStock(Integer productId, Integer stock) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId)
               .set(Product::getStock, stock);
        return update(null, wrapper);
    }

    @Override
    public boolean toggleOnSale(Integer productId, Boolean isOnSale) {
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId)
               .set(Product::getIsOnSale, isOnSale);
        return update(null, wrapper);
    }

    @Override
    public boolean checkStock(Integer productId, Integer quantity) {
        Product product = getById(productId);
        return product != null && product.getStock() >= quantity;
    }

    // ========== 供应商管理相关方法实现 ==========

    @Override
    public IPage<Product> getSupplierProductPage(Page<Product> page, Integer supplierId, Integer categoryId, String keyword, Boolean isOnSale, String sort) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        // 必须指定供应商ID
        wrapper.eq(Product::getSupplierId, supplierId);

        if (categoryId != null) {
            // 获取该分类及其所有子分类的ID列表
            List<Integer> categoryIds = getAllChildCategoryIds(categoryId);
            wrapper.in(Product::getCategoryId, categoryIds);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getProductName, keyword)
                   .or()
                   .like(Product::getSubTitle, keyword);
        }
        if (isOnSale != null) {
            wrapper.eq(Product::getIsOnSale, isOnSale);
        }

        // 处理排序
        applySort(wrapper, sort);

        IPage<Product> result = page(page, wrapper);
        loadMainImages(result.getRecords());
        return result;
    }

    @Override
    public boolean isProductBelongToSupplier(Integer productId, Integer supplierId) {
        Product product = getById(productId);
        return product != null && product.getSupplierId().equals(supplierId);
    }

    @Override
    public List<Product> getProductsBySupplierId(Integer supplierId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getSupplierId, supplierId)
               .orderByDesc(Product::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public java.util.Map<String, Object> getSupplierProductStatistics(Integer supplierId, boolean isAdmin) {
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        // 如果不是管理员，只统计指定供应商的商品
        if (!isAdmin && supplierId != null) {
            wrapper.eq(Product::getSupplierId, supplierId);
        }
        
        // 商品总数
        long totalCount = count(wrapper);
        statistics.put("totalCount", totalCount);
        
        // 在售商品数量（上架且库存大于0）
        LambdaQueryWrapper<Product> onSaleWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && supplierId != null) {
            onSaleWrapper.eq(Product::getSupplierId, supplierId);
        }
        onSaleWrapper.eq(Product::getIsOnSale, true)
                     .gt(Product::getStock, 0);
        long onSaleCount = count(onSaleWrapper);
        statistics.put("onSaleCount", onSaleCount);
        
        // 下架商品数量
        LambdaQueryWrapper<Product> offSaleWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && supplierId != null) {
            offSaleWrapper.eq(Product::getSupplierId, supplierId);
        }
        offSaleWrapper.eq(Product::getIsOnSale, false);
        long offSaleCount = count(offSaleWrapper);
        statistics.put("offSaleCount", offSaleCount);
        
        // 缺货商品数量（库存为0）
        LambdaQueryWrapper<Product> outOfStockWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && supplierId != null) {
            outOfStockWrapper.eq(Product::getSupplierId, supplierId);
        }
        outOfStockWrapper.le(Product::getStock, 0);
        long outOfStockCount = count(outOfStockWrapper);
        statistics.put("outOfStockCount", outOfStockCount);
        
        // 库存预警商品数量（库存小于等于10）
        LambdaQueryWrapper<Product> lowStockWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && supplierId != null) {
            lowStockWrapper.eq(Product::getSupplierId, supplierId);
        }
        lowStockWrapper.le(Product::getStock, 10)
                       .gt(Product::getStock, 0);
        long lowStockCount = count(lowStockWrapper);
        statistics.put("lowStockCount", lowStockCount);
        
        // 今日新增商品数量
        LambdaQueryWrapper<Product> todayWrapper = new LambdaQueryWrapper<>();
        if (!isAdmin && supplierId != null) {
            todayWrapper.eq(Product::getSupplierId, supplierId);
        }
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDateTime startOfDay = today.atStartOfDay();
        java.time.LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        todayWrapper.between(Product::getCreatedAt, startOfDay, endOfDay);
        long todayAddedCount = count(todayWrapper);
        statistics.put("todayAddedCount", todayAddedCount);

        return statistics;
    }

    /**
     * 获取指定分类及其所有子分类的ID列表
     * @param categoryId 分类ID
     * @return 包含该分类及其所有子分类的ID列表
     */
    private List<Integer> getAllChildCategoryIds(Integer categoryId) {
        List<Integer> result = new ArrayList<>();
        result.add(categoryId);

        // 递归获取所有子分类
        List<Integer> childIds = getChildCategoryIds(categoryId);
        result.addAll(childIds);

        return result;
    }

    /**
     * 递归获取指定分类的所有子分类ID
     * @param parentId 父分类ID
     * @return 所有子分类ID列表
     */
    private List<Integer> getChildCategoryIds(Integer parentId) {
        List<Integer> result = new ArrayList<>();

        // 获取直接子分类
        List<ProductCategory> children = productCategoryMapper.selectByParentId(parentId);

        for (ProductCategory child : children) {
            result.add(child.getId());
            // 递归获取子分类的子分类
            result.addAll(getChildCategoryIds(child.getId()));
        }

        return result;
    }
}

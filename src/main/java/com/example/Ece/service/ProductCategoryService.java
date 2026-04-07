package com.example.Ece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Ece.entity.ProductCategory;

import java.util.List;

/**
 * 商品分类Service接口
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 获取所有显示的分类
     */
    List<ProductCategory> getAllShowCategories();

    /**
     * 根据父ID获取子分类
     */
    List<ProductCategory> getCategoriesByParentId(Integer parentId);

    /**
     * 获取分类树
     */
    List<ProductCategory> getCategoryTree();

    /**
     * 根据ID获取分类
     */
    ProductCategory getCategoryById(Integer id);

    /**
     * 添加分类
     */
    boolean addCategory(ProductCategory category);

    /**
     * 更新分类
     */
    boolean updateCategory(ProductCategory category);

    /**
     * 删除分类
     */
    boolean deleteCategory(Integer id);
}

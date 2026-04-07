package com.example.Ece.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Ece.entity.ProductCategory;
import com.example.Ece.mapper.ProductCategoryMapper;
import com.example.Ece.service.ProductCategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类Service实现类
 */
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    @Override
    public List<ProductCategory> getAllShowCategories() {
        return baseMapper.selectAllShowCategories();
    }

    @Override
    public List<ProductCategory> getCategoriesByParentId(Integer parentId) {
        return baseMapper.selectByParentId(parentId);
    }

    @Override
    public List<ProductCategory> getCategoryTree() {
        List<ProductCategory> allCategories = getAllShowCategories();
        return buildCategoryTree(allCategories, 0);
    }

    /**
     * 构建分类树
     */
    private List<ProductCategory> buildCategoryTree(List<ProductCategory> categories, Integer parentId) {
        List<ProductCategory> tree = new ArrayList<>();
        for (ProductCategory category : categories) {
            if (category.getParentId().equals(parentId)) {
                category.setChildren(buildCategoryTree(categories, category.getId()));
                tree.add(category);
            }
        }
        return tree;
    }

    @Override
    public ProductCategory getCategoryById(Integer id) {
        return getById(id);
    }

    @Override
    public boolean addCategory(ProductCategory category) {
        return save(category);
    }

    @Override
    public boolean updateCategory(ProductCategory category) {
        return updateById(category);
    }

    @Override
    public boolean deleteCategory(Integer id) {
        // 检查是否有子分类
        List<ProductCategory> children = getCategoriesByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("该分类下存在子分类，无法删除");
        }
        return removeById(id);
    }
}

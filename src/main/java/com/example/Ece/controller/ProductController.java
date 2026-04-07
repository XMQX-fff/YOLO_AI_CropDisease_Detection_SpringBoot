package com.example.Ece.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.Product;
import com.example.Ece.entity.ProductCategory;
import com.example.Ece.service.ProductCategoryService;
import com.example.Ece.service.ProductFavoriteService;
import com.example.Ece.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品Controller
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryService categoryService;

    @Autowired
    private ProductFavoriteService favoriteService;

    @GetMapping
    public Result<IPage<Product>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isOnSale,
            @RequestParam(required = false) String sort) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        IPage<Product> result = productService.getProductPage(page, categoryId, keyword, isOnSale, sort);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        Product product = productService.getProductDetail(id, userId);
        if (product != null) {
            productService.increaseViewCount(id);
            return Result.success(product);
        }
        return Result.error("商品不存在");
    }

    @GetMapping("/recommend")
    public Result<List<Product>> recommend(
            @RequestParam(defaultValue = "8") Integer limit) {
        List<Product> products = productService.getRecommendProducts(limit);
        return Result.success(products);
    }

    @GetMapping("/hot")
    public Result<List<Product>> hot(
            @RequestParam(defaultValue = "8") Integer limit) {
        List<Product> products = productService.getHotProducts(limit);
        return Result.success(products);
    }

    @GetMapping("/new")
    public Result<List<Product>> newProducts(
            @RequestParam(defaultValue = "8") Integer limit) {
        List<Product> products = productService.getNewProducts(limit);
        return Result.success(products);
    }

    @PostMapping
    public Result<Boolean> add(@RequestBody Product product) {
        boolean success = productService.addProduct(product);
        return success ? Result.success(true) : Result.error("添加失败");
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Integer id, @RequestBody Product product) {
        product.setId(id);
        boolean success = productService.updateProduct(product);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean success = productService.deleteProduct(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    @PutMapping("/{id}/status")
    public Result<Boolean> toggleStatus(
            @PathVariable Integer id,
            @RequestParam Boolean isOnSale) {
        boolean success = productService.toggleOnSale(id, isOnSale);
        return success ? Result.success(true) : Result.error("操作失败");
    }

    @GetMapping("/category/list")
    public Result<?> categoryList() {
        return Result.success(categoryService.getCategoryTree());
    }

    @PutMapping("/category/{id}/status")
    public Result<Boolean> updateCategoryStatus(
            @PathVariable Integer id,
            @RequestParam Boolean isShow) {
        ProductCategory category = categoryService.getById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        category.setIsShow(isShow);
        boolean success = categoryService.updateById(category);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    @GetMapping("/{id}/stock")
    public Result<Boolean> checkStock(
            @PathVariable Integer id,
            @RequestParam Integer quantity) {
        boolean hasStock = productService.checkStock(id, quantity);
        return Result.success(hasStock);
    }
}

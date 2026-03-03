package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.Product;
import com.example.Ece.mapper.ProductMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Resource
    ProductMapper productMapper;

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "0") Integer userId) {
        LambdaQueryWrapper<Product> wrapper = Wrappers.<Product>lambdaQuery();
        
        // 根据userId过滤产品（匹配supplier_id）
        if (userId != 0) {
            wrapper.eq(Product::getSupplierId, userId);
        }
        
        // 搜索条件
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Product::getProductName, search);
        }
        
        // 排序
        wrapper.orderByDesc(Product::getId);
        
        // 分页查询
        Page<Product> productPage = productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(productPage);
    }

    @PostMapping
    public Result<?> save(@RequestBody Product product) {
        productMapper.insert(product);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Product product) {
        productMapper.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        productMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return Result.success(productMapper.selectById(id));
    }
}
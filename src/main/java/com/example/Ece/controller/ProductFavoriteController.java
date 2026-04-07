package com.example.Ece.controller;

import com.example.Ece.common.Result;
import com.example.Ece.entity.ProductFavorite;
import com.example.Ece.service.ProductFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品收藏Controller
 */
@RestController
@RequestMapping("/favorite")
public class ProductFavoriteController {

    @Autowired
    private ProductFavoriteService favoriteService;
    @GetMapping
    public Result<List<ProductFavorite>> list(@RequestParam Integer userId) {
        List<ProductFavorite> favorites = favoriteService.getFavoritesByUserId(userId);
        return Result.success(favorites);
    }
    @PostMapping
    public Result<Boolean> add(
            @RequestParam Integer userId,
            @RequestParam Integer productId) {
        boolean success = favoriteService.addFavorite(userId, productId);
        return Result.success(success);
    }
    @DeleteMapping
    public Result<Boolean> remove(
            @RequestParam Integer userId,
            @RequestParam Integer productId) {
        boolean success = favoriteService.removeFavorite(userId, productId);
        return Result.success(success);
    }
    @PostMapping("/toggle")
    public Result<Boolean> toggle(
            @RequestParam Integer userId,
            @RequestParam Integer productId) {
        boolean success = favoriteService.toggleFavorite(userId, productId);
        return Result.success(success);
    }
    @GetMapping("/check")
    public Result<Boolean> check(
            @RequestParam Integer userId,
            @RequestParam Integer productId) {
        boolean isFavorite = favoriteService.isFavorite(userId, productId);
        return Result.success(isFavorite);
    }
    @GetMapping("/count")
    public Result<Long> getCount(@RequestParam Integer userId) {
        Long count = favoriteService.getFavoriteCount(userId);
        return Result.success(count);
    }
}

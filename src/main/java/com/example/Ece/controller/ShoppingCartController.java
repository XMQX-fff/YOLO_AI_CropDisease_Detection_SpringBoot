package com.example.Ece.controller;

import com.example.Ece.common.Result;
import com.example.Ece.entity.ShoppingCart;
import com.example.Ece.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车Controller
 */
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService cartService;
    @GetMapping
    public Result<List<ShoppingCart>> list(@RequestParam Integer userId) {
        List<ShoppingCart> cartList = cartService.getCartByUserId(userId);
        return Result.success(cartList);
    }
    @PostMapping
    public Result<Boolean> add(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            boolean success = cartService.addToCart(userId, productId, quantity);
            return Result.success(success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    @PutMapping("/{cartId}")
    public Result<Boolean> updateQuantity(
            @PathVariable Integer cartId,
            @RequestParam Integer quantity) {
        try {
            boolean success = cartService.updateQuantity(cartId, quantity);
            return Result.success(success);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    @DeleteMapping("/{cartId}")
    public Result<Boolean> delete(@PathVariable Integer cartId) {
        boolean success = cartService.removeFromCart(cartId);
        return Result.success(success);
    }
    @DeleteMapping("/batch")
    public Result<Boolean> batchDelete(@RequestParam List<Integer> cartIds) {
        boolean success = cartService.batchRemove(cartIds);
        return Result.success(success);
    }
    @DeleteMapping("/clear")
    public Result<Boolean> clear(@RequestParam Integer userId) {
        boolean success = cartService.clearCart(userId);
        return Result.success(success);
    }
    @PutMapping("/{cartId}/select")
    public Result<Boolean> toggleSelected(
            @PathVariable Integer cartId,
            @RequestParam Boolean selected) {
        boolean success = cartService.toggleSelected(cartId, selected);
        return Result.success(success);
    }
    @PutMapping("/selectAll")
    public Result<Boolean> toggleAllSelected(
            @RequestParam Integer userId,
            @RequestParam Boolean selected) {
        boolean success = cartService.toggleAllSelected(userId, selected);
        return Result.success(success);
    }
    @GetMapping("/selected")
    public Result<List<ShoppingCart>> getSelected(@RequestParam Integer userId) {
        List<ShoppingCart> cartList = cartService.getSelectedCart(userId);
        return Result.success(cartList);
    }
    @GetMapping("/count")
    public Result<Long> getCount(@RequestParam Integer userId) {
        Long count = cartService.getCartCount(userId);
        return Result.success(count);
    }
    @GetMapping("/total")
    public Result<BigDecimal> calculateTotal(@RequestParam Integer userId) {
        BigDecimal total = cartService.calculateTotal(userId);
        return Result.success(total);
    }
}

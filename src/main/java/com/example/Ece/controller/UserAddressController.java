package com.example.Ece.controller;

import com.example.Ece.common.Result;
import com.example.Ece.entity.UserAddress;
import com.example.Ece.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户收货地址Controller
 */
@RestController
@RequestMapping("/address")
public class UserAddressController {

    @Autowired
    private UserAddressService addressService;
    @GetMapping
    public Result<List<UserAddress>> list(@RequestParam Integer userId) {
        List<UserAddress> addresses = addressService.getAddressByUserId(userId);
        return Result.success(addresses);
    }
    @GetMapping("/default")
    public Result<UserAddress> getDefault(@RequestParam Integer userId) {
        UserAddress address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }
    @PostMapping
    public Result<Boolean> add(@RequestBody UserAddress address) {
        boolean success = addressService.addAddress(address);
        return Result.success(success);
    }
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Integer id, @RequestBody UserAddress address) {
        address.setId(id);
        boolean success = addressService.updateAddress(address);
        return Result.success(success);
    }
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        boolean success = addressService.deleteAddress(id);
        return Result.success(success);
    }
    @PutMapping("/{id}/default")
    public Result<Boolean> setDefault(
            @RequestParam Integer userId,
            @PathVariable Integer id) {
        boolean success = addressService.setDefaultAddress(userId, id);
        return Result.success(success);
    }
    @GetMapping("/{id}")
    public Result<UserAddress> detail(@PathVariable Integer id) {
        UserAddress address = addressService.getAddressById(id);
        if (address != null) {
            return Result.success(address);
        }
        return Result.error("地址不存在");
    }
}

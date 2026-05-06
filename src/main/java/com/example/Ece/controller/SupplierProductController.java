package com.example.Ece.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.entity.Product;
import com.example.Ece.entity.SupplierInfo;
import com.example.Ece.entity.User;
import com.example.Ece.service.ProductService;
import com.example.Ece.service.SupplierInfoService;
import com.example.Ece.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 供应商商品管理Controller
 * 供应商只能管理自己旗下的商品，管理员可以管理所有商品
 */
@RestController
@RequestMapping("/supplier/product")
public class SupplierProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierInfoService supplierInfoService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 从请求中获取当前登录用户
     * 优先从request属性中获取（由AuthInterceptor设置）
     * 如果为空，则尝试从JWT Token中解析
     */
    private User getCurrentUser(HttpServletRequest request) {
        // 首先尝试从request属性中获取（AuthInterceptor已设置）
        User currentUser = (User) request.getAttribute("currentUser");
        
        // 如果request中没有，尝试从JWT Token中解析
        if (currentUser == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && !authHeader.isEmpty()) {
                String token = jwtUtils.extractTokenFromHeader(authHeader);
                if (token != null && jwtUtils.validateToken(token)) {
                    Integer userId = jwtUtils.getUserIdFromToken(token);
                    String username = jwtUtils.getUsernameFromToken(token);
                    String role = jwtUtils.getRoleFromToken(token);
                    
                    if (userId != null) {
                        currentUser = new User();
                        currentUser.setId(userId);
                        currentUser.setUsername(username);
                        currentUser.setRole(role);
                    }
                }
            }
        }
        
        return currentUser;
    }

    /**
     * 获取当前登录用户的供应商ID
     */
    private Integer getCurrentSupplierId(HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (currentUser == null) {
            return null;
        }
        // 如果是管理员，返回null表示可以查看所有
        if ("admin".equals(currentUser.getRole())) {
            return null;
        }
        // 根据用户ID获取供应商信息
        if(currentUser.getRole().equals("supplier")){
            return currentUser.getId();
        }
        return null;
    }

    /**
     * 检查当前用户是否是管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        return currentUser != null && "admin".equals(currentUser.getRole());
    }

    /**
     * 检查用户是否已登录
     */
    private boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    /**
     * 获取商品列表（供应商只能看自己，管理员看所有）
     */
    @GetMapping("/list")
    public Result<IPage<Product>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isOnSale,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) String sort,
            HttpServletRequest request) {

        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }

        Page<Product> page = new Page<>(pageNum, pageSize);
        IPage<Product> result;

        // 如果是管理员且指定了供应商ID，则查看该供应商的商品
        if (isAdmin(request)) {
            if (supplierId != null) {
                result = productService.getSupplierProductPage(page, supplierId, categoryId, keyword, isOnSale, sort);
            } else {
                // 管理员查看所有商品
                result = productService.getProductPage(page, categoryId, keyword, isOnSale, sort);
            }
        } else {
            // 供应商只能查看自己的商品
            Integer currentSupplierId = getCurrentSupplierId(request);
            if (currentSupplierId == null) {
                return Result.error("403", "您不是供应商，无法查看商品");
            }
            result = productService.getSupplierProductPage(page, currentSupplierId, categoryId, keyword, isOnSale, sort);
        }

        return Result.success(result);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Integer id, HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }
        
        Product product = productService.getProductDetail(id, null);
        if (product == null) {
            return Result.error("404", "商品不存在");
        }
        
        // 如果不是管理员，检查商品是否属于当前供应商
        if (!isAdmin(request)) {
            Integer currentSupplierId = getCurrentSupplierId(request);
            if (currentSupplierId == null || !currentSupplierId.equals(product.getSupplierId())) {
                return Result.error("403", "您无权查看此商品");
            }
        }
        
        return Result.success(product);
    }

    /**
     * 添加商品
     */
    @PostMapping
    public Result<Boolean> add(@RequestBody Product product, HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }
        
        // 如果不是管理员，设置当前供应商ID
        if (!isAdmin(request)) {
            Integer currentSupplierId = getCurrentSupplierId(request);
            if (currentSupplierId == null) {
                return Result.error("403", "您不是供应商，无法添加商品");
            }
            product.setSupplierId(currentSupplierId);
        }
        
        boolean success = productService.addProduct(product);
        return success ? Result.success(true) : Result.error("添加失败");
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Integer id, @RequestBody Product product, HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }
        
        // 检查权限
        if (!isAdmin(request)) {
            Integer currentSupplierId = getCurrentSupplierId(request);
            if (currentSupplierId == null) {
                return Result.error("403", "您不是供应商，无法更新商品");
            }
            // 检查商品是否属于当前供应商
            if (!productService.isProductBelongToSupplier(id, currentSupplierId)) {
                return Result.error("403", "您无权更新此商品");
            }
            // 确保供应商不能修改商品的供应商ID
            product.setSupplierId(null);
        }
        
        product.setId(id);
        boolean success = productService.updateProduct(product);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id, HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }
        
        // 检查权限
        if (!isAdmin(request)) {
            Integer currentSupplierId = getCurrentSupplierId(request);
            if (currentSupplierId == null) {
                return Result.error("403", "您不是供应商，无法删除商品");
            }
            // 检查商品是否属于当前供应商
            if (!productService.isProductBelongToSupplier(id, currentSupplierId)) {
                return Result.error("403", "您无权删除此商品");
            }
        }
        
        boolean success = productService.deleteProduct(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    /**
     * 上架/下架商品
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> toggleStatus(
            @PathVariable Integer id,
            @RequestParam Boolean isOnSale,
            HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }
        
        // 检查权限
        if (!isAdmin(request)) {
            Integer currentSupplierId = getCurrentSupplierId(request);
            if (currentSupplierId == null) {
                return Result.error("403", "您不是供应商，无法操作商品");
            }
            // 检查商品是否属于当前供应商
            if (!productService.isProductBelongToSupplier(id, currentSupplierId)) {
                return Result.error("403", "您无权操作此商品");
            }
        }
        
        boolean success = productService.toggleOnSale(id, isOnSale);
        return success ? Result.success(true) : Result.error("操作失败");
    }

    /**
     * 更新库存
     */
    @PutMapping("/{id}/stock")
    public Result<Boolean> updateStock(
            @PathVariable Integer id,
            @RequestParam Integer stock,
            HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }
        
        // 检查权限
        if (!isAdmin(request)) {
            Integer currentSupplierId = getCurrentSupplierId(request);
            if (currentSupplierId == null) {
                return Result.error("403", "您不是供应商，无法更新库存");
            }
            // 检查商品是否属于当前供应商
            if (!productService.isProductBelongToSupplier(id, currentSupplierId)) {
                return Result.error("403", "您无权更新此商品库存");
            }
        }
        
        boolean success = productService.updateStock(id, stock);
        return success ? Result.success(true) : Result.error("更新失败");
    }

    /**
     * 获取供应商商品统计信息
     * 返回当前供应商名下的商品总数、在售商品数量、缺货商品数量等
     */
    @GetMapping("/statistics")
    public Result<java.util.Map<String, Object>> getStatistics(HttpServletRequest request) {
        // 检查用户是否已登录
        if (!isAuthenticated(request)) {
            return Result.error("401", "请先登录");
        }

        Integer supplierId = null;
        boolean isAdminUser = isAdmin(request);

        // 如果不是管理员，获取当前供应商ID
        if (!isAdminUser) {
            supplierId = getCurrentSupplierId(request);
            if (supplierId == null) {
                return Result.error("403", "您不是供应商，无法查看统计信息");
            }
        }

        // 获取统计数据
        java.util.Map<String, Object> statistics = productService.getSupplierProductStatistics(supplierId, isAdminUser);
        return Result.success(statistics);
    }
}

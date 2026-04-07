package com.example.Ece.util;

import com.example.Ece.entity.User;

/**
 * 用户上下文工具类
 * 用于获取当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    /**
     * 设置当前用户
     */
    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    /**
     * 获取当前用户
     */
    public static User getCurrentUser() {
        return currentUser.get();
    }

    /**
     * 清除当前用户
     */
    public static void clear() {
        currentUser.remove();
    }

    /**
     * 获取当前用户ID
     */
    public static Integer getCurrentUserId() {
        User user = currentUser.get();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前用户角色
     */
    public static String getCurrentUserRole() {
        User user = currentUser.get();
        return user != null ? user.getRole() : null;
    }

    /**
     * 检查当前用户是否是管理员
     */
    public static boolean isAdmin() {
        String role = getCurrentUserRole();
        return "admin".equals(role);
    }

    /**
     * 检查当前用户是否是普通用户
     */
    public static boolean isCommonUser() {
        String role = getCurrentUserRole();
        return "common".equals(role);
    }

    /**
     * 检查当前用户是否是供应商
     */
    public static boolean isSupplier() {
        String role = getCurrentUserRole();
        return "supplier".equals(role);
    }
}

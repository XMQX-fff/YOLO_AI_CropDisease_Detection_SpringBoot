package com.example.Ece.service;

/**
 * 数据访问控制服务接口
 */
public interface DataAccessControlService {

    /**
     * 检查用户是否有权限访问资源
     *
     * @param userId       用户ID
     * @param userRole     用户角色
     * @param resourceType 资源类型
     * @param action       操作类型
     * @param resourceId   资源ID（可选）
     * @return 是否有权限
     */
    boolean checkPermission(Integer userId, String userRole, String resourceType, String action, Integer resourceId);

    /**
     * 检查用户是否是资源的所有者
     *
     * @param userId       用户ID
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @return 是否是所有者
     */
    boolean isResourceOwner(Integer userId, String resourceType, Integer resourceId);

    /**
     * 为查询添加用户ID过滤条件
     *
     * @param userId       用户ID
     * @param userRole     用户角色
     * @param resourceType 资源类型
     * @return 用户ID过滤条件（null表示不需要过滤）
     */
    Integer getUserIdFilter(Integer userId, String userRole, String resourceType);
}

package com.example.Ece.service;

import com.example.Ece.entity.User;

/**
 * 数据访问审计服务接口
 */
public interface DataAccessAuditService {

    /**
     * 记录数据访问日志
     *
     * @param user         操作用户
     * @param action       操作类型
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @param accessType   访问类型：OWN/ADMIN/UNAUTHORIZED
     * @param errorMessage 错误信息
     */
    void logAccess(User user, String action, String resourceType, Integer resourceId, 
                   String accessType, String errorMessage);

    /**
     * 异步记录访问日志
     */
    void logAccessAsync(User user, String action, String resourceType, Integer resourceId,
                        String accessType, String errorMessage);
}

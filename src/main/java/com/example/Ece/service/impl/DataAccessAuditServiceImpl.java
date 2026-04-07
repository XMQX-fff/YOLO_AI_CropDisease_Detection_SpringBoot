package com.example.Ece.service.impl;

import com.example.Ece.entity.DataAccessAuditLog;
import com.example.Ece.entity.User;
import com.example.Ece.mapper.DataAccessAuditLogMapper;
import com.example.Ece.service.DataAccessAuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 数据访问审计服务实现类
 */
@Service
@Slf4j
public class DataAccessAuditServiceImpl implements DataAccessAuditService {

    @Autowired
    private DataAccessAuditLogMapper auditLogMapper;

    @Override
    public void logAccess(User user, String action, String resourceType, Integer resourceId,
                          String accessType, String errorMessage) {
        try {
            DataAccessAuditLog auditLog = new DataAccessAuditLog();
            
            if (user != null) {
                auditLog.setUserId(user.getId());
                auditLog.setUsername(user.getUsername());
                auditLog.setUserRole(user.getRole());
            } else {
                auditLog.setUserId(0);
                auditLog.setUsername("anonymous");
                auditLog.setUserRole("unknown");
            }
            
            auditLog.setAction(action);
            auditLog.setResourceType(resourceType);
            auditLog.setResourceId(resourceId);
            auditLog.setAccessType(accessType);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setCreateTime(LocalDateTime.now());

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                auditLog.setRequestIp(getClientIp(request));
                auditLog.setRequestUrl(request.getRequestURI());
                auditLog.setRequestMethod(request.getMethod());
                auditLog.setRequestParams(request.getQueryString());
            }

            auditLogMapper.insert(auditLog);
            
            log.debug("数据访问审计日志已记录：userId={}, action={}, resourceType={}",
                    auditLog.getUserId(), action, resourceType);
                    
        } catch (Exception e) {
            log.error("记录数据访问审计日志失败", e);
        }
    }

    @Override
    @Async
    public void logAccessAsync(User user, String action, String resourceType, Integer resourceId,
                               String accessType, String errorMessage) {
        logAccess(user, action, resourceType, resourceId, accessType, errorMessage);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

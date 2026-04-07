package com.example.Ece.service.impl;

import com.example.Ece.mapper.*;
import com.example.Ece.service.DataAccessControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据访问控制服务实现类
 */
@Service
@Slf4j
public class DataAccessControlServiceImpl implements DataAccessControlService {

    @Autowired
    private GreenhouseMapper greenhouseMapper;

    @Autowired
    private ImgRecordsMapper imgRecordsMapper;

    @Autowired
    private VideoRecordsMapper videoRecordsMapper;

    @Autowired
    private CameraRecordsMapper cameraRecordsMapper;

    @Autowired
    private GreenhouseAlertHistoryMapper alertHistoryMapper;

    @Autowired
    private GreenhouseAlertRuleMapper alertRuleMapper;

    @Override
    public boolean checkPermission(Integer userId, String userRole, String resourceType, String action, Integer resourceId) {
        // 管理员拥有所有权限
        if ("admin".equals(userRole)) {
            return true;
        }

        // 普通用户只能访问自己的数据
        if ("common".equals(userRole)) {
            if (resourceId == null) {
                // 查询操作，返回true，由查询条件过滤
                return true;
            }
            // 检查是否是资源所有者
            return isResourceOwner(userId, resourceType, resourceId);
        }

        // 供应商角色根据具体业务逻辑判断
        if ("supplier".equals(userRole)) {
            // 供应商可能需要访问某些公共数据
            return true;
        }

        return false;
    }

    @Override
    public boolean isResourceOwner(Integer userId, String resourceType, Integer resourceId) {
        if (userId == null || resourceId == null) {
            return false;
        }

        try {
            switch (resourceType) {
                case "greenhouse":
                    return greenhouseMapper.checkUserOwnership(resourceId, userId) > 0;
                case "imgrecords":
                    return imgRecordsMapper.checkUserOwnership(resourceId, userId) > 0;
                case "videorecords":
                    return videoRecordsMapper.checkUserOwnership(resourceId, userId) > 0;
                case "camerarecords":
                    return cameraRecordsMapper.checkUserOwnership(resourceId, userId) > 0;
                case "greenhouse_alert_history":
                    return alertHistoryMapper.checkUserOwnership(resourceId, userId) > 0;
                case "greenhouse_alert_rule":
                    return alertRuleMapper.checkUserOwnership(resourceId, userId) > 0;
                default:
                    log.warn("未知的资源类型：{}", resourceType);
                    return false;
            }
        } catch (Exception e) {
            log.error("检查资源所有权失败：resourceType={}, resourceId={}, userId={}", 
                    resourceType, resourceId, userId, e);
            return false;
        }
    }

    @Override
    public Integer getUserIdFilter(Integer userId, String userRole, String resourceType) {
        // 管理员不需要过滤
        if ("admin".equals(userRole)) {
            return null;
        }

        // 普通用户只能看到自己的数据
        if ("common".equals(userRole)) {
            return userId;
        }

        // 供应商根据业务需求决定
        if ("supplier".equals(userRole)) {
            // 供应商可能需要看到所有数据或特定数据
            return null;
        }

        return userId;
    }
}

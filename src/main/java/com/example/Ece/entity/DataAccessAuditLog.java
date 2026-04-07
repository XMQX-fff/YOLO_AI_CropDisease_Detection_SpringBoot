package com.example.Ece.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据访问审计日志实体类
 */
@Data
@TableName("data_access_audit_log")
public class DataAccessAuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userId;

    private String username;

    private String userRole;

    private String action;

    private String resourceType;

    private Integer resourceId;

    private String accessType;

    private String requestIp;

    private String requestUrl;

    private String requestMethod;

    private String requestParams;

    private Integer responseStatus;

    private Integer executionTime;

    private String errorMessage;

    private LocalDateTime createTime;
}

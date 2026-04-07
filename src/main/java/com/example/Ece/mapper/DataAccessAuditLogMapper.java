package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.DataAccessAuditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据访问审计日志Mapper接口
 */
@Mapper
public interface DataAccessAuditLogMapper extends BaseMapper<DataAccessAuditLog> {
}

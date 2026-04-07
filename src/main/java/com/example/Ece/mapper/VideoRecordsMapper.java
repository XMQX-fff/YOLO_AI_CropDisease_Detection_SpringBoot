package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.VideoRecords;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface VideoRecordsMapper extends BaseMapper<VideoRecords> {
    
    /**
     * 检查用户是否是记录的所有者
     */
    @Select("SELECT COUNT(*) FROM videorecords WHERE id = #{recordId} AND user_id = #{userId}")
    int checkUserOwnership(@Param("recordId") Integer recordId, @Param("userId") Integer userId);
}

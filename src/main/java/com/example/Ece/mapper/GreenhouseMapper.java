package com.example.Ece.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Ece.entity.Greenhouse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface GreenhouseMapper extends BaseMapper<Greenhouse> {
    @Select("select * from greenhouse where greenhouse_name = #{greenhouseName}")
    Greenhouse selectByGreenhouseName(String greenhouseName);
    
    /**
     * 检查用户是否是温室的所有者
     */
    @Select("SELECT COUNT(*) FROM greenhouse WHERE id = #{greenhouseId} AND user_id = #{userId}")
    int checkUserOwnership(@Param("greenhouseId") Integer greenhouseId, @Param("userId") Integer userId);
} 
package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.CameraRecordsDTO;
import com.example.Ece.entity.CameraRecords;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.entity.User;
import com.example.Ece.mapper.CameraRecordsMapper;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import com.example.Ece.util.UserContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/cameraRecords")
public class CameraRecordsController {
    @Resource
    CameraRecordsMapper cameraRecordsMapper;
    @Resource
    GreenhouseDetectionMapper greenhouseDetectionMapper;

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        return UserContext.getCurrentUser();
    }

    /**
     * 检查是否是管理员
     */
    private boolean isAdmin(User user) {
        return user != null && "admin".equals(user.getRole());
    }

    @GetMapping("/all")
    public Result<?> GetAll() {
        User currentUser = getCurrentUser();
        LambdaQueryWrapper<CameraRecords> wrapper = Wrappers.<CameraRecords>lambdaQuery();
        
        // 非管理员只能查看自己的数据
        if (!isAdmin(currentUser)) {
            wrapper.eq(CameraRecords::getUserId, currentUser.getId());
        }
        
        return Result.success(cameraRecordsMapper.selectList(wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        User currentUser = getCurrentUser();
        CameraRecords record = cameraRecordsMapper.selectById(id);
        
        if (record == null) {
            return Result.error("-1", "记录不存在");
        }
        
        // 非管理员只能查看自己的数据
        if (!isAdmin(currentUser) && !currentUser.getId().equals(record.getUserId())) {
            return Result.error("-1", "您没有权限访问此记录");
        }
        
        return Result.success(record);
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search3,
                              @RequestParam(defaultValue = "") String search2) {
        User currentUser = getCurrentUser();
        LambdaQueryWrapper<CameraRecords> wrapper = Wrappers.<CameraRecords>lambdaQuery();
        wrapper.orderByDesc(CameraRecords::getStartTime);
        
        // 非管理员只能查看自己的数据
        if (!isAdmin(currentUser)) {
            wrapper.eq(CameraRecords::getUserId, currentUser.getId());
        }
        
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(CameraRecords::getUsername, search);
        }
        if (StrUtil.isNotBlank(search1)) {
            wrapper.like(CameraRecords::getKind, search1);
        }
        if (StrUtil.isNotBlank(search2)) {
            wrapper.like(CameraRecords::getWeight, search2);
        }
        if (StrUtil.isNotBlank(search3)) {
            wrapper.like(CameraRecords::getConf, search3);
        }
        Page<CameraRecords> Page = cameraRecordsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(Page);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        User currentUser = getCurrentUser();
        CameraRecords existing = cameraRecordsMapper.selectById(id);
        
        if (existing == null) {
            return Result.error("-1", "记录不存在");
        }
        
        // 非管理员只能删除自己的数据
        if (!isAdmin(currentUser) && !currentUser.getId().equals(existing.getUserId())) {
            return Result.error("-1", "您没有权限删除此记录");
        }
        
        cameraRecordsMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> updates(@RequestBody CameraRecords cameraRecords) {
        User currentUser = getCurrentUser();
        CameraRecords existing = cameraRecordsMapper.selectById(cameraRecords.getId());
        
        if (existing == null) {
            return Result.error("-1", "记录不存在");
        }
        
        // 非管理员只能修改自己的数据
        if (!isAdmin(currentUser) && !currentUser.getId().equals(existing.getUserId())) {
            return Result.error("-1", "您没有权限修改此记录");
        }
        
        cameraRecordsMapper.updateById(cameraRecords);
        return Result.success();
    }


    @PostMapping
    @Transactional
    public Result<?> save(@RequestBody CameraRecordsDTO cameraRecordsDTO) {
        User currentUser = getCurrentUser();
        System.out.println(cameraRecordsDTO);
        
        CameraRecords cameraRecords = new CameraRecords();
        cameraRecords.setWeight(cameraRecordsDTO.getWeight());
        cameraRecords.setOutVideo(cameraRecordsDTO.getOutVideo());
        cameraRecords.setConf(cameraRecordsDTO.getConf());
        cameraRecords.setUsername(cameraRecordsDTO.getUsername());
        cameraRecords.setKind(cameraRecordsDTO.getKind());
        cameraRecords.setStartTime(cameraRecordsDTO.getStartTime());
        
        // 设置用户ID
        cameraRecords.setUserId(currentUser.getId());
        
        cameraRecordsMapper.insert(cameraRecords);
        
        if (cameraRecordsDTO.getGreenhouseId() != null) {
            GreenhouseDetection detection = new GreenhouseDetection();
            detection.setGreenhouseId(cameraRecordsDTO.getGreenhouseId());
            detection.setDetectionType("camera");
            detection.setDetectionId(cameraRecords.getId());
            detection.setDiseaseCount(0);
            if (cameraRecordsDTO.getKind() != null && !cameraRecordsDTO.getKind().isEmpty()) {
                String[] kinds = cameraRecordsDTO.getKind().split(",");
                detection.setDiseaseCount(kinds.length);
                detection.setDiseaseNames(cameraRecordsDTO.getKind());
            }
            detection.setRecordTime(LocalDateTime.now());
            greenhouseDetectionMapper.insert(detection);
        }
        
        return Result.success();
    }
}

package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.ImgRecordsDTO;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.entity.ImgRecords;
import com.example.Ece.entity.User;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import com.example.Ece.mapper.ImgRecordsMapper;
import com.example.Ece.util.UserContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/imgRecords")
public class ImgRecordsController {
    @Resource
    ImgRecordsMapper imgRecordsMapper;
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
        LambdaQueryWrapper<ImgRecords> wrapper = Wrappers.<ImgRecords>lambdaQuery();
        
        // 非管理员只能查看自己的数据
        if (!isAdmin(currentUser)) {
            wrapper.eq(ImgRecords::getUserId, currentUser.getId());
        }
        
        return Result.success(imgRecordsMapper.selectList(wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        User currentUser = getCurrentUser();
        ImgRecords record = imgRecordsMapper.selectById(id);
        
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
        LambdaQueryWrapper<ImgRecords> wrapper = Wrappers.<ImgRecords>lambdaQuery();
        wrapper.orderByDesc(ImgRecords::getStartTime);
        
        // 非管理员只能查看自己的数据
        if (!isAdmin(currentUser)) {
            wrapper.eq(ImgRecords::getUserId, currentUser.getId());
        }
        
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(ImgRecords::getUsername, search);
        }
        if (StrUtil.isNotBlank(search1)) {
            wrapper.like(ImgRecords::getKind, search1);
        }
        if (StrUtil.isNotBlank(search2)) {
            wrapper.like(ImgRecords::getLabel, search2);
        }
        if (StrUtil.isNotBlank(search3)) {
            wrapper.like(ImgRecords::getConf, search3);
        }
        Page<ImgRecords> Page = imgRecordsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(Page);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        User currentUser = getCurrentUser();
        ImgRecords existing = imgRecordsMapper.selectById(id);
        
        if (existing == null) {
            return Result.error("-1", "记录不存在");
        }
        
        // 非管理员只能删除自己的数据
        if (!isAdmin(currentUser) && !currentUser.getId().equals(existing.getUserId())) {
            return Result.error("-1", "您没有权限删除此记录");
        }
        
        imgRecordsMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> updates(@RequestBody ImgRecords imgrecords) {
        User currentUser = getCurrentUser();
        ImgRecords existing = imgRecordsMapper.selectById(imgrecords.getId());
        
        if (existing == null) {
            return Result.error("-1", "记录不存在");
        }
        
        // 非管理员只能修改自己的数据
        if (!isAdmin(currentUser) && !currentUser.getId().equals(existing.getUserId())) {
            return Result.error("-1", "您没有权限修改此记录");
        }
        
        imgRecordsMapper.updateById(imgrecords);
        return Result.success();
    }


    @PostMapping
    @Transactional
    public Result<?> save(@RequestBody ImgRecordsDTO imgRecordsDTO) {
        User currentUser = getCurrentUser();
        System.out.println(imgRecordsDTO);
        
        ImgRecords imgrecords = new ImgRecords();
        imgrecords.setWeight(imgRecordsDTO.getWeight());
        imgrecords.setInputImg(imgRecordsDTO.getInputImg());
        imgrecords.setOutImg(imgRecordsDTO.getOutImg());
        imgrecords.setConfidence(imgRecordsDTO.getConfidence());
        imgrecords.setAllTime(imgRecordsDTO.getAllTime());
        imgrecords.setConf(imgRecordsDTO.getConf());
        imgrecords.setLabel(imgRecordsDTO.getLabel());
        imgrecords.setUsername(imgRecordsDTO.getUsername());
        imgrecords.setKind(imgRecordsDTO.getKind());
        imgrecords.setStartTime(imgRecordsDTO.getStartTime());
        
        // 设置用户ID
        imgrecords.setUserId(currentUser.getId());
        
        imgRecordsMapper.insert(imgrecords);
        
        if (imgRecordsDTO.getGreenhouseId() != null) {
            GreenhouseDetection detection = new GreenhouseDetection();
            detection.setGreenhouseId(imgRecordsDTO.getGreenhouseId());
            detection.setDetectionType("image");
            detection.setDetectionId(imgrecords.getId());
            detection.setDiseaseCount(0);
            if (imgRecordsDTO.getLabel() != null && !imgRecordsDTO.getLabel().isEmpty()) {
                String[] labels = imgRecordsDTO.getLabel().split(",");
                detection.setDiseaseCount(labels.length);
                detection.setDiseaseNames(imgRecordsDTO.getLabel());
            }
            detection.setRecordTime(LocalDateTime.now());
            greenhouseDetectionMapper.insert(detection);
        }
        
        return Result.success();
    }
}

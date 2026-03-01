package com.example.Ece.controller;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.CameraRecordsDTO;
import com.example.Ece.entity.CameraRecords;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.mapper.CameraRecordsMapper;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/cameraRecords")
public class   CameraRecordsController {
    @Resource
    CameraRecordsMapper cameraRecordsMapper;
    @Resource
    GreenhouseDetectionMapper greenhouseDetectionMapper;

    @GetMapping("/all")
    public Result<?> GetAll() {
        return Result.success(cameraRecordsMapper.selectList(null));
    }
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        System.out.println(id);
        return Result.success(cameraRecordsMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search3,
                              @RequestParam(defaultValue = "") String search2) {
        LambdaQueryWrapper<CameraRecords> wrapper = Wrappers.<CameraRecords>lambdaQuery();
        wrapper.orderByDesc(CameraRecords::getStartTime);
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
        cameraRecordsMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> updates(@RequestBody CameraRecords cameraRecords) {
        cameraRecordsMapper.updateById(cameraRecords);
        return Result.success();
    }


    @PostMapping
    @Transactional
    public Result<?> save(@RequestBody CameraRecordsDTO cameraRecordsDTO) {
        System.out.println(cameraRecordsDTO);
        
        CameraRecords cameraRecords = new CameraRecords();
        cameraRecords.setWeight(cameraRecordsDTO.getWeight());
        cameraRecords.setOutVideo(cameraRecordsDTO.getOutVideo());
        cameraRecords.setConf(cameraRecordsDTO.getConf());
        cameraRecords.setUsername(cameraRecordsDTO.getUsername());
        cameraRecords.setKind(cameraRecordsDTO.getKind());
        cameraRecords.setStartTime(cameraRecordsDTO.getStartTime());
        
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

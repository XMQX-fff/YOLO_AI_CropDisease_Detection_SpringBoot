package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.VideoRecordsDTO;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.entity.VideoRecords;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import com.example.Ece.mapper.VideoRecordsMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/videoRecords")
public class VideoRecordsController {
    @Resource
    VideoRecordsMapper videoRecordsMapper;
    @Resource
    GreenhouseDetectionMapper greenhouseDetectionMapper;

    @GetMapping("/all")
    public Result<?> GetAll() {
        return Result.success(videoRecordsMapper.selectList(null));
    }
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        System.out.println(id);
        return Result.success(videoRecordsMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search3,
                              @RequestParam(defaultValue = "") String search2) {
        LambdaQueryWrapper<VideoRecords> wrapper = Wrappers.<VideoRecords>lambdaQuery();
        wrapper.orderByDesc(VideoRecords::getStartTime);
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(VideoRecords::getUsername, search);
        }
        if (StrUtil.isNotBlank(search1)) {
            wrapper.like(VideoRecords::getKind, search1);
        }
        if (StrUtil.isNotBlank(search2)) {
            wrapper.like(VideoRecords::getWeight, search2);
        }
        if (StrUtil.isNotBlank(search3)) {
            wrapper.like(VideoRecords::getConf, search3);
        }
        Page<VideoRecords> Page = videoRecordsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(Page);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        videoRecordsMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> updates(@RequestBody VideoRecords videoRecords) {
        videoRecordsMapper.updateById(videoRecords);
        return Result.success();
    }


    @PostMapping
    @Transactional
    public Result<?> save(@RequestBody VideoRecordsDTO videoRecordsDTO) {
        System.out.println(videoRecordsDTO);
        
        VideoRecords videoRecords = new VideoRecords();
        videoRecords.setWeight(videoRecordsDTO.getWeight());
        videoRecords.setInputVideo(videoRecordsDTO.getInputVideo());
        videoRecords.setOutVideo(videoRecordsDTO.getOutVideo());
        videoRecords.setConf(videoRecordsDTO.getConf());
        videoRecords.setUsername(videoRecordsDTO.getUsername());
        videoRecords.setKind(videoRecordsDTO.getKind());
        videoRecords.setStartTime(videoRecordsDTO.getStartTime());
        
        videoRecordsMapper.insert(videoRecords);
        
        if (videoRecordsDTO.getGreenhouseId() != null) {
            GreenhouseDetection detection = new GreenhouseDetection();
            detection.setGreenhouseId(videoRecordsDTO.getGreenhouseId());
            detection.setDetectionType("video");
            detection.setDetectionId(videoRecords.getId());
            detection.setDiseaseCount(0);
            if (videoRecordsDTO.getKind() != null && !videoRecordsDTO.getKind().isEmpty()) {
                String[] kinds = videoRecordsDTO.getKind().split(",");
                detection.setDiseaseCount(kinds.length);
                detection.setDiseaseNames(videoRecordsDTO.getKind());
            }
            detection.setRecordTime(LocalDateTime.now());
            greenhouseDetectionMapper.insert(detection);
        }
        
        return Result.success();
    }
}

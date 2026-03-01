package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.Ece.common.Result;
import com.example.Ece.dto.ImgRecordsDTO;
import com.example.Ece.entity.GreenhouseDetection;
import com.example.Ece.entity.ImgRecords;
import com.example.Ece.mapper.GreenhouseDetectionMapper;
import com.example.Ece.mapper.ImgRecordsMapper;
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

    @GetMapping("/all")
    public Result<?> GetAll() {
        return Result.success(imgRecordsMapper.selectList(null));
    }
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable int id) {
        System.out.println(id);
        return Result.success(imgRecordsMapper.selectById(id));
    }

    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String search1,
                              @RequestParam(defaultValue = "") String search3,
                              @RequestParam(defaultValue = "") String search2) {
        LambdaQueryWrapper<ImgRecords> wrapper = Wrappers.<ImgRecords>lambdaQuery();
        wrapper.orderByDesc(ImgRecords::getStartTime);
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(ImgRecords::getUsername, search);
        }
        if (StrUtil.isNotBlank(search1)) {
            wrapper.like(ImgRecords::getKind, search1);
        }
        if (StrUtil.isNotBlank(search2)) {
            wrapper.like(ImgRecords::getLable, search2);
        }
        if (StrUtil.isNotBlank(search3)) {
            wrapper.like(ImgRecords::getConf, search3);
        }
        Page<ImgRecords> Page = imgRecordsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(Page);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable int id) {
        imgRecordsMapper.deleteById(id);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<?> updates(@RequestBody ImgRecords imgrecords) {
        imgRecordsMapper.updateById(imgrecords);
        return Result.success();
    }


    @PostMapping
    @Transactional
    public Result<?> save(@RequestBody ImgRecordsDTO imgRecordsDTO) {
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

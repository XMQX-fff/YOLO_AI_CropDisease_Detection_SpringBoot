package com.example.Ece.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.Ece.common.Result;
import com.example.Ece.dto.PestDetectionDTO;
import com.example.Ece.entity.CameraRecords;
import com.example.Ece.entity.ImgRecords;
import com.example.Ece.entity.User;
import com.example.Ece.entity.VideoRecords;
import com.example.Ece.mapper.CameraRecordsMapper;
import com.example.Ece.mapper.ImgRecordsMapper;
import com.example.Ece.mapper.VideoRecordsMapper;
import com.example.Ece.util.UserContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pest-detection")
public class PestDetectionController {

    @Resource
    private ImgRecordsMapper imgRecordsMapper;

    @Resource
    private VideoRecordsMapper videoRecordsMapper;

    @Resource
    private CameraRecordsMapper cameraRecordsMapper;

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

    @GetMapping("/list")
    public Result<?> getPestDetectionList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "") String sourceType,
            @RequestParam(defaultValue = "") String username,
            @RequestParam(defaultValue = "") String kind,
            @RequestParam(defaultValue = "") String startTime,
            @RequestParam(defaultValue = "") String endTime) {

        User currentUser = getCurrentUser();
        List<PestDetectionDTO> allRecords = new ArrayList<>();

        // 查询图片识别记录
        if (StrUtil.isBlank(sourceType) || "image".equals(sourceType)) {
            LambdaQueryWrapper<ImgRecords> imgWrapper = Wrappers.<ImgRecords>lambdaQuery();
            
            // 非管理员只能查看自己的数据
            if (!isAdmin(currentUser)) {
                imgWrapper.eq(ImgRecords::getUserId, currentUser.getId());
            }
            
            if (StrUtil.isNotBlank(username)) {
                imgWrapper.like(ImgRecords::getUsername, username);
            }
            if (StrUtil.isNotBlank(kind)) {
                imgWrapper.like(ImgRecords::getKind, kind);
            }
            if (StrUtil.isNotBlank(startTime)) {
                imgWrapper.ge(ImgRecords::getStartTime, startTime);
            }
            if (StrUtil.isNotBlank(endTime)) {
                imgWrapper.le(ImgRecords::getStartTime, endTime);
            }
            List<ImgRecords> imgList = imgRecordsMapper.selectList(imgWrapper);
            for (ImgRecords img : imgList) {
                PestDetectionDTO dto = new PestDetectionDTO();
                dto.setId(img.getId());
                dto.setSourceType("image");
                dto.setWeight(img.getWeight());
                dto.setInputPath(img.getInputImg());
                dto.setOutputPath(img.getOutImg());
                dto.setConf(img.getConf());
                dto.setUsername(img.getUsername());
                dto.setKind(img.getKind());
                dto.setStartTime(img.getStartTime());
                dto.setConfidence(img.getConfidence());
                dto.setAllTime(img.getAllTime());
                dto.setLabel(img.getLabel());
                allRecords.add(dto);
            }
        }

        // 查询视频识别记录
        if (StrUtil.isBlank(sourceType) || "video".equals(sourceType)) {
            LambdaQueryWrapper<VideoRecords> videoWrapper = Wrappers.<VideoRecords>lambdaQuery();
            
            // 非管理员只能查看自己的数据
            if (!isAdmin(currentUser)) {
                videoWrapper.eq(VideoRecords::getUserId, currentUser.getId());
            }
            
            if (StrUtil.isNotBlank(username)) {
                videoWrapper.like(VideoRecords::getUsername, username);
            }
            if (StrUtil.isNotBlank(kind)) {
                videoWrapper.like(VideoRecords::getKind, kind);
            }
            if (StrUtil.isNotBlank(startTime)) {
                videoWrapper.ge(VideoRecords::getStartTime, startTime);
            }
            if (StrUtil.isNotBlank(endTime)) {
                videoWrapper.le(VideoRecords::getStartTime, endTime);
            }
            List<VideoRecords> videoList = videoRecordsMapper.selectList(videoWrapper);
            for (VideoRecords video : videoList) {
                PestDetectionDTO dto = new PestDetectionDTO();
                dto.setId(video.getId());
                dto.setSourceType("video");
                dto.setWeight(video.getWeight());
                dto.setInputPath(video.getInputVideo());
                dto.setOutputPath(video.getOutVideo());
                dto.setConf(video.getConf());
                dto.setUsername(video.getUsername());
                dto.setKind(video.getKind());
                dto.setStartTime(video.getStartTime());
                allRecords.add(dto);
            }
        }

        // 查询监控识别记录
        if (StrUtil.isBlank(sourceType) || "camera".equals(sourceType)) {
            LambdaQueryWrapper<CameraRecords> cameraWrapper = Wrappers.<CameraRecords>lambdaQuery();
            
            // 非管理员只能查看自己的数据
            if (!isAdmin(currentUser)) {
                cameraWrapper.eq(CameraRecords::getUserId, currentUser.getId());
            }
            
            if (StrUtil.isNotBlank(username)) {
                cameraWrapper.like(CameraRecords::getUsername, username);
            }
            if (StrUtil.isNotBlank(kind)) {
                cameraWrapper.like(CameraRecords::getKind, kind);
            }
            if (StrUtil.isNotBlank(startTime)) {
                cameraWrapper.ge(CameraRecords::getStartTime, startTime);
            }
            if (StrUtil.isNotBlank(endTime)) {
                cameraWrapper.le(CameraRecords::getStartTime, endTime);
            }
            List<CameraRecords> cameraList = cameraRecordsMapper.selectList(cameraWrapper);
            for (CameraRecords camera : cameraList) {
                PestDetectionDTO dto = new PestDetectionDTO();
                dto.setId(camera.getId());
                dto.setSourceType("camera");
                dto.setWeight(camera.getWeight());
                dto.setInputPath(null);
                dto.setOutputPath(camera.getOutVideo());
                dto.setConf(camera.getConf());
                dto.setUsername(camera.getUsername());
                dto.setKind(camera.getKind());
                dto.setStartTime(camera.getStartTime());
                allRecords.add(dto);
            }
        }

        // 按开始时间倒序排序
        allRecords.sort((a, b) -> {
            if (a.getStartTime() == null && b.getStartTime() == null) {
                return 0;
            }
            if (a.getStartTime() == null) {
                return 1;
            }
            if (b.getStartTime() == null) {
                return -1;
            }
            return b.getStartTime().compareTo(a.getStartTime());
        });

        // 手动分页
        int total = allRecords.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        List<PestDetectionDTO> pageList;
        if (start >= total) {
            pageList = new ArrayList<>();
        } else {
            pageList = allRecords.subList(start, end);
        }

        // 构建分页结果
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("records", pageList);
        result.put("total", total);
        result.put("size", pageSize);
        result.put("current", pageNum);
        result.put("pages", (int) Math.ceil((double) total / pageSize));

        return Result.success(result);
    }

    @GetMapping("/statistics")
    public Result<?> getStatistics() {
        User currentUser = getCurrentUser();
        
        LambdaQueryWrapper<ImgRecords> imgWrapper = Wrappers.<ImgRecords>lambdaQuery();
        LambdaQueryWrapper<VideoRecords> videoWrapper = Wrappers.<VideoRecords>lambdaQuery();
        LambdaQueryWrapper<CameraRecords> cameraWrapper = Wrappers.<CameraRecords>lambdaQuery();
        
        // 非管理员只能查看自己的数据统计
        if (!isAdmin(currentUser)) {
            imgWrapper.eq(ImgRecords::getUserId, currentUser.getId());
            videoWrapper.eq(VideoRecords::getUserId, currentUser.getId());
            cameraWrapper.eq(CameraRecords::getUserId, currentUser.getId());
        }
        
        long imgCount = imgRecordsMapper.selectCount(imgWrapper);
        long videoCount = videoRecordsMapper.selectCount(videoWrapper);
        long cameraCount = cameraRecordsMapper.selectCount(cameraWrapper);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("imageCount", imgCount);
        stats.put("videoCount", videoCount);
        stats.put("cameraCount", cameraCount);
        stats.put("totalCount", imgCount + videoCount + cameraCount);

        return Result.success(stats);
    }
}
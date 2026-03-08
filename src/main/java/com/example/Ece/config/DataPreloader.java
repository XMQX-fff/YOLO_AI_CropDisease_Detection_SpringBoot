package com.example.Ece.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.Ece.entity.Greenhouse;
import com.example.Ece.entity.GreenhouseEnvironmentHistory;
import com.example.Ece.mapper.GreenhouseEnvironmentHistoryMapper;
import com.example.Ece.mapper.GreenhouseMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataPreloader {

    @Resource
    private GreenhouseEnvironmentHistoryMapper greenhouseEnvironmentHistoryMapper;
    
    @Resource
    private GreenhouseMapper greenhouseMapper;

    private final Random random = new Random();

    @PostConstruct
    public void preloadMissingData() {
        // 获取所有温室
        List<Greenhouse> greenhouses = greenhouseMapper.selectList(null);
        
        if (greenhouses.isEmpty()) {
            return;
        }

        // 计算过去7天的日期范围
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // 包含今天在内共7天

        // 遍历每一天
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            // 对每个温室检查当天是否有数据
            for (Greenhouse greenhouse : greenhouses) {
                checkAndGenerateData(greenhouse, date);
            }
        }
    }

    private void checkAndGenerateData(Greenhouse greenhouse, LocalDate date) {
        Integer greenhouseId = greenhouse.getId();
        
        // 检查当天是否有数据
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        LambdaQueryWrapper<GreenhouseEnvironmentHistory> wrapper = Wrappers.<GreenhouseEnvironmentHistory>lambdaQuery();
        wrapper.eq(GreenhouseEnvironmentHistory::getGreenhouseId, greenhouseId);
        wrapper.ge(GreenhouseEnvironmentHistory::getRecordTime, startOfDay);
        wrapper.le(GreenhouseEnvironmentHistory::getRecordTime, endOfDay);

        long count = greenhouseEnvironmentHistoryMapper.selectCount(wrapper);

        // 如果没有数据，生成随机数据
        if (count == 0) {
            generateRandomData(greenhouse, date);
        }
    }

    private void generateRandomData(Greenhouse greenhouse, LocalDate date) {
        Integer greenhouseId = greenhouse.getId();
        String greenhouseName = greenhouse.getGreenhouseName();

        // 生成一天内的8个时间点数据（每3小时一次）
        List<LocalTime> times = Arrays.asList(
            LocalTime.of(0, 0),
            LocalTime.of(3, 0),
            LocalTime.of(6, 0),
            LocalTime.of(9, 0),
            LocalTime.of(12, 0),
            LocalTime.of(15, 0),
            LocalTime.of(18, 0),
            LocalTime.of(21, 0)
        );

        List<GreenhouseEnvironmentHistory> records = new ArrayList<>();

        for (LocalTime time : times) {
            LocalDateTime recordTime = date.atTime(time);
            
            // 根据时间和温室类型生成合理的随机值
            GreenhouseEnvironmentHistory record = GreenhouseEnvironmentHistory.builder()
                .greenhouseId(greenhouseId)
                .temperature(generateTemperature(time))
                .airHumidity(generateAirHumidity(time))
                .soilHumidity(generateSoilHumidity())
                .co2Concentration(generateCo2Concentration(time))
                .soilPh(generateSoilPh())
                .lightIntensity(generateLightIntensity(time))
                .recordTime(recordTime)
                .dataSource("system")
                .build();

            records.add(record);
        }

        // 循环插入数据
        if (!records.isEmpty()) {
            for (GreenhouseEnvironmentHistory record : records) {
                greenhouseEnvironmentHistoryMapper.insert(record);
            }
            System.out.println("为温室 " + greenhouseName + " 生成了 " + date + " 的环境数据");
        }
    }

    // 生成温度：白天较高，晚上较低
    private BigDecimal generateTemperature(LocalTime time) {
        int hour = time.getHour();
        double baseTemp;
        if (hour >= 6 && hour < 18) {
            // 白天：25-30度
            baseTemp = 25 + random.nextDouble() * 5;
        } else {
            // 晚上：18-22度
            baseTemp = 18 + random.nextDouble() * 4;
        }
        return BigDecimal.valueOf(baseTemp).setScale(1, RoundingMode.HALF_UP);
    }

    // 生成空气湿度：晚上较高，白天较低
    private Integer generateAirHumidity(LocalTime time) {
        int hour = time.getHour();
        int baseHumidity;
        if (hour >= 18 || hour < 6) {
            // 晚上：60-80%
            baseHumidity = 60 + random.nextInt(21);
        } else {
            // 白天：40-60%
            baseHumidity = 40 + random.nextInt(21);
        }
        return baseHumidity;
    }

    // 生成土壤湿度：40-70%
    private Integer generateSoilHumidity() {
        return 40 + random.nextInt(31);
    }

    // 生成CO2浓度：白天较低，晚上较高
    private Integer generateCo2Concentration(LocalTime time) {
        int hour = time.getHour();
        int baseCo2;
        if (hour >= 6 && hour < 18) {
            // 白天：400-600 ppm
            baseCo2 = 400 + random.nextInt(201);
        } else {
            // 晚上：600-800 ppm
            baseCo2 = 600 + random.nextInt(201);
        }
        return baseCo2;
    }

    // 生成土壤pH值：6.0-7.5
    private BigDecimal generateSoilPh() {
        double ph = 6.0 + random.nextDouble() * 1.5;
        return BigDecimal.valueOf(ph).setScale(1, RoundingMode.HALF_UP);
    }

    // 生成光照强度：白天强，晚上弱
    private Integer generateLightIntensity(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 7 && hour < 17) {
            // 白天：5000-10000 lux
            return 5000 + random.nextInt(5001);
        } else if (hour >= 6 && hour < 7 || hour >= 17 && hour < 18) {
            // 黎明/黄昏：1000-5000 lux
            return 1000 + random.nextInt(4001);
        } else {
            // 晚上：0-500 lux
            return random.nextInt(501);
        }
    }
}

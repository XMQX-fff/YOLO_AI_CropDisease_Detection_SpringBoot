package com.example.Ece.dto;

import com.example.Ece.entity.Logistics;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class LogisticsDTO extends Logistics {
    // 中文物流状态
    private String chineseStatus;
    
    // 构造函数，从Logistics转换
    public LogisticsDTO(Logistics logistics) {
        super(logistics.getId(), logistics.getOrderId(), logistics.getLogisticsNo(),
              logistics.getStatus(), logistics.getInfo(), logistics.getUpdatedAt(), logistics.getLogisticsCompany());
    }
}
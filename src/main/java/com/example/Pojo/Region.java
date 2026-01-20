package com.example.Pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Region {
    private String regionCode;
    private String parentCode;
    private String name;
    private Integer level;
    private BigDecimal centerLat;
    private BigDecimal centerLng;
    private String polygon; // 存储多边形 WKT 文本
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
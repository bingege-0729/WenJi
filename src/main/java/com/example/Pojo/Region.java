package com.example.Pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Region {
    private String regionCode;//区域编码
    private String parentCode;//父级区域编码
    private String name;//区域名称
    private Integer level;//区域级别
    private BigDecimal centerLat;//区域中心点纬度
    private BigDecimal centerLng;//区域中心点经度
    private String polygon; // 存储多边形 WKT 文本
    private Integer sortOrder;//排序
    private Boolean isActive;//状态 0-禁用 1-正常
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//修改时间
}
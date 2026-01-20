package com.example.Pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HeritageSite {
    private String siteId;
    private String siteCode;
    private String name;
    private String enName;
    private Integer type;
    private String category;
    private String level; // 你的推荐权重核心字段
    private String provinceCode;
    private String cityCode;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;

    /**
     * 空间索引字段。
     * 插入时建议：ST_GeomFromText('POINT(lng lat)')
     * 查询时建议：ST_AsText(location_point)
     */
    private Object locationPoint;

    private String geohash;
    private String coverImage;
    private String description;
    private String history;
    private String techniques;
    private String bestSeason;
    private Integer suitableDuration;
    private String contactPhone;
    private String officialUrl;
    private Integer status;
    private Boolean isRecommended;
    private BigDecimal popularity;
    private Integer visitCount;
    private BigDecimal rating;
    private Integer capacity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

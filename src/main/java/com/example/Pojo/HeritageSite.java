package com.example.Pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HeritageSite {
    private String siteId;
    private String siteCode;
    private String name;
    private String enName;
    private Integer type;
    private String category;
    private String level;
    private String provinceCode;
    private String cityCode;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Object locationPoint; // 存储经纬度
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

    // 关联数据
    private List<SiteImage> images;
    private List<OpeningHours> openingHours;
    private Boolean isOpening;
}

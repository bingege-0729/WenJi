package com.example.Pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HeritageSite {
    private String siteId;//站点ID
    private String siteCode;//站点编码
    private String name;//站点名称
    private String enName;//站点英文名称
    private Integer type;//站点类型
    private String category;//
    private String level; //非遗级别 1-国家 2-省 3-市
    private String provinceCode;//省份编码
    private String cityCode;//市编码
    private String address;// 地址
    private BigDecimal latitude;//纬度
    private BigDecimal longitude;//经度

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

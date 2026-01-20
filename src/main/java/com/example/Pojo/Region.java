package com.example.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
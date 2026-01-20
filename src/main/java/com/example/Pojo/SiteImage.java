package com.example.Pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteImage {
    private Long imageId;
    private String siteId;
    private String imageUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private Integer sortOrder;
    private Boolean isCover;
    private Integer imageType;
    private Integer width;
    private Integer height;
    private Integer size;
    private String uploadBy;
    private Integer status;
    private LocalDateTime createTime;
}
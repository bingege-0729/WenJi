package com.example.Pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SiteImage {
    private Long imageId;//图片ID
    private String siteId;//站点ID
    private String imageUrl;  //图片URL
    private String thumbnailUrl;//缩略图URL
    private String title;//图片标题
    private String description;//图片描述
    private Integer sortOrder;//排序
    private Boolean isCover;//是否是封面图片
    private Integer imageType;//图片类型
    private Integer width;//图片宽度
    private Integer height;//图片高度
    private Integer size;//图片大小
    private String uploadBy;//上传者
    private Integer status;//状态 0-禁用 1-正常
    private LocalDateTime createTime;//创建时间
}
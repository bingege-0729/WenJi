# 1202026

## 数据库修改：

### **heritage_sites表中记录经纬度字段类型（DECIMAL 类型）应该使用 MySQL 的几何类型字段**

```mysql
	'location_point' POINT COMMENT '地理位置点',  -- 新增几何字段
    -- 索引
    INDEX idx_province_code(province_code),
    INDEX idx_city_code(city_code),
    INDEX idx_level(level),
    INDEX idx_type(type),
    INDEX idx_geohash(geohash),
    INDEX idx_popularity(popularity),
    SPATIAL INDEX idx_location(location_point)  -- 空间索引
    
    SPATIAL 索引要求：几何字段必须为 NOT NULL
```

### 添加外键：

```mysql
-- 为 opening_hours 表添加外键
ALTER TABLE opening_hours
ADD CONSTRAINT fk_opening_hours_site
FOREIGN KEY (site_id) REFERENCES heritage_sites(site_id);

-- 为 site_images 表添加外键
ALTER TABLE site_images
ADD CONSTRAINT fk_site_images_site
FOREIGN KEY (site_id) REFERENCES heritage_sites(site_id);
```


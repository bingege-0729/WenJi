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
## HeritageController接口调试：

输入信息：

```
GET http://localhost:8080/map/initial?lng=116.3&lat=39.9
```

输出信息：

```json
"code": 1,
    "msg": "成功",
    "data": [
        {
            "siteId": "S001",
            "siteCode": null,
            "name": "故宫博物院（木结构营造技艺）",
            "enName": null,
            "type": null,
            "category": null,
            "level": "国家级",
            "provinceCode": null,
            "cityCode": null,
            "address": "北京市东城区景山前街4号",
            "latitude": 39.9163000,
            "longitude": 116.3974000,
            "locationPoint": null,
            "geohash": null,
            "coverImage": null,
            "description": null,
            "history": null,
            "techniques": null,
            "bestSeason": null,
            "suitableDuration": null,
            "contactPhone": null,
            "officialUrl": null,
            "status": null,
            "isRecommended": null,
            "popularity": 9.90,
            "visitCount": null,
            "rating": 4.90,
            "capacity": null,
            "createTime": null,
            "updateTime": null,
            "images": [
                {
                    "imageId": 1,
                    "siteId": "S001",
                    "imageUrl": "https://example.com/gugong_cover.jpg",
                    "thumbnailUrl": null,
                    "title": null,
                    "description": null,
                    "sortOrder": 1,
                    "isCover": true,
                    "imageType": null,
                    "width": null,
                    "height": null,
                    "size": null,
                    "uploadBy": null,
                    "status": 1,
                    "createTime": "2026-01-20T12:45:21"
                }
            ],
            "openingHours": [
                {
                    "hourId": 1,
                    "siteId": "S001",
                    "dayOfWeek": 1,
                    "openTime": "08:30:00",
                    "closeTime": "17:00:00",
                    "isOpen": true,
                    "specialDate": null,
                    "remark": null,
                    "createTime": "2026-01-20T12:45:15",
                    "updateTime": "2026-01-20T12:45:15"
                },
                {
                    "hourId": 2,
                    "siteId": "S001",
                    "dayOfWeek": 2,
                    "openTime": "08:30:00",
                    "closeTime": "17:00:00",
                    "isOpen": true,
                    "specialDate": null,
                    "remark": null,
                    "createTime": "2026-01-20T12:45:15",
                    "updateTime": "2026-01-20T12:45:15"
                }
            ],
            "isOpening": true
        },
        {
            "siteId": "S002",
            "siteCode": null,
            "name": "西安城隍庙庙会",
            "enName": null,
            "type": null,
            "category": null,
            "level": "省级",
            "provinceCode": null,
            "cityCode": null,
            "address": "西安市西大街129号",
            "latitude": 34.2633000,
            "longitude": 108.9393000,
            "locationPoint": null,
            "geohash": null,
            "coverImage": null,
            "description": null,
            "history": null,
            "techniques": null,
            "bestSeason": null,
            "suitableDuration": null,
            "contactPhone": null,
            "officialUrl": null,
            "status": null,
            "isRecommended": null,
            "popularity": 8.50,
            "visitCount": null,
            "rating": 4.50,
            "capacity": null,
            "createTime": null,
            "updateTime": null,
            "images": [
                {
                    "imageId": 2,
                    "siteId": "S002",
                    "imageUrl": "https://example.com/chenghuangmiao.jpg",
                    "thumbnailUrl": null,
                    "title": null,
                    "description": null,
                    "sortOrder": 1,
                    "isCover": true,
                    "imageType": null,
                    "width": null,
                    "height": null,
                    "size": null,
                    "uploadBy": null,
                    "status": 1,
                    "createTime": "2026-01-20T12:45:21"
                }
            ],
            "openingHours": [
                {
                    "hourId": 3,
                    "siteId": "S002",
                    "dayOfWeek": 1,
                    "openTime": "09:00:00",
                    "closeTime": "18:00:00",
                    "isOpen": true,
                    "specialDate": null,
                    "remark": null,
                    "createTime": "2026-01-20T12:45:15",
                    "updateTime": "2026-01-20T12:45:15"
                }
            ],
            "isOpening": false
        }
    ]
}
```



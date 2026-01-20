package com.example.Mapper;

import com.example.Pojo.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RegionMapper {
    /**
     * 根据经纬度查询所在的城市信息
     */
    @Select("SELECT region_code, name, level FROM regions " +
            "WHERE level = 2 " +
            "AND ST_Contains(ST_GeomFromText(polygon), ST_GeomFromText(CONCAT('POINT(', #{lng}, ' ', #{lat}, ')'))) " +
            "AND is_active = 1 LIMIT 1")
    Region getCityByLocation(@Param("lng") Double lng, @Param("lat") Double lat);
}

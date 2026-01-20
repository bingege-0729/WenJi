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
    Region getCityByLocation(@Param("lng") Double lng, @Param("lat") Double lat);

    /**
     * 根据编码获取区域信息
     */
    Region selectByCode(@Param("regionCode") String regionCode);
}

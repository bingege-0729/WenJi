package com.example.Service.Impl;

import com.example.Mapper.HeritageSiteMapper;
import com.example.Mapper.RegionMapper;
import com.example.Pojo.HeritageSite;
import com.example.Pojo.Region;
import com.example.Service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {
    @Autowired
    private HeritageSiteMapper heritageSiteMapper;
    @Autowired
    private RegionMapper regionMapper;

    public List<HeritageSite> getNearbyHeritageSites(Double lng, Double lat) {
        //通过regions表判定用户当前的city_code
        Region currentRegion = regionMapper.getCityByLocation(lng, lat);
        String cityCode = (currentRegion != null) ? currentRegion.getRegionCode() : null;

        //查询推荐列表
        return heritageSiteMapper.selectRecommendedSites(lng, lat, cityCode, 10);
    }
}
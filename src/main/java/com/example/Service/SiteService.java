package com.example.Service;

import com.example.Pojo.HeritageSite;

import java.util.List;

public interface SiteService {
    /**
     * 根据用户当前位置和城市筛选推荐景点
     * @param lng
     * @param lat
     * @return
     */
    List<HeritageSite> getNearbyHeritageSites(Double lng, Double lat);
}

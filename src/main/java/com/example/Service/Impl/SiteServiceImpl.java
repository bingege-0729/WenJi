package com.example.Service.Impl;

import com.example.Mapper.HeritageSiteMapper;
import com.example.Mapper.OpeningHoursMapper;
import com.example.Mapper.RegionMapper;
import com.example.Mapper.SiteImageMapper;
import com.example.Pojo.HeritageSite;
import com.example.Pojo.OpeningHours;
import com.example.Pojo.Region;
import com.example.Service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {
    @Autowired
    private HeritageSiteMapper heritageSiteMapper;
    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private OpeningHoursMapper openingHoursMapper;
    @Autowired
    private SiteImageMapper siteImageMapper;

    public List<HeritageSite> getNearbyHeritageSites(Double lng, Double lat) {
        //获取当前城市
        Region currentRegion = regionMapper.getCityByLocation(lng, lat);
        if (currentRegion == null) {
            currentRegion = regionMapper.selectByCode("110100");//默认北京
        }
        String cityCode = (currentRegion != null) ? currentRegion.getRegionCode() : null;

        LocalTime now = LocalTime.now();
        int dayOfWeek = LocalDate.now().getDayOfWeek().getValue();

        //查询推荐列表
        List<HeritageSite> sites = heritageSiteMapper.selectRecommendedSites(lng, lat, cityCode, 10);

        //组装数据
        for (HeritageSite site : sites) {
            site.setImages(siteImageMapper.findBySiteId(site.getSiteId()));
            List<OpeningHours> hours = openingHoursMapper.findBySiteId(site.getSiteId());
            site.setOpeningHours(hours);
            site.setIsOpening(checkCurrentOpeningStatus(hours, dayOfWeek, now));
        }
        return sites;
    }

    /**
     * 检查当前时间是否在营业时间段内
     * @param hours
     * @param dayOfWeek
     * @param now
     * @return
     */
    private Boolean checkCurrentOpeningStatus(List<OpeningHours> hours, int dayOfWeek, LocalTime now) {
        if (hours == null || hours.isEmpty()) return false;
        return hours.stream()
                .filter(h -> h.getDayOfWeek() == dayOfWeek && h.getIsOpen())
                .anyMatch(h -> now.isAfter(h.getOpenTime()) && now.isBefore(h.getCloseTime()));
    }

    /**
     * 获取景点详情
     * @param siteId
     * @return
     */
    public HeritageSite getDetail(String siteId) {
        return heritageSiteMapper.getSiteDetail(siteId);
    }
}
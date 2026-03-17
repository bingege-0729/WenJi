package com.example.Service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.Mapper.TravelBlogMapper;
import com.example.Pojo.TravelBlog;
import com.example.Service.BadgeService;
import com.example.Service.PointService;
import com.example.Service.TravelBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TravelBlogServiceImpl implements TravelBlogService {

    @Autowired
    private TravelBlogMapper travelBlogMapper;

    @Autowired
    private PointService pointService;

    @Autowired
    private BadgeService badgeService;

    @Override
    @Transactional
    public void publishBlog(TravelBlog blog) {
        // 1. 保存博客
        // 假设发布一篇博客获得 10 积分
        int points = 10;
        blog.setPointsEarned(points);
        travelBlogMapper.insert(blog);

        // 2. 增加积分
        pointService.addPoints(blog.getUserId(), points, "blog", "发布旅游心得打卡");

        // 3. 异步/同步检查徽章 (此处演示同步调用)
        badgeService.checkAndDistributeBadges(blog.getUserId(), "blog");
        badgeService.checkAndDistributeBadges(blog.getUserId(), "footprint");
    }

    @Override
    public List<TravelBlog> getUserBlogs(Integer userId) {
        return travelBlogMapper.selectList(
                Wrappers.<TravelBlog>lambdaQuery()
                        .eq(TravelBlog::getUserId, userId)
                        .orderByDesc(TravelBlog::getCreateTime)
        );
    }
}

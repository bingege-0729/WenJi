package com.example.Service;

import com.example.Pojo.TravelBlog;

import java.util.List;

public interface TravelBlogService {
    /**
     * 发布旅游打卡博客
     * @param blog 博客对象
     */
    void publishBlog(TravelBlog blog);

    /**
     * 获取用户的打卡足迹 (去过的地方)
     * @param userId 用户ID
     * @return 博客列表
     */
    List<TravelBlog> getUserBlogs(Integer userId);
}

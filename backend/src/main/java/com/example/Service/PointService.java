package com.example.Service;

public interface PointService {
    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param points 变动积分
     * @param source 来源 (blog/badge)
     * @param description 描述
     */
    void addPoints(Integer userId, Integer points, String source, String description);
}

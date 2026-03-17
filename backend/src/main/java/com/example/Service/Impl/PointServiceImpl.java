package com.example.Service.Impl;

import com.example.Mapper.PointRecordMapper;
import com.example.Mapper.UserMapper;
import com.example.Pojo.PointRecord;
import com.example.Pojo.User;
import com.example.Service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointRecordMapper pointRecordMapper;

    @Override
    @Transactional
    public void addPoints(Integer userId, Integer points, String source, String description) {
        // 1. 更新用户积分
        User user = userMapper.selectById(userId);
        if (user != null) {
            Integer currentPoints = user.getPoints() == null ? 0 : user.getPoints();
            user.setPoints(currentPoints + points);
            userMapper.updateById(user);
        }

        // 2. 记录积分流水
        PointRecord record = PointRecord.builder()
                .userId(userId)
                .type(1) // 获得
                .source(source)
                .points(points)
                .description(description)
                .build();
        pointRecordMapper.insert(record);
    }
}

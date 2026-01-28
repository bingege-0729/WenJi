package com.example.Mapper;

import com.example.Pojo.OpeningHours;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OpeningHoursMapper {
    // 根据景点ID查询营业时间表
    List<OpeningHours> findBySiteId(String siteId);
}

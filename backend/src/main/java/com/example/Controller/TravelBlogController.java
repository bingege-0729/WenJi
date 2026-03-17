package com.example.Controller;

import com.example.Common.Result;
import com.example.Pojo.TravelBlog;
import com.example.Service.TravelBlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.Common.Utils.GetUserIdUtils.getCurrentUserId;

@RestController
@RequestMapping("/travel")
@Tag(name = "旅游心得管理", description = "旅游心得打卡接口")
public class TravelBlogController {

    @Autowired
    private TravelBlogService travelBlogService;

    @Operation(summary = "发布旅游心得打卡")
    @PostMapping("/publish")
    public Result<Void> publish(@RequestBody TravelBlog blog) {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.error("未登录");
        
        blog.setUserId(userId.intValue());
        travelBlogService.publishBlog(blog);
        return Result.success();
    }

    @Operation(summary = "获取我的足迹 (博客列表)")
    @GetMapping("/myBlogs")
    public Result<List<TravelBlog>> getMyBlogs() {
        Long userId = getCurrentUserId();
        if (userId == null) return Result.error("未登录");
        
        List<TravelBlog> blogs = travelBlogService.getUserBlogs(userId.intValue());
        return Result.success(blogs);
    }
}

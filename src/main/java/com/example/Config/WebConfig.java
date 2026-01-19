package com.example.Config;

import com.example.Interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry  registry){
        registry.addInterceptor(loginInterceptor)//添加拦截器，但是要避免拦截登录和注册接口
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/register","/auth/login","/");
    }
}

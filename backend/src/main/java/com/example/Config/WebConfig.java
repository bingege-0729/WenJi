package com.example.Config;

import com.example.Interceptor.LoginInterceptor;
import com.example.Interceptor.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private RefreshTokenInterceptor refreshTokenInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(refreshTokenInterceptor)
                .order(0)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login","/user/captcha","/",
                        "/uploads/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html");
        //拦截所有请求，确保这个拦截器是先执行
        registry.addInterceptor(loginInterceptor)//添加拦截器，但是要避免拦截登录和注册接口
                .addPathPatterns("/**")//拦截所有接口
                .excludePathPatterns("/user/login",
                        "/user/captcha",
                        "/",
                        "/uploads/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );


    }

    /**
     * 静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/develop/Projectes/WenJi/uploads/");
    }

    /**
     * 跨域配置
     * @param  registry 跨域注册
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition");
    }
}

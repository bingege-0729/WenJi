package com.example.Interceptor;

import com.example.Utils.JwtUtils;
import com.example.Utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的 URL
        String uri = request.getRequestURI();
        
        // 检查是否为登录或注册请求，如果是则放行
        if ("/user/login".equals(uri) || "/user/register".equals(uri)) {
            return true;
        }
        
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            // 如果没有token，返回401
            response.setStatus(401);
            return false;
        }
        
        // 移除"Bearer "前缀（如果存在）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        
        try{
            //从Redis中获取相同的Token
            ValueOperations<String,String> operations=stringRedisTemplate.opsForValue();
            String redisToken=operations.get(token);
            if(redisToken==null){
                //token已经失效
                throw new RuntimeException("Token已失效");
            }
            
            // 验证JWT令牌
            Map<String,Object> claims= JwtUtils.parseToken(token);
            //把业务数据存储到ThreadLocal中
            ThreadLocalUtil.set(claims);

            return true;//放行
        }catch (Exception e){
            //http状态码未401
            response.setStatus(401);
            return false;
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //请求处理完成后，清空ThreadLocal中的数据,防止内存泄露
        ThreadLocalUtil.remove();
    }
}

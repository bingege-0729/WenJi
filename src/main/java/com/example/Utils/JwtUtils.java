package com.example.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // 使用符合安全要求的密钥，HS256 算法
    // 注意：在生产环境中，密钥应通过安全的方式存储和管理，不要硬编码在代码中
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 设置令牌的有效期为 24 小时（单位：毫秒）
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L; // 24小时

    /**
     * 生成 JWT 令牌
     *
     * @param claims 令牌中包含的自定义信息，如用户ID、用户名等
     * @return 生成的 JWT 令牌字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                // 设置自定义声明（Payload）
                .addClaims(claims)
                // 设置签名算法和密钥
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                // 设置令牌的过期时间（当前时间 + 有效期）
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // 构建并返回紧凑的 JWT 字符串
                .compact();
    }

    /**
     * 通过用户名生成 JWT 令牌
     * @param username 用户名
     * @return 生成的 JWT 令牌字符串
     */
    public static String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        return generateToken(claims);
    }

    /**
     * 解析 JWT 令牌
     *
     * @param token 要解析的 JWT 令牌字符串
     * @return 包含令牌信息的 Claims 对象
     * @throws Exception 如果令牌无效、过期或签名不匹配，则抛出异常
     */
    public static Claims parseToken(String token) throws Exception {
        return Jwts.parserBuilder()
                // 设置签名密钥
                .setSigningKey(SECRET_KEY)
                // 构建解析器
                .build()
                // 解析并验证令牌
                .parseClaimsJws(token)
                // 获取 Payload 部分的 Claims
                .getBody();
    }
}
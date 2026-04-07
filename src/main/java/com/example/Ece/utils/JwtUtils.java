package com.example.Ece.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
public class JwtUtils {

    // JWT密钥
    private static final String SECRET = "your-256-bit-secret-your-256-bit-secret";
    
    // Token有效期（毫秒）- 7天
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000;
    
    // Token前缀
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 生成JWT Token
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @return JWT Token
     */
    public String generateToken(Integer userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * 从Token中解析Claims
     * @param token JWT Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证Token是否有效
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return false;
        }
        // 检查是否过期
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.after(new Date());
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null && claims.get("userId") != null) {
            return Integer.valueOf(claims.get("userId").toString());
        }
        return null;
    }

    /**
     * 从Token中获取用户名
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return (String) claims.get("username");
        }
        return null;
    }

    /**
     * 从Token中获取用户角色
     * @param token JWT Token
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return (String) claims.get("role");
        }
        return null;
    }

    /**
     * 从Authorization Header中提取Token
     * @param authHeader Authorization Header
     * @return Token（去除前缀）
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        return authHeader;
    }
}

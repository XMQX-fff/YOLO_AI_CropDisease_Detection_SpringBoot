package com.example.Ece.interceptor;

import com.example.Ece.entity.User;
import com.example.Ece.mapper.UserMapper;
import com.example.Ece.util.UserContext;
import com.example.Ece.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证拦截器
 * 解析JWT Token并将用户信息设置到request属性中
 * 对于需要认证的接口，如果认证失败则返回401
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Authorization Header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && !authHeader.isEmpty()) {
            // 提取Token
            String token = jwtUtils.extractTokenFromHeader(authHeader);
            
            // 验证Token
            if (token != null && jwtUtils.validateToken(token)) {
                // 从Token中获取用户ID
                Integer userId = jwtUtils.getUserIdFromToken(token);
                
                if (userId != null) {
                    // 查询用户信息
                    User user = userMapper.selectById(userId);
                    
                    if (user != null) {
                        // 将用户信息设置到request属性中
                        request.setAttribute("currentUser", user);
                        // 将用户信息设置到UserContext中
                        UserContext.setCurrentUser(user);
                        // 认证成功，放行
                        return true;
                    }
                }
            }
        }
        
        // 认证失败，返回401错误
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", "401");
        result.put("message", "认证失败，请先登录");
        result.put("success", false);
        
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(result));
        writer.flush();
        writer.close();
        
        // 阻止请求继续
        return false;
    }
}

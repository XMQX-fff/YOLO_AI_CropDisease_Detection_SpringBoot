package com.example.Ece.config;

import com.example.Ece.interceptor.AuthInterceptor;
import com.example.Ece.interceptor.UserContextClearInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private UserContextClearInterceptor userContextClearInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册认证拦截器，拦截所有路径
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // 排除不需要认证的接口
                .excludePathPatterns(
                        // 用户登录注册相关
                        "/user/login",
                        "/user/register",
                        "/user/verifyCode",
                        // 商品浏览相关（前台展示）
                        "/product",
                        "/product/**",
                        "/product/category/**"
                );

        // 注册用户上下文清理拦截器，拦截所有路径，顺序在最后
        registry.addInterceptor(userContextClearInterceptor)
                .addPathPatterns("/**");
    }
}

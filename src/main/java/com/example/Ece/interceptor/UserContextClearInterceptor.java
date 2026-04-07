package com.example.Ece.interceptor;

import com.example.Ece.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户上下文清理拦截器
 * 在请求完成后清理ThreadLocal中的用户信息，防止内存泄漏
 */
@Component
public class UserContextClearInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 请求处理完成后清理UserContext
        UserContext.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 确保在任何情况下都清理UserContext
        UserContext.clear();
    }
}

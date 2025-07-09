package com.sln.commoncgi.service.impl;

import com.sln.commoncgi.model.RouteConfig;
import com.sln.commoncgi.service.RoutingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 路由服务实现类
 */
@Slf4j
@Service
public class RoutingServiceImpl implements RoutingService {

    private List<RouteConfig> routeConfigs;

    @PostConstruct
    public void init() {
        // 初始化路由配置（简化实现，实际应该从配置文件或数据库加载）
        routeConfigs = new ArrayList<>();
        
        // 添加示例路由配置
        RouteConfig loginRoute = new RouteConfig();
        loginRoute.setUrl("/v1/login");
        loginRoute.setServiceName("login");
        
        // 配置鉴权
        RouteConfig.AuthConfig authConfig = new RouteConfig.AuthConfig();
        authConfig.setType("jwt");
        authConfig.setAuthFields(Arrays.asList("name", "age", "nonce"));
        authConfig.setSign("sign");
        authConfig.setEnabled(true);
        loginRoute.setAuth(authConfig);
        
        // 配置限流
        RouteConfig.RateLimitConfig rateLimitConfig = new RouteConfig.RateLimitConfig();
        rateLimitConfig.setEnabled(true);
        rateLimitConfig.setLimitField("userId");
        rateLimitConfig.setRequestsPerSecond(5);
        rateLimitConfig.setBucketCapacity(10);
        loginRoute.setRateLimit(rateLimitConfig);
        
        routeConfigs.add(loginRoute);
        
        // 添加用户服务路由
        RouteConfig userRoute = new RouteConfig();
        userRoute.setUrl("/v1/user");
        userRoute.setServiceName("user");
        
        RouteConfig.RateLimitConfig userRateLimit = new RouteConfig.RateLimitConfig();
        userRateLimit.setEnabled(true);
        userRateLimit.setLimitField("userId");
        userRateLimit.setRequestsPerSecond(10);
        userRateLimit.setBucketCapacity(20);
        userRoute.setRateLimit(userRateLimit);
        
        routeConfigs.add(userRoute);
        
        log.info("路由配置初始化完成，共加载{}个路由", routeConfigs.size());
    }

    @Override
    public RouteConfig getRouteConfig(HttpServletRequest request) {
        return getRouteConfig(request.getRequestURI());
    }

    @Override
    public RouteConfig getRouteConfig(String urlPath) {
        for (RouteConfig config : routeConfigs) {
            if (matchUrl(config.getUrl(), urlPath)) {
                log.debug("找到匹配的路由配置: {} -> {}", urlPath, config.getServiceName());
                return config;
            }
        }
        
        log.warn("未找到匹配的路由配置: {}", urlPath);
        return null;
    }

    /**
     * URL匹配逻辑
     * 支持精确匹配和前缀匹配
     */
    private boolean matchUrl(String configUrl, String requestUrl) {
        // 精确匹配
        if (configUrl.equals(requestUrl)) {
            return true;
        }
        
        // 前缀匹配（如果配置URL以/*结尾）
        if (configUrl.endsWith("/*")) {
            String prefix = configUrl.substring(0, configUrl.length() - 2);
            return requestUrl.startsWith(prefix);
        }
        
        // 路径参数匹配（简化实现）
        if (configUrl.contains("{") && configUrl.contains("}")) {
            return matchPathVariable(configUrl, requestUrl);
        }
        
        return false;
    }

    /**
     * 路径参数匹配（简化实现）
     */
    private boolean matchPathVariable(String configUrl, String requestUrl) {
        String[] configParts = configUrl.split("/");
        String[] requestParts = requestUrl.split("/");
        
        if (configParts.length != requestParts.length) {
            return false;
        }
        
        for (int i = 0; i < configParts.length; i++) {
            String configPart = configParts[i];
            String requestPart = requestParts[i];
            
            // 如果是路径参数（以{开头，以}结尾），则跳过比较
            if (configPart.startsWith("{") && configPart.endsWith("}")) {
                continue;
            }
            
            // 普通路径段必须完全匹配
            if (!configPart.equals(requestPart)) {
                return false;
            }
        }
        
        return true;
    }
}
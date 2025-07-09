package com.sln.commoncgi.service;

import com.sln.commoncgi.model.RouteConfig;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 路由服务接口
 * 负责根据请求匹配对应的路由配置
 */
public interface RoutingService {
    
    /**
     * 根据请求获取匹配的路由配置
     * 
     * @param request HTTP请求
     * @return 匹配的路由配置，如果没有匹配则返回null
     */
    RouteConfig getRouteConfig(HttpServletRequest request);
    
    /**
     * 根据URL路径获取匹配的路由配置
     * 
     * @param urlPath URL路径
     * @return 匹配的路由配置，如果没有匹配则返回null
     */
    RouteConfig getRouteConfig(String urlPath);
}
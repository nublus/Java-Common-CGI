package com.sln.commoncgi.service;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 请求转发服务接口
 * 负责将请求转发到目标服务
 */
public interface ForwardService {
    
    /**
     * 转发请求到目标服务
     * 
     * @param request 原始HTTP请求
     * @param serviceName 目标服务名称
     * @return 目标服务的响应
     */
    ResponseEntity<String> forwardRequest(HttpServletRequest request, String serviceName);
}
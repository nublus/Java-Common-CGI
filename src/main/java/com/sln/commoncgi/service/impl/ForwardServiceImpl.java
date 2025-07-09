package com.sln.commoncgi.service.impl;

import com.sln.commoncgi.service.ForwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * 请求转发服务实现类
 */
@Slf4j
@Service
public class ForwardServiceImpl implements ForwardService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity<String> forwardRequest(HttpServletRequest request, String serviceName) {
        try {
            // 构建目标URL（这里简化处理，实际应该从服务发现获取）
            String targetUrl = buildTargetUrl(serviceName, request.getRequestURI());
            
            // 复制请求头
            HttpHeaders headers = copyHeaders(request);
            
            // 复制请求体
            String requestBody = getRequestBody(request);
            
            // 创建HTTP实体
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            // 根据HTTP方法转发请求
            HttpMethod method = HttpMethod.valueOf(request.getMethod());
            
            log.info("转发请求到: {} {}", method, targetUrl);
            
            // 发送请求并返回响应
            return restTemplate.exchange(targetUrl, method, entity, String.class);
            
        } catch (Exception e) {
            log.error("请求转发失败: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("请求转发失败: " + e.getMessage());
        }
    }

    /**
     * 构建目标URL
     */
    private String buildTargetUrl(String serviceName, String requestUri) {
        // 简化实现，实际应该从配置或服务发现获取服务地址
        String baseUrl = getServiceBaseUrl(serviceName);
        return baseUrl + requestUri;
    }

    /**
     * 获取服务基础URL
     */
    private String getServiceBaseUrl(String serviceName) {
        // 简化实现，实际应该从Consul等服务发现组件获取
        // 这里使用硬编码的映射
        switch (serviceName.toLowerCase()) {
            case "login":
                return "http://localhost:8081";
            case "user":
                return "http://localhost:8082";
            default:
                return "http://localhost:8080";
        }
    }

    /**
     * 复制请求头
     */
    private HttpHeaders copyHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // 跳过一些不需要转发的头
            if (!shouldSkipHeader(headerName)) {
                headers.add(headerName, headerValue);
            }
        }
        
        return headers;
    }

    /**
     * 判断是否跳过某个请求头
     */
    private boolean shouldSkipHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.equals("host") || 
               lowerName.equals("content-length") ||
               lowerName.equals("connection");
    }

    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }
}
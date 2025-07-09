package com.sln.commoncgi.model;

import lombok.Data;

import java.util.List;

/**
 * 路由配置实体类
 */
@Data
public class RouteConfig {
    
    /**
     * 请求URL路径
     */
    private String url;
    
    /**
     * 目标服务名称
     */
    private String serviceName;
    
    /**
     * 鉴权配置
     */
    private AuthConfig auth;
    
    /**
     * 限流配置
     */
    private RateLimitConfig rateLimit;
    
    /**
     * 鉴权配置内部类
     */
    @Data
    public static class AuthConfig {
        /**
         * 鉴权类型（如jwt）
         */
        private String type;
        
        /**
         * 需要鉴权的字段
         */
        private List<String> authFields;
        
        /**
         * 签名字段名
         */
        private String sign;
        
        /**
         * 是否启用鉴权
         */
        private boolean enabled = false;
    }
    
    /**
     * 限流配置内部类
     */
    @Data
    public static class RateLimitConfig {
        /**
         * 是否启用限流
         */
        private boolean enabled = false;
        
        /**
         * 限流字段（如根据用户ID限流）
         */
        private String limitField;
        
        /**
         * 每秒允许的请求数
         */
        private int requestsPerSecond = 10;
        
        /**
         * 令牌桶容量
         */
        private int bucketCapacity = 20;
    }
}
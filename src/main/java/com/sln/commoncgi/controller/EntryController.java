package com.sln.commoncgi.controller;

import com.sln.commoncgi.model.RouteConfig;
import com.sln.commoncgi.service.ForwardService;
import com.sln.commoncgi.service.RoutingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 统一请求入口控制器
 * 所有外部请求都通过这个控制器进行处理和转发
 */
@Slf4j
@RestController
public class EntryController {

    @Autowired
    private RoutingService routingService;
    
    @Autowired
    private ForwardService forwardService;

    /**
     * 处理所有HTTP请求的统一入口
     * 支持GET、POST、PUT、DELETE等方法
     */
    @RequestMapping(value = "/v1/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> handleRequest(HttpServletRequest request) {
        
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        
        log.info("收到请求: {} {}", method, requestUri);
        
        try {
            // 1. 根据请求URL获取路由配置
            RouteConfig routeConfig = routingService.getRouteConfig(request);
            
            if (routeConfig == null) {
                log.warn("未找到匹配的路由配置: {}", requestUri);
                return ResponseEntity.notFound().build();
            }
            
            // 2. 执行请求转发
            // TODO: 这里后续会添加限流、鉴权等切面处理
            ResponseEntity<String> response = forwardService.forwardRequest(request, routeConfig.getServiceName());
            
            log.info("请求转发完成: {} {} -> {}", method, requestUri, response.getStatusCode());
            
            return response;
            
        } catch (Exception e) {
            log.error("请求处理失败: {} {}", method, requestUri, e);
            return ResponseEntity.internalServerError()
                    .body("请求处理失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("CGI服务运行正常");
    }
    
    /**
     * 获取路由配置信息（用于调试）
     */
    @GetMapping("/debug/routes")
    public ResponseEntity<String> getRoutes() {
        // 这里可以返回当前的路由配置信息
        return ResponseEntity.ok("路由配置调试接口");
    }
}


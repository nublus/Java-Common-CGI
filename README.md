# Java Common CGI
## 什么是CGI：
> CGI也就是通用网关，外部的普通请求打到CGI之后，会通过一些内部的通用逻辑，进行限频、分流、鉴权和转发等操作，使用CGI可以将外部请求转化为内部请求，隔离外部的攻击、错误请求等，过滤出正确的请求，并根据业务逻辑进行分流
## 本CGI需要的功能
这部分可以查阅相关资料/GPT来获得实现方案

架构设计：使用切面进行设计，每个操作作为CGI的一个切面，通过一定的顺序（可以是外部配置传入、也可以是写死）进行编排，比如对于一个http请求，先进行限频操作（防止恶意攻击），再根据某些字段进行鉴权，最后根据另外一些字段进行分流，从而完成一个简单的CGI转发流程。

### 基本功能
+ HTTP请求CGI转发
>basic: 响应http请求，实现by url进行分流操作
advanced: 能够根据http的字段，通过配置描述，进行分流操作

+ HTTP请求鉴权
> basic: 根据配置描述，对特定url进行鉴权，要求使用JWT完成

+ HTTP限频
> basic: 通过redis记录信息，使用令牌桶算法实现by字段粒度的CGI限频，也即某为高校提供的服务，要求能根据高校限频，高校之间不应该相互影响

+ 负载均衡
> basic: 实现简单的负载均衡策略

+ 接入服务发现
> 使用consul组件作为服务发现工具, consul应当构成一个集群，包含raft协议同步数据的
> server节点，和剩余的client节点，CGI要通过blocking query机制来获取当前的服务信息，
> 最终找到正确的服务

+ 输入配置
```json
{
    [
        {
        "url":"/v1/login",
        "service_name": "login",
        "auth":{
            "type":"jwt",
            "auth_fields":["name","age","nonce"],
            "sign":"sign"
        }
        }
    ]
}
```


解释配置：url为/v1/login的请求代表login服务，我需要用jwt鉴权，鉴权的字段是auth_fields中
的字段，用字母序排好后按照 ` a={}&b={}&c={} `的次序拼接，尝试对签名使用对称加密解密，对比解密的字段是否和拼接字段一样，若一样则鉴权成功，为鉴权成功的用户发放token，缓存记录这部分token，下次请求带上token即可，不再使用签名校验

输出
成功完成对下游的转发工作

### 项目结构
java-common-cgi/
├── controller/
│   └── EntryController.java         // 所有请求入口
├── aop/
│   ├── RateLimitAspect.java         // 限流切面
│   ├── AuthAspect.java              // 鉴权切面
│   └── RoutingAspect.java           // 分流切面
├── config/
│   └── RouteConfigLoader.java       // 加载配置 JSON 映射
├── service/
│   ├── RateLimiterService.java
│   ├── AuthService.java
│   ├── RoutingService.java
│   └── ForwardService.java          // 请求转发
├── consul/
│   └── ConsulClient.java            // 通过 blocking query 获取服务
├── model/
│   └── RouteConfig.java             // 路由配置实体类
├── util/
│   └── JwtUtils.java                // JWT工具类
└── Application.java                 // 启动类


+ 参考资料: 
> [CGI详解](https://blog.csdn.net/weixin_39609623/article/details/86312439)
[CGI](https://zhuanlan.zhihu.com/p/25013398)


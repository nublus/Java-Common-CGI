## 转发
前端 / 客户端：继续使用 HTTP + JSON（通用、兼容性好）

后端服务之间：用 gRPC + Protobuf（高性能、高可靠）

网关作为桥梁，接收 JSON、转为 Protobuf，调用 gRPC，再将结果转回 JSON。
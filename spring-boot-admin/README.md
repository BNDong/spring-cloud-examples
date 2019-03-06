# spring-boot-admin

spring 监控管理组件。Spring Boot Admin 提供了很多功能，如显示 name、id 和 version，显示在线状态，Loggers 的日志级别管理，Threads 线程管理，Environment 管理等。

* **user**: admin
* **password**：123456

## 认证配置

服务端配置：

```
spring.security.user=用户名
spring.security.password=用户密码
```

客户端配置：

```
spring.boot.admin.client.url=服务端地址
spring.boot.admin.client.username=服务端配置用户名
spring.boot.admin.client.password=服务端配置用户密码
```
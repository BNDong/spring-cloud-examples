# spring-cloud-examples
🌀 Personal learning use cases. 个人学习用例。
<br>使用 Docker 容器构建服务，宿主机IP：```192.168.1.254```，本地IP：```192.168.1.188```
## spring-cloud
|spring       |version|
|:-----------:|:----------:|
|**boot**     |2.0.0.RELEASE|
|**cloud**    |Finchley.SR2|

* [spring-cloud-eureka](https://github.com/BNDong/spring-cloud-examples/tree/master/spring-cloud-eureka) **- 注册中心**
    * spring-cloud-starter-netflix-eureka-server - [注册中心服务端]
    * spring-boot-starter-security - [安全认证]
    * spring-boot-admin-starter-client - [SBA Client]
* [spring-cloud-config](https://github.com/BNDong/spring-cloud-examples/tree/master/spring-cloud-config) **- 配置中心：动态配置**
    * spring-cloud-config-server - [配置中心服务端]
    * spring-cloud-starter-bus-amqp - [消息驱动]
    * spring-cloud-starter-netflix-eureka-client - [注册中心客户端]
    * spring-cloud-starter-netflix-ribbon - [客户端负载]
    * spring-cloud-netflix-sidecar - [异构语言]
    * spring-boot-admin-starter-client - [SBA Client]
* [spring-cloud-zuul](https://github.com/BNDong/spring-cloud-examples/tree/master/spring-cloud-zuul) **- 网关：跨域配置，断路器，重试，异常处理**
    * spring-cloud-starter-netflix-zuul - [网关服务端]
    * spring-cloud-starter-netflix-eureka-client - [注册中心客户端]
    * spring-cloud-starter-config - [配置中心客户端]
    * spring-cloud-starter-bus-amqp - [消息驱动]
    * spring-cloud-zuul-ratelimit - [限流]
    * spring-retry - [重试机制]
    * spring-boot-admin-starter-client - [SBA Client]
    * spring-boot-starter-freemarker - [模板引擎]
* [spring-boot-admin](https://github.com/BNDong/spring-cloud-examples/tree/master/spring-boot-admin) **- spring boot admin**
    * spring-cloud-starter-netflix-eureka-client - [注册中心客户端]
    * spring-cloud-starter-config - [配置中心客户端]
    * spring-cloud-starter-bus-amqp - [消息驱动]
    * spring-boot-starter-security - [安全认证]
    * spring-boot-admin-server - [SBA Server]
    * spring-boot-admin-server-ui - [SBA Server UI]
    * spring-boot-starter-mail - [email]

## spring-docker-compose
容器构建编排，将 *.env 中```REGISTRY_NAMESPACE```修改为```public-container```即可 pull 镜像。
```
├─ spring-docker-compose
│  ├─ compose - 容器构建编排配置
│  ├─ conf - 容器配置
│  ├─ extensions - 依赖
│  ├─ logs - 日志
│  ├─ sh - 脚本
│  ├─ volumes - 数据卷
│  ├─ .dockerignore
│  ├─ .gitignore
│  ├─ Dockerfile
│  └─ sources.list
```
## spring-git-config
配置中心：```{application}/${spring.application.name}-${spring.cloud.config.profile}.yml```
# spring-cloud-examples
🌀 Personal learning use cases. 个人学习用例。
<br>以异构其他语言为目的微服务架构，高自由度，可扩展，可伸缩。
<br>使用 Docker 容器构建服务。本地架构服务器IP：```192.168.1.254```，本地开发服务器IP：```192.168.1.188```
## architecture

<p align="center">
<img src="/gh-static/architecture.png" alt="architecture">
</p>

## spring version

|spring       |version|
|:-----------:|:----------:|
|**boot**     |2.0.6.RELEASE|
|**cloud**    |Finchley.SR2|

## service

|application  |port        |describe    |
|:-----------:|:----------:|:-----------|
|**spring-cloud-eureka**|9010|注册中心：安全认证|
|**spring-cloud-eureka-1** |9011|注册中心2：安全认证|
|**spring-cloud-config** |9020|配置中心：配置刷新|
|**spring-cloud-zuul** |9030|API网关：回退、熔断、重试、限流、鉴权|
|**spring-boot-admin** |9040|架构监控：服务、网关、日志、配置|
|**spring-cloud-oauth**|9050|授权中心：注册、签发、鉴权、撤销|
|**spring-cloud-sidecar**|--|异构客户端|

### eureka
* username:eureka
* passwod:123456

![eureka](/gh-static/eureka1.png)

![eureka](/gh-static/eureka2.png)

### spring-docker-compose
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
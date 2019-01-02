# spring-cloud-examples
🌀 Personal learning use cases. 个人学习用例。
<br>以异构语言为目的微服务架构，高自由度，可扩展，可伸缩。
<br>使用 Docker 构建服务。本地架构服务器IP：```192.168.1.254```，本地开发计算机IP：```192.168.1.188```
# architecture

![architecture](/gh-static/architecture.png)

# spring version

|spring       |version|
|:-----------:|:----------:|
|**boot**     |2.0.6.RELEASE|
|**cloud**    |Finchley.SR2|

# project

|application  |port        |describe    |
|:-----------:|:----------:|:-----------|
|**spring-cloud-eureka**|9010/9011|注册中心：安全认证|
|**spring-cloud-config** |9020|配置中心：配置刷新|
|**spring-cloud-zuul** |9030|API网关：回退、熔断、重试、限流、鉴权|
|**spring-boot-admin** |9040|boot管理：监控、日志、配置|
|**spring-cloud-oauth**|9050|授权中心：注册、签发、鉴权、撤销|
|**spring-cloud-sidecar**|--|异构客户端代理|

## cloud-eureka
* **username:** eureka
* **passwod:** 123456

### 服务注册
![eureka](/gh-static/eureka1.png)

### 注册历史
![eureka](/gh-static/eureka2.png)

## cloud-admin
* **username:** admin
* **passwod:** 123456

### 服务状态
![admin](/gh-static/admin1.png)

### 服务详情
![admin](/gh-static/admin2.png)

### 环境变量
![admin](/gh-static/admin3.png)

### 运行日志
![admin](/gh-static/admin4.png)

### 日志设置
![admin](/gh-static/admin5.png)

### 线程监控
![admin](/gh-static/admin6.png)

### API监控
![admin](/gh-static/admin7.png)

## cloud-rabbitmq
* **username:** guest
* **passwod:** guest
### 消息队列
![rabbitmq](/gh-static/rabbitmq1.png)

### 消息监控
![rabbitmq](/gh-static/rabbitmq2.png)

## cloud-config
* **配置刷新:** ```[POST] /actuator/bus-refresh``` ("application/json; charset=UTF-8")
* **web hook:** ```[POST] /monitor```

## cloud-oauth
oauth2.0 + jwt，支持 token 自定义数据，支持 token 撤销机制。
<br>支持的4种授权模式 grant_type
```
authorization_code, implicit, password, client_credentials
```
### 获取 token
* authorization_code模式：通过用户获取 code，进而获取 token
```
1. [GET] /oauth/authorize?client_id=SampleClientId&response_type=code&redirect_uri=http://callback.com/login
用户同意授权后响应：
浏览器重定向到：http://callback.com/login?code=1E37Xk，接收code,然后后端调用步骤2获取token
2. [POST] /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=authorization_code&redirect_uri=http://callback.com/login&code=1E37Xk&extend[id]=2222
响应：extend 为自定义数据，数据会包含在token中
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "read",
    "userId": "1",
    "extend": {
        "id": "2222"
    }
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
```
* password模式：直接使用用户获取 token
```
[POST] /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=password&scope=read&username=zhangsan&password=tgb.258&extend[id]=2222
响应：extend 为自定义数据，数据会包含在token中
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "read",
    "userId": "1",
    "extend": {
        "id": "2222"
    }
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
```
### 验证 token
```[POST] /oauth/check_token?token=a.b.c```
### 刷新 token
```[POST] /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=refresh_token&refresh_token=d.e.f```
### 撤销 token
```[POST] /oauth/revokeToken?client_id=SampleClientId&client_secret=tgb.258&access_token=a.b.c```
### 获取 public key
```[GET] /oauth/token_key```
### 注册用户
```[POST] /oauth/signUp?username=lisi&password=yourpass&client_id=SampleClientId&client_secret=tgb.258```
### 管理后台
```[GET] /management/user/```

![oauth](/gh-static/oauth1.png)
## cloud-zuul
API网关，支持鉴权，断路器机制，回退机制，统一异常处理，接口限流

### oauth token
传递 token 三种方式
* 请求时添加Authorization header

```Authorization : Bearer xxxxx```
* 请求地址添加参数access_token

```/api/a?access_token=xxxxx```
* cookie方式 添加access_token

```access_token=xxxxx```

# cloud-docker-compose
## 目录结构
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
## 容器构建
```
cd ./cloud-docker-compose/compose
cp docker-compose-dev.env .env
docker-compose -f docker-compose-dev.yml up -d
```
*********************
```
[root@localhost ~]#  docker ps --format "table {{.Command}}\t{{.Ports}}\t{{.Names}}"
COMMAND                  PORTS                                                                                        NAMES
"docker-entrypoint.s…"   0.0.0.0:9051->6379/tcp                                                                       cloud_oauth_redis
"/bin/bash"              0.0.0.0:9011->9011/tcp                                                                       cloud_eureka_1
"/bin/bash"              0.0.0.0:9030->9030/tcp                                                                       cloud_zuul
"/bin/bash"              0.0.0.0:9010->9010/tcp                                                                       cloud_eureka
"/bin/bash"              0.0.0.0:9020->9020/tcp                                                                       cloud_config
"php -S 0.0.0.0:80"      0.0.0.0:9032->80/tcp                                                                         cloud_zuul_phpredisadmin
"docker-entrypoint.s…"   4369/tcp, 5671/tcp, 0.0.0.0:5672->5672/tcp, 15671/tcp, 25672/tcp, 0.0.0.0:15672->15672/tcp   rabbitmq
"php -S 0.0.0.0:80"      0.0.0.0:9052->80/tcp                                                                         cloud_oauth_phpredisadmin
"docker-entrypoint.s…"   0.0.0.0:9031->6379/tcp                                                                       cloud_zuul_redis
"/bin/bash"              0.0.0.0:9040->9040/tcp                                                                       cloud_admin
"docker-entrypoint.s…"   33060/tcp, 0.0.0.0:9053->3306/tcp                                                            cloud_oauth_mysql
"/bin/bash"              0.0.0.0:9050->9050/tcp                                                                       cloud_oauth                                                                    cloud_oauth
```
## shell
```
cd ./cloud-docker-compose/sh
chmod 0755 *.sh
./xxxx.sh
```
* ```sh/docker_in.sh``` - 进入容器
* ```sh/jar_restart.sh``` - 重启 jar 包
* ```sh/jar_start.sh``` - 启动 jar 包
* ```sh/jar_stop.sh``` - 停止 jar 包

# could-git-config
配置仓库：```{application}/${spring.application.name}-${spring.cloud.config.profile}.yml```

# dependent project
* [dnmp](https://github.com/yeszao/dnmp)
* [oauth2-server](https://github.com/jobmission/oauth2-server)
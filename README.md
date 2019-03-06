# spring-cloud-examples
🌀 Personal learning use cases. 个人学习用例。
<br>以异构语言为目的的微服务架构，高自由度，可扩展，可伸缩。
<br>使用 Docker 构建服务。本地架构服务器IP：```192.168.1.254```，本地开发计算机IP：```192.168.1.188```
# architecture

![architecture](/gh-static/architecture.png)

## startup dependency

![startup](/gh-static/startup.png)

> ※启动服务组件的时候，请注意服务之间的依赖！

# version

|type         |version|
|:-----------:|:----------:|
|**spring boot**  |2.0.6.RELEASE|
|**spring cloud** |Finchley.SR2|
|**jdk** |1.8|

# project

|application  |port        |describe    |
|:-----------:|:----------:|:-----------|
|**spring-cloud-eureka**|9010/9011|注册中心：安全认证|
|**spring-cloud-config** |9020|配置中心：配置刷新|
|**spring-cloud-zuul** |9030|API网关：回退、熔断、重试、限流、鉴权|
|**spring-boot-admin** |9040|boot管理：监控、日志、配置|
|**spring-cloud-oauth**|9050|授权中心：注册、签发、鉴权、撤销|
|**spring-cloud-sidecar**|--|异构客户端代理|

# startup project

服务启动的顺序，参考服务依赖。

## 启动容器

容器的启动参考：[cloud-docker-compose](https://github.com/BNDong/spring-cloud-examples/tree/master/cloud-docker-compose) 的说明文档

## 上传jar包

> 项目打包命令：
> <br>```mvn clean``` ：删除 target 目录
> <br>```mvn package``` ：重新打包

打包后的 jar 包，上传至 ```cloud-docker-compose/volumes/[容器名称]/``` 目录下。

## 进入容器操作 jar 包

> 关于进入容器和 jar 包的相关操作，封装了操作脚本：
> <br>宿主机中脚本目录： ```cloud-docker-compose/sh/```
> <br>容器中脚本目录：```/usr/local/sh/```

* **进入容器**
<br>运行脚本：```./docker_in.sh```
<br>出现以下界面：
```
The container currently running:
--------------------------------------------------
1 statistics_service_redis_even
2 statistics_service_redis_odd
3 statistics_service_phpredisadmin_odd
4 statistics_service_phpredisadmin_even
5 statistics_service_phpredisadmin
6 openzipkin
7 openzipkin_dependencies
8 openzipkin_mysql
9 behavior_mycat
10 log_service_nginx
...
--------------------------------------------------
Please enter the container line number: [输入需要进入容器的编号]
```
输入需要进入容器的编号，回车进入容器。

* **启动 jar**
<br>运行脚本：```./jar_start.sh```
<br>出现以下界面：
```
Enter the jar storage directory(default: /data):[jar 包存储目录] 
--------------------------------------------------
1 ****.jar
--------------------------------------------------
Enter the line number to run the jar package(default: 1): [启动的 jar 包编号]
Enter the log storage directory(default: /dev/null): [启动信息输出目录，默认不输出（调试使用）。项目中有关于此信息的默认日志文件]
Input configuration environment(default: dev): [启动环境，加载不同的配置文件]
```

* **关闭 jar**
<br>运行脚本：```./jar_stop.sh```
<br>出现以下界面：
```
The jar package that is running:
--------------------------------------------------
   47 ?        Sl   249:10 java -jar /data/cloud-eureka-1-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
 7609 ?        S+     0:00 grep java -jar .*jar [※忽略此运行进程]
--------------------------------------------------
Input PID of the process: [输入需要结束的进程 PID]
```

* **重启 jar**
<br>运行脚本：```./jar_restart.sh```
<br>出现以下界面：
```
Enter the jar storage directory(default: /data): [jar 包存储目录] 
--------------------------------------------------
1 ***.jar
--------------------------------------------------
Enter the line number to run the jar package(default: 1): [启动的 jar 包编号]
Enter the log storage directory(default: /dev/null):[启动信息输出目录，默认不输出（调试使用）。项目中有关于此信息的默认日志文件]
Input configuration environment(default: dev): [重新启动环境，加载不同的配置文件]
```

# project log

项目日志位置：
* 宿主机日志目录：```cloud-docker-compose/logs/spring/```
* 容器日志目录：```/data/logs/spring/[服务名称]/```

![logs](/gh-static/logs.png)

# partial page

### 服务注册
![eureka](/gh-static/eureka1.png)

### 注册历史
![eureka](/gh-static/eureka2.png)

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

### 消息队列
![rabbitmq](/gh-static/rabbitmq1.png)

### 消息监控
![rabbitmq](/gh-static/rabbitmq2.png)

# project dependent
* [dnmp](https://github.com/yeszao/dnmp)
* [oauth2-server](https://github.com/jobmission/oauth2-server)
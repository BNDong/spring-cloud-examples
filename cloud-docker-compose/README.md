# 目录结构

```
├─ spring-docker-compose
│  ├─ compose - 容器构建编排配置
│  │  ├─ *.env - 环境变量文件
│  │  └─ *.yml - 容器编排配置文件
│  ├─ conf - 容器配置
│  │  ├─ [容器名称]
│  │  │  ├── conf.d - Nginx用户站点配置目录
│  │  │  ├── nginx.conf - Nginx默认配置文件
│  │  │  ├── mysql.cnf - MySQL用户配置文件
│  │  │  ├── php-fpm.conf - PHP-FPM配置文件（部分会覆盖php.ini配置）
│  │  │  └── php.ini - PHP默认配置文件
│  │  ...
│  ├─ extensions - 依赖
│  ├─ logs - 日志
│  ├─ sh - 脚本
│  ├─ volumes - 数据卷
│  ├─ .dockerignore
│  ├─ .gitignore
│  ├─ Dockerfile
│  └─ sources.list
```

# 容器构建

```
# 进入容器构建编排配置目录
cd ./cloud-docker-compose/compose
# 设置环境变量
cp docker-compose-dev.env .env
# 启动容器
docker-compose -f docker-compose-dev.yml up -d
```

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
"/bin/bash"              0.0.0.0:9050->9050/tcp                                                                       cloud_oauth
```

# shell

```
# 进入脚本目录
cd ./cloud-docker-compose/sh
# 设置权限
chmod 0755 *.sh
# 启动脚本
./xxxx.sh
```

* ```sh/docker_in.sh``` - 进入容器
* ```sh/jar_restart.sh``` - 重启 jar 包
* ```sh/jar_start.sh``` - 启动 jar 包
* ```sh/jar_stop.sh``` - 停止 jar 包

# 在正式环境中安全使用

要在正式环境中使用，请：

* 在php.ini中关闭XDebug调试
* 增强MySQL数据库访问的安全策略
* 增强redis访问的安全策略

---

详细文档参考 [dnmp](https://github.com/yeszao/dnmp)
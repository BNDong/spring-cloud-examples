# spring-cloud-zuul

网关

# token

## 获取请求 token

传递 token 三种方式

* 请求时添加Authorization header

```Authorization : Bearer xxxxx```

* 请求地址添加参数access_token

```/api/a?access_token=xxxxx```

* cookie方式 添加access_token

```access_token=xxxxx```

<br>
相关处理类：

```
com.microview.zuul.config.CustomTokenExtractor
```

## 开启验证 token

配置：
```
zuul.oauth.enabled=true
```

## 验证 token

* **本地验证**
本地验证是通过 public key 进行的验证，在网关中也是对 token 的第一步验证。

配置：
```
zuul.oauth.tokenKeyUri=获取 public key 的地址
```
处理类：
```
com.microview.zuul.config.ResourceServerConfiguration
```

* **授权中心验证**
请求授权中心进行验证。

配置：
```
zuul.oauth.checkTokenUri=验证 token 的地址
```
处理类：
```
com.microview.zuul.filter.AuthTokenFilter
```

## 验证 token 白名单

配置：
```
zuul.oauth.whiteList
```

# sign

## 加签

加签可以使用阿里云API网关的加签方式，做了初步的解析验证！

* [demo-sign-java](https://github.com/aliyun/api-gateway-demo-sign-java)
* [demo-sign-php](https://github.com/aliyun/api-gateway-demo-sign-php)
* [demo-sign-python](https://github.com/aliyun/api-gateway-demo-sign-python)
* [demo-sign-net](https://github.com/aliyun/api-gateway-demo-sign-net)
* [demo-sign-android](https://github.com/aliyun/api-gateway-demo-sign-android)

## 开启验证签名

配置：
```
zuul.sign.enabled=true
```

## 验证签名

配置：
```
zuul.oauth.appks
```
处理类：
```
com.microview.zuul.filter.AuthSignFilter
```

## 验证签名白名单

配置：
```
zuul.sign.whiteList
```

# doc
[Spring Cloud：统一异常处理](https://www.cnblogs.com/bndong/p/10135370.html)


# spring-cloud-oauth

授权中心：oauth2.0 + jwt，支持 token 自定义数据，支持 token 撤销机制。

# 获取 token

## authorization_code模式

通过用户获取 code，进而获取 token

### 获取 code
```
[GET] /oauth/authorize?client_id=SampleClientId&response_type=code&redirect_uri=http://callback.com/login
```

|parameter    |declare     |default|null|
|:-----------:|:----------:|:-----:|:-----:|
|**client_id**|客户端ID|-|N|
|**response_type**|授权类型|-|N|
|**redirect_uri**|回调uri（需与数据库中的授权回调一致）|-|N|

用户同意授权后响应：
浏览器重定向到：```http://callback.com/login?code=1E37Xk```，接收 code，获取 code 后通过下面接口换取 token。

```
[POST] /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=authorization_code&redirect_uri=http://callback.com/login&code=1E37Xk&extend[id]=2222&user_client_type=pc
```

|parameter    |declare     |default|null|
|:-----------:|:----------:|:-----:|:-----:|
|**client_id**|客户端ID|-|N|
|**client_secret**|客户端secret|-|N|
|**grant_type**|授权类型|-|N|
|**redirect_uri**|回调uri|-|N|
|**code**|code，只能使用一次|-|N|
|**extend[*]**|自定义数据|-|Y|
|**user_client_type**|用户客户端类型|default|Y|

请求成功响应示例：

```json
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "read",
    "userId": "1",
    "extend": {
        "id": "2222"
    },
    "user_client_type": "pc",
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
```


## password模式

直接使用用户账户获取 token

```
[POST] /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=password&scope=read&username=zhangsan&password=tgb.258&extend[id]=2222&user_client_type=pc
```

|parameter    |declare     |default|null|
|:-----------:|:----------:|:-----:|:-----:|
|**client_id**|客户端ID|-|N|
|**client_secret**|客户端secret|-|N|
|**grant_type**|授权类型|-|N|
|**scope**|权限域|-|N|
|**username**|用户名|-|N|
|**password**|用户密码|-|N|
|**extend[*]**|自定义数据|-|Y|
|**user_client_type**|用户客户端类型|default|Y|

请求成功响应示例：

```json
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "read",
    "userId": "1",
    "extend": {
        "id": "2222"
    },
    "oauth_user_id": "13",
    "user_client_type": "pc",
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
```

# 验证 token

```
[POST] /oauth/check_token?token=a.b.c
```

|parameter    |declare     |default|null|
|:-----------:|:----------:|:-----:|:-----:|
|**token**|access_token|-|N|

请求成功响应示例：

```json
{
    "extend": {
        "id": "4545454"
    },
    "oauth_user_id": "13",
    "user_name": "test",
    "scope": [
        "read"
    ],
    "active": true,
    "exp": 1551434179,
    "user_client_type": "default",
    "authorities": [
        "ROLE_USER"
    ],
    "jti": "01decd3e-1f2b-478d-8fb8-c51d558050ec",
    "client_id": "SampleClientId"
}
```

# 刷新 token

refresh_token 只能使用一次，刷新成功后会返回全新的 access_token 和 refresh_token。

```
[POST] /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=refresh_token&refresh_token=d.e.f
```

|parameter    |declare     |default|null|
|:-----------:|:----------:|:-----:|:-----:|
|**client_id**|客户端ID|-|N|
|**client_secret**|客户端secret|-|N|
|**grant_type**|授权模式|-|N|
|**refresh_token**|刷新token|-|N|

请求成功响应示例：

```json
{
    "access_token": "aa.bb.cc",
    "token_type": "bearer",
    "refresh_token": "dd.ee.ff",
    "expires_in": 7199,
    "scope": "read",
    "oauth_user_id": "13",
    "user_client_type": "default",
    "jti": "67b81bb0-896b-4db2-8ee8-155beec9ed54"
}
```

# 撤销 token

* **撤销单客户端类型的 token**

撤销 token 关联用户，当前客户端类型的授权，用户的其它客户端类型授权不影响！

```
[POST] /oauth/revokeToken?client_id=SampleClientId&client_secret=tgb.258&access_token=a.b.c
```

|parameter    |declare     |default|null|
|:-----------:|:----------:|:-----:|:-----:|
|**client_id**|客户端ID|-|N|
|**client_secret**|客户端secret|-|N|
|**access_token**|access_token|-|N|

请求成功响应示例：

```json
{
    "status": 1,
    "timestamp": 1551665251216
}
```

* **撤销全部客户端类型的 token**

撤销 token 关联用户，全部客户端类型的授权，用户的其它客户端类型授权一并撤销！

```
[POST] /oauth/revokeTokenAll?client_id=SampleClientId&client_secret=tgb.258&access_token=a.b.c
```

|parameter    |declare     |default|null|
|:-----------:|:----------:|:-----:|:-----:|
|**client_id**|客户端ID|-|N|
|**client_secret**|客户端secret|-|N|
|**access_token**|access_token|-|N|

请求成功响应示例：

```json
{
    "status": 1,
    "timestamp": 1551665251216
}
```

# 获取 public key

```
[GET] /oauth/token_key
```

请求成功响应示例：

```json
{
    "alg": "SHA256withRSA",
    "value": "-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCml+IEplekodveRT7v+PNkMDwSD0s4s+5/CVYEp2yzEzVnv/AsDk2wiGb2glhkkbpMmRsCqYGrnby81QCMTZ9n2oVxcfW5kDEKuw13u2snRSGIGh4TCvpG3t2p08gtrg+el7d5uge43Oo7A4Wf/nPT09Os/sdvbEuOvpw+RhjT/wIDAQAB\n-----END PUBLIC KEY-----"
}
```

# 注册用户

```
[POST] /oauth/signUp?username=lisi&password=yourpass&client_id=SampleClientId&client_secret=tgb.258
```

|parameter    |declare     |default|null|
|:-----------:|:----------:|:-----:|:-----:|
|**username**|用户名|-|N|
|**password**|用户密码|-|N|
|**client_id**|授权客户端ID|-|N|
|**client_secret**|授权客户端secret|-|N|

请求成功响应示例：

```json
{
    "status": 1,
    "timestamp": 1551665715913
}
```

# 管理后台

```
[GET] /management/user/
```

![admin](/gh-static/oauth2_admin.png)

# 部分功能位置

* **添加 token 自定义数据**

com.revengemission.sso.oauth2.server.config.AuthorizationServerConfiguration.accessTokenConverter()

* **token 自定义数据缓存，授权处理**

com.revengemission.sso.oauth2.server.config.CustomAuthTokenExtendHandler

* **请求拦截器，处理自定义授权相关操作**

com.revengemission.sso.oauth2.server.filter.AuthTokenFilter

# doc
[Spring Cloud：Security OAuth2 自定义异常响应](https://www.cnblogs.com/bndong/p/10275430.html)
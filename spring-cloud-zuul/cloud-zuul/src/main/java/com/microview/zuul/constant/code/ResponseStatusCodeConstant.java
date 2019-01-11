package com.microview.zuul.constant.code;

/**
 * 响应 StatusCode 常量定义类
 */
public class ResponseStatusCodeConstant {
    private ResponseStatusCodeConstant() {}

    public static final int OAUTH_TOKEN_FAILURE = 2001; // token 失效
    public static final int OAUTH_TOKEN_MISSING = 2008; // token 缺失
    public static final int OAUTH_TOKEN_DENIED  = 2009; // token 权限不足
}
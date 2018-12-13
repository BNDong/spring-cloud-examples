package com.microview.zuul.constant.code;

/**
 * 响应 Code 常量定义类
 */
public class ResponseCodeConstant {
    private ResponseCodeConstant() {}

    public static final int REQUEST_SUCCESS = 1; // 请求成功
    public static final int REQUEST_FAILED  = 0; // 请求失败
    public static final int SYSTEM_ERROR    = -1; // 系统错误
}
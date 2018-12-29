package com.revengemission.sso.oauth2.server.domain;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回错误结果构建处理
 */
public class TokenErrorResponseResult {

    public static String build(String error, String errorDescription) {
        Map<String, String> result = new HashMap<>();
        result.put("error", error);
        result.put("error_description", errorDescription);
        return JSON.toJSONString(result);
    }
}
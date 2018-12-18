package com.microview.zuul.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;
import java.util.Map;

/**
 * 返回结果构建处理
 * @param <T>
 */
public class ResultJsonTools<T> {

    private int    code;
    private int    statusCode;
    private String msg;
    private T      data;

    private static final int DEFAULT_STATUS_CODE = 0;

    /**
     * construction
     * @param code 请求状态码
     * @param statusCode 信息状态码
     * @param msg  信息
     * @param data 数据
     */
    public ResultJsonTools(int code, int statusCode, String msg, T data) {
        this.code       = code;
        this.statusCode = statusCode;
        this.msg        = msg;
        this.data       = data;
    }

    public static String build(int code, int statusCode, String msg) {
        ResultJsonTools<String> resultJsonTools = new ResultJsonTools<>(code, statusCode, msg, "");
        return resultJsonTools.getResultJson();
    }

    public static String build(int code, String msg) {
        return ResultJsonTools.build(code, ResultJsonTools.DEFAULT_STATUS_CODE, msg);
    }

    public static String build(int code, int statusCode, String msg, JSONArray data) {
        ResultJsonTools<JSONArray> resultJsonTools = new ResultJsonTools<>(code, statusCode, msg, data);
        return resultJsonTools.getResultJson();
    }

    public static String build(int code, String msg, JSONArray data) {
        return ResultJsonTools.build(code, ResultJsonTools.DEFAULT_STATUS_CODE, msg, data);
    }


    public static String build(int code, int statusCode, String msg, Map data) {
        JSONObject jsonObjectData = JSONObject.parseObject(JSON.toJSONString(data));
        ResultJsonTools<JSONObject> resultJsonTools = new ResultJsonTools<>(code, statusCode, msg, jsonObjectData);
        return resultJsonTools.getResultJson();
    }

    public static String build(int code, String msg, Map data) {
        return ResultJsonTools.build(code, ResultJsonTools.DEFAULT_STATUS_CODE, msg, data);
    }


    public static String build(int code, int statusCode, String msg, List data) {
        JSONArray jsonArrayData = JSONArray.parseArray(JSON.toJSONString(data));
        return ResultJsonTools.build(code, statusCode, msg, jsonArrayData);
    }

    public static String build(int code, String msg, List data) {
        return ResultJsonTools.build(code, ResultJsonTools.DEFAULT_STATUS_CODE, msg, data);
    }

    private String getResultJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", this.code);
        jsonObject.put("status_code", this.statusCode);
        jsonObject.put("msg", this.msg);
        jsonObject.put("data", this.data);
        return JSON.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect);
    }
}
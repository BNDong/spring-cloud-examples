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
    private String msg;
    private T      data;

    /**
     * construction
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     */
    public ResultJsonTools(int code, String msg, T data) {
        this.code = code;
        this.msg  = msg;
        this.data = data;
    }

    public static String build(int code, String msg) {
        ResultJsonTools<String> resultJsonTools = new ResultJsonTools<String>(code, msg, "");
        return resultJsonTools.getResultJson();
    }

    public static String build(int code, String msg, JSONArray data) {
        ResultJsonTools<JSONArray> resultJsonTools = new ResultJsonTools<JSONArray>(code, msg, data);
        return resultJsonTools.getResultJson();
    }

    public static String build(int code, String msg, Map data) {
        JSONObject jsonObjectData = JSONObject.parseObject(JSON.toJSONString(data));
        ResultJsonTools<JSONObject> resultJsonTools = new ResultJsonTools<JSONObject>(code, msg, jsonObjectData);
        return resultJsonTools.getResultJson();
    }

    public static String build(int code, String msg, List data) {
        JSONArray jsonArrayData = JSONArray.parseArray(JSON.toJSONString(data));
        return ResultJsonTools.build(code, msg, jsonArrayData);
    }

    private String getResultJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", this.code);
        jsonObject.put("msg", this.msg);
        jsonObject.put("data", this.data);
        return JSON.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect);
    }
}
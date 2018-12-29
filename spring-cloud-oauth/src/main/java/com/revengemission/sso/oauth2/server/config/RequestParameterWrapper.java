package com.revengemission.sso.oauth2.server.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

public class RequestParameterWrapper extends HttpServletRequestWrapper {

    private Map<String, String[]> parameterMap; // 所有参数的Map集合

    public RequestParameterWrapper(HttpServletRequest request) {
        super(request);
        parameterMap = request.getParameterMap();
    }

    /**
     * 获取所有参数名
     *
     * @return 返回所有参数名
     */
    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> vector = new Vector<String>(parameterMap.keySet());
        return vector.elements();
    }
    /**
     * 获取指定参数名的值，如果有重复的参数名，则返回第一个的值 接收一般变量 ，如text类型
     */
    @Override
    public String getParameter(String name) {
        String[] results = parameterMap.get(name);
        if (results != null){
            return results[0];
        }
        return null;
    }
    /**
     * 获取指定参数名的所有值的数组，接收数组变量
     */
    @Override
    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }
    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }
    public void setParameterMap(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }
}
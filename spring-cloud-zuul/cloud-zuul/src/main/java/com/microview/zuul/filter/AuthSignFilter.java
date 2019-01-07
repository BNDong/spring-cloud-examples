package com.microview.zuul.filter;

import com.microview.zuul.constant.SystemHeader;
import com.microview.zuul.constant.code.ResponseCodeConstant;
import com.microview.zuul.constant.message.ResponseMessageConstant;
import com.microview.zuul.utils.ResultJsonUtil;
import com.microview.zuul.utils.SignUtil;
import com.microview.zuul.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Component
@ConfigurationProperties(prefix = "zuul.sign")
public class AuthSignFilter implements Filter {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Boolean enabled;

    private List<String> whiteList;

    private Map<String,String> appks;

    private HttpServletRequest  request;

    private HttpServletResponse response;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.request  = (HttpServletRequest) servletRequest;
        this.response = (HttpServletResponse) servletResponse;

        if (enabled && shouldFilter()) {

            Map<String,String> headersMap = upperFirstCharHeaders();

//            log.info("=====================================================");
//            log.info(headersMap.toString());
//            log.info("************************************");

            // 请求携带签名
            String requestSign = headersMap.get(SystemHeader.X_CA_SIGNATURE);

//            log.info("请求携带签名："+requestSign);
//            log.info("************************************");

            // 剔除不参与签名的数据
            headersMap.remove(SystemHeader.X_CA_SIGNATURE);
            headersMap.remove(SystemHeader.X_CA_SIGNATURE_HEADERS);

            // 扩展请求头
            List<String> signHeaderPrefixList = new ArrayList<String>();
            // 请求 path
            String queryString = request.getQueryString();
            String path = request.getServletPath() + (queryString != null ? "?" + queryString : "");
            // 请求 querys
            Map<String,String> querys = new HashMap<String, String>();
            // 请求 bodys
            Map<String,String> bodys  = new HashMap<String, String>();

            // 获取 appkey 和 appsecret
            String appkey = headersMap.get(SystemHeader.X_CA_KEY);
            String appsecret = "null";
            if (appkey != null) {
                String sysAppsecret = appks.get(appkey);
                appsecret = sysAppsecret != null ? sysAppsecret : "null";
            }

//            log.info("appkey："+appkey);
//            log.info("appsecret："+appsecret);
//            log.info("************************************");

            // 数据签名
            String sign = SignUtil.sign(
                    appsecret,
                    request.getMethod(),
                    path,
                    headersMap,
                    querys,
                    bodys,
                    signHeaderPrefixList
            );

//            log.info("网关生成签名："+sign);
//            log.info("************************************");

            if (!sign.equals(requestSign)) {
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.getWriter().write(ResultJsonUtil.build(
                        ResponseCodeConstant.REQUEST_FAILED,
                        ResponseMessageConstant.SIGN_CHECK_ERROR
                ));
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

    private boolean shouldFilter() {
        String url = request.getRequestURI();
        for(String rules : whiteList) {
            String pattern = "^"+ rules +"(/.*)?";
            if (Pattern.matches(pattern, url)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 处理请求头headers，以“-”分割，首字母大写
     */
    private Map<String,String> upperFirstCharHeaders() {
        Map<String,String> headersMap = new HashMap<String,String>();
        Enumeration headerNames       = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key  = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            StringBuilder fkey = new StringBuilder();
            String[] names = key.split("-");

            for(int i=0; i<names.length; i++){
                fkey.append(StringUtil.toUpperFirstChar(names[i]));
                if (i < names.length-1) {
                    fkey.append("-");}
            }

            headersMap.put(fkey.toString(), value);
        }
        return headersMap;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public void setAppks(Map<String, String> appks) {
        this.appks = appks;
    }
}
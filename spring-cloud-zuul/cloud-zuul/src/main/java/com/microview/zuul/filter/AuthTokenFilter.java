package com.microview.zuul.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microview.zuul.config.CustomTokenExtractor;
import com.microview.zuul.constant.code.ResponseCodeConstant;
import com.microview.zuul.constant.message.ResponseMessageConstant;
import com.microview.zuul.utils.ResultJsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Component
@ConfigurationProperties(prefix = "zuul.oauth")
public class AuthTokenFilter implements Filter {

    private Boolean enabled;

    private String checkTokenUri;

    private List<String> whiteList;

    private HttpServletRequest request;

    private HttpServletResponse response;

    @Autowired
    CustomTokenExtractor customTokenExtractor;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.request  = (HttpServletRequest) servletRequest;
        this.response = (HttpServletResponse) servletResponse;

        if (enabled && shouldFilter()) {

            String token = customTokenExtractor.extractToken(request);
            if (StringUtils.isEmpty(token)) {
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.getWriter().write(ResultJsonUtil.build(
                        ResponseCodeConstant.REQUEST_FAILED,
                        ResponseMessageConstant.OAUTH_TOKEN_MISSING
                ));
                return;
            } else {
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                HttpEntity<String> formEntity = new HttpEntity<String>(headers);
                ResponseEntity<String> forEntity = restTemplate.postForEntity(checkTokenUri + "?token=" + token, formEntity, String.class);
                if (forEntity.getStatusCodeValue() == HttpStatus.OK.value()) {
                    JSONObject jsonObject = JSONArray.parseObject(forEntity.getBody());
                    if (jsonObject.containsKey("error") && jsonObject.containsKey("error_description")) {
                        response.setHeader("Content-Type", "application/json;charset=UTF-8");
                        response.getWriter().write(ResultJsonUtil.build(
                                ResponseCodeConstant.REQUEST_FAILED,
                                jsonObject.get("error_description").toString()
                        ));
                        return;
                    }
                } else {
                    response.setHeader("Content-Type", "application/json;charset=UTF-8");
                    response.getWriter().write(ResultJsonUtil.build(
                            ResponseCodeConstant.REQUEST_FAILED,
                            ResponseMessageConstant.OAUTH_TOKEN_CHECK_ERROR
                    ));
                    return;
                }
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

    public void setCheckTokenUri(String checkTokenUri) {
        this.checkTokenUri = checkTokenUri;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
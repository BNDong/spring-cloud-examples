package com.revengemission.sso.oauth2.server.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.revengemission.sso.oauth2.server.config.CustomAuthTokenExtendHandler;
import com.revengemission.sso.oauth2.server.config.RequestParameterWrapper;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.TokenErrorResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthTokenFilter implements Filter {

    @Autowired
    CustomAuthTokenExtendHandler customAuthTokenExtendHandler;

    private final String JTI = "jti";
    private final String ATI = "ati";

    private HttpServletRequest request;
    private HttpServletResponse response;
    private Map<String, String[]> parameterMap;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        this.request  = (HttpServletRequest) servletRequest;
        this.response = (HttpServletResponse) servletResponse;

        RequestParameterWrapper requestParameterWrapper = new RequestParameterWrapper(request);
        this.parameterMap = new HashMap<>(requestParameterWrapper.getParameterMap());
        String requestUri = request.getRequestURI();

        // ============ 验证 token 权限验证 =====================
        if (requestUri.equals("/oauth/check_token") && !checkUserAuthorize("token", JTI)) {
            responseWriteTokenExpiration(); return;

        // ============ 刷新 token 权限验证 =====================
        } else if (requestUri.equals("/oauth/token")) {
            String[] grantType = (String[]) parameterMap.get("grant_type");
            if (grantType[0].equals("refresh_token") && !checkUserAuthorize("refresh_token", ATI)) {
                responseWriteTokenExpiration(); return;
            }

        // ============ 撤销 token 权限验证 =====================
        } else if (requestUri.equals("/oauth/revokeToken") && !checkUserAuthorize("access_token", JTI)) {
            responseWriteTokenExpiration(); return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

    private Boolean checkUserAuthorize(String parameterToken, String ti) {
        String[] token = (String[]) parameterMap.get(parameterToken);
        String tokenJson = JwtHelper.decode(token[0]).getClaims();
        Map<String, String> jsonMap = JSONObject.parseObject(tokenJson, new TypeReference<Map<String, String>>(){});
        return customAuthTokenExtendHandler.checkUserAuthorize(jsonMap.get(GlobalConstant.TOKEN_USER_ID_STR), jsonMap.get(ti));
    }

    private void responseWriteTokenExpiration() {
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getWriter().write(TokenErrorResponseResult.build("invalid_token", "tokens have an expiration"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
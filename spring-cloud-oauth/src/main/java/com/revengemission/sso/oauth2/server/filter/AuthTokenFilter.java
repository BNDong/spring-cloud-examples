package com.revengemission.sso.oauth2.server.filter;

import com.revengemission.sso.oauth2.server.config.CustomAuthTokenExtendHandler;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.TokenErrorResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthTokenFilter implements Filter {

    @Autowired
    CustomAuthTokenExtendHandler customAuthTokenExtendHandler;

    private HttpServletRequest request;
    private HttpServletResponse response;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        this.request  = (HttpServletRequest) servletRequest;
        this.response = (HttpServletResponse) servletResponse;

        String requestUri = request.getRequestURI();

        // ============ 验证 token 权限验证 =====================
        if (requestUri.equals("/oauth/check_token")
                && !customAuthTokenExtendHandler.checkUserAuthorize(request.getParameter("token"), GlobalConstant.TOKEN_JTI)) {
            responseWriteTokenExpiration(); return;

        // ============ 刷新 token 权限验证 =====================
        } else if (requestUri.equals("/oauth/token")) {
            String grantType = request.getParameter("grant_type");
            if (grantType.equals("refresh_token")
                    && !customAuthTokenExtendHandler.checkUserAuthorize(request.getParameter("refresh_token"), GlobalConstant.TOKEN_ATI)) {
                responseWriteTokenExpiration(); return;
            }

        // ============ 撤销 token 权限验证 =====================
        } else if ((requestUri.equals("/oauth/revokeToken") || requestUri.equals("/oauth/revokeTokenAll"))
                && !customAuthTokenExtendHandler.checkUserAuthorize(request.getParameter("access_token"), GlobalConstant.TOKEN_JTI)) {
            responseWriteTokenExpiration(); return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }

    private void responseWriteTokenExpiration() {
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getWriter().write(TokenErrorResponseResult.build("invalid_token", "tokens have an expiration"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
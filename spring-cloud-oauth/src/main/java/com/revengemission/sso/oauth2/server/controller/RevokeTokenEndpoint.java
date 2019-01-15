package com.revengemission.sso.oauth2.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.revengemission.sso.oauth2.server.config.CustomAuthTokenExtendHandler;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FrameworkEndpoint
public class RevokeTokenEndpoint {

    @Autowired
    CustomAuthTokenExtendHandler customAuthTokenExtendHandler;

    @Autowired
    ClientDetailsService clientDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping(
            value = {"/oauth/revokeToken"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public ResponseResult revokeToken(@RequestParam(value = "client_id") String clientId,
                              @RequestParam(value = "client_secret") String clientSecret,
                              @RequestParam(value = "access_token") String accessToken) {
        ResponseResult responseResult = new ResponseResult();

        if (StringUtils.isAnyBlank(clientId, clientSecret, accessToken)) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage(GlobalConstant.ERROR_MESSAGE_ILLEGAL_PARAMETER);
            return responseResult;
        }

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null || !passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            responseResult.setStatus(GlobalConstant.ERROR_DENIED);
            responseResult.setMessage(GlobalConstant.ERROR_MESSAGE_DENIED);
            return responseResult;
        }
        String tokenJson = JwtHelper.decode(accessToken).getClaims();
        Map<String, String> jsonMap = JSONObject.parseObject(tokenJson, new TypeReference<Map<String, String>>() {});
        customAuthTokenExtendHandler.setUserAuthTokenWhitelist(jsonMap.get(GlobalConstant.TOKEN_USER_ID_STR), "revokeToken");
        return responseResult;
    }

    @RequestMapping(
            value = {"/oauth/revokeTokenAll"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public ResponseResult revokeTokenAll(@RequestParam(value = "client_id") String clientId,
                                      @RequestParam(value = "client_secret") String clientSecret,
                                      @RequestParam(value = "access_token") String accessToken) {
        ResponseResult responseResult = new ResponseResult();

        if (StringUtils.isAnyBlank(clientId, clientSecret, accessToken)) {
            responseResult.setStatus(GlobalConstant.ERROR);
            responseResult.setMessage(GlobalConstant.ERROR_MESSAGE_ILLEGAL_PARAMETER);
            return responseResult;
        }

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null || !passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
            responseResult.setStatus(GlobalConstant.ERROR_DENIED);
            responseResult.setMessage(GlobalConstant.ERROR_MESSAGE_DENIED);
            return responseResult;
        }
        String tokenJson = JwtHelper.decode(accessToken).getClaims();
        Map<String, String> jsonMap = JSONObject.parseObject(tokenJson, new TypeReference<Map<String, String>>() {});
        customAuthTokenExtendHandler.cancelUserAuthorization(jsonMap.get(GlobalConstant.TOKEN_USER_ID_STR));
        return responseResult;
    }
}
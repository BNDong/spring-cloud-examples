package com.revengemission.sso.oauth2.server.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.utils.JSONUtil;
import com.revengemission.sso.oauth2.server.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class CustomAuthTokenExtendHandler {

    private final String EXTEND_DATA_KEY_PREFIX     = "OAuthToken_extendData_";
    private final String USER_WHITE_LIST_KEY_PREFIX = "OAuthToken_userTokenWhitelist_";

    @Value("${jwt.token.accessTokenValiditySeconds}")
    private int accessTokenValiditySeconds;

    @Value("${jwt.token.refreshTokenValiditySeconds}")
    private int refreshTokenValiditySeconds;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 从 request 和 redis 中获取自定义数据
     * request 中自定义数据优先级大于 redis
     * @return
     */
    Map<String, String> getRequestExtendData() {
        RequestParameterWrapper requestParameterWrapper = new RequestParameterWrapper(request);
        Map<String, String[]> parameterMap = new HashMap<>(requestParameterWrapper.getParameterMap());
        String[] grantType = (String[]) parameterMap.get("grant_type");
        Set<String> keyset = request.getParameterMap().keySet();

        Map<String, String> requestExtendMap = new HashMap<String, String>();
        Map<String, String> cacheExtendMap   = new HashMap<String, String>();
        Map<String, String> resultMap        = new HashMap<String, String>();

        // 获取请求中自定义数据
        String pattern = "^"+ GlobalConstant.TOKEN_EXTEND_STR +"\\[.*]$";
        for (String str : keyset) {
            if(Pattern.matches(pattern, str)) {
                String[] data = parameterMap.get(str);
                requestExtendMap.put(str.substring(7, (str.length() - 1)), data[0]);
            }
        }

        // 获取缓存中自定义数据
        if (grantType[0].equals("refresh_token")) {
            RedisUtil redisUtil = new RedisUtil();
            redisUtil.setRedisTemplate(stringRedisTemplate);
            String[] refreshToken = (String[]) parameterMap.get("refresh_token");
            String key = getRefreshTokenCacheKey(this.getTokenUserClientType(refreshToken[0]), refreshToken[0]);
            cacheExtendMap = this.getJsonValueCorrespondingToKeyAndConvertMap(key);
        }

        resultMap.putAll(cacheExtendMap);
        resultMap.putAll(requestExtendMap);
        return resultMap;
    }

    /**
     * 缓存 token 自定义数据
     * @param extendMap 自定义数据
     * @param refreshTokenStr 刷新token
     */
    void extendDataCache(Map<String, String> extendMap, String refreshTokenStr) {
        if (extendMap.size() > 0) {
            RedisUtil redisUtil = new RedisUtil();
            redisUtil.setRedisTemplate(stringRedisTemplate);

            String userClientType = this.getTokenUserClientType(refreshTokenStr);
            String key = this.getRefreshTokenCacheKey(userClientType, refreshTokenStr);

            // 设置该用户该客户端类型数据
            redisUtil.setEx(
                    key,
                    JSON.toJSONString(extendMap),
                    (refreshTokenValiditySeconds + 60),
                    TimeUnit.SECONDS);

            // 清除该用户该客户端类型其他数据
            Set<String> keys = redisUtil.keys(getUserSingleClientPatternKey(userClientType));
            keys.remove(key);
            if (keys.size() > 0) { redisUtil.delete(keys); }
        }
    }

    /**
     * 设置用户单客户端类型授权
     */
    void setUserAuthTokenWhitelist(Map<String, Object> additionalInformation) {
        String userId = additionalInformation.get(GlobalConstant.TOKEN_USER_ID_STR).toString();

        if (null != userId) {
            String userClientType = additionalInformation.get(GlobalConstant.TOKEN_USER_CLIENT_TYPE).toString();
            String jti = additionalInformation.get(GlobalConstant.TOKEN_JTI).toString();

            RedisUtil redisUtil = new RedisUtil();
            redisUtil.setRedisTemplate(stringRedisTemplate);

            String key = this.getUserTokenCacheKey(userId);

            Map<String, String> cacheWhitelistMap = this.getJsonValueCorrespondingToKeyAndConvertMap(key);
            cacheWhitelistMap.put(userClientType, jti);

            redisUtil.set(key, JSON.toJSONString(cacheWhitelistMap));
        }
    }

    /**
     * 设置用户单客户端类型授权
     */
    public void setUserAuthTokenWhitelist(String userId, String jti) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(stringRedisTemplate);

        String key = this.getUserTokenCacheKey(userId);
        String userClientType = this.getUserClientType();

        Map<String, String> cacheWhitelistMap = this.getJsonValueCorrespondingToKeyAndConvertMap(key);
        cacheWhitelistMap.put(userClientType, jti);

        redisUtil.set(key, JSON.toJSONString(cacheWhitelistMap));
    }

    /**
     * 注销用户全部授权
     */
    public void cancelUserAuthorization(String userId) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(stringRedisTemplate);
        redisUtil.delete(this.getUserTokenCacheKey(userId));
    }

    /**
     * 验证用户授权 token
     * @param token 需要验证的 token
     * @param tiType jti || ati
     * @return
     */
    public Boolean checkUserAuthorize(String token, String tiType) {
        String tokenJson = JwtHelper.decode(token).getClaims();
        this.getUserClientType();
        Map<String, String> jsonMap = JSONObject.parseObject(tokenJson, new TypeReference<Map<String, String>>(){});

        String userClientType = this.getTokenUserClientType(token);
        String tiValue = jsonMap.get(tiType);
        String userId = jsonMap.get(GlobalConstant.TOKEN_USER_ID_STR);
        if (null != tiValue && null != userId) {
            String key = getUserTokenCacheKey(userId);
            Map<String, String> cacheWhitelistMap = this.getJsonValueCorrespondingToKeyAndConvertMap(key);
            String wjti = cacheWhitelistMap.get(userClientType);
            return (null != wjti && wjti.equals(tiValue));
        }
        return false;
    }

    /**
     * 获取用户客户端类型，优先从 token 中获取
     */
    String getUserClientType() {

        String token = request.getParameter("token");
        if (null != token && !token.equals("")) {
            return this.getTokenUserClientType(token);
        }

        token = request.getParameter("access_token");
        if (null != token && !token.equals("")) {
            return this.getTokenUserClientType(token);
        }

        token = request.getParameter("refresh_token");
        if (null != token && !token.equals("")) {
            return this.getTokenUserClientType(token);
        }

        return getRequestUserClientType();
    }

    /**
     * 从 request 中获取用户客户端类型
     * @return String
     */
    private String getRequestUserClientType() {
        String userClientType = request.getParameter(GlobalConstant.TOKEN_USER_CLIENT_TYPE);
        if (userClientType == null || userClientType.equals("")) {
            return "default";
        }
        return userClientType;
    }

    /**
     * 从 token 中获取用户客户端类型
     * @return String
     */
    private String getTokenUserClientType(String token) {
        String tokenJson = JwtHelper.decode(token).getClaims();
        Map<String, String> jsonMap = JSONObject.parseObject(tokenJson, new TypeReference<Map<String, String>>(){});
        String userClientType = jsonMap.get(GlobalConstant.TOKEN_USER_CLIENT_TYPE);
        if (userClientType == null || userClientType.equals("")) {
            return "default";
        }
        return userClientType;
    }

    /**
     * 获取缓存自定义数据 key
     * @param userClientType 用户客户端类型
     * @param refreshTokenStr 刷新token
     * @return String
     */
    private String getRefreshTokenCacheKey(String userClientType, String refreshTokenStr) {
        return this.EXTEND_DATA_KEY_PREFIX + userClientType + "_" + refreshTokenStr.substring((refreshTokenStr.length() - 35));
    }

    /**
     * 获取单用户某客户端类型，缓存数据模糊匹配的 key
     * @param userClientType 用户客户端类型
     * @return String
     */
    private String getUserSingleClientPatternKey(String userClientType) {
        return this.EXTEND_DATA_KEY_PREFIX + userClientType + "_*";
    }

    /**
     * 获取缓存用户 token 授权 key
     * @param userId 用户ID
     * @return String
     */
    private String getUserTokenCacheKey(String userId) {
        return this.USER_WHITE_LIST_KEY_PREFIX + userId;
    }

    /**
     * 获取 redis key 对应的 json value 并转换为 map
     * @param key 键
     * @return Map<String, String>
     */
    private Map<String, String> getJsonValueCorrespondingToKeyAndConvertMap(String key) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(stringRedisTemplate);

        Map<String, String> dataMap = new HashMap<String, String>();
        if (redisUtil.hasKey(key)) {
            String jsonStr = redisUtil.get(key);
            if (JSONUtil.isJSONValid(jsonStr)) {
                dataMap = JSONObject.parseObject(jsonStr, new TypeReference<Map<String, String>>(){});
            }
        }
        return dataMap;
    }
}
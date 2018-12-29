package com.revengemission.sso.oauth2.server.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class CustomAuthTokenExtendHandler {

    @Value("${jwt.token.accessTokenValiditySeconds}")
    private int accessTokenValiditySeconds;

    @Value("${jwt.token.refreshTokenValiditySeconds}")
    private int refreshTokenValiditySeconds;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取自定义数据
     * @return
     */
    public Map<String, String> getRequestExtendData() {
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
            String[] refreshToken = (String[]) parameterMap.get("refresh_token");
            String key = getRefreshTokenCacheKey(refreshToken[0]);
            RedisUtil redisUtil = new RedisUtil();
            redisUtil.setRedisTemplate(stringRedisTemplate);
            if (redisUtil.hasKey(key)) {
                String jsonStr = redisUtil.get(key);
                cacheExtendMap = JSONObject.parseObject(jsonStr, new TypeReference<Map<String, String>>(){});
            }
        }

        resultMap.putAll(cacheExtendMap);
        resultMap.putAll(requestExtendMap);
        return resultMap;
    }

    /**
     * 使用刷新 token 缓存自定义数据
     * @param extendMap 自定义数据
     * @param refreshTokenStr 刷新token
     */
    public void extendDataCache(Map<String, String> extendMap, String refreshTokenStr) {
        if (extendMap.size() > 0) {
            RedisUtil redisUtil = new RedisUtil();
            redisUtil.setRedisTemplate(stringRedisTemplate);
            redisUtil.setEx(
                    getRefreshTokenCacheKey(refreshTokenStr),
                    JSON.toJSONString(extendMap), (refreshTokenValiditySeconds + 60), TimeUnit.SECONDS);
        }
    }

    /**
     * 设置用户授权 token
     */
    public void setUserAuthTokenWhitelist(Map<String, Object> additionalInformation) {
        String userId = additionalInformation.get(GlobalConstant.TOKEN_USER_ID_STR).toString();
        if (null != userId) {
            RedisUtil redisUtil = new RedisUtil();
            redisUtil.setRedisTemplate(stringRedisTemplate);
            redisUtil.set(getUserTokenCacheKey(userId), additionalInformation.get("jti").toString());
        }
    }

    /**
     * 设置用户授权 token
     */
    public void setUserAuthTokenWhitelist(String userId, String jti) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(stringRedisTemplate);
        redisUtil.set(getUserTokenCacheKey(userId), jti);
    }

    /**
     * 验证用户授权 token
     * @param userId 用户ID
     * @param jti jwt jti
     * @return
     */
    public Boolean checkUserAuthorize(String userId, String jti) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(stringRedisTemplate);
        String key = getUserTokenCacheKey(userId);
        if (redisUtil.hasKey(key)) {
            return redisUtil.get(key).equals(jti);
        }
        return false;
    }

    /**
     * 获取缓存自定义数据 key
     * @param refreshTokenStr 刷新token
     * @return String
     */
    private String getRefreshTokenCacheKey(String refreshTokenStr) {
        return "OAuthToken_extendData_" + refreshTokenStr.substring((refreshTokenStr.length() - 35));
    }

    /**
     * 获取缓存用户token数据 key
     * @param userId 用户ID
     * @return String
     */
    private String getUserTokenCacheKey(String userId) {
        return "OAuthToken_userTokenWhitelist_" + userId;
    }
}
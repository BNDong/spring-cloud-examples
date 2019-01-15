package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.domain.GlobalConstant;
import com.revengemission.sso.oauth2.server.domain.UserInfo;
import com.revengemission.sso.oauth2.server.service.impl.ClientDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    ClientDetailsServiceImpl clientDetailsService;

    @Autowired
    UserDetailsService userDetailsService;

    @Value("${jwt.jks.keypass}")
    private String keypass;

    @Autowired
    CustomAuthTokenExtendHandler customAuthTokenExtendHandler;

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter() {
            /***
             * 重写增强token方法,用于自定义一些token返回的信息
             */
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

                /** 自定义一些token属性 **/
                Map<String, Object> additionalInformation = new HashMap<>();

                //Important !,client_credentials mode ,no user!
                if (authentication.getUserAuthentication() != null) {
                    UserInfo user = (UserInfo) authentication.getUserAuthentication().getPrincipal();// 与登录时候放进去的UserDetail实现类一致
                    additionalInformation.put(GlobalConstant.TOKEN_USER_ID_STR, user.getUserId());
                }

                // 添加请求中自定义数据
                Map<String, String> extendMap = customAuthTokenExtendHandler.getRequestExtendData();
                if (extendMap.size() > 0) { additionalInformation.put(GlobalConstant.TOKEN_EXTEND_STR, extendMap); }

                // 添加用户客户端类型
                additionalInformation.put(GlobalConstant.TOKEN_USER_CLIENT_TYPE, customAuthTokenExtendHandler.getUserClientType());

                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
                OAuth2AccessToken result = super.enhance(accessToken, authentication);
                String accessTokenStr    = result.getValue();
                String refreshTokenStr   = result.getRefreshToken().getValue();

                // 缓存自定义数据
                customAuthTokenExtendHandler.extendDataCache(extendMap, refreshTokenStr);

                // 更新用户授权token
                customAuthTokenExtendHandler.setUserAuthTokenWhitelist(result.getAdditionalInformation());
                return result;
            }

        };
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), keypass.toCharArray());
        accessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwt"));

        // 测试用,资源服务使用相同的字符达到一个对称加密的效果,生产时候使用RSA非对称加密方式
        //accessTokenConverter.setSigningKey("123");
        return accessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore() {
        TokenStore tokenStore = new JwtTokenStore(accessTokenConverter());
        return tokenStore;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//      import!,client和user的加密方式须一致
//        clients.inMemory()
//                .withClient("SampleClientId")
//                .authorizedGrantTypes("implicit", "authorization_code", "refresh_token", "password", "client_credentials")
//                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
//                .scopes("read", "write", "trust")
//                .secret("secret")
//                .accessTokenValiditySeconds(3600)
//                .refreshTokenValiditySeconds(2592000);
        clients.withClientDetails(clientDetailsService);
    }

    @Bean
    public ApprovalStore approvalStore() {
        TokenApprovalStore approvalStore = new TokenApprovalStore();
        approvalStore.setTokenStore(tokenStore());
        return approvalStore;
    }

    @Bean
    public DefaultOAuth2RequestFactory requestFactory() {
        return new DefaultOAuth2RequestFactory(clientDetailsService);
    }

    @Bean
    public UserApprovalHandler userApprovalHandler() {
        ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
        userApprovalHandler.setApprovalStore(approvalStore());
        userApprovalHandler.setClientDetailsService(clientDetailsService);
        userApprovalHandler.setRequestFactory(requestFactory());
        return userApprovalHandler;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //注入authenticationManager来支持password模式
        endpoints.authenticationManager(authenticationManager);
        endpoints.accessTokenConverter(accessTokenConverter());
        endpoints.tokenStore(tokenStore());
        //!!!要使用refresh_token的话，需要额外配置userDetailsService!!!
        endpoints.userDetailsService(userDetailsService);
        endpoints.reuseRefreshTokens(true);

        endpoints.userApprovalHandler(userApprovalHandler());

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
                .checkTokenAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')")
                .allowFormAuthenticationForClients();
    }

}

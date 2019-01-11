package com.microview.zuul.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableResourceServer
@ConfigurationProperties(prefix = "zuul.oauth")
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Boolean enabled;

    private String tokenKeyUri;

    private List<String> whiteList;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * Token转换器必须与认证服务一致
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();

        String publicKey = null;

        /*try {
            Resource resource = new ClassPathResource("public.txt");
            publicKey = IOUtils.toString(resource.getInputStream(), "UTF-8");
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("get public key exception", e);
            }
        }*/

        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> result = new HashMap<>();
            result = restTemplate.getForObject(tokenKeyUri, result.getClass());
            if (result != null && result.size() > 0) {
                publicKey = result.get("value");
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("get public key exception", e);
            }
        }
        accessTokenConverter.setVerifierKey(publicKey);
        return accessTokenConverter;
    }

    /**
     * 创建一个默认的资源服务token
     */
    @Bean
    public ResourceServerTokenServices defaultTokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);

        if (enabled) {
            for(String rules : whiteList) {
                http.authorizeRequests().antMatchers(rules + "/**").permitAll();
            }
        } else {
            http.authorizeRequests().antMatchers("/**").permitAll();
        }

        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .authorizeRequests()
                .antMatchers("/get/**")
                .access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenExtractor(new CustomTokenExtractor());
        resources.authenticationEntryPoint(new AuthExceptionEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler());
    }

    public void setTokenKeyUri(String tokenKeyUri) {
        this.tokenKeyUri = tokenKeyUri;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
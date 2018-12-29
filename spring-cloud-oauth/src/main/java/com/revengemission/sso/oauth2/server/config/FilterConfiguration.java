package com.revengemission.sso.oauth2.server.config;

import com.revengemission.sso.oauth2.server.filter.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Bean
    public FilterRegistrationBean tokenFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(authTokenFilter);
        registration.addUrlPatterns("/oauth/*");
        return registration;
    }
}
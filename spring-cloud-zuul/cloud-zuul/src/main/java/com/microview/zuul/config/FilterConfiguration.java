package com.microview.zuul.config;

import com.microview.zuul.filter.AuthSignFilter;
import com.microview.zuul.filter.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private AuthSignFilter authSignFilter;

    @Bean
    public FilterRegistrationBean authSignFilterRegistration() {
        FilterRegistrationBean<AuthSignFilter> registration = new FilterRegistrationBean<AuthSignFilter>();
        registration.setFilter(authSignFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean authTokenFilterRegistration() {
        FilterRegistrationBean<AuthTokenFilter> registration = new FilterRegistrationBean<AuthTokenFilter>();
        registration.setFilter(authTokenFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
package com.microview.zuul.config;

import com.microview.zuul.filter.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @Autowired
    private AuthTokenFilter localApiAuthTokenFilter;

    @Bean
    public FilterRegistrationBean tokenFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(localApiAuthTokenFilter);
        registration.addUrlPatterns("/*");
        return registration;
    }
}
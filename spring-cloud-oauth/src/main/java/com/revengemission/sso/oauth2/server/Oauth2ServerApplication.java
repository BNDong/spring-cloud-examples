package com.revengemission.sso.oauth2.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/*
*
* https://projects.spring.io/spring-security-oauth/docs/oauth2.html
* https://spring.io/guides/tutorials/spring-boot-oauth2/
*
* */
@SpringBootApplication
@EnableDiscoveryClient
public class Oauth2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ServerApplication.class, args);
    }
}

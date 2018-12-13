package com.microview.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class MonitorController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 自动刷新配置
     */
    @PostMapping("/monitor")
    public String monitor() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("CLOUD-CONFIG-SERVER");
        String url = String.format("http://%s:%s/%s", serviceInstance.getHost(), serviceInstance.getPort(), "actuator/bus-refresh");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        HttpEntity<String> formEntity = new HttpEntity<String>(headers);
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url, formEntity, String.class);
        return forEntity.getBody();
    }
}
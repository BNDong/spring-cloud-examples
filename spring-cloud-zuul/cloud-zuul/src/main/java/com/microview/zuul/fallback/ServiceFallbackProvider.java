package com.microview.zuul.fallback;

import com.microview.zuul.constant.code.ResponseCodeConstant;
import com.microview.zuul.constant.message.ResponseMessageConstant;
import com.microview.zuul.tools.ResultJsonTools;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Zuul 回退机制处理器。
 * Provides fallback when a failure occurs on a route 提供一个回退机制当路由后面的服务发生故障时。
 */
@Component
public class ServiceFallbackProvider implements FallbackProvider {

    @Override
    public String getRoute() {
        return "*"; // 服务 serviceId，如果需要所有调用都支持回退，则 return "*" 或 return null
    }

    /**
     * 请求用户服务失败，返回信息给消费者客户端
     */
    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new ClientHttpResponse(){

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(ResultJsonTools.build(
                        ResponseCodeConstant.SYSTEM_ERROR,
                        ResponseMessageConstant.SYSTEM_ERROR
                    ).getBytes("UTF-8"));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8); //和body中的内容编码一致，否则容易乱码
                return headers;
            }

            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK; // fallback时的状态码
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {}
        };
    }
}
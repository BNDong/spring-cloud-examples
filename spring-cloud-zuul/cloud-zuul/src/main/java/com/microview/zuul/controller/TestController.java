package com.microview.zuul.controller;

import com.microview.zuul.exception.BusinessException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@ConfigurationProperties(prefix = "person")
public class TestController {

    @RequestMapping(value = "err")
    public void error(){
        throw new BusinessException(400, "业务异常错误信息");
    }

    @RequestMapping(value = "err2")
    public void error2(){
        throw new NullPointerException("手动抛出异常信息");
    }

    @RequestMapping(value = "err3")
    public int error3(){
        int a = 10 / 0;
        return a;
    }


    private List<String> ttt;

    public void setTtt(List<String> ttt) {
        this.ttt = ttt;
    }

    @PostMapping("/test")
    public String test(HttpServletRequest request) {
        Enumeration<?> enum1 = request.getHeaderNames();
        while (enum1.hasMoreElements()) {
            String key = (String) enum1.nextElement();
            String value = request.getHeader(key);
            System.out.println(key + "\t" + value);
        }
        return "okok";
    }
}
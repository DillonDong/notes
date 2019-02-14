package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author:fudingcheng
 * @date:2019-02-10
 * @description:
 */
@RestController
public class HelloServlet {

    @Resource
    Config config;

    @Value("${name}")
    String name;

    @RequestMapping("/hello")
    public String hello() {
        System.out.println(config);
        System.out.println(name);
        return "hello Spring Boot";
    }
}

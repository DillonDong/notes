package com.itheima.shop;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubboConfiguration
@SpringBootApplication
public class PayWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayWebApplication.class,args);
    }

}

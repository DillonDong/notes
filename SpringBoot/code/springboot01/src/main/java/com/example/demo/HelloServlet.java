package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:fudingcheng
 * @date:2019-02-10
 * @description:
 */
@RestController
public class HelloServlet {

    @RequestMapping("/hello")
    public String hello() {
        return "hello Spring Boot";
    }
}

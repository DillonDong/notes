package com.itheima.shop.test;

import com.itheima.shop.springboot.rocketmq.ProducerApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProducerApplication.class})
@Slf4j
public class ProducerTest {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void testSendMessage(){
        rocketMQTemplate.convertAndSend("springboot-rocketmq","Hello Springboot Rockekmq");
        log.info("消息发送成功");
    }

}

package com.mandala.mq.produce.controller;

import com.mandala.mq.produce.config.JmsConfig;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author songzhenliang
 * @Date 2023-05-18 09:49
 */
@RestController
@RequestMapping("msg/")
public class MsgController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @PostMapping("sendMsg")
    private String sendMsg(){
        rocketMQTemplate.convertAndSend(JmsConfig.TOPIC, "这是一条测试消息");

        System.out.println("发送成功");
        return "OK";
    }
}

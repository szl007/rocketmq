package com.mandala.mq.produce.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author songzhenliang
 * @Date 2023-05-26 11:18
 */
@RestController
@RequestMapping("mqMessageController")
@Slf4j
public class MqMessageController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value(value = "${rocketmq.producer.topic}:${rocketmq.producer.sync-tag}")
    private String syncTag;

    @Value(value = "${rocketmq.producer.topic}:${rocketmq.producer.async-tag}")
    private String asyncTag;

    @Value(value = "${rocketmq.producer.topic}:${rocketmq.producer.oneway-tag}")
    private String onewayTag;


    /**
     * 同步消息
     * @return
     */
    @RequestMapping("pushSyncMessgage")
    public String pushSyncMessgage(@RequestParam("id") int id){
        log.info("push message id {}",id);
        return "";
    }

}


package com.mandala.mq.produce.controller;

import com.alibaba.fastjson.JSONObject;
import com.mandala.mq.produce.listener.SendCallBackListener;
import com.mandala.mq.produce.model.ResponseMsg;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
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
    public ResponseMsg pushSyncMessgage(@RequestParam("id") int id){
        log.info("push sync message id {}",id);

        // 构建消息
        String messageStr = "order id :" + id;
        Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS, id).build();

        // 发送同步消息
        SendResult sendResult = rocketMQTemplate.syncSend(syncTag, message);
        log.info("pushSyncMessage finish : {}, result : {}", id, JSONObject.toJSONString(sendResult));
        if(sendResult.getSendStatus() == SendStatus.SEND_OK){
            ResponseMsg.success(sendResult);
        }
        return ResponseMsg.fail();
    }


    /**
     * 异步消息
     * @param id
     * @return
     */
    @RequestMapping("pushAsyncMessage")
    public ResponseMsg pushAsyncMessage(@RequestParam("id") int id){
        log.info("push async message id {}", id);

        String messageStr = "order id :" + id;
        Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS, id).build();
        rocketMQTemplate.asyncSend(asyncTag, message, new SendCallBackListener(id));
        log.info("pushAsyncMessage finish :{}", id);
        return ResponseMsg.success();
    }
}


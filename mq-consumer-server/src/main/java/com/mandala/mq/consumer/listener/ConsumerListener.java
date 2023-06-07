package com.mandala.mq.consumer.listener;

import com.mandala.mq.consumer.config.JmsConfig;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author songzhenliang
 * @Date 2023-05-19 15:02
 */
@Component
@RocketMQMessageListener(topic = JmsConfig.TOPIC, consumerGroup = "my-consumer-group")
public class ConsumerListener implements RocketMQListener {
    @Override
    public void onMessage(Object o) {
        System.out.println("接收消息：" + o.toString());
    }
}

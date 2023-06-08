package com.mandala.mq.produce.controller;

import com.alibaba.fastjson.JSONObject;
import com.mandala.mq.produce.listener.SendCallBackListener;
import com.mandala.mq.produce.model.OrderStep;
import com.mandala.mq.produce.model.ResponseMsg;
import com.mandala.mq.produce.util.ListSplitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
    @PostMapping("pushSyncMessgage")
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
    @PostMapping("pushAsyncMessage")
    public ResponseMsg pushAsyncMessage(@RequestParam("id") int id){
        log.info("push async message id {}", id);

        String messageStr = "order id :" + id;
        Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS, id).build();
        rocketMQTemplate.asyncSend(asyncTag, message, new SendCallBackListener(id));
        log.info("pushAsyncMessage finish :{}", id);
        return ResponseMsg.success();
    }

    /**
     * 单项消息 (不关注发送结果)
     * @param id
     * @return
     */
    public ResponseMsg pushOneWayMessage(@RequestParam("id") int id){
        log.info("单项消息 id: {}", id);
        String messageStr = "order id: "+ id;
        Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS, id).build();
        rocketMQTemplate.sendOneWay(onewayTag, message);
        log.info("单项消息 结束 id：{}",id);
        return ResponseMsg.success();
    }

    /**
     * 发送包含顺序的单向消息
     * @param id
     * @return
     */
    public ResponseMsg pushSequeueMessage(@RequestParam("id") int id){
        log.info("顺序单项消息 id:{}", id);
        for(int i = 0; i < 3; i++){
            String myId = id + "" + i;
            List<OrderStep> orderList = OrderStep.buildOrderSteps(myId);
            orderList.forEach(order ->{
                String messageStr = String.format("order id : %s, desc : %s", order.getId(), order.getDesc());
                Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS, order.getId()).build();
                //顺序下发
                rocketMQTemplate.setMessageQueueSelector(new MessageQueueSelector() {
                    /**
                     * 设置放入同一个队列的规则
                     * @param list 消息列表
                     * @param message 消息
                     * @param o 关键信息
                     * @return
                     */
                    @Override
                    public MessageQueue select(List<MessageQueue> list, org.apache.rocketmq.common.message.Message message, Object o) {
                        int queueNum = Integer.valueOf(String.valueOf(o)) % list.size();
                        log.info(String.format("queueNum : %s, message : %s", queueNum, new String(message.getBody())));
                        return list.get(queueNum);
                    }
                });
                rocketMQTemplate.syncSendOrderly(syncTag, message, order.getId());
            });
        }
        log.info("顺序单项详细 结束：{}", id);
        return ResponseMsg.success();
    }

    /**
     * 延迟消息
     * @param id
     * @return
     */
    public ResponseMsg pushDelayMessage(@RequestParam("id") int id){
        log.info("延迟消息 id:{}", id);
        String messageStr = "order id :" + id;
        Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS,id).build();
        // 设置超时和延时推送
        // 超时时针对请求broker然后结果返回给product的耗时
        // 现在RocketMq并不支持任意时间的延时，需要设置几个固定的延时等级，从1s到2h分别对应着等级1到18
        //private String messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
        SendResult result = rocketMQTemplate.syncSend(syncTag, message, 1 * 1000l, 4);
        if (result.getSendStatus() == SendStatus.SEND_OK) {
            return ResponseMsg.success(result);
        }
        return ResponseMsg.fail();
    }

    /**
     * 同时发送10条消息  批量操作
     * @param id
     * @return
     */
    public ResponseMsg pushBatchMessage(@RequestParam("id") int id){
        log.info("批量发送消息 id: {}", id);
        List<Message> messages = new ArrayList<>();
        for(int i =0; i < 3; i++){
            String myId = id + "" +i;
            String messageStr = "order id " + id;
            Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS,id).build();
            messages.add(message);
        }
        // 批量下发消息到broker,不支持消息顺序操作，并且对消息体有大小限制（不超过4M）
        ListSplitter splitter = new ListSplitter(messages, 1024 * 1024 * 4);
        while (splitter.hasNext()){
            List<Message> list = splitter.next();
            rocketMQTemplate.syncSend(syncTag, list);
        }
        log.info("批量发送消息 结束 {}", id);
        return ResponseMsg.success();
    }

    /**
     * sql过滤消息
     * @return
     */
    public ResponseMsg pushSqlMessage(@RequestParam("id") int id){
        log.info("sql 消息 id: {}", id);
        List<Message> messages = new ArrayList<>();
        for (int i =0; i < 10; i++){
            String myId = id + "" + id;
            String messageStr = "order id : " + myId;
            Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS, id).setHeader("money", 1).build();
            messages.add(message);
        }
        rocketMQTemplate.syncSend(syncTag, messages);
        log.info("sql消息 结束 {}", id);
        return ResponseMsg.success();
    }

    /**
     * 事务消息
     * @param id
     * @return
     */
    public ResponseMsg pushTransactionMessage(@RequestParam("id") int id){
        String messageStr = "order id :" + id;
        Message<String> message = MessageBuilder.withPayload(messageStr).setHeader(RocketMQHeaders.KEYS, id).setHeader("money", 10)
                .setHeader(RocketMQHeaders.TRANSACTION_ID, id).build();
        TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction(syncTag, message, null);
        log.info("result : {}", JSONObject.toJSONString(result));
        return ResponseMsg.success();
    }
}


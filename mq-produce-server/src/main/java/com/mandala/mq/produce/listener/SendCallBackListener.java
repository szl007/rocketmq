package com.mandala.mq.produce.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * @Author songzhenliang
 * @Date 2023-06-07 17:10
 */
@Slf4j
@Data
public class SendCallBackListener implements SendCallback {

    private Integer id;

    public SendCallBackListener(Integer id){
        this.id = id;
    }

    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("onSuccess result: {}", JSONObject.toJSONString(sendResult));
    }

    @Override
    public void onException(Throwable throwable) {
        log.info("onException : {}", throwable);
    }
}

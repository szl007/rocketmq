package com.mandala.mq.produce.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author songzhenliang
 * @Date 2023-05-26 14:08
 */
@Data
public class OrderStep {

    /**
     * 订单id
     */
    private String id;

    /**
     * 订单描述
     */
    private String desc;


    public OrderStep(String id, String desc){
        this.id = id;
        this.desc = desc;
    }


    public static List<OrderStep> buildOrderSteps(String id){
        List<OrderStep> orderList = new ArrayList<>();

        OrderStep createOrder = new OrderStep(id, "create order");
        orderList.add(createOrder);

        OrderStep payOrder = new OrderStep(id, "pay order");
        orderList.add(payOrder);

        OrderStep callBackOrder = new OrderStep(id, "call back order");
        orderList.add(callBackOrder);

        return orderList;
    }
}

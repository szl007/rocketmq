package com.mandala.mq.produce.model;

import lombok.Data;

/**
 * @Author songzhenliang
 * @Date 2023-05-26 14:15
 */
@Data
public class ResponseMsg {


    public static final int CODE_FAIL = 500;

    public static final int CODE_SUCCESS = 200;

    public static final String MSG_SUCCESS = "success";

    public static final String MSG_FAIL = "fail";

    private int code;

    private String msg;

    private Object data;

    public ResponseMsg(){}

    public ResponseMsg(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public ResponseMsg(int code, String msg, Object data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseMsg fail(){
        return new ResponseMsg(CODE_FAIL, MSG_FAIL);
    }

    public static ResponseMsg success(){
        return new ResponseMsg(CODE_SUCCESS, MSG_SUCCESS);
    }

    public static ResponseMsg success(Object data){
        return new ResponseMsg(CODE_SUCCESS, MSG_SUCCESS, data);
    }

}

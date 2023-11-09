package com.zhike.util.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

//响应数据类
@Getter
@AllArgsConstructor
public class ResponseData<K> {
    private Boolean success;//是否请求成功
    private String message;//请求通知
    private String code;//业务状态码
    private K data;//数据

    public ResponseData(Boolean success, String message, String code) {
        this.success = success;
        this.message = message;
        this.code = code;
    }
}

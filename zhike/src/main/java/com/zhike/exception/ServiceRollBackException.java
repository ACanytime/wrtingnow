package com.zhike.exception;

import lombok.Getter;

@Getter
public class ServiceRollBackException extends ServiceException{


    public ServiceRollBackException(String message, String code) {
        super(message, code);
    }
}

package com.zhike.exception;

import lombok.Getter;

@Getter
public class ValidateParamException extends ServiceException{

    public ValidateParamException(String message, String code) {
        super(message, code);
    }
}

package com.mahmoudzain.mnp_porting_system.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException  {

    private final MnpErrors error;
    private final Object[] arguments;

    public BusinessException(MnpErrors error, Object... arguments) {
        super(error.getMessageTemplate());
        this.error = error;
        this.arguments = arguments;
    }
}


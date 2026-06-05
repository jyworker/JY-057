package com.hugmom.back.common;

import lombok.Getter;

/**
 * 自定义业务异常
 * 
 * @author HugMom Team
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造函数（使用默认错误码500）
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAIL.getCode();
        this.message = message;
    }

    /**
     * 构造函数（自定义错误码）
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数（使用ResultCode枚举）
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
    }
}

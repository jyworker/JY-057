package com.hugmom.back.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果类
 * 
 * @author HugMom Team
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 私有构造函数
     */
    private Result() {
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setSuccess(true);
        return result;
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(msg);
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    /**
     * 失败响应（默认消息）
     */
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMsg(ResultCode.FAIL.getMsg());
        result.setSuccess(false);
        return result;
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.FAIL.getCode());
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    /**
     * 失败响应（自定义状态码和消息）
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    /**
     * 失败响应（使用ResultCode枚举）
     */
    public static <T> Result<T> fail(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMsg(resultCode.getMsg());
        result.setSuccess(false);
        return result;
    }
}

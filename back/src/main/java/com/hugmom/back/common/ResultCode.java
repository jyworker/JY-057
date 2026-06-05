package com.hugmom.back.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 * 
 * @author HugMom Team
 */
@Getter
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAIL(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * Token失效
     */
    TOKEN_EXPIRED(401, "Token失效，请重新登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 伦理阻断 - 已故亲人不可拥抱
     */
    ETHICS_DECEASED_NO_HUG(403, "该成员已进入纪念模式，无法使用拥抱功能"),

    /**
     * 伦理阻断 - 已故亲人不可报平安
     */
    ETHICS_DECEASED_NO_SAFETY(403, "该成员已进入纪念模式，无法使用报平安功能"),

    /**
     * 伦理阻断 - 健在亲人不可使用纪念功能
     */
    ETHICS_ALIVE_NO_MEMORIAL(403, "该功能仅适用于纪念模式"),

    /**
     * 系统内部错误
     */
    INTERNAL_ERROR(500, "服务器内部错误");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
